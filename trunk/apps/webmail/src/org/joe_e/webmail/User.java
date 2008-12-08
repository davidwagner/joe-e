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
import org.joe_e.file.InvalidFilenameException;
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
		try {
			File maildir = Filesystem.file(inbox, "Maildir");
			File newFolder = Filesystem.file(maildir, "new");
			for (File message : Filesystem.list(newFolder)) {
				try {
					Reader reader = ASCII.input(Filesystem.read(message));
					BufferedReader in = new BufferedReader(reader);
					String string = "Status: unread\n";
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
				} catch (IOException e) {
				}
			}
			File curFolder = Filesystem.file(maildir, "cur");
			for (File message : Filesystem.list(curFolder)) {
				try {
					Reader reader = ASCII.input(Filesystem.read(message));
					BufferedReader in = new BufferedReader(reader);
					String string = "Status: read\n";
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
				} catch (IOException e) {
				}
			}
		} catch (InvalidFilenameException e) {
		} catch (IOException e) {
		}
		return out;
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