import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class DataHandler implements Runnable {
	Thread handleThread;
	String lastInput;
	String output;
	boolean writable;
	int counter;
	
	//Constructor
	public DataHandler(SimpleRead reader) throws IOException, InterruptedException {
		
		//Initialize variables
		lastInput = "";
		output = "";
		
		//Set up a thread for writing the output file
		handleThread = new Thread(this);
		handleThread.start();
		
		//Wait for the serial port listener beginning to work
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		stopper(reader);	//Start determine whether pause or record
	}
	
	private void stopper(SimpleRead reader) throws IOException, InterruptedException {
		while(true) {
			//If redundant < 3, record
			while(counter < 3) {
				writable = false;	//down the flag to prevent file writing
				output = output + reader.input;	//record
				System.out.println("content: " + output);	//Debugging
				this.comparer(reader);	//Increase the counter
				Thread.sleep(50);	//Wait for next package
			}
			//If redundant > 3, pause
			while(counter >= 3) {
				writable = true;	//erect the flag for starting writing the file
				System.out.println("pauseing...");	//Debugging
				this.comparer(reader);	//Increase or erase the counter
				Thread.sleep(50);	//Wait for next package
			}
		}
	}
	
	private void comparer(SimpleRead reader) {
		if(lastInput.equals(reader.input)) {
			counter++;	//Increase the counter to record redundant
		}
		else {
			counter = 0;	//Erase the counter to stop pausing
		}
		lastInput = reader.input;	//Store input for next comparison
	}

	public String featureResize(String input, int base)
	{
		String output = "";
		String[] temp = input.split(",");
		int num = temp.length;
		if (num*2<base) return ""; // noise filter
		while (num>base) {
			int diff = num-base;
			
			int interval = num/diff;
			if(interval==1) interval = 2; //Incase base < temp.length/2

			int current = 0;
			while(current<temp.length) {
				temp[current] = "-1";
				current = current + interval;
				num --;
			}
		}

		for(String item : temp)
	        if(!"-1".equals(item))
	        	output=output+item+",";
	    
	    return output.substring(0,output.length()-1);
	}
	
	public void run() {
		try {
			while(true) {
				//Write if the recording is pausing
				if(writable) {
					
					//File writing
					FileWriter outputFile = new FileWriter("SampleData.txt", true);
					PrintWriter printer = new PrintWriter(outputFile);
					printer.print(featureResize(output, 12) + "\\");
					
					//Reset output and flag, close writers
					output = "";
					writable = false;
					printer.close();
					outputFile.close();
				}
				else {
					Thread.sleep(50);	//wait if still recording
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
