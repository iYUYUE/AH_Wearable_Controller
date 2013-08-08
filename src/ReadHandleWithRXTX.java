import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * This version of the TwoWaySerialComm example makes use of the 
 * SerialPortEventListener to avoid polling.
 *
 */
public class ReadHandleWithRXTX
{
    static DataHandler handler;
    static LinkedList<String> buffer = new LinkedList<String>();
    public ReadHandleWithRXTX()
    {
        super();
    }
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                               
                (new Thread(new SerialWriter(out))).start();
                
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);
                handler = new DataHandler(buffer);
                handler.run();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
        private byte[] readBuffer = new byte[1024];
        String input = "";
        String lastInput = "";
        boolean fragment = false;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
          
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {
                    if ( data == '\n' ) {
                        break;
                    }
                    readBuffer[len++] = (byte) data;
                }
                input = new String(readBuffer,0,len).trim();
            	if(buffer.size() < 12) {
            		buffer.add(input);
            		input = "";
            	}
            	else {
            		for(int i = 0;i < input.length();i++)
            			buffer.removeFirst();
                		buffer.add(input);
                		input = "";
            		}
				//For debugging
				//System.out.println("number of bytes: " + len + "\tcontent: " + input);
            	if(buffer.size() == 12) {
            		handler.write();
            	}
            }
            catch ( IOException | InterruptedException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }             
        }

    }
    
    public class DataHandler implements Runnable {
    	Thread handleThread;
    	String output;
    	String lastOutput;
    	boolean writable;
    	LinkedList<String> buffer;
    	
    	public DataHandler(LinkedList<String> buffer) throws IOException, InterruptedException {
    		this.buffer = buffer;
    		writable = true;
    		//Initialize variables
//    		lastInput = "";
    		output = "";
    		lastOutput = "";
    		
    	//	stopper(reader);	//Start determine whether pause or record
    	}
		@Override
		public void run() {
    		//Wait for the serial port listener beginning to work
    		try {
    			Thread.sleep(200000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
		}
		
		private boolean writable() {
			for(int i = 0;i < buffer.size();i++) {
				output += buffer.get(i);
			}
			if(lastOutput.equals(output)) {
				return false;
			}
			else {
				lastOutput = output;
				return true;
			}
		}
		public void write() throws InterruptedException {
			try {
//				while(true) {
//					//Write if the recording is pausing
					if(writable()) {
						output = output.replaceAll("32,31,","");
						System.out.println("32,31," + output + "32,31" + "\\");
						//File writing
						FileWriter outputFile = new FileWriter("SampleData.txt", true);
						PrintWriter printer = new PrintWriter(outputFile);
						//printer.println(featureResize(output, 12) + "\\");
						//Reset output and flag, close writers
						printer.println("32,31," + output + "32,31" + "\\");
						output = "";
						//writable = false;
						printer.close();
						outputFile.close();
						Thread.sleep(50);
					}
					else {
						output = "";
						System.out.println("pausing...");
					}
					
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    }
    
    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }            
        }
    }
    

    
    public static void main ( String[] args )
    {
        try
        {
            (new ReadHandleWithRXTX()).connect("COM8");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}