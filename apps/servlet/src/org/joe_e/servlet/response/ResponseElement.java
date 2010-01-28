package org.joe_e.servlet.response;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import java.util.regex.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
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
		"height", "href", "hreflang", "hspace", "id", "ismap", "label", "lang",
		"longdesc", "maxlength", "media", "method", "multiple", "name", "nohref",
		"noshade", "nowrap", "prompt", "readonly", "rel", "rev", "rows",
		"rowspan", "rules", "scope", "selected", "shape", "size", "span", "src",
		"start", "summary", "tabindex", "target", "title", "type", "usemap",
		"valign", "value", "vspace", "width"};
	
	// places where content can be linked in
	private static final String[] linkAttributes = {"src", "href"};
	
	// illegal link formats
	private static final Pattern invalidUrl = Pattern.compile(
			"(^\\s*j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:)|" + // javascript
			"(^\\s*v\\s*i\\s*e\\s*w\\s*-s\\s*o\\s*u\\s*r\\s*c\\s*e\\s*:)|" + // view-source
			"(^\\s*d\\s*a\\s*t\\s*a\\s*:)|" +                               // data
			"(^\\s*v\\s*b\\s*s\\s*s\\s*r\\s*i\\s*p\\s*t\\s*:)|" +           // vbscript
			"(^\\s*a\\s*b\\s*o\\s*u\\s*t\\s*:)|" +                          // about
			"(^\\s*s\\s*h\\s*e\\s*l\\s*l\\s*:)", Pattern.CASE_INSENSITIVE); // shell
	
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
	
	/**
	 * To set an attribute, check that the <code>attName</code> is in <code>allowedAttributes</code>
	 * Then if this is a link attribute, check that the link is legal. Additionally make
	 * sure that no element can have type="text/javascript" and that style tags do not
	 * contain any script. 
	 */
	public void setAttribute(String attName, String value) {
		for (int i = 0; i < allowedAttributes.length; i++) {
			if (allowedAttributes[i].equals(attName)) {
				for (int j = 0; j < linkAttributes.length; j++) {
					if (linkAttributes[j].equals(attName)) {
						if (checkLink (value))
							break;
						else {
							throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal link: " + value);
						}
					}
				}
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

	/**
	 * for now we only allow fully qualified urls beginning with http://.
	 * TODO: this is really bad, how do we allow for relative urls?
	 * @param value
	 * @return
	 */
	public boolean checkLink (String value) {
		return invalidUrl.matcher(value).find() ? false : true;
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