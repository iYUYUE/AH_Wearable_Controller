import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class analysis {

  public static void main( String[ ] args ) throws IOException {

    int N = 1848; // output lines
    int i;
    double[] a = new double[N];
    double[] b = new double[N];
    boolean[] c = new boolean[N];
    int[] num = new int[5];
    int[] right = new int[5];
    String line;

    i = 0;
    BufferedReader br1 = new BufferedReader(new FileReader("./data/analysis/SampleData_mix_testing"));
    while ((line = br1.readLine()) != null) {
       a[i] = Double.parseDouble(line.substring(0,1));
       num[(int)a[i]-1]++;
       i++;
    }
    br1.close();

    i = 0;
    BufferedReader br2 = new BufferedReader(new FileReader("./data/analysis/SampleData.output"));
    while ((line = br2.readLine()) != null) {
       b[i] = Double.parseDouble(line);
       i++;
    }
    br2.close();

    for(i=0; i<N; i++)
      if(c[i] = a[i]==b[i])
        right[(int)a[i]-1]++;

    PrintWriter printer = new PrintWriter("./data/analysis/compare");

    for(i=0; i<N; i++)
      printer.println("practical: "+a[i]+" predicted: "+b[i]+" "+c[i]);

    printer.println("Accuracy: ");
    for(i=0; i<5; i++)
      printer.println(i+1+": "+(double)right[i]/num[i]+"  ("+right[i]+"/"+num[i]+")");

    printer.close();
    
  } // main
  
} // class
