package ics440_p2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ICS440_P2 {

    private static StationData findSDInList(ConcurrentLinkedQueue <StationData> queue, String id) {
        // Given the Station ID of a WeatherData reading, iterate through the 
        // provided Collection of StationData and return the StationData object
        // with the requested ID.
        StationData returnThis = null;
        for ( StationData Station : queue ) {
            if ( Station.id.equals(id) ) {
                returnThis = Station;
            }
        }
        return returnThis;
    }
    
    public static void main(String[] args) {
        
        // Get start-date, end-date and Max/Min from user:
        // https://coderanch.com/t/598292/java/date-input-user-java
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;

        String tempString = "";
        Scanner reader = new Scanner(System.in);
        
        while (true) {
            try { 
                System.out.print("Please enter a Start Date in YYYY-MM-DD format: ");
                tempString = reader.nextLine(); 
                startDate = df.parse(tempString);
                System.out.println(startDate.toString());
                System.out.print("Please enter an End Date in YYYY-MM-DD format: ");
                tempString = reader.nextLine();
                endDate = df.parse(tempString);
                System.out.println(endDate.toString());
                if ( (startDate.compareTo(endDate)) == 1 ) {
                    System.out.println("Start Date after End Date. Try again.");
                    throw new Exception();
                }
                break;
            }
            catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
        
        // Get Max or Min temp preference here.
        Boolean SearchForTMax = true;
        while (true) {
            try {
                System.out.print("Search for Maximum or Minimum temperatures in date range? (Please type 'max' or 'min' and hit Enter.): ");
                tempString = reader.nextLine();
                if ( "max".equalsIgnoreCase(tempString) ) {
                    SearchForTMax = true;
                    break;
                } else if ( "min".equalsIgnoreCase(tempString) ) {
                    SearchForTMax = false;
                    break;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
            catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
        
        System.out.println("Looking for ghcnd_hcn subdirectory in: "+System.getProperty("user.dir"));
        
        // Get directory and load list of files
        String localFolderPath = "ghcnd_hcn";
        // String localFolderPath = "quicktest";
        ConcurrentLinkedQueue fileNameQueue = new ConcurrentLinkedQueue();
        File folder = new File(localFolderPath);
        File[] allFiles = folder.listFiles();
        for (File thisFile : allFiles) {
            // Verify that there are *.dly files in it.
            if (thisFile.getName().endsWith(".dly") ) { 
                fileNameQueue.add(thisFile);
            }
        }
        if ( fileNameQueue.isEmpty() ) {
            System.out.println("There were no *.dly files in that directory. Please try a different path.");
            System.exit(1);
        } else {
            System.out.println(String.format("Found %d files in %s. Continuing...", fileNameQueue.size(), localFolderPath));
        }
        
        // Get path to Stations file.
        // parse it and complain if there are exceptions, then start again.
        System.out.println("Looking for ghcnd-stations.txt file in: "+System.getProperty("user.dir"));
        String localStationFilePath = "ghcnd-stations.txt";
        ConcurrentLinkedQueue stationQueue = new ConcurrentLinkedQueue();
        // Populate Collection of Stations
        WeatherFileParser.StationLoader(localStationFilePath, stationQueue, startDate, endDate);
        if ( stationQueue.isEmpty() ) {
            System.out.println("There was no station data there. Please try a different path.");
            System.exit(1);
        } else {
            System.out.println(String.format("Found %d stations defined in %s. Continuing...", stationQueue.size(), localStationFilePath));
        }

        // Keep start time so we can figure out how long this took/takes.
        // https://stackoverflow.com/questions/3382954/measure-execution-time-for-a-java-method
        long startTime = System.currentTimeMillis();
        
        // Dynamically scale thread count to match system core count.
        // https://stackoverflow.com/questions/4759570/finding-number-of-cores-in-java
        int maxThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("maxThreads = " + Integer.toString(maxThreads));

        // create a queue for weatherData. Pass this to the file parser threads
        // so they can dump all of the data into the same Collection.
        ConcurrentLinkedQueue weatherQueue = new ConcurrentLinkedQueue();

        // Set up execution thread pool and create a Callable/Future for each fileName.
        // https://www.journaldev.com/1090/java-callable-future-example

        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<Future<Boolean>> FutureList = new ArrayList<Future<Boolean>>();
        while ( ! fileNameQueue.isEmpty() ) {
            Callable<Boolean> weatherFileWorker = new WeatherFileParser( weatherQueue, (File) fileNameQueue.poll(), startDate, endDate, SearchForTMax );
            Future <Boolean> weatherFileFuture = executor.submit(weatherFileWorker);
            FutureList.add(weatherFileFuture);
        }
        for ( Future <Boolean> f : FutureList ) {
            try {
                if ( f.get() ) {continue;}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        
        System.out.println("Completed Processing All Files");

        // weatherQueue now contains Max/Min five results from each file.
        // Filter using four threads.
        ConcurrentLinkedQueue filteredWeatherQueue = new ConcurrentLinkedQueue();
        
        ExecutorService Annihilator  = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> AnotherFutureList = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < 4; i++) {
            Callable<Boolean> MaxMinWorker = new MaxMinWeatherDataCollector(weatherQueue, filteredWeatherQueue, SearchForTMax);
            Future <Boolean> weatherDataFuture = Annihilator.submit(MaxMinWorker);
            AnotherFutureList.add(weatherDataFuture);
        }
        for ( Future <Boolean> f : AnotherFutureList ) {
            try {
                if ( f.get() ) { continue; }
            } catch (Exception e) { e.printStackTrace(); }
        }
        Annihilator.shutdown();
        
        // weatherQueue now contains up to 20 Max/Min results. Use a
        // MaxMinWeatherData Collector to find the top five.
        MaxMinWeatherDataCollector FinalFilter = new MaxMinWeatherDataCollector(SearchForTMax, 5);
        while ( ! filteredWeatherQueue.isEmpty() ) {
            FinalFilter.push((WeatherData) filteredWeatherQueue.poll());
        }
        WeatherData[] FinalFive = FinalFilter.dumpValues();
        
        // Output
        StationData tempSD;
        WeatherData tempData;
        for (int i = 0; i < 5; i++ )
        {
            tempData = FinalFive[i];
            System.out.println(tempData.toString() );
            // Find StationData with id matching thisData.id, and print out its
            // toString(). If it exists, anyway. Otherwise, the findSDInList
            // function will return a null and we can skip it.
            tempSD = findSDInList(stationQueue, tempData.id);
            if ( tempSD != null ) { System.out.println( tempSD.toString() ); }
            
        }
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(String.format("Process took %d seconds!", elapsedTime/1000) );
    }

}
