import libsvm.*;
import java.io.IOException;
import java.util.StringTokenizer;
import pca_transform.*;
import Jama.Matrix;
import math.jwave.*;
import math.jwave.transforms.*;

import java.util.Arrays;
import java.io.PrintWriter;
import java.text.DecimalFormat;

class svm_predict {

	private svm_model model;
	private PCA pca;
	private FeatureMap test;
	private Transform t;

	public svm_predict(String modelFileRoute) throws IOException {
		DecimalFormat formatter = new DecimalFormat("0.000000");
		// String modelFileRoute = "SampleData_mix_trainning.model";
		test = new FeatureMap();
		t = new Transform( new DiscreteFourierTransform( ) );

		String trainingDataPath = "SampleData_mix.csv";
    	Matrix originalData = DataReader.read(test.getFile(trainingDataPath));
    	Matrix trainingData = originalData.getMatrix(0, originalData.getRowDimension()-1, 0, originalData.getColumnDimension()-2);
		pca = new PCA(trainingData);

		// trainingData = 
  //       pca.transform(trainingData, PCA.TransformationType.WHITENING);

		// String outputDataPath = "SampleData_mix";
	 // 	PrintWriter printer = new PrintWriter(outputDataPath);

	 //    // System.out.println("Output Data:");
	 //    for(int r = 0; r < trainingData.getRowDimension(); r++){
	 //      printer.print((int)originalData.get(r, originalData.getColumnDimension()-1)+" ");
	 //      for(int c = 0; c < trainingData.getColumnDimension(); c++){
	 //        printer.print(c+1+":"+formatter.format(trainingData.get(r,c))+" ");

	 //      }
	 //      printer.println("");
	 //    }
		
		// for(int i=0; i<originalData.getColumnDimension(); i++)
		// 	System.out.println("// "+originalData.get(0, i));

		model = svm.svm_load_model(modelFileRoute);
	}

	public double predict(double[] input) throws IOException {

		double[][] temp = new double[1][12];

		// double[] cc = {31.333333,20.666667,-5.141131,-7.750000,-5.141131,-2.583333,-0.666667,0.000000,3.807798,-2.583333,3.807798,7.750000};

		temp[0] = t.forward(test.featureResize(input,12));

		// System.out.println("afterforward: "+Arrays.deepToString(temp));

		System.out.println(Arrays.toString((test.featureResize(input,12))));

		Matrix inputData = new Matrix(temp);

		Matrix transformedData = pca.transform(inputData, PCA.TransformationType.WHITENING);

		// for(int i=0; i<transformedData.getColumnDimension(); i++)
		// 	System.out.println(transformedData.get(0, i));
		
		int m = transformedData.getColumnDimension();

		svm_node[] x = new svm_node[m];
		for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = j+1;
				x[j].value = transformedData.get(0,j);
			}

		return svm.svm_predict(model,x);
				
	}

}
