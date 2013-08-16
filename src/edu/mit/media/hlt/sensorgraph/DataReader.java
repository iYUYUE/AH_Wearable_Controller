import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import math.jwave.*;
import math.jwave.transforms.*;
import math.jwave.transforms.wavelets.*;
import java.util.Arrays;

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
			// if(lineNo == 1) continue;
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

			double[] tempRow_c = sample.featureResize(vector, 12);
			double[] tempRow = sample.featureResize(vector, 12);

			if (tempRow != null) {
				// if(lineNo == 1)
				// System.out.println(Arrays.toString(tempRow));

				tempRow = t.forward(tempRow); // discrete fourier transform
				vector = new double[tempRow.length+1];
				for(int i = 0; i < tempRow.length; i++)
					vector[i] = tempRow[i];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);
				vectors.add(vector);
				vector = new double[tempRow.length+1];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);

				tempRow[0] = 32;
				tempRow[1] = 31;
				for(int i = 2; i < tempRow_c.length; i++)
					tempRow[i] = tempRow_c[i-2];
				tempRow = t.forward(tempRow);
				for(int i = 0; i < tempRow.length; i++)
					vector[i] = tempRow[i];
				vectors.add(vector);
				vector = new double[tempRow.length+1];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);

				tempRow[0] = 32;
				tempRow[1] = 31;
				tempRow[2] = 32;
				tempRow[3] = 31;
				for(int i = 4; i < tempRow_c.length; i++)
					tempRow[i] = tempRow_c[i-4];
				tempRow = t.forward(tempRow);
				for(int i = 0; i < tempRow.length; i++)
					vector[i] = tempRow[i];
				vectors.add(vector);
				vector = new double[tempRow.length+1];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);

				tempRow[tempRow.length-2] = 32;
				tempRow[tempRow.length-1] = 31;
				for(int i = 0; i < tempRow_c.length-2; i++)
					tempRow[i] = tempRow_c[i+2];
				tempRow = t.forward(tempRow);
				for(int i = 0; i < tempRow.length; i++)
					vector[i] = tempRow[i];
				vectors.add(vector);
				vector = new double[tempRow.length+1];
				vector[tempRow.length] = Double.parseDouble(elems[elemsNo]);

				tempRow[tempRow.length-2] = 32;
				tempRow[tempRow.length-1] = 31;
				tempRow[tempRow.length-4] = 32;
				tempRow[tempRow.length-3] = 31;

				for(int i = 0; i < tempRow_c.length-4; i++)
					tempRow[i] = tempRow_c[i+4];
				tempRow = t.forward(tempRow);
				for(int i = 0; i < tempRow.length; i++) {
					// if(lineNo == 1)
					// 	System.out.println(vectors.size()+" // "+Arrays.toString(vectors.get(0)));
					// 	//  System.out.println(Arrays.toString(vector));
					vector[i] = tempRow[i];
				}
				vectors.add(vector);
			}

		}
		// System.out.println("// "+Arrays.toString(vectors.get(0)));

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