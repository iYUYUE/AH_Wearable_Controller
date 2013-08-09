import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// import acm.program.ConsoleProgram;
// import acm.util.ErrorException;


public class textData {
	
	public static void main(String argv[]) throws IOException {

	    String line;

	    FeatureMap test = new FeatureMap();
	    DataInputStream in = new DataInputStream(test.getFile(argv[0]));
		BufferedReader rd = new BufferedReader(new InputStreamReader(in));
	    
		PrintWriter wr =new PrintWriter(new FileWriter("./copy.txt"));
		while(true){
			line = rd.readLine();
			
			if(line.charAt(0)== '\\') break;
		}
		
		while(true){
			
			line = rd.readLine();
			while(true){
				if(line==null) break;
				if(line.charAt(0)!='\\') break;
				line=rd.readLine();
			}
			
			if(line==null) break;
			
			while(true){
				
				String mline=line;
				line = rd.readLine();
				
                if(line.charAt(0)=='\\'){
					
			        System.out.println(mline);
			        wr.println(mline);
			        break;
				}					
				if(mline.length()>line.length()){
					
					line=mline;	
				}	
			}
		}
		rd.close();
		wr.close();
			
	}
	

}
