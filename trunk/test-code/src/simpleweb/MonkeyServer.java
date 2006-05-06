package simpleweb;

import org.joe_e.Incapable;

import java.io.OutputStream;
import java.io.PrintStream;

public class MonkeyServer implements Incapable {
	void serve(String fileName, OutputStream serveOut) 
	{
		try {
			PrintStream ps = new PrintStream(serveOut);
			ps.println("Monkey see, monkey do!");
			ps.println(fileName.substring(16) + " is made of poo!");
			serveOut.flush();
		} catch (Exception e) {
			e.printStackTrace(ps);
		}
	}
}
