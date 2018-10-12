package ics440_p2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author dave_pierce
 */
public class MaxMinWeatherDataCollector {
    // Holds the values.
    private WeatherData[] bucket;
    // Used to track number of values stored.
    private int sizeThis, currentSize;
    // Used to keep track of minimum value in the array.
    private WeatherData min;
    private int minIndex;
    // Used to keep track of maximum value in the array.
    private WeatherData max;
    private int maxIndex;
    
    // behavior switch. If maxOrMin is true, the instance will collect and
    // return the largest values submitted. If maxOrMin is false, the instance
    // will collect and return the smallest values submitted.
    boolean maxOrMin;
    
    MaxMinWeatherDataCollector(boolean maxOrMin, int sizeThis) {
        this.bucket = new WeatherData[sizeThis];
        this.sizeThis = sizeThis;
        this.currentSize = 0;
        this.maxOrMin = maxOrMin;
        this.min = null;
        this.max = null;
        this.minIndex = 0;
        this.maxIndex = 0;
    }
    
    private void updateMaxMin() {
        // Iterate through bucket and find the new max/min values.
        for (int i = 0; i < this.currentSize; i++)
        {
            if ( ( this.bucket[i].compareTo(this.max) ) == 1 ) {
                this.max = this.bucket[i]; 
                this.maxIndex = i;
            }
            if ( ( this.bucket[i].compareTo(this.min) ) < 1 ) {
                this.min = this.bucket[i];  
                this.minIndex = i;
            }
        }
    }
    
    public void push(WeatherData x) {
        // if the bucket is completely empty:
        if ( this.currentSize == 0 ) {
            max = min = this.bucket[this.currentSize] = x;
            maxIndex = minIndex = this.currentSize;
            this.currentSize++;
        // If the bucket is not full yet:
        } else if (this.currentSize < this.sizeThis) {
            // Add the thing to the bucket.
            this.bucket[this.currentSize] = x;
            this.currentSize++;
        // the bucket is full; just replace smallest/largest value accordingly.
        } else { 
            if ( this.maxOrMin ) {
                // If the new value x is larger than the smalled value in the array, replace it.
                if ( (x.compareTo(this.min)) == 1 ) { min = this.bucket[minIndex] = x; }
            } else {
                // If the new value x is smaller than the largest value in the array, replace it.
                if ( (x.compareTo(this.max)) < 1 ) { max = this.bucket[maxIndex] = x; }
            }
        }
        // finally, update our max and min values.
        updateMaxMin();
    }
    
    public WeatherData[] dumpValues() { return Arrays.copyOfRange( this.bucket, 0, currentSize); }
    public WeatherData getMax() { return this.max; }
    public WeatherData getMin() { return this.min; }
    public String toString() {
        String retString = "Values: \n";
        for ( int i = 0; i < this.currentSize; i++ )
        { retString += String.format(this.bucket[i].toString() + "\n"); }
        return retString;
    }
}