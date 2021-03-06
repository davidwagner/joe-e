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

	private final String accountsFile = "/home/akshayk/accounts/";
	private final String mailboxes = "/var/mail/vhosts/boink.joe-e.org/";
	
	public void fillHttpSession(HttpSession session) {
		try {
			Dispatcher.logMsg("Initializing session with AuthenticationAgent and AccountManager");
			session.setAttribute("auth", new AuthenticationAgent(MessageDigest.getInstance("sha"), new File(accountsFile), new File(mailboxes), new TransportAgent()));
			session.setAttribute("manager", new AccountManager(MessageDigest.getInstance("sha"), new File(accountsFile), new PostfixClient()));
		} catch (NoSuchAlgorithmException e) {
			Dispatcher.logMsg("NoSuchAlgorithmException when instantiating AuthAgent and AccountManager");
		}
	}

}
