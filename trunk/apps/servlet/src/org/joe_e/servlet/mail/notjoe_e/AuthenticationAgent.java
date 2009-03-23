package org.joe_e.servlet.mail.notjoe_e;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.file.InvalidFilenameException;

public class AuthenticationAgent {

	private MessageDigest digest;
	private File accounts;
	private final String accountsFile = "/Users/akshay/Desktop/accounts/";
	
	public AuthenticationAgent() {
		try {
			digest = MessageDigest.getInstance("md5");
			accounts = new File(accountsFile);
		} catch (NoSuchAlgorithmException e) {
		}
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
