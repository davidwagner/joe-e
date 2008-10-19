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
		
	public void testInsert() {
		Authentication auth = new Authentication();
		try {
			auth.addAccount("kanav", "arora", null);
			assertFalse(auth.addAccount("ankit", "desai", null));
		} catch (IOException e) {
			assertTrue("Caught IOException", false);
		} catch (NoSuchAlgorithmException e) {
			assertTrue("Caught NoSuchAlgorithmException", false);
		}
	}

	public void testLogin() {
		Authentication auth = new Authentication();
		try {
			User u = auth.authenticate("ankit", "desai", null);
			assertTrue(u.getUserName().equals("ankit"));
			u = auth.authenticate("akshay", "krish", null);
			assertTrue(u.getUserName().equals("akshay"));
		} catch (IOException e) {
			assertTrue ("Caught IOException", false);
		}
	}
}