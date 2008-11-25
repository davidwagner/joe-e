package org.joe_e.webmail;

import org.joe_e.array.ImmutableArray;
import org.w3c.dom.Element;


/**
 * @TODO: should implement immutable or powerless, then we can maybe call new
 * @author akshay
 */
public class Message implements org.joe_e.Immutable {

	private final String id;
	private final String subject;
	private final String body;
	private final String sender;
	private final String recipient;
	private final String status;
	
	/** @TODO: can we get the timeStamp 
	 * I think not!
	 **/
	private final String timeStamp;
	
	
	/**
	 * parse the message into appropriate fields and set up this
	 * message object
	 * Return-Path: <akshayk@boink.CS.Berkeley.EDU>
	 * X-Original-To: akshayk@boink.joe-e.org
     * Delivered-To: akshayk@boink.joe-e.org
     * Received: by boink.cs.berkeley.edu (Postfix, from userid 1005)
	 * id F0C3C4500C; Sat, 22 Nov 2008 22:43:25 -0800 (PST)
     * Message-Id: <20081123064325.F0C3C4500C@boink.cs.berkeley.edu>
     * Date: Sat, 22 Nov 2008 22:43:24 -0800 (PST)
     * From: akshayk@boink.CS.Berkeley.EDU (Akshay Krishnamurthy)
     * To: undisclosed-recipients:;
	 * Subject: hey
	 * 
     * hey there
	 * @param body
	 */
	public Message(String input) {
		ImmutableArray<String> message = ImmutableArray.array(input.split("\n\n"));
		this.body = message.get(1);
		message = ImmutableArray.array((message.get(1)).split("\n"));
		
		String sender = "";
		String recipient = "";
		String id = "";
		String subject = "";
		String timestamp = "";
		String status = "";
		
		for (String line: message) {
			if (line.length() > 4 && line.substring(0, 4).equals("From")) {
				sender = line.substring(5);
			} else if (line.length() > 12 && line.substring(0, 12).equals("Delivered-To")) {
				recipient = line.substring(13);
			} else if (line.length() > 10 && (line.substring(0, 10).equals("Message-Id")
		|| line.substring(0, 10).equals("Message-ID"))) {
				id = line.substring(13, line.length()-1);
			} else if (line.length() > 7 && line.substring(0, 7).equals("Subject")) {
				subject = line.substring(8);
			} else if (line.length() > 4 && line.substring(0, 4).equals("Date")) {
				timestamp = line.substring(5);
			} else if (line.length() > 7 && line.substring(0, 7).equals("Status")) {
				status = line.substring(8);
			}
		}
		
		this.sender = sender;
		this.recipient = recipient;
		this.id = id;
		this.timeStamp = timestamp;
		this.subject = subject;
		this.status = status;
	}
	
	
	public String getSubject() {
		return subject;
	}
	public String getBody() {
		return body;
	}
	public String getId() {
		return id;
	}
	public String getSender() {
		return sender;
	}
	public String getTimeStamp() {
		return this.timeStamp;
	}
	public String getStatus() {
		return this.status;
	}
}
