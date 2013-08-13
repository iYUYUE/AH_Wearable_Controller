import libsvm.*;
import java.io.IOException;
import java.util.StringTokenizer;
import pca_transform.*;
import Jama.Matrix;
import math.jwave.*;
import math.jwave.transforms.*;


class svm_predict {

	private svm_model model;
	private PCA pca;
	private FeatureMap test;
	private Transform t;

	public svm_predict(String modelFileRoute) throws IOException {
		// String modelFileRoute = "SampleData_mix_trainning.model";
		test = new FeatureMap();
		t = new Transform( new DiscreteFourierTransform( ) );

		String trainingDataPath = "SampleData_mix.csv";
    	Matrix originalData = DataReader.read(test.getFile(trainingDataPath));
    	Matrix trainingData = originalData.getMatrix(0, originalData.getRowDimension()-1, 0, originalData.getColumnDimension()-2);
		pca = new PCA(trainingData);

		model = svm.svm_load_model(modelFileRoute);
	}

	public double predict(double[] input) throws IOException {

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
