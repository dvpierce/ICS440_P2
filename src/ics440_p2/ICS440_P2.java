package ics440_p2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;

public class ICS440_P2 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
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
        //
        //
        //
        //
        //
        
        // Get directory of files (path)
        // Verify that there are *.dly files in it.
        
        // Get path to Stations file.
        // parse it and complain if there are exceptions, then start again.
        
        // Create Queue of Files.
        String localFolderPath = "c:\\users\\dave_pierce\\Downloads\\ghcnd_hcn2";
        String localStationFilePath = "c:\\users\\dave_pierce\\Downloads\\ghcnd-stations.txt";
        ConcurrentLinkedQueue fileNameQueue = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue stationQueue = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue weatherQueue = new ConcurrentLinkedQueue();

        // Dynamically scale thread count to match system core count.
        // https://stackoverflow.com/questions/4759570/finding-number-of-cores-in-java
        int maxThreads = Runtime.getRuntime().availableProcessors();
        // System.out.println("maxThreads = " + Integer.toString(maxThreads));

        // Populate Collection of Stations
        WeatherFileParser.StationLoader(localStationFilePath, stationQueue, startDate, endDate);

        // Find file names in path.
        // https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File folder = new File(localFolderPath);
        File[] allFiles = folder.listFiles();
        // Enqueue File Objects
        for (File thisFile : allFiles) {
            fileNameQueue.add(thisFile);
        }

        // Set up execution thread pool and create a callable for each fileName.
        //
        //
        //
        //
        
        while ( ! fileNameQueue.isEmpty() )
        {
            WeatherFileParser.GetMaxFive(weatherQueue, (File) fileNameQueue.remove(), startDate, endDate);
            // WeatherFileParser.GetMinFive(weatherQueue, (File) fileNameQueue.remove(), startDate, endDate);
        }
        
        // weatherQueue now contains Max/Min five results from each file.
        // Filter using four threads with MaxMinWeatherDataCollectors
        //
        //
        //
        //
        
        // weatherQueue now contains up to 20 Max/Min results. Use a
        // MaxMinWeatherData Collector to find the top five.
        //
        //
        //
        //
        
        
        // Output
        while ( ! weatherQueue.isEmpty() )
        {
            WeatherData thisData = (WeatherData) weatherQueue.remove();
            System.out.println(thisData.toString() );
            // Find StationData with id matching thisData.id, and print out its toString().
            // System.out.println();
            
        }
    }

}
