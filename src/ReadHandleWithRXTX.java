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
    svm_predict demo;
    
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
                
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);
                demo = new svm_predict("SampleData_mix_trainning.model");
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
            		// Thread.sleep(50);	//for data to file
            		Thread.sleep(150);	//for real-time prediction
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
    	int index;
    	double sample[];
    	
    	public DataHandler(LinkedList<String> buffer) throws IOException, InterruptedException {
    		this.buffer = buffer;
    		writable = true;
    		//Initialize variables
    		output = "";
    		lastOutput = "";
    		index = 0;
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
		
		public void write() throws InterruptedException, IOException {
			
			//processing on input data, from string to double[]
			for(int i = 0;i < buffer.size();i++) {
				output += buffer.get(i);
			}
			output = output.replaceAll("32,31,","");
			output = "32,31," + output + "32,31,";
			String parseToDouble[] = output.split(",");
			sample = new double[parseToDouble.length];
			for (int i = 0; i < sample.length; i++)
		     {
		        sample[i] = Double.parseDouble(parseToDouble[i]);
		     }
			
			System.out.println(demo.predict(sample));
			//System.out.println("32,31," + output + "32,31" + "\\");
			output = "";
		}
		
		/* A writer for record data into files
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
		
		public void write() throws InterruptedException, IOException {
			if(writable()) {
				//System.out.println(index++);
				output = output.replaceAll("32,31,","");
				//File writing
				FileWriter outputFile = new FileWriter("SampleData.txt", true);
				PrintWriter printer = new PrintWriter(outputFile);
				//Reset output and flag, close writers
				printer.println("32,31," + output + "32,31" + ",4");
				System.out.println("32,31," + output + "32,31,");
				output = "";
				writable = false;
				printer.close();
				outputFile.close();
				Thread.sleep(20);
			}
			else {
						FileWriter outputFile = new FileWriter("SampleData.txt", true);
						PrintWriter printer = new PrintWriter(outputFile);
						output = "";
						printer.println("\\");
				System.out.println("pausing...");
						printer.close();
						outputFile.close();
				Thread.sleep(20);
			}
		}
    	
    }
    */
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