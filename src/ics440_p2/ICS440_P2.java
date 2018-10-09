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
     */
    public static void main(String[] args) throws IOException {
        // Create Queue of Files.
        String localFolderPath = "c:\\users\\dave_pierce\\Downloads\\ghcnd_hcn";
        String localStationFilePath = "c:\\users\\dave_pierce\\Downloads\\ghcnd-stations.txt";
        ConcurrentLinkedQueue myQueue = new ConcurrentLinkedQueue();
        ConcurrentLinkedQueue stationQueue = new ConcurrentLinkedQueue();
        
        // Populate set of Stations
        File ghcndStations = new File(localStationFilePath);
        FileReader StationReader;
        BufferedReader StationBuff;
        String line;
        
        try {
            StationReader = new FileReader(ghcndStations);
            StationBuff = new BufferedReader(StationReader);
            while ( ( line = StationBuff.readLine() ) != null ) {
                stationQueue.add(new StationData(line));
            }
        } catch (Exception e) {
            Logger.getLogger(ICS440_P2.class.getName()).log(Level.SEVERE, null, e);
        }
        
//        while (stationQueue.size() > 0) {
//            System.out.println(stationQueue.remove().toString() + " " + Integer.toString(stationQueue.size()) );
//        }
        
        /*
        // Find file names in path.
        // https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File folder = new File(localFolderPath);
        File[] allFiles = folder.listFiles();
        // Enqueue File Objects
        for (int i = 0; i < allFiles.length; i++) {
            myQueue.add(allFiles[i]);
        }*/
        
        
        
    }
    
}
