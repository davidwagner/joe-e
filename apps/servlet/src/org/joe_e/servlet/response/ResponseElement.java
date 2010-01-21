package org.joe_e.servlet.response;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class ResponseElement extends ElementImpl implements Element, TypeInfo {

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
	
	public ResponseElement(CoreDocumentImpl d, Element e) {
		super(d, e.getNodeName());
	}
	
	public void setAttribute(String attName, String value) {
		for (int i = 0; i < allowedAttributes.length; i++) {
			if (allowedAttributes[i].equals(attName)) {
				super.setAttribute(attName, value);
				return;
			}
		}
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Illegal attribute");
	}
}