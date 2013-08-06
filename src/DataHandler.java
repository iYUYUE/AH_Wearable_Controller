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
	
	public void run() {
		try {
			while(true) {
				//Write if the recording is pausing
				if(writable) {
					
					//File writing
					FileWriter outputFile = new FileWriter("SampleData.txt", true);
					PrintWriter printer = new PrintWriter(outputFile);
					printer.print(output + "\\");
					
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
