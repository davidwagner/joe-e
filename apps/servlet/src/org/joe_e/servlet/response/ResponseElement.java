package org.joe_e.servlet.response;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import java.util.regex.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

/**
 * We have our own implementation of <code>Element</code> which prevents
 * the application for using certain attributes, and additionally checks
 * that urls are safe (TODO: this isn't done well).  
 * @author akshay
 *
 */
public class ResponseElement extends ElementImpl implements Element, TypeInfo {

	// The list of allowed attributes. 
	private static final String[] allowedAttributes = {"abbr", "accept", 
		"accept-charset", "accesskey", "action", "align", "alt", "axis", "border",
		"cellpadding", "cellspacing", "char", "charoff", "charset", "checked",
		"cite", "class", "clear", "cols", "colspan", "color", "compact", "coords",
		"datetime", "dir", "disabled", "enctype", "for", "frame", "headers",
		"height", "hreflang", "hspace", "id", "ismap", "label", "lang",
		"longdesc", "maxlength", "media", "method", "multiple", "name", "nohref",
		"noshade", "nowrap", "prompt", "readonly", "rel", "rev", "rows",
		"rowspan", "rules", "scope", "selected", "shape", "size", "span",
		"start", "summary", "tabindex", "target", "title", "type", "usemap",
		"valign", "value", "vspace", "width"};
	
	// places where content can be linked in
	private static final String[] linkAttributes = {"src", "href"};
	
	// illegal style formats. 
	private static final Pattern invalidStyleValue = Pattern.compile(
			"("+cssContentsRegexp("javascript") + ")|(" + 
			cssContentsRegexp("expression")+")", Pattern.CASE_INSENSITIVE);
		
	/**
	 * Construct a ResponseElement. 
	 * @param d
	 * @param e
	 */
	public ResponseElement(CoreDocumentImpl d, Element e) {
		super(d, e.getNodeName());
	}
	
	public Node appendChild(Node n) {
		return super.appendChild(n);
	}
	
	/**
	 * To set an attribute, check that the <code>attName</code> is in <code>allowedAttributes</code>
	 * Link attributes cannot be added using this method, use <code>addLinkAttribute</code>. Additionally make
	 * sure that no element can have type="text/javascript" and that style tags do not
	 * contain any script. 
	 */
	public void setAttribute(String attName, String value) {
		for (int i = 0; i < allowedAttributes.length; i++) {
			if (allowedAttributes[i].equals(attName)) {
				if (attName.equals("type") && value.length() >= 15 && value.substring(0, 15).equals("text/javascript")) {
					throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal type attribute value: " + value);
				}
				if (attName.equals("style")) {
					if (checkStyle (value))
						break;
					else {
						throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal style attribute value: " + value);
					}
				}
				super.setAttribute(attName, value);
				return;
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal attribute: " + attName);
	}
	
	public void addLinkAttribute(String attName, ResponseUrl value) {
		for (int i = 0; i < linkAttributes.length; i++) {
			if (attName.equals(linkAttributes[i])) {
				super.setAttribute(attName, value.getURL());
				return;
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal link attribute: " + attName);
	}
	
	/**
	 * Check that the style attribute doesn't contain script. 
	 * @param value
	 * @return
	 */
	public boolean checkStyle (String value) {
		return invalidStyleValue.matcher(value).find() ? false : true;
	}
	
	
	/**
	 * Taken from DeXSS
	 */
	private static String cssContentsRegexp(String literal) {
		final String cruft = "(\\s|(/\\*.*\\*/)+)*";
		StringBuffer buf = new StringBuffer(literal.length() * (cruft.length() + 1));
		for (int i = 0; i < literal.length(); i++) {
			buf.append(literal.charAt(i));
			if (i < literal.length()-1)
				buf.append(cruft);       // whitespace or CSS /* */ comments between letters                                                                                                                                 
		}
		return buf.toString();
	}


}