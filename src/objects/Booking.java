package objects;

import java.io.Serializable;

public class Booking implements Serializable{
    String trip;

	String person;

    public Booking( String trip, String person){
        this.trip = trip;
        this.person = person;
        
    }

    public Booking(String trip){
        this.trip = trip;
        
    }
    
    
    public String getTrip() {
		return trip;
	}

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPerson() {
        return person;
    }
    
    public String toString(){
    	return person + " " + trip;
    }
}