import java.io.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class TtoA {

  public static void main( String[ ] args ) throws IOException {
  	String input_file_name = "SampleData_mix_trainning";
    BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
    double[][] sample = new double[1107][12];
    for(int i=0;i<1107;i++)
		{
			String line = fp.readLine();
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
			st.nextToken();
			for(int j=0;j<12;j++) {
				st.nextToken();
				sample[i][j] = atof(st.nextToken());
			}


		}
    fp.close();

    String output = Arrays.deepToString(sample);

    System.out.print(output);

    PrintWriter printer = new PrintWriter("arrays.txt");
    printer.print(output);
    printer.close();

  } // main

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

} // class
