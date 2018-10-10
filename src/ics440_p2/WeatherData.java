package ics440_p2;

class WeatherData {
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
}

