package org.joe_e.webmail.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

public class testStringMatch {

	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("/Users/akshay/Documents/workspace/WebMail/.classpath"));
			String libraryJar = "/Users/akshay/Downloads/library-2.1.0.jar";
			String s = "";
			String output = "";
			boolean found = false;
			boolean seenSrc = false;
			while((s = in.readLine()) != null) {
				if (s.equals("\t<classpathentry kind=\"lib\" path=\"" + libraryJar + "\"/>")) {
					found = true;
				}
				if (s.equals("</classpath>") && !found) {
					output += "\t<classpathentry kind=\"lib\" path=\"" + libraryJar + "\"/>\n";
				}
				
				
				if (s.equals("\t<classpathentry kind=\"src\" path=\"src\"/>")) {
					seenSrc = true;
					output += "\t<classpathentry kind=\"src\" path=\"\"/>\n";
				} else if (seenSrc && s.split("src").length != 1) {
					System.out.println("ignoring " + s);
					// ignore this line
				} else if (s.equals("\t<classpathentry kind=\"output\" path=\"bin\"/>")) {
					output += "\t<classpathentry kind=\"output\" path=\"\"/>\n";
				} else {
					output += s + "\n";
				}
			}
			System.out.println(output);
		} catch (Exception e) {
			
		}
	}
}
