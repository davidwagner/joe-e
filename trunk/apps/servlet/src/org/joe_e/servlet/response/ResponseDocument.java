package org.joe_e.servlet.response;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class ResponseDocument extends DocumentImpl implements Document  {

	private static final String[] allowedTags = {"a", "abbr", "acronym", "address", "area", "b", 
			"big", "blockquote", "br", "button", "caption", "center", "cite", "code",
			"col", "colgroup", "dd", "del", "dfn", "dir", "div", "dl", "dt", "em",
			"fieldset", "font", "form", "h1", "h2", "h3", "h4", "h5", "h6", "hr",
			"i", "img", "input", "ins", "kbd", "label", "legend", "li", "map",
			"menu", "ol", "optgroup", "option", "p", "pre", "q", "s", "samp", 
			"select", "small", "span", "strike", "strong", "sub", "sup", "table",
			"tbody", "td", "textarea", "tfoot", "th", "thead", "tr", "tt", "u",
			"ul", "var"};

	
	public Element createElement(String type) throws DOMException {
		for (int i = 0; i < allowedTags.length; i++) {
			if (allowedTags[i].equals(type)) {
				return new ResponseElement(this, super.createElement(type));
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal tag");
	}
}
