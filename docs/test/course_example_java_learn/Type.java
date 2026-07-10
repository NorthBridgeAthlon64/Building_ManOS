import java.io.*;

public class Type {
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader(args[0]);
			int c = 0;
			while( (c=fr.read())!=-1) {
				System.out.print((char) c);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}