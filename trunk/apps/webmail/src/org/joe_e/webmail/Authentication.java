package org.joe_e.webmail;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpSession;
// import java.nio.ByteBuffer;

public class Authentication implements org.joe_e.Equatable {
	
	
	private HashMap<String, String> accounts = null;
	private final String accountsFile = "/Users/akshay/Desktop/accounts";
	private final String mailboxesRoot = "/Users/akshay/Desktop/mailboxes/";
	private File mailboxes;
	
	
	public Authentication() {
		try {
			this.initMap();
			this.mailboxes = new File(mailboxesRoot);
		} catch (IOException e) {
			
		}
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
		
		if (accounts == null) {
			initMap();
		}
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		/** 
		 * @TODO: add a salt
		 * @TODO: hash multiple times
		 * @TODO: can't use String.getBytes(), so we need a workaround to update
		 * 		  the digest.
		 **/
		//ByteBuffer buf = ByteBuffer.allocate(password.length());
		//for (char c: password.toCharArray()) {
		//	buf.putChar(c);
		//}
		//digest.update(buf);
		digest.update(password.getBytes());
		String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
		if (accounts.get(username) != null && accounts.get(username).equals(hashedPassword)) {
			// then we can authenticate the user
			// but we must also remove the authentication agent from the session
			// so that it cannot authenticate another user.
			session.removeAttribute("auth");
			File child = new File(mailboxes, username);
			return new User(username, child);
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
		
		if (accounts == null) {
			initMap();
		}
		if (accounts.containsKey(username)) {
			return false;
		}
		
		FileWriter file = new FileWriter(new File(accountsFile), true);
		for (char c : username.toCharArray()) {
			file.append(c);
		}
		file.append(' ');
		MessageDigest digest = MessageDigest.getInstance("md5");
		digest.update(password.getBytes());
		String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
		
		for (char c : hashedPassword.toCharArray()) {
			file.append(c);
		}
		file.append('\n');
		file.flush();
		accounts = null;
		initMap();
		
		File mailbox = new File(mailboxes, username);
		mailbox.createNewFile();
		return true;
	}
	
	public void initMap() throws IOException {
		try {
			accounts = new HashMap<String, String>();
			File inputFile = new File(accountsFile);
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = "";
			while ((line = in.readLine()) != null) {
				//System.out.println(line);
				String[] arr = line.split(" ");
				if (arr.length == 2) {
					accounts.put(arr[0], arr[1]);
				}
			}
		} catch (FileNotFoundException e) {
		}
	}
}
