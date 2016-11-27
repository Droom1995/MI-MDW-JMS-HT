package processors;

import java.util.Hashtable;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import objects.Booking;
import objects.Trip;



public class Processor implements MessageListener {
 
    // connection factory
    private QueueConnectionFactory qconFactory;
 
    // connection to a queue
    private QueueConnection qcon;
 
    // session within a connection
    private QueueSession qsession;
 
    // queue receiver that receives a message to the queue
    private QueueReceiver qreceiver;
 
    // queue where the message will be sent to
    private Queue queue;
 
    private String receiveQueue;
    static Producer bookingProducer;
    static Producer tripProducer;
    
    public Processor(String receiveQueue){
    	this.receiveQueue = receiveQueue;
    }
    
    
    // callback when the message exist in the queue
    public void onMessage(Message msg) {
        try {
        	System.out.println("In queue "+ receiveQueue + " message Received: " + msg.toString());
        	Object obj = ((ObjectMessage) msg).getObject();
        	System.out.println(obj.toString());
            if (obj instanceof Booking) {
                Booking booking = ((Booking) obj);
                bookingProducer.send("jms/mdw-booking-queue",booking);
            } else if (obj instanceof Trip){
                Trip trip = (Trip) obj;
                tripProducer.send("jms/mdw-trip-queue",trip);
            } else
            	System.out.println("Error");
        } catch (Exception jmse) {
            System.err.println("An exception occurred: " + jmse.getMessage());
        }
    }
 
    // create a connection to the WLS using a JNDI context
    public void init(Context ctx, String queueName)
            throws NamingException, JMSException {
 
        qconFactory = (QueueConnectionFactory) ctx.lookup(Config.JMS_FACTORY);
        qcon = qconFactory.createQueueConnection();
        qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = (Queue) ctx.lookup(queueName);
 
        qreceiver = qsession.createReceiver(queue);
        qreceiver.setMessageListener(this);
 
        qcon.start();
    }
 
    // close sender, connection and the session
    public void close() throws JMSException {
        qreceiver.close();
        qsession.close();
        qcon.close();
    }
 
    // start receiving messages from the queue
    public void receive() throws Exception {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Config.JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, Config.PROVIDER_URL);
 
        InitialContext ic = new InitialContext(env);
 
        init(ic, receiveQueue);
 
        System.out.println("Connected to " + queue.toString() + ", receiving messages...");
        try {
            synchronized (this) {
                while (true) {
                    this.wait();
                }
            }
        } finally {
            close();
            System.out.println("Finished.");
        }
    }
    
    public static void main(String[] args){
		Processor orderProcessor = new Processor("jms/mdw-order-queue");
		bookingProducer = new Producer("jms/mdw-booking-queue");
		tripProducer  = new Producer("jms/mdw-trip-queue");
		try {
			orderProcessor.receive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
}