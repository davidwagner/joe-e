package org.joe_e.servlet.response;

import java.io.*;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * This is our wrapper implementation of a HttpServletResponse. This exposes the DOM API
 * instead of the PrintWriter API of the default implementation. All other features
 * are just forwarded to the underlying HttpServletResponse object. 
 * @author akshay
 *
 */
public class ServletResponseWrapper implements HttpServletResponse {
	
	HttpServletResponse response;
	ResponseDocument doc;

    public ServletResponseWrapper(HttpServletResponse res) throws IOException, ParserConfigurationException {
		response = res;
		ResponseDocumentBuilderFactory dbfac = ResponseDocumentBuilderFactory.newInstance();
		ResponseDocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		doc = docBuilder.newDocument();
	}

	public void addCookie(Cookie arg0) {
		response.addCookie(arg0);
	}

	public void addDateHeader(String arg0, long arg1) {
		response.addDateHeader(arg0, arg1);
	}

	public void addHeader(String arg0, String arg1) {
		response.addHeader(arg0, arg1);
	}

	public void addIntHeader(String arg0, int arg1) {
		response.addIntHeader(arg0, arg1);
	}

	public boolean containsHeader(String arg0) {
		return response.containsHeader(arg0);
	}

	public String encodeRedirectURL(String arg0) {
		return response.encodeRedirectURL(arg0);
	}

	/**
	 * @deprecated
	 */
	public String encodeRedirectUrl(String arg0) {
		return response.encodeRedirectUrl(arg0);
	}

	public String encodeURL(String arg0) {
		return response.encodeURL(arg0);
	}

	/**
	 * @deprecated
	 */
	public String encodeUrl(String arg0) {
		return response.encodeUrl(arg0);
	}

	public void sendError(int arg0) throws IOException {
		response.sendError(arg0);
	}

	public void sendError(int arg0, String arg1) throws IOException {
		response.sendError(arg0, arg1);
	}

	public void sendRedirect(String arg0) throws IOException {
		response.sendRedirect(arg0);
	}

	public void setDateHeader(String arg0, long arg1) {
		response.setDateHeader(arg0, arg1);
	}

	public void setHeader(String arg0, String arg1) {
		response.setHeader(arg0, arg1);
	}

	public void setIntHeader(String arg0, int arg1) {
		response.setIntHeader(arg0, arg1);
	}

	public void setStatus(int arg0) {
		response.setStatus(arg0);
	}

	/**
	 * @deprecated
	 */
	public void setStatus(int arg0, String arg1) {
		response.setStatus(arg0, arg1);
	}

	/** 
	 * Instead of flushing the print writer, we convert the Document object
	 * to a String of xml, write that string to the response's writer and then
	 * flush the writer's buffer. 
	 */
	public void flushBuffer() throws IOException {
		try {
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			if (xmlString != null) {
				response.addHeader("Content-type", "text/html");
				response.getWriter().write(xmlString);
				response.flushBuffer();
			}
		} catch (TransformerConfigurationException e) {
			throw new IOException (e.getMessage());
		} catch (TransformerException e) {
			throw new IOException (e.getMessage());
		}
	}
	
	/**
	 * Get a Document object that implements the DOM API. 
	 * @return
	 */
    public ResponseDocument getDocument() {
    	return doc;
    }
	public int getBufferSize() {
		return response.getBufferSize();
	}

	public String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}

	public String getContentType() {
		return response.getContentType();
	}

	public Locale getLocale() {
		return response.getLocale();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

	public boolean isCommitted() {
		return response.isCommitted();
	}

	public void reset() {
		response.reset();
	}

	public void resetBuffer() {
		response.resetBuffer();
	}

	public void setBufferSize(int arg0) {
		response.setBufferSize(arg0);
	}

	public void setCharacterEncoding(String arg0) {
		response.setCharacterEncoding(arg0);
	}

	public void setContentLength(int arg0) {
		response.setContentLength(arg0);
	}

	public void setContentType(String arg0) {
		response.setContentType(arg0);
	}

	public void setLocale(Locale arg0) {
		response.setLocale(arg0);
	}

}
