package org.joe_e.servlet.mail.notjoe_e;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpSession;

import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.SessionInitializer;
import org.joe_e.servlet.mail.AccountManager;
import org.joe_e.servlet.mail.AuthenticationAgent;

public class SessionInit implements SessionInitializer {

	private final String accountsFile = "/Users/akshay/Desktop/accounts/";
	
	public void fillHttpSession(HttpSession session) {
		try {
			Dispatcher.logger.finer("Initializing session with AuthenticationAgent and AccountManager");
			session.setAttribute("auth", new AuthenticationAgent(MessageDigest.getInstance("md5"), new File(accountsFile)));
			session.setAttribute("manager", new AccountManager(MessageDigest.getInstance("md5"), new File(accountsFile), new PostfixClient()));
		} catch (NoSuchAlgorithmException e) {
			Dispatcher.logger.severe("NoSuchAlgorithmException when instantiating AuthAgent and AccountManager");
		}
	}

}
