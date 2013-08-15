package edu.mit.media.hlt.sensorgraph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import math.jwave.*;
import math.jwave.transforms.*;


import Jama.Matrix;

/** Reads data matrix from a CSV file 
 * @author Mateusz Kobos
 * */
public class DataReader {

	/**
	 * Read data matrix from a CSV file. 
	 * The first row (corresponding to the header) is ignored. Some lines may
	 * be commented out by a '#' character. 
	 * @param inStream CSV file
	 * @param ignoreLastColumn if True, the last column is ignored. This is 
	 * helpful when the last column corresponds to a class of a vector.
	 * @return data matrix
	 */
	public static Matrix read(InputStream inStream) 
			throws IOException{
		DataInputStream in = new DataInputStream(inStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int lineNo = 0;
		ArrayList<double[]> vectors = new ArrayList<double[]>();
		
		// Feature Resize Mapping
		FeatureMap sample = new FeatureMap();
		// JWave
		Transform t = new Transform( new DiscreteFourierTransform( ) );

		while ((line = br.readLine()) != null) {
			lineNo++;
			/** Ignore the header line */
			if(lineNo == 1) continue;
			/** Ignore the comments */
			int commentIndex = line.indexOf('#');
			if(commentIndex != -1)
				line.substring(0, commentIndex);
			line = line.trim();
			/** Ignore empty lines */
			if (line.length() == 0) continue;
			String[] elems = line.split(",");
			int elemsNo = elems.length-1;
			double[] vector = new double[elemsNo];
			for(int i = 0; i < elemsNo; i++)
				vector[i] = Double.parseDouble(elems[i]);
			double[] tempRow = sample.featureResize(vector, 12);
			if (tempRow != null) {
				tempRow = t.forward(tempRow); // discrete fourier transform
				vector = new double[tempRow.length+1];
				for(int i = 0; i < tempRow.length; i++)
					vector[i] = tempRow[i];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);
				vectors.add(vector);

				vector[0] = 32;
				vector[1] = 31;
				for(int i = 2; i < tempRow.length; i++)
					vector[i] = tempRow[i-2];
				vectors.add(vector);

				vector[2] = 32;
				vector[3] = 31;
				for(int i = 4; i < tempRow.length; i++)
					vector[i] = tempRow[i-4];
				vectors.add(vector);

				vector[tempRow.length-2] = 32;
				vector[tempRow.length-1] = 31;
				for(int i = 0; i < tempRow.length-2; i++)
					vector[i] = tempRow[i+2];
				vectors.add(vector);

				vector[tempRow.length-4] = 32;
				vector[tempRow.length-3] = 31;
				for(int i = 0; i < tempRow.length-4; i++)
					vector[i] = tempRow[i+4];
				vectors.add(vector);
			}
		}
		
		double[][] vectorsArray = new double[vectors.size()][];
		for(int r = 0; r < vectors.size(); r++)
			vectorsArray[r] = vectors.get(r);
		Matrix m = new Matrix(vectorsArray);
		return m;
	}

	protected InputStream getFile(String filePath){
		return  getClass().getResourceAsStream(filePath);
	}
	
	
	
}
