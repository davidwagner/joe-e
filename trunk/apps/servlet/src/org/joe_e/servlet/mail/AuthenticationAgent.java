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

public class AuthenticationAgent {

	private MessageDigest digest;
	private File accounts;
	
	public AuthenticationAgent(MessageDigest d, File a) {
		digest = d;
		accounts = a;
	}

	public boolean authenticate(String username, String password) {
		try {
			byte[] bytes = ASCII.encode(password);
			digest.update(bytes);
			String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
			Reader reader = ASCII.input(Filesystem.read(Filesystem.file(accounts, username)));
			BufferedReader in = new BufferedReader(reader);
			if (hashedPassword.equals(in.readLine())) {
				return true;
			}
		} catch (FileNotFoundException e ) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
	}
}
