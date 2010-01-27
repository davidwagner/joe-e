package org.joe_e.servlet.response;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class ResponseDocument extends DocumentImpl implements Document  {

	private static final String[] allowedTags = {"a", "abbr", "acronym", "address", "area", "b", 
			"big", "blockquote", "body", "br", "button", "caption", "center", "cite", "code",
			"col", "colgroup", "dd", "del", "dfn", "dir", "div", "dl", "dt", "em",
			"fieldset", "font", "form", "h1", "h2", "h3", "h4", "h5", "h6", "head", 
			"hr", "html", "i", "img", "input", "ins", "kbd", "label", "legend", "li", "map",
			"menu", "ol", "optgroup", "option", "p", "pre", "q", "s", "samp", 
			"select", "small", "span", "strike", "strong", "sub", "sup", "table",
			"tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "u",
			"ul", "var"};
	
	public Element createElement(String type) throws DOMException {
		for (int i = 0; i < allowedTags.length; i++) {
			if (allowedTags[i].equals(type)) {
				return new ResponseElement(this, super.createElement(type));
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal tag: " + type);
	}

    public void addJSLink(String jsFile) {
	Element js = super.createElement("script");
	js.setAttribute("src", jsFile);
	this.getDocumentElement().appendChild(js);
    }
    
    public void addCSSLink(String cssFile) {
        Element css = super.createElement("link");
        css.setAttribute("href", cssFile);
        css.setAttribute("type", "text/css");
	css.setAttribute("rel", "stylesheet");
        Element docElem = this.getDocumentElement();
        NodeList heads = docElem.getElementsByTagName("head");
        if (heads.getLength() == 0) {
	    Element head = super.createElement("head");
	    head.appendChild(css);
	    docElem.insertBefore(head, docElem.getFirstChild());
        } else {
	    heads.item(0).appendChild(css);
        }
    }
}
