package ics440_p2;

public class StationData { 
    String id; 
    float latitude; 
    float longitude; 
    float elevation; 
    String state; 
    String name;
    
    public StationData(String thisLine) {
        this.id = thisLine.substring(0,11);
        this.latitude = Float.valueOf(thisLine.substring(12,20).trim()); 
        this.longitude = Float.valueOf(thisLine.substring(21,30).trim());
        this.elevation = Float.valueOf(thisLine.substring(31,37).trim());
        this.state = thisLine.substring(38,40);
        this.name = thisLine.substring(41,71);
    }
    
    public String toString() {
        
        return String.format("id=%s, latitude=%f, longitude=%f, elevation=%f, state=%s, name=%s", 
                this.id, this.latitude, this.longitude, this.elevation, this.state, this.name);
    }
} 
