package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.mail.notjoe_e.PostfixClient;

public class AccountManager {
	
	private File accounts;
	private MessageDigest digest;
	private PostfixClient client;
	
	public AccountManager(MessageDigest d, File a, PostfixClient c) {
		digest = d;
		accounts = a;
		client = c;
	}
	
	public boolean addAccount(String username, String password) {
		Dispatcher.logger.finest("Request to create account for: " + username);
		try {
			for (File f : Filesystem.list(accounts)) {
				if (f.getName().equals(username)) {
					Dispatcher.logger.fine("Username " + username + " already exists");
					return false;
				}
			}
		} catch (IOException e1) {
			return false;
		}
		try {
			Writer out = ASCII.output(Filesystem.writeNew(Filesystem.file(accounts, username)));
			byte[] bytes = ASCII.encode(password);
			digest.update(bytes);
			String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
			for (char c : hashedPassword.toCharArray()) {
				out.append(c);
			}
			out.flush();
			client.updateDatabase(username);
		} catch (Exception e) {
			// clean up the file that we just wrote out
			try {
				for(File f : Filesystem.list(accounts)) {
					if (f.getName().equals(username)) {
						f.delete();
					}
				}
				Dispatcher.logger.finest("Caught an exception, either IO or crypto related, unable to create account");
				return false;
			} catch (IOException e1) {
				Dispatcher.logger.finest("Caught an exception, either IO or crypto related, unable to create account");
				return false;
			}
		}
		Dispatcher.logger.finest("Successfully created account for " + username);
		return true;
	}
}
