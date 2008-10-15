package org.joe_e.webmail;

import java.io.File;

/**
 * This class represents a capability to a user's account information
 * including mailbox.
 * 
 * @author akshay
 *
 */
public class User {

	private String name;
	File inbox;
	// capability to send out email
	
	public User (String username) {
		name = username;
	}

	public String getUserName() {
		return name;
	}
}