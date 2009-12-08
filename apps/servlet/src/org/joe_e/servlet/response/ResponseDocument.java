package org.joe_e.servlet.response;

import java.util.Stack;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class ResponseDocument extends DocumentImpl implements Document  {

	/**
	 * Perform depth first search and check that no script tags are in this
	 * document.
	 */
	public void checkDocument() {
		NodeList lst = this.getElementsByTagName("script");
		for (int i = 0; i < lst.getLength(); i++) {
			Node n = lst.item(i);
			n.setTextContent("");
		}
	}
}
