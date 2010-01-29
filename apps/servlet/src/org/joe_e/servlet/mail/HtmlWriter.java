package org.joe_e.servlet.mail;


import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ResponseElement;

public class HtmlWriter {

	public static ResponseElement printHeader(ResponseDocument doc) {
		ResponseElement root = doc.createElement("html");
		doc.appendChild(root);

		ResponseElement head = doc.createElement("head");
		ResponseElement title = doc.createElement("title");
		head.appendChild(title);
		title.appendChild(doc.createTextNode("Joe-E Mail"));
		root.appendChild(head);

		ResponseElement body = doc.createElement("body");
		root.appendChild(body);
		return body;
	}

}
