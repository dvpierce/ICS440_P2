package ics440_p2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class WeatherData implements Comparable<WeatherData> {
    String id; 
    int year; 
    int month; 
    int day; 
    String element; 
    int value; 
    String qflag; 
    
    public String toString() {
        return String.format("id=%s year=%d month=%d day=%d element=%s value=%.1f°C qflag=%s", 
                this.id, this.year, this.month, this.day, this.element, (float) this.value/10.0, this.qflag);
        
    }
    
    public Date getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = String.format("%d-%d-%d", this.year, this.month, this.day);
        try {
            return df.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
    
    public int compareTo(WeatherData other)
    {
        if ( this.value == other.value ) { return 0; }
        else if ( this.value > other.value ) { return 1; }
        else { return -1; }
    }

    private boolean greaterThan(WeatherData other) {
        return ( this.value > other.value );
    }
    private boolean lessThan(WeatherData other) {
        return ( this.value < other.value );
    }
    private boolean equals(WeatherData other) {
        return ( this.value == other.value );
    }
}

