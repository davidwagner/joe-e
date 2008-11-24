package org.joe_e.webmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.*;

import org.joe_e.array.ConstArray;
import org.joe_e.array.ImmutableArray;
import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class represents a capability to a user's account information
 * including mailbox.
 * 
 * @author akshay
 *
 */
public class User implements org.joe_e.Equatable {

	private String name;
	File inbox;
	// capability to send out email
	
	public User (String username, File box) {
		name = username;
		inbox = box;
	}

	public String getUserName() {
		return name;
	}
		
	public ImmutableArray<Message> getMessages() {
		ImmutableArray<Message> out = null;
		File maildir = Filesystem.file(inbox, "Maildir");
		File newFolder = Filesystem.file(maildir, "new");
		if (newFolder.exists()) {
			for (File message : newFolder.listFiles()) {
				try {
					Reader reader = ASCII.input(Filesystem.read(message));
					BufferedReader in = new BufferedReader(reader);
					String string = "";
					String s = "";
					while (( s = in.readLine()) != null) {
						string += s + "\n";
					}
					Message msg = new Message(string);
					if (out == null) {
						out = ImmutableArray.array(msg);
					} else {
						out = out.with(msg);
					}
				} catch (FileNotFoundException f) {
					Message msg = new Message(f.getMessage());
					out = ImmutableArray.array(msg);
				} catch (IOException e) {
					Message msg = new Message(e.getMessage());
					out = ImmutableArray.array(msg);
				}
			}
		}
		return out;
		// TODO: check this usage with Adrian
		/*ImmutableArray<Message> out = ImmutableArray.array();
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(inbox);
			NodeList messages = document.getElementsByTagName("message");
			for (int i = 0; i < messages.getLength(); i++) {
				// TODO: can we just make a new element?
				out = out.with(new Message((Element) messages.item(i)));
			}
		} catch (ParserConfigurationException p) {
			return out;
		} catch (SAXException e) {
			return out;
		} catch (IOException x) {
			return out;
		}
		
		return out;*/
	}
	public Message getMessage(String id) {
		for (Message m : this.getMessages()) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		return null;
	}
	
	public boolean equals(Object o) {
		try {
			User u = (User) o;
			if (this.name.equals(u.name)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
}