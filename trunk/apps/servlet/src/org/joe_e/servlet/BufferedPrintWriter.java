package org.joe_e.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * This class is just a wrapper around a ordinary PrintWriter, and it allows us
 * to recover the string that we wrote to the Writer. However, the semantics here
 * are different, in particular, no output is flushed until explicitly told to
 * do so by the ResponseFactoryWrapper. The class is used to provide a consistent
 * API with the servlet API, but while allowing us to run jslint on the response
 * to each request. 
 * @author akshay
 *
 */
public class BufferedPrintWriter extends PrintWriter {
	
	private String text = null;
	
	public BufferedPrintWriter(Writer out) {
		super(out);
	}

	public BufferedPrintWriter(OutputStream out) {
		super(out);
	}

	public BufferedPrintWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public BufferedPrintWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public BufferedPrintWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public BufferedPrintWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public BufferedPrintWriter(String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public BufferedPrintWriter(File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}
	
	/**
	 * we can't get errors
	 */
	public boolean checkError() {
		return false;
	}
	
	/**
	 * do nothing
	 */
	public void close() {	
	}

	/**
	 * For now do nothing. 
	 * TODO: How do we allow someone to not use jslint?
	 */
	public void flush() {
	    super.write(text);
	    super.flush();
	}
	
//	private void newLine() {
//		if (text == null) {
//			text = "\n";
//		} else {
//			text += "\n";
//		}
//	}
	
	/**
	 * do nothing
	 */
	protected void setError() {
	}
	
	public void write(char[] buf) {
		String s = new String(buf);
		if (text == null) {
			text = s;
		} else {
			text += s;
		}
		//		super.write(buf);
	}
	
	public void write(char[] buf, int off, int length) {
		String s = new String(buf, off, length);
		if (text == null) {
			text = s;
		} else {
			text += s;
		}
		//		super.write(buf, off, length);
	}
	
	public void write(int c) {
		char[] buf = new char[1];
		buf[0] = (char) c;
		write(buf);
		//		super.write(c);
	}
	
	public void write(String s) {
		if (text == null) {
			text = s;
		} else {
			text += s;
		}
		//		super.write(s);
	}
	
	public void write(String s, int off, int len) {
		if (text == null) {
			text = s.substring(off, off + len);
		} else {
			text += s.substring(off, off+len);
		}
		//		super.write(s, off, len);
	}
	
	public String getText() {
		return text;
	}
	
	public void clear() {
		text = null;
	}

}
