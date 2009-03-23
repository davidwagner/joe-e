package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;

public class AccountManager {
	
	private File accounts;
	private MessageDigest digest;
	
	public AccountManager(MessageDigest d, File a) {
		digest = d;
		accounts = a;
	}
	
	public boolean addAccount(String username, String password) {
		try {
			for (File f : Filesystem.list(accounts)) {
				if (f.getName().equals(username)) {
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
		} catch (Exception e) {
			// clean up the file that we just wrote out
			try {
				for(File f : Filesystem.list(accounts)) {
					if (f.getName().equals(username)) {
						f.delete();
					}
				}
				return false;
			} catch (IOException e1) {
				return false;
			}
		}
		return true;
	}
}
