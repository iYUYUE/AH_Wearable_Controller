import java.io.IOException;

public class demo {

  public static void main( String[ ] args ) throws IOException {
    svm_predict demo = new svm_predict("SampleData_mix_trainning.model");
    double[] sample = {32,31,32,7,32,0,32,0,32,0,32,0,32,31}; //3
    System.out.println(demo.predict(sample));
    
  } // main

} // class
