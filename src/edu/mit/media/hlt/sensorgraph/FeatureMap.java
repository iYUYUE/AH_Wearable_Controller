package edu.mit.media.hlt.sensorgraph;

import java.util.*;
import java.io.InputStream;

class FeatureMap {

	public FeatureMap() {

	}

	public double[] featureResize(double[] input, int base)
	{
		double[] output;
		int count;
		int num = input.length;

		if(num==base) {
			return input;
		} else if(num>base) {
			int diff = num-base;
			
			int interval = num/diff;
			if(interval==1) interval = 2; //Incase base < input.length/2

			int current = interval-1;
			while(current<input.length && num>base) {
				input[current] = -1.;
				current = current + interval;
				num --;
			}

			output = new double[num];

		} else {
			if (num*2<base) return null; // noise filter

			output = new double[base];
			for(int i=0; i<base-1; i+=2) {
				output[i]=32.;
				output[i+1]=31.;
			}
		}

		count = 0;

		for(double item : input) {
			// System.out.println(item);
	        if(item != -1.)
	        	output[count++] = item;
		}
			

	    return (num>base)? featureResize(output, base):output;
	}

	public static void main(String argv[]) {
		// test 1
		// for(int i=2; i<=50; i+=2) {
		// 	Double[] zero = new Double[i];
		// 	for(int j=0; j<i; j++)
		// 		zero[j]=0.;
		// 	featureMap sample = new featureMap();

		// 	int num_before = zero.length;
		// 	Double[] value = sample.featureResize(zero, 12);
		// 	int num_after = (value==null)?0:value.length;
		// 	System.out.println(num_before + ":" + num_after );
		// }

		// Feature Resize Mapping
	}

	protected InputStream getFile(String filePath){
		return  getClass().getResourceAsStream(filePath);
	}

	
}
