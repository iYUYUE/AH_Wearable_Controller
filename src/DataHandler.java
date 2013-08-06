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
	public DataHandler(SimpleRead reader) throws IOException, InterruptedException {
		lastInput = "";
		output = "";
		handleThread = new Thread(this);
		handleThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopper(reader);
	}
	
	private void stopper(SimpleRead reader) throws IOException, InterruptedException {
		while(true) {
			while(counter < 3) {
				writable = false;
				output = output + reader.input;
				System.out.println("content: " + output);
				this.comparer(reader);
				Thread.sleep(50);
			}
			while(counter >= 3) {
				writable = true;
				System.out.println("pauseing...");
				this.comparer(reader);
				Thread.sleep(50);
			}
		}
	}
	
	private void comparer(SimpleRead reader) {
		if(lastInput.equals(reader.input)) {
			counter++;
		}
		else {
			counter = 0;
		}
		lastInput = reader.input;
	}
	public void run() {
		try {
			while(true) {
				if(writable) {
					FileWriter outputFile = new FileWriter("SampleData.txt", true);
					PrintWriter printer = new PrintWriter(outputFile);
					printer.print(output + "\\");
					output = "";
					writable = false;
					printer.close();
					outputFile.close();
				}
				else {
					Thread.sleep(50);
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
