import Jama.Matrix;
import java.io.IOException;
import pca_transform.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class demo {

  public static void main( String[ ] args ) throws IOException {
    FeatureMap test = new FeatureMap();
    DecimalFormat formatter = new DecimalFormat("0.000000");

    String trainingDataPath = "./data/sample/SampleData_up.txt";
    Matrix originalData = DataReader.read(test.getFile(trainingDataPath));
    Matrix trainingData = originalData.getMatrix(0, originalData.getRowDimension()-1, 0, originalData.getColumnDimension()-2);

    // System.out.println("originalData: "+originalData.getRowDimension()+"*"+originalData.getColumnDimension());
    // System.out.println("trainingData: "+trainingData.getRowDimension()+"*"+trainingData.getColumnDimension());
    
    PCA pca = new PCA(trainingData);
    Matrix transformedData =
      pca.transform(trainingData, PCA.TransformationType.WHITENING);

    FileWriter outputFile = new FileWriter("./data/sample/pp/SampleData_up", true);
    PrintWriter printer = new PrintWriter(outputFile);

    // System.out.println("Output Data:");
    for(int r = 0; r < transformedData.getRowDimension(); r++){
      printer.print((int)originalData.get(r, originalData.getColumnDimension()-1)+" ");
      for(int c = 0; c < transformedData.getColumnDimension(); c++){
        printer.print(c+1+":"+formatter.format(transformedData.get(r,c))+" ");

      }
      printer.println("");
    }

    printer.close();
    outputFile.close();
    
  } // main
  
} // class
