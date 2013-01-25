package math_ner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Join {


	public static void main(String[] args) {

		FileInputStream fstream;
		FileInputStream fstream2;
		try {
			FileWriter ostream = new FileWriter(args[2]);
			BufferedWriter out = new BufferedWriter(ostream);
			
			// Expected
			fstream = new FileInputStream(args[0]);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// Regexed
			fstream2 = new FileInputStream(args[1]);
			DataInputStream in2 = new DataInputStream(fstream2);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

			String line,line2;
		
			/*
			 *  Todo .... Build with regex 4 other tags
			 * 
			 */
			while ((line = br.readLine()) != null && (line2 = br2.readLine()) != null) {
				String[] line_split = line.split("\t");
				String[] line2_split = line2.split("\t");
				if(line_split[0].equals(line2_split[0])) {
					out.write(line_split[0]);
					out.write("\t");
					out.write(line_split[1]);
					out.write("\t");
					out.write(line2_split[1]);
					out.newLine();
				} else {
					System.err.println("Files dont match!");
					System.exit(-1);
				}
				
			}

			in.close();
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
