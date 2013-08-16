import Jama.Matrix;
import java.io.IOException;
import pca_transform.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;


public class demo {
  public static String argv[];

  public static void main( String[ ] args ) throws IOException {
    // System.out.println(args[0]);
    FeatureMap test = new FeatureMap();
    DecimalFormat formatter = new DecimalFormat("0.000000");

    String trainingDataPath = "./data/sample2/SampleData_mix.txt";
    Matrix originalData = DataReader.read(test.getFile(trainingDataPath));
    Matrix trainingData = originalData.getMatrix(0, originalData.getRowDimension()-1, 0, originalData.getColumnDimension()-2);

    // System.out.println("originalData: "+originalData.getRowDimension()+"*"+originalData.getColumnDimension());
    // System.out.println("trainingData: "+trainingData.getRowDimension()+"*"+trainingData.getColumnDimension());
    
    PCA pca = new PCA(trainingData);
    Matrix transformedData = 
        pca.transform(trainingData, PCA.TransformationType.WHITENING);

    String outputDataPath = "./data/sample2/pp/SampleData_mix";
    PrintWriter printer = new PrintWriter(outputDataPath);

    // System.out.println("Output Data:");
    for(int r = 0; r < transformedData.getRowDimension(); r++){
      printer.print((int)originalData.get(r, originalData.getColumnDimension()-1)+" ");
      for(int c = 0; c < transformedData.getColumnDimension(); c++){
        printer.print(c+1+":"+formatter.format(transformedData.get(r,c))+" ");

      }
      printer.println("");
    }

    int N = transformedData.getRowDimension();
    // System.out.println(N);

    printer.close();

    // output shuffle
    
    int i = 0;
    String[] a = new String[N];
    
    BufferedReader br = new BufferedReader(new FileReader(outputDataPath));
    String line;
    while ((line = br.readLine()) != null) {
       a[i] = line;
       i++;
    }
    br.close();

    // shuffle array and print permutation
    shuffle(a);
    shuffle(a);

    String trainningSamplePath = "./data/sample2/pp/SampleData_mix_trainning";
    String testingSamplePath = "./data/sample2/pp/SampleData_mix_testing";

    PrintWriter printer1 = new PrintWriter(trainningSamplePath);
    for (i=0; i<a.length*3/4; i++) {
        printer1.println(a[i]);
    }
    printer1.close();

    // PrintWriter printer2 = new PrintWriter("./data/sample/pp/SampleData_mix_crossValidate");
    // for (i=a.length/2; i<a.length*3/4; i++) {
    //     printer2.println(a[i]);
    // }
    // printer2.close();

    PrintWriter printer3 = new PrintWriter(testingSamplePath);
    for (i=a.length*3/4; i<a.length; i++) {
        printer3.println(a[i]);
    }
    printer3.close();

    String[] argv = new String[1];
    argv[0] = trainningSamplePath;
    svm_train t = new svm_train();
    t.run(argv);
    
  } // main

  // swaps array elements i and j
  public static void exch(String[] a, int i, int j) {
      String swap = a[i];
      a[i] = a[j];
      a[j] = swap;
  }

  // take as input an array of strings and rearrange them in random order
  public static void shuffle(String[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + (int) (Math.random() * (N-i));   // between i and N-1
            exch(a, i, r);
        }
    }

  // take as input an array of strings and print them out to standard output
  public static void show(String[] a) {
      for (int i = 0; i < a.length; i++) {
          System.out.println(a[i]);
      }
  }
  
} // class
