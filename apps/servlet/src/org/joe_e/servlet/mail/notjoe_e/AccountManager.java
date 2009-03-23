package org.joe_e.servlet.mail.notjoe_e;

import java.io.File;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;

public class AccountManager {
	
	private final String accountsFile = "/Users/akshay/Desktop/accounts/";
	private File accounts;
	private MessageDigest digest;
	
	public AccountManager() {
		try {
			digest = MessageDigest.getInstance("md5");
			accounts = new File(accountsFile);
		} catch (NoSuchAlgorithmException e) {
		}
	}
	
	public boolean addAccount(String username, String password) {
		for (String s: accounts.list()) {
			if (s.equals(username)) {
				return false;
			}
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
		} catch (Exception e) {
			// clean up the file that we just wrote out
			for(File f : accounts.listFiles()) {
				if (f.getName().equals(username)) {
					f.delete();
				}
			}
			return false;
		}
		return true;
	}
}
