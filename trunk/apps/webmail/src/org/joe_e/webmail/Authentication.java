package org.joe_e.webmail;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authentication {
	
	
	public static HashMap<String, String> accounts = null;
	public static final String accountsFile = "/Users/akshay/Desktop/accounts";
	
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
	public static User authenticate(String username, String password) 
		throws IOException {
		if (accounts == null) {
			initMap();
		}
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		// add a salt
		// hash multiple times
		digest.update(password.getBytes());
		String hashedPassword = new BigInteger(1,digest.digest()).toString(16);
		if (accounts.get(username) != null && accounts.get(username).equals(hashedPassword)) {
			// then we can authenticate the user
			return new User(username);
		}
		
		return null;
	}
	
	/**
	 * Adds an account to the database "file" of accounts
	 * @param username
	 * @param password
	 */
	public static boolean addAccount(String username, String password) throws IOException, NoSuchAlgorithmException {
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
		
		return true;
	}
	
	public static void initMap() throws IOException {
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
			System.out.println("File not found");
		}
	}
}
