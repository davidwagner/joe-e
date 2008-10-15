package org.joe_e.webmail.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import junit.framework.TestCase;
import org.joe_e.webmail.Authentication;
import org.joe_e.webmail.User;

/**
 * @author akshay
 *
 */
public class TestAuth extends TestCase {
	
	public void testInitMap() {
		try {
			Authentication.initMap();
		} catch (IOException e) {
			assertTrue("Caught IOException", false);
		}
		HashMap<String, String> accounts = Authentication.accounts;
		assertFalse("accounts.isEmpty()", accounts.isEmpty());
		assertTrue("ankit in accounts", accounts.keySet().contains("ankit"));
		assertTrue("akshay in accounts", accounts.keySet().contains("akshay"));
	}
	
	public void testInsert() {
		try {
			Authentication.addAccount("kanav", "arora");
			assertFalse(Authentication.addAccount("ankit", "desai"));
		} catch (IOException e) {
			assertTrue("Caught IOException", false);
		} catch (NoSuchAlgorithmException e) {
			assertTrue("Caught NoSuchAlgorithmException", false);
		}
	}

	public void testLogin() {
		try {
			User u = Authentication.authenticate("ankit", "desai");
			assertTrue(u.getUserName().equals("ankit"));
			u = Authentication.authenticate("akshay", "krish");
			assertTrue(u.getUserName().equals("akshay"));
		} catch (IOException e) {
			assertTrue ("Caught IOException", false);
		}
	}
}