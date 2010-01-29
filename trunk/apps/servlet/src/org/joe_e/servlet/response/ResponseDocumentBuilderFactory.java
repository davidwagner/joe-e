package org.joe_e.servlet.response;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Again this is used to obtain an instance of <code>ResponseDocument</code>. 
 * This can give you a <code>ResponseDocumentBuilder</code> which can in turn
 * give you a <code>ResponseDocument</code>. Other than these features, this
 * class is incomplete. 
 * @author akshay
 *
 */
public class ResponseDocumentBuilderFactory extends DocumentBuilderFactory {

	@Override
	public Object getAttribute(String name) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getFeature(String name) throws ParserConfigurationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResponseDocumentBuilder newDocumentBuilder()
			throws ParserConfigurationException {
		return new ResponseDocumentBuilder();
	}

	public static ResponseDocumentBuilderFactory newInstance() {
		return new ResponseDocumentBuilderFactory();
	}
	
	@Override
	public void setAttribute(String name, Object value)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFeature(String name, boolean value)
			throws ParserConfigurationException {
		// TODO Auto-generated method stub

	}

}
