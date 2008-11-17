package org.joe_e.webmail;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpSession;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;



/**
 * @TODO: we need to be careful about synchronized access to mailboxes file
 * @author akshay
 *
 */
public class Authentication implements org.joe_e.Equatable {
	
	
	private File mailboxes;
	private File accounts;
	private File postfixRecipients;
	private MessageDigest digest;
	
	public Authentication(File accountsRoot, File mailboxesRoot, File postfix, MessageDigest d) {
		accounts = accountsRoot;
		mailboxes = mailboxesRoot;
		postfixRecipients = postfix;
		digest = d;
	}
	
	/**
	 * Determines whether this username password corresponds to a user 
	 * in our system. If so we return the capability to that user's data. 
	 * If not the return value is null.
	 * 
	 * @param username
	 * @param password
	 * @return User capability to the user's mailbax
	 * @TODO: this method will have to be reviewed for correctness
	 */
	public  User authenticate(String username, String password, HttpSession session) 
		throws IOException {
		
		if (session.getAttribute("auth") != this) {
			// illegal usage of authentication agent
			return null;
		}

		/** 
		 * @TODO: add a salt
		 * @TODO: hash multiple times
		 * @TODO: can't use String.getBytes(), so we need a workaround to update
		 * 		  the digest.
		 * @TODO: not sure this is workaround is ok... it still reveals default charset
		 **/
		try {
			byte[] bytes = ASCII.encode(password);
			digest.update(bytes);
			String hashedPassword = new BigInteger(1,digest.digest()).toString(16);

			Reader reader = ASCII.input(Filesystem.read(Filesystem.file(accounts, username)));

			BufferedReader in = new BufferedReader(reader);
			if (hashedPassword.equals(in.readLine())) {
				session.removeAttribute("auth");
				File mailbox = Filesystem.file(mailboxes, username);
				return new User(username, mailbox);
			}
		} catch (FileNotFoundException e) {
			return null;
		}
		return null;
		
	}
	
	/**
	 * Adds an account to the database "file" of accounts
	 * This operation can keep the authentication agent in the session
	 * 
	 * @param username
	 * @param password
	 */
	public boolean addAccount(String username, String password, HttpSession session) 
		throws IOException, NoSuchAlgorithmException {
		
		if (session.getAttribute("auth") != this) {
			// account not created b/c illegal use of auth agent
			return false;
		}
		
		Writer out = ASCII.output(Filesystem.writeNew(Filesystem.file(accounts, username)));
		byte[] bytes = ASCII.encode(password);
		
		digest.update(bytes);
		String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
		
		for (char c : hashedPassword.toCharArray()) {
			out.append(c);
		}
		out.flush();
		
		// once we have the accounts file then we need to update
		// postfix databases so that postfix knows about this new
		// account
		/*Writer rec = ASCII.output(Filesystem.writeNew(postfixRecipients));
		for char(c : username.toCharArray()) {
			
		}*/
		
		// TODO do we have to createnewfile here?
		// if so then we have to tame File class
		File mailbox = Filesystem.file(mailboxes, username);
		
		// destroy this authentication agent
		session.removeAttribute("auth");
		return true;
	}
}
