package org.joe_e.servlet.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.joe_e.charset.ASCII;
import org.joe_e.charset.UTF8;
import org.joe_e.file.Filesystem;
import org.joe_e.file.InvalidFilenameException;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.mail.notjoe_e.TransportAgent;


public class AuthenticationAgent {

	private MessageDigest digest;
	private File accounts;
	private File mailboxes;
	private boolean active;
	private TransportAgent transport;
	
	public AuthenticationAgent(MessageDigest d, File a, File m, TransportAgent t) {
		digest = d;
		accounts = a;
		mailboxes = m;
		active = true;
		transport = t;
	}

	public FileTransportPair authenticate(String username, String password) {
		if (active) {
			Dispatcher.logMsg("Request to authenticate " + username);
			try {
				byte[] bytes = UTF8.encode(password);
				digest.update(bytes);
				String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
				Reader reader = ASCII.input(Filesystem.read(Filesystem.file(accounts, username)));
				BufferedReader in = new BufferedReader(reader);
				if (hashedPassword.equals(in.readLine())) {
					Dispatcher.logMsg("Successfully authenticated " + username);
					File f = Filesystem.file(mailboxes, username);
					active = false;
					return new FileTransportPair(f, transport);
				}
			} catch (FileNotFoundException e ) {
			} catch (IOException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvalidFilenameException e) {
			}
		}
		return null;
	}
	
	public class FileTransportPair {
		public File f;
		public TransportAgent t;
		
		public FileTransportPair(File file, TransportAgent transport) {
			f = file;
			t = transport;
		}
	}
}
