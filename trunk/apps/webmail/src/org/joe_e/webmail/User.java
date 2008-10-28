package org.joe_e.webmail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.*;

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
		
	public ArrayList<Message> getMessages() {
		/** @TODO This is not OK! we can't just make an ArrayList **/
		ArrayList<Message> out = new ArrayList<Message>();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(inbox);
			NodeList messages = document.getElementsByTagName("message");
			
			for (int i = 0; i < messages.getLength(); i++) {
				/** @TODO: can't just make a new message **/
				out.add(new Message((Element) messages.item(i)));
			}
		} catch (ParserConfigurationException p) {
			return out;
		} catch (SAXException e) {
			return out;
		} catch (IOException x) {
			return out;
		}
		
		return out;
	}
	public Message getMessage(int id) {
		for (Message m : this.getMessages()) {
			if (m.getId() == id) {
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