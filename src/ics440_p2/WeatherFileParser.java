/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ics440_p2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dave_pierce
 */
public class WeatherFileParser implements Callable<Boolean> {
    
    private ConcurrentLinkedQueue weatherQueue;
    private File fileName;
    private Date startDate, endDate;
    private Boolean SearchForTMax;
    
    public WeatherFileParser(ConcurrentLinkedQueue weatherQueue, File fileName, Date startDate, Date endDate, Boolean SearchForTMax) {
        this.weatherQueue = weatherQueue;
        this.fileName = fileName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.SearchForTMax = SearchForTMax;
    }

    public Boolean call() {
        if (SearchForTMax) {
            GetMaxFive(this.weatherQueue, this.fileName, this.startDate, this.endDate);
        } else {
            GetMinFive(this.weatherQueue, this.fileName, this.startDate, this.endDate);
        }
        return true;
    }
    
    public static void FileParser(ConcurrentLinkedQueue WeatherDataQueue,
            File nextFile, String elementToFind)
    {
        FileReader fr;
        BufferedReader br;
        String thisLine;
        
        try {
            fr = new FileReader(nextFile);
            br = new BufferedReader(fr);
            while ( ( thisLine = br.readLine() ) != null ) {
                // Since we are only concerned with temp readings matching
                // elementToFind, just skip everything else.
                String element = thisLine.substring(17,21);
                if ( ! element.equalsIgnoreCase(elementToFind) )
                { continue; } // skips to next line.
                
                // Moving on:
                String id = thisLine.substring(0,11);
                int year = Integer.valueOf(thisLine.substring(11,15).trim());
                int month = Integer.valueOf(thisLine.substring(15,17).trim());
                int days = (thisLine.length() - 21) / 8; // Calculate the number of days in the line
                
                for (int i = 0; i < days; i++) { // Process each day in the line.
                    // Skip today if the value is "missing".
                    int value = Integer.valueOf(thisLine.substring(21+8*i,26+8*i).trim());
                    if (value == -9999)
                    { continue; } 

                    // Also skip days with non-empty qflags
                    // "Your program should discard the value if qflag is
                    // anything other than an empty (space) column."
                    String qflag = thisLine.substring(27+8*i,28+8*i);
                    if ( ! qflag.equalsIgnoreCase(" ") )
                    { continue; }

                    WeatherData wd = new WeatherData();
                    wd.day = i + 1;
                    wd.id = id;
                    wd.year = year;
                    wd.month = month;
                    wd.element = element;
                    wd.value = value;
                    wd.qflag = qflag;
                    WeatherDataQueue.add(wd);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ICS440_P2.class.getName()).log(Level.SEVERE, null, e);
        }
        
        

    }


    public static void StationLoader(String localStationFilePath,
            ConcurrentLinkedQueue stationQueue,
            Date startDate, Date endDate)
    {
                
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
        } catch (IOException e) {
            Logger.getLogger(ICS440_P2.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void GetMaxFive(ConcurrentLinkedQueue weatherQueue, File fileName, Date startDate, Date endDate) {
        // Create a collection for weather data for the specified file and
        // enqueue that data

        ConcurrentLinkedQueue<WeatherData> allWeatherDataInFile = new ConcurrentLinkedQueue();
        FileParser(allWeatherDataInFile, fileName, "TMAX");
        
        // Go through allWeatherDataInFile and find the maximum five temperatures
        MaxMinWeatherDataCollector maxTemps = new MaxMinWeatherDataCollector(true, 5);
        WeatherData thisDate;
        while ( ! allWeatherDataInFile.isEmpty() ) {
            thisDate = allWeatherDataInFile.poll();
//            System.out.println(thisDate.toString());
//            System.out.println(thisDate.getDate());
//            System.out.println(startDate);
            if ( ( startDate.compareTo(thisDate.getDate()) < 1 ) && 
                    ( thisDate.getDate().compareTo(endDate) ) < 1 )
            { maxTemps.push(thisDate); }
        }
        for ( WeatherData x : maxTemps.dumpValues()) {
            weatherQueue.add(x);
        }
    }

    private void GetMinFive(ConcurrentLinkedQueue weatherQueue, File fileName, Date startDate, Date endDate) {
        // Create a collection for weather data for the specified file and
        // enqueue that data

        ConcurrentLinkedQueue<WeatherData> allWeatherDataInFile = new ConcurrentLinkedQueue();
        FileParser(allWeatherDataInFile, fileName, "TMIN");
        
        // Go through allWeatherDataInFile and find the maximum five temperatures
        MaxMinWeatherDataCollector maxTemps = new MaxMinWeatherDataCollector(false, 5);
        WeatherData thisDate;
        while ( ! allWeatherDataInFile.isEmpty() ) {
            thisDate = allWeatherDataInFile.poll();
            if ( ( startDate.compareTo(thisDate.getDate()) < 1 ) && 
                    ( thisDate.getDate().compareTo(endDate) ) < 1 )
            { maxTemps.push(thisDate); }
        }
        for ( WeatherData x : maxTemps.dumpValues()) {
            weatherQueue.add(x);
        }
    }
}
