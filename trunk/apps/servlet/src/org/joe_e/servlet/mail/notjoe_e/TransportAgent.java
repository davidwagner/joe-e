package org.joe_e.servlet.mail.notjoe_e;

import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

public class TransportAgent {

	String username;
	
	public TransportAgent() {
		username = null;
    }

    public void send(javax.mail.Message msg) throws MessagingException {
    	if (username == null) {
    		throw new MessagingException("No associated username, Someone who isn't logged in is trying to send mail!");
    	}
    	msg.setFrom(new InternetAddress(username+"@boink.joe-e.org"));
    	Transport.send(msg);
    }

    public void setUsername(String u) {
    	username = u;
    }
}