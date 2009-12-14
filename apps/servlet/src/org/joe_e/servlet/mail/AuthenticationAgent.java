package org.joe_e.servlet.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.file.InvalidFilenameException;
import org.joe_e.servlet.Dispatcher;

public class AuthenticationAgent {

	private MessageDigest digest;
	private File accounts;
	private File mailboxes;
	
	public AuthenticationAgent(MessageDigest d, File a, File m) {
		digest = d;
		accounts = a;
		mailboxes = m;
	}

	public File authenticate(String username, String password) {
		Dispatcher.logger.finest("Request to authenticate " + username);
		try {
			byte[] bytes = ASCII.encode(password);
			digest.update(bytes);
			String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
			Reader reader = ASCII.input(Filesystem.read(Filesystem.file(accounts, username)));
			BufferedReader in = new BufferedReader(reader);
			if (hashedPassword.equals(in.readLine())) {
				Dispatcher.logger.finest("Successfully authenticated " + username);
				return Filesystem.file(mailboxes, username);
			}
		} catch (FileNotFoundException e ) {
		} catch (IOException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvalidFilenameException e) {
		}
		return null;
	}
}
