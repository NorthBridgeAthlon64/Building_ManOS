import java.io.*;

public class Type2 {
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader(args[0]);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			int c = 0;
			while( (s=br.readLine())!=null) {
				System.out.println(s);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}