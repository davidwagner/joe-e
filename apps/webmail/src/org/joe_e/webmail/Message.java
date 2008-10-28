package org.joe_e.webmail;

import org.w3c.dom.Element;


/**
 * @TODO: should implement immutable or powerless, then we can maybe call new
 * @author akshay
 */
public class Message implements org.joe_e.Immutable {

	private final int id;
	private final String subject;
	private final String body;
	private final String sender;
	private final String recipient;
	
	/** @TODO: can we get the timeStamp 
	 * I think not!
	 **/
	private final String timeStamp;
	
	public Message(Element node) {
		id = Integer.parseInt(node.getAttribute("id"));
		subject = node.getElementsByTagName("subject").item(0).getTextContent();
		body = node.getElementsByTagName("body").item(0).getTextContent();
		sender = "";
		recipient = "";
		timeStamp = "";
	}
	
	public Message(User sender, String recipient, String subject, String body) {
		//doesn't have an ID yet
		id = -1;
		this.subject = subject;
		this.body = body;
		/** @TODO: add the correct domain name here **/
		this.sender = sender.getUserName() + "@local";
		this.recipient = recipient;
		this.timeStamp = "Current Time";
	}
	
	public String getSubject() {
		return subject;
	}
	public String getBody() {
		return body;
	}
	public int getId() {
		return id;
	}
}
