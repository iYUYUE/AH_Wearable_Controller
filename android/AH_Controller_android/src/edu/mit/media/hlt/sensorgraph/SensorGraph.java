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
	//private TextView mValueTV;
	
	private ArduinoReceiver arduinoReceiver;
	private TextView text;
    private String str="";
	private ImageView btnMove;
	private RelativeLayout.LayoutParams params;
	private int yCoord = 100, xCoord = 500;
    private final static int SPEED=25;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arduinoReceiver = new ArduinoReceiver();
        setContentView(R.layout.main);
         
        text = (TextView)findViewById(R.id.value);
        
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
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	
	public class DataHandler extends Thread {

    	String output;
    	double sample[];
    	svm_predict demo;
		vote voter;
		int lastResult;
		
		
    	public DataHandler() throws IOException, InterruptedException {
    		//debugging
			System.out.println("Okay!");
			
			//initialize voter for judgement on redundant results after an action
			voter = new vote();
			
			//initialize svm
    		AssetManager assetManager = getAssets();
    		InputStream sampleDataMix = assetManager.open("data/sampledata_mix.csv");
    		InputStream trainedModel = assetManager.open("data/sampledata_mix_trainning.model");
    		InputStreamReader tempISR = new InputStreamReader(trainedModel);
    		BufferedReader tempBuffer = new BufferedReader(tempISR);			
			try {
				demo = new svm_predict(tempBuffer, sampleDataMix);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
    		//Initialize fields
    		output = "";
    		lastResult = 0;
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
			int i= (int) demo.predict(sample);
//			System.out.println(output);
//			System.out.println(i);
			int result = vote.sendToJudge(i);
			System.out.println(result);
			if(result != lastResult)
				buttonMove(result);
			lastResult = result;
			output = "";
		}
	}
	
	
		protected void buttonMove(int m){
			int n = m;
			
			switch(n){
				case 1: 
				   if (xCoord > 0.0) {
							 xCoord -=SPEED;
				
							 params.leftMargin = xCoord;
							 params.topMargin = yCoord;
							 btnMove.setLayoutParams(params);
							 str += "left\n";
							 text.setText(str);
					} 
					break;
			
				case 4: 
					 if (yCoord > 0.0) {
						yCoord -=SPEED;
						params.leftMargin = xCoord;
						params.topMargin  = yCoord;
						btnMove.setLayoutParams(params);
						str += "up\n";
						text.setText(str);
					 }
					 break;
					 
				case 3:  
						xCoord +=SPEED;
						params.leftMargin = xCoord;
						params.topMargin  = yCoord;
						btnMove.setLayoutParams(params);
						str += "right\n";
						text.setText(str);
						break;
						  
				case 2:  
						yCoord += SPEED;
						params.leftMargin = xCoord;
						params.topMargin  = yCoord;
						btnMove.setLayoutParams(params);
						str += "down\n";
						text.setText(str);
						break;
						
			   default: break;
			}
		}
	}

