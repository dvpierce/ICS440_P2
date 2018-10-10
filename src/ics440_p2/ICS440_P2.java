package ics440_p2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICS440_P2 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
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
        WeatherFileParser.StationLoader(localStationFilePath, stationQueue);

        // Find file names in path.
        // https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File folder = new File(localFolderPath);
        File[] allFiles = folder.listFiles();
        // Enqueue File Objects
        for (File thisFile : allFiles) {
            fileNameQueue.add(thisFile);
        }

        while ( ! fileNameQueue.isEmpty() )
        {
            WeatherFileParser.GetMaxFive(weatherQueue, (File) fileNameQueue.remove());
//             WeatherFileParser.GetMinFive(weatherQueue, fileNameQueue.remove());
        }
        
//        while(! weatherQueue.isEmpty() )
//        {
//            System.out.println(weatherQueue.remove().toString());
//        }
    }

}
