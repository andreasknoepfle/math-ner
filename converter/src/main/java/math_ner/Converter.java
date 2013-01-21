package math_ner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter {

	private static  String TAG_NAME_MATH_VALUE_END = "<tag name=\"MATH\" value=\"end\"/>";
	private static  String TAG_NAME_MATH_VALUE_START = "<tag name=\"MATH\" value=\"start\"/>";

	public static void main(String[] args) {

		FileInputStream fstream;
		try {
			FileWriter ostream = new FileWriter(args[1]);
			BufferedWriter out = new BufferedWriter(ostream);
			fstream = new FileInputStream(args[0]);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line;
			String tag="O";
			/*
			 *  Todo .... Build with regex 4 other tags
			 * 
			 */
			while ((line = br.readLine()) != null) {
				
				if(line.equals(TAG_NAME_MATH_VALUE_START)) {
					tag = "MATH";
				} else if(line.compareTo(TAG_NAME_MATH_VALUE_END) == 0) {
					tag = "O";
				} else {
					out.write(line+"\t"+tag+"\n");
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
