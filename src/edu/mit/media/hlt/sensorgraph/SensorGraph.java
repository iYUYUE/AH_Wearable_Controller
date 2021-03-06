/*
  SensorGraph - Example to use with Amarino 2.0
  Copyright (c) 2010 Bonifaz Kaufmann. 
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package edu.mit.media.hlt.sensorgraph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

/**
 * <h3>Application that receives sensor readings from Arduino displaying it graphically.</h3>
 * 
 * This example demonstrates how to catch data sent from Arduino forwarded by Amarino 2.0.
 * SensorGraph registers a BroadcastReceiver to catch Intents with action string: <b>AmarinoIntent.ACTION_RECEIVED</b>
 * 
 * @author Bonifaz Kaufmann - April 2010
 *
 */
public class SensorGraph extends Activity {
	
	private static final String TAG = "SensorGraph";
	
	// change this to your Bluetooth device address 
	private static final String DEVICE_ADDRESS =  "7F:67:DA:66:5E:0A"; //"00:06:66:03:73:7B";
	
	//private GraphView mGraph; 
	private TextView mValueTV;
	
	private ArduinoReceiver arduinoReceiver;
	
	private ImageView btnMove;
	private RelativeLayout.LayoutParams params;
	private int yCoord = 100, xCoord = 500;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arduinoReceiver = new ArduinoReceiver();
        setContentView(R.layout.main);
        
        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
        params.leftMargin = xCoord; //XCOORD
        params.topMargin = yCoord; //YCOORD
        btnMove = (ImageView)findViewById(R.id.button_move);
        btnMove.setLayoutParams(params);
		
		
		
		
        // get handles to Views defined in our layout file
        //mGraph = (GraphView)findViewById(R.id.graph);
        
        
        
        //mGraph.setMaxValue(1024);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver

		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		
		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(this, DEVICE_ADDRESS);

	}


	@Override
	protected void onStop() {
		super.onStop();
		
		// if you connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(this, DEVICE_ADDRESS);
		
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}
	
	
	

	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	

    
	public class ArduinoReceiver extends BroadcastReceiver {
	    LinkedList<String> buffer = new LinkedList<String>();
		DataHandler handler;
	    svm_predict demo;
	    
	    
		public ArduinoReceiver() {
			try {
				(new Thread(handler = new DataHandler())).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
            String input = null;
			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			
			if (dataType == AmarinoIntent.STRING_EXTRA){
				input = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
	            	while(buffer.size() > 5)
	            		buffer.removeFirst();
	            	buffer.add(input);
					if(buffer.size() == 6) {
		            	try {
							handler.write(buffer);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
						}
					}

            		
            		try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	
	public class DataHandler extends Thread {

    	String output;
    	String lastOutput;
    	boolean writable;
    	int index;
    	double sample[];
    	svm_predict demo;
    	
    	public DataHandler() throws IOException, InterruptedException {
    		AssetManager assetManager = getAssets();
    		
			System.out.println("Okay!");
    		InputStream sampleDataMix = assetManager.open("data/sampledata_mix.csv");
    		InputStream trainedModel = assetManager.open("data/sampledata_mix_trainning.model");

    		InputStreamReader tempISR = new InputStreamReader(trainedModel);
    		BufferedReader tempBuffer = new BufferedReader(tempISR);
    		
			try {
				demo = new svm_predict(tempBuffer, sampleDataMix);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public void write(LinkedList<String> buffer) throws InterruptedException, IOException {
			//processing on input data, from string to double[]
			for(int i = 0;i < buffer.size();i++) {
				output += buffer.get(i);
			}
//			output = output.replaceAll("32,31,","");
//			output = "32,31," + output + "32,31,";
			String parseToDouble[] = output.split(",");
			sample = new double[parseToDouble.length];
			for (int i = 0; i < sample.length; i++)
		     {
		        sample[i] = Double.parseDouble(parseToDouble[i]);
		     }
			double i=demo.predict(sample);
			System.out.println(output);
			System.out.println(demo.predict(sample));
			buttonMove(i);
			output = "";
		}
		}
	
	
	protected void buttonMove(double m){
		int n =(int)m;
		
	   
		
		switch(n){
		case 1: 
			   if (xCoord > 0.0) {
			             xCoord -= 5;
			
			             params.leftMargin = xCoord;
			             params.topMargin = yCoord;
			             btnMove.setLayoutParams(params);
		        } 
		        break;
		
		
		case 2: 
			     if (yCoord > 0.0) {
			             yCoord -= 5;
			             
			             params.leftMargin = xCoord;
			             params.topMargin  = yCoord;
			             btnMove.setLayoutParams(params);
			             
//			             btnMove.setX((float)xCoord*10);
//			             btnMove.setY((float)yCoord*10);
		         }
		         break;
		
		
		case 3:  
			     xCoord += 5;
		
			     params.leftMargin = xCoord;
	             params.topMargin  = yCoord;
	             btnMove.setLayoutParams(params);
		         break;
	              
	    case 4:  
	    	     yCoord += 5;
			  
	    	     params.leftMargin = xCoord;
	             params.topMargin  = yCoord;
	             btnMove.setLayoutParams(params);
				 break;
	   default:  break;
		
	}
	}
	}

