package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joe_e.charset.ASCII;
import org.joe_e.charset.UTF8;
import org.joe_e.file.Filesystem;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.mail.notjoe_e.PostfixClient;


/**
 * This class is responsible for account management, and represents the capability
 * to add an account to the webmail system. 
 * @author akshay
 *
 */
public class AccountManager {
	
	private File accounts;
	private MessageDigest digest;
	private PostfixClient client;
	
	private String subject = "Welcome to Joe-E Mail";
	private String body = "Welcome to Joe-E Mail";
	
	/**
	 * Construct an account manager, by giving him a way encrypt passwords,
	 * a reference to the database of usernames, and the capability to
	 * mess with postfix. 
	 * @param d
	 * @param a
	 * @param c
	 */
	public AccountManager(MessageDigest d, File a, PostfixClient c) {
		digest = d;
		accounts = a;
		client = c;
	}
	
	/**
	 * Add an account to the system by giving the account manager a username
	 * and password. This does some minimal checking and then adds the account
	 * to the users database. It also configures postfix to accept email for this
	 * account and sends the user a welcome message. 
	 * @param username
	 * @param password
	 * @return <code>true</code> if the account was successfully created
	 */
	public boolean addAccount(String username, String password) {
		// TODO: username should be lower-cased, but that's not allowed
		// by Joe-E.
		if (!checkUsername(username)) {
			return false;
		}
		synchronized(AccountManager.class) {
			Dispatcher.logMsg("Request to create account for: " + username);
			File userFile;
			Writer out;
			try {
				userFile = Filesystem.file(accounts, username);
				out = ASCII.output(Filesystem.writeNew(userFile));
			} catch (IOException e) {
				Dispatcher.logMsg("Caught an IO Exception, probably the username already exists");
				return false;
			}
			// at this point we know that the username doesn't exist. Any error handling should clean
			// up the file system
			try {
				byte[] bytes = UTF8.encode(password);
				digest.update(bytes);
				String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
				for (char c : hashedPassword.toCharArray()) {
					out.append(c);
				}
				out.flush();
				if (!client.updateDatabase(username)) {
					userFile.delete();
					Dispatcher.logMsg("postfix client failed, unable to create account");
					return false;
				}
			} catch (Exception e) {
				// clean up the file that we just wrote out
				userFile.delete();
				Dispatcher.logMsg("Caught an exception, either IO or crypto related, unable to create account");
				return false;
			}
			Dispatcher.logMsg("Successfully created account for " + username);
		}
		
		// Now we have to send a welcome email to the account so their directory gets created
		// args are ok so we can send it to the outgoing mail client
		//Message message = new Message(user, to, subject, body);
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "10025");
		
		Session mailSession = Session.getDefaultInstance(props, null);
		javax.mail.Message msg = new MimeMessage(mailSession);
		try {
			msg.setText(body);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress("akshayk@boink.joe-e.org"));
			msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(username+"@boink.joe-e.org"));
		} catch (Exception e) {
			return false;
		}
		
		try {
	        Transport.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean checkUsername(String username) {
		return Pattern.compile("[A-Za-z][A-Za-z0-9\\.\\-_]*").matcher(username).matches();
	}
}
