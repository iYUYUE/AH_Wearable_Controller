import java.io.IOException;


public class DataHandler implements Runnable {
	Thread handleThread;
	String lastInput;
	int counter;
	public DataHandler(SimpleRead reader) throws IOException, InterruptedException {
		lastInput = "";
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
			while(counter < 10) {
				System.out.println("content: " + reader.input);
				this.comparer(reader);
				Thread.sleep(5);
			}
			while(counter >= 10) {
				System.out.println("pauseing...");
				this.comparer(reader);
				Thread.sleep(5);
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
			Thread.sleep(20000);
		} catch (InterruptedException e) {
		}
	}

}
