/*
 * @(#)SimpleRead.java	1.12 98/06/25 SMI
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license 
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */

import java.io.*;
import java.util.*;
import javax.comm.*;

public class SimpleRead implements Runnable, SerialPortEventListener {
	static CommPortIdentifier portId;
	static Enumeration portList;

	InputStream inputStream;
	SerialPort serialPort;
	Thread readThread;
	String input;
	String lastInput;

	//Discover Serial Port and if COM8 is open, start listening the port and handling data
	public static void main(String[] args) throws IOException, InterruptedException {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				
				//To choose one port. Now is COM8
				if (portId.getName().equals("COM8")) {
					SimpleRead reader = new SimpleRead();
					DataHandler handler = new DataHandler(reader);
					System.out.println("reached.");
				}
			}
		}
	}

	//Settings of COM8 (57600,8,1,odd)
	public SimpleRead() {
		try {
			serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
		} catch (PortInUseException e) {
		}
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
		}
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
		} catch (UnsupportedCommOperationException e) {
		}
		input = "";
		readThread = new Thread(this);
		readThread.start();
	}
	
	//Have nothing to do with the thread
	public void run() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
		}
	}
	
	//Event of the listener
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[64];	//Buffer size

			try {
				while (inputStream.available() > 0) {
					int numBytes = inputStream.read(readBuffer);	//Data length
					input = new String(readBuffer);	//Read from buffer
					input = input.trim();	//Delete space
					
					//For debugging
					//System.out.println("number of bytes: " + numBytes + "\tcontent: " + input);
				}
			} catch (IOException e) {
			}
			break;
		}
	}
}
