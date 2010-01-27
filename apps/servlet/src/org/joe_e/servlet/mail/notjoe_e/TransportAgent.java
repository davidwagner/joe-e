package org.joe_e.servlet.mail.notjoe_e;

import javax.mail.Transport;
import javax.mail.MessagingException;

public class TransportAgent {

    public TransportAgent() {
    }

    public void send(javax.mail.Message msg) throws MessagingException {
	Transport.send(msg);
    }
}