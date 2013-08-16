package edu.mit.media.hlt.sensorgraph;

import libsvm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import pca_transform.*;
import Jama.Matrix;
import math.jwave.*;
import math.jwave.transforms.*;

import android.content.ContextWrapper;

class svm_predict {

	private svm_model model;
	private PCA pca;
	private FeatureMap test;
	private Transform t;

	public svm_predict(BufferedReader trainedModel, InputStream sampleData) throws IOException {
		// String modelFileRoute = "SampleData_mix_trainning.model";
		test = new FeatureMap();
		t = new Transform( new DiscreteFourierTransform( ) );
		
//		
//		String readLine;
//		while (((readLine = trainedModel.readLine()) != null))
//			System.out.println(readLine);
//		//		String trainingDataPath = "SampleData_mix.csv";
//		
//		BufferedReader br = new BufferedReader(new InputStreamReader(sampleData));
//		while (((readLine = br.readLine()) != null))
//			System.out.println(readLine);
//		 
		
    	Matrix originalData = DataReader.read(sampleData);
    	Matrix trainingData = originalData.getMatrix(0, originalData.getRowDimension()-1, 0, originalData.getColumnDimension()-2);
		pca = new PCA(trainingData);

		model = svm.svm_load_model(trainedModel);
	}

	public double predict(double[] input) throws IOException {

//		for(int i = 0;i < input.length;i++)
//			System.out.println(input[i]);
		
		double[][] temp = new double[1][12];
		
		temp[0] = t.forward(test.featureResize(input,12));

		Matrix inputData = new Matrix(temp);

		Matrix transformedData = pca.transform(inputData, PCA.TransformationType.WHITENING);
		
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
