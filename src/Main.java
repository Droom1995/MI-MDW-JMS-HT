import objects.Booking;
import objects.Trip;
import processors.Consumer;
import processors.Processor;
import processors.Producer;

public class Main {

    public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Consumer bookingConsumer = new Consumer("jms/mdw-booking-queue");
		try {
			bookingConsumer.receive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
