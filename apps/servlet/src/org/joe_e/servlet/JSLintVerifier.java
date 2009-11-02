package org.joe_e.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class JSLintVerifier {
	private static String message = null;
	
	public static boolean verify (String s) {
		File file = new File("/tmp/script.js");
		try {
			file.createNewFile();
			FileWriter f = new FileWriter(file);
			f.write(s);
			f.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			Process p = Runtime.getRuntime().exec("java -cp /Users/akshay/Downloads/rhino1_7R2/js-14.jar org.mozilla.javascript.tools.shell.Main /Users/akshay/Documents/workspace/servlet/jslint.js /tmp/script.js");
			int exit = p.waitFor();
			BufferedReader reader = new BufferedReader (new InputStreamReader (p.getInputStream()));
			String out = null;
			message = "";
			while ((out = reader.readLine()) != null) {
				message += out + "\n";
			}
			return (exit == 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	public static String getMessage () {
		return message;
	}
    public static void main(String[] args) {
	System.out.println("Starting JSLint verifier");
	String message = "";
			try {
			Process p = Runtime.getRuntime().exec("java -cp /Users/akshay/Downloads/rhino1_7R2/js-14.jar org.mozilla.javascript.tools.shell.Main /Users/akshay/Documents/workspace/servlet/jslint.js /tmp/script.js");
			int exit = p.waitFor();
			BufferedReader reader = new BufferedReader (new InputStreamReader (p.getInputStream()));
			String out = null;
			message = "";
			while ((out = reader.readLine()) != null) {
				message += out + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			System.out.println(message);
    }

}
