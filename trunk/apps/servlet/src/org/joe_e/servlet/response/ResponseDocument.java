package org.joe_e.servlet.response;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

/**
 * We have our own DOM implementation that only allows specific tags and attributes
 * This class represents a DOM document. For the most part its functionality is 
 * inherited from DocumentImpl.
 * @author akshay
 *
 */
public class ResponseDocument extends DocumentImpl implements Document  {
	public static final long serialVersionUID = 1L;

	// These are the legal tags that can be created. This helps prevent dynamically
	// generated javascript
	private static final String[] allowedTags = {"a", "abbr", "acronym", "address", "area", "b", 
		"big", "blockquote", "body", "br", "button", "caption", "center", "cite", "code",
		"col", "colgroup", "dd", "del", "dfn", "dir", "div", "dl", "dt", "em",
		"fieldset", "font", "form", "h1", "h2", "h3", "h4", "h5", "h6", "head", 
		"hr", "html", "i", "img", "input", "ins", "kbd", "label", "legend", "li", "map",
		"menu", "ol", "optgroup", "option", "p", "pre", "q", "s", "samp", 
		"select", "small", "span", "strike", "strong", "sub", "sup", "table",
		"tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "u",
		"ul", "var"};

	/**
	 * Create an element with name "type" unless it is not an allowed tag. 
	 */
	public ResponseElement createElement(String type) throws DOMException {
		for (int i = 0; i < allowedTags.length; i++) {
			if (allowedTags[i].equals(type)) {
				return new ResponseElement(this, super.createElement(type));
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal tag: " + type);
	}
	
	public Text createTextNode(String s) {
		return super.createTextNode(s);
	}

	public Node appendChild(Node n) {
		return super.appendChild(n);
	}
	
	/**
	 * We allow each page to link in a static javascript file. This method
	 * adds the html for that. Since this method is suppressed from Joe-E, it
	 * cannot be called by app-level code. 
	 * @param jsFile
	 */
	public void addJSLink(String jsFile) {
		Element js = super.createElement("script");
		js.setAttribute("src", jsFile);
		if (this.getDocumentElement() != null) {
			this.getDocumentElement().appendChild(js);
		}
	}

	/**
	 * We allow each page to link in a static css file. This method
	 * adds the html for that. Since this method is suppressed from Joe-E, it
	 * cannot be called by app-level code. 
	 * @param cssFile
	 */
	public void addCSSLink(String cssFile) {
		Element css = super.createElement("link");
		css.setAttribute("href", cssFile);
		css.setAttribute("type", "text/css");
		css.setAttribute("rel", "stylesheet");
		Element docElem = this.getDocumentElement();
		if (docElem != null) {
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
	
	public void addCSRFTokens(String token) {
		NodeList forms = this.getElementsByTagName("form");
		for (int i = 0; i < forms.getLength(); i++) {
			Node form = forms.item(i);
			Element input = super.createElement("input");
			input.setAttribute("type", "hidden");
			input.setAttribute("name", "__joe-e__csrftoken");
			input.setAttribute("value", token);
			form.insertBefore(input, form.getFirstChild());
		}
	}
}
