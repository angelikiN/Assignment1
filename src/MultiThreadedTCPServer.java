package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {

	private static int repetitions;
	private static int n=0;
	private static double throughputpersec = 0;
	
    private static class TCPWorker implements Runnable {

        private Socket client;
        private String clientbuffer;
        

        public TCPWorker(Socket client) {
            this.client = client;
            this.clientbuffer = "";
        }

        @Override
        public void run() {
        	if(n<repetitions) {
        		/*if(n==0)
        			throughputpersec = (double) System.currentTimeMillis();
        		*/
        		n++;
        		
	            try {
	             
	            	System.out.println("Client connected with: " + this.client.getInetAddress());
	
	                DataOutputStream output = new DataOutputStream(client.getOutputStream());
	                BufferedReader reader = new BufferedReader(
	                        new InputStreamReader(this.client.getInputStream())
	                );
	                
	                this.clientbuffer = reader.readLine();
	                System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);
	
	                 int separatorIndex = this.clientbuffer.lastIndexOf(',');
	                    
	                  String clnum = this.clientbuffer.substring(separatorIndex + 1);
	                
	                  //int size = (int) ((Math.random() * 1700.0 * 1024.0) + (300.0*1024.0)); 
			          //client.setSendBufferSize(size);
	                  
	                output.writeBytes("WELCOME " + clnum + System.lineSeparator());
	            } catch (IOException e) {
	                e.printStackTrace();
	            }

        	}
        	/*if(n == repetitions) {
        		throughputpersec = ((double) n)/(((double) System.currentTimeMillis() - throughputpersec)/1000.00);
        		System.out.println("Server Throughput = " + throughputpersec + " per second");
        	}*/
        }

    }

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

    public static void main(String args[]) {
        
    	int port = 5678;
    	
    	try {
        	
        	if(args.length>0) {
        		port = Integer.parseInt(args[0]);
        		n= Integer.parseInt(args[1]);
        	}
        	//System.out.println(args[0]);
        	repetitions = n;
        	
        	ServerSocket socket = new ServerSocket(port);

            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());

            while (true) {
                Socket client = socket.accept();
                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(client)
                );

            }
            
        }catch(java.lang.ArrayIndexOutOfBoundsException e1) {
        	System.out.println("Invalid Arguments : java MultiThreadedTCPServer <Port> <Repetitions>");
        }catch (IOException e2) {
            e2.printStackTrace();
        }
   
    }

}

