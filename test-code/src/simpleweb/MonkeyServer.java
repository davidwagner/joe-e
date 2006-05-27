package simpleweb;

import org.joe_e.Powerless;

import java.io.OutputStream;
import java.io.PrintStream;

public class MonkeyServer implements Powerless {
	void serve(String fileName, OutputStream serveOut) 
	{
		PrintStream ps = new PrintStream(serveOut);
		try {
			ps.println("Monkey see, monkey do!");
			ps.println(fileName.substring(16) + " is made of poo!");
			serveOut.flush();
		} catch (Exception e) {
			e.printStackTrace(ps);
		}
	}
}
