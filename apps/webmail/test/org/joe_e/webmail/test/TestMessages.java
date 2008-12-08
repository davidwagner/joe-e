package org.joe_e.webmail.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.joe_e.webmail.Message;

public class TestMessages {

	public static void main(String[] args) {
		ArrayList<String> out = new ArrayList<String>();
		File maildir = new File("/var/mail/vhosts/boink.joe-e.org/akshayk/Maildir/new");
		for (File message : maildir.listFiles()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(message));
				String msg = "";
				String s = "";
				while ((s = reader.readLine()) != null) {
					msg += s + "\n";
				}
				Message newMessage = new Message(msg);

				System.out.println(newMessage);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
