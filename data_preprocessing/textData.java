import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import acm.program.ConsoleProgram;
import acm.util.ErrorException;


public class textData extends ConsoleProgram {
	
	private BufferedReader openFile(String str){ 
		BufferedReader rd = null;
		while(rd==null){
		try{
			String filename =readLine(str);
			rd= new BufferedReader(new FileReader(filename));
			
			
			}
			
		 catch( IOException ex){
			  System.out.println("Cannot read the file");
			
		}

	}
		
        return rd;
		
	}
	
	public void run(){
	    String line;
	    String l="";
	    BufferedReader rd =openFile("Please enter File:");
	    
		

		try{
			PrintWriter wr =new PrintWriter(new FileWriter("F://copy.txt"));
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
						
				        println(mline);
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
			
		} catch (IOException ex){
			throw new ErrorException(ex);
		}
	}
	

}
