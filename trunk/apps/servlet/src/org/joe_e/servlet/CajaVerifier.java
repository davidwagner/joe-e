package org.joe_e.servlet;

import com.google.caja.plugin.PluginCompilerForServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class CajaVerifier {

    public static String cajole(String s) {
	Dispatcher.logger.fine(s);
	       File file = new File("/tmp/script.html");
		try {
			file.createNewFile();
			FileWriter f = new FileWriter(file);
			f.write(s);
			f.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			Dispatcher.logger.fine("Error with writing to caja file");
			return null;
		}
		return PluginCompilerForServlet.go(new String[]{"--input", "/tmp/script.html", "--out", "/i/dont/care"});
    }

    public static void main(String[] args) {
        System.out.println(PluginCompilerForServlet.go(new String[]{"--input", "/home/akshayk/index.html", "--out", "/i/dont/care"}));

    }

}