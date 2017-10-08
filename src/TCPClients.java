import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

class Threads implements Runnable {

	String serverip;
	int port;
	
	boolean flag_latencystart = true;
	boolean flag_latencyend = true;
	boolean flag_latencyprint = true;
	long latencytime = 0;
	
	private Thread t;
	private String threadName; //user id
	   
	Threads( String name, String ip, int p) {
	
		threadName = name;
		serverip = ip;
		port = p;

	}
	
    public void run() {
    		
	        try {
	        	
	        	InetAddress addr = null;
	        	//Socket socket = null ;//=  new Socket("35.163.132.48", 5678);
	        	
	        	for(int request=0; request<300; request++) {
	        		
	        		if(flag_latencystart) {
	        			flag_latencystart= false;
	        			latencytime = System.currentTimeMillis();
	        		}
	        		
	        		String message, response;
	        		addr = InetAddress.getLocalHost();
		            Socket socket = new Socket(serverip , port);
		            
		            	          
		            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		            BufferedReader server = new BufferedReader(
		                    new InputStreamReader(socket.getInputStream())
		            );
		
		            message = "HELLO, " + addr.toString() + ", " +
		             Integer.toString(port) + ", " + threadName + System.lineSeparator();
		
		            output.writeBytes(message);
		            response = server.readLine();
		
		            System.out.println("[" + new Date() + "] Received: " + response);
		              
		            if(flag_latencyend) {
	        			flag_latencyend= false;
	        			latencytime = System.currentTimeMillis() - latencytime;
	        		}
	        	}
	        	if(flag_latencyprint) {
	        		flag_latencyend = false;
	        		System.out.println(getLatency());
	        	}
	        	//socket.close();
	        	
	        } catch (IOException e) {
	    
	        	e.printStackTrace();
	        
	        }

    }
    
    public void start () {
 
        if (t == null) {
           t = new Thread (this, threadName);
           t.start();
        }
        
     }
    
    long getLatency() {
    	return latencytime;
    }

}

public class TCPClients{
	
	public static void main(String[] args) {
		
		int i;
		long lat = 0;
		boolean flag_latency = true;
		int USERS_NUM = 10;
		String ip = null;
		int port = 5678;
		
		try {
			 
			ip = args[0];
			port = Integer.parseInt(args[1]);
		
			for(i=1;i<=USERS_NUM;i++){
				Threads thr = new Threads(String.valueOf(i),ip,port);
				thr.start();
				
				if(flag_latency) {
					flag_latency = false;
					lat = thr.getLatency();
					
				}
			}
			
		} catch(java.lang.ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid Arguments : java TCPClients <Server ip> <Port>");
		}
		
	}
}
