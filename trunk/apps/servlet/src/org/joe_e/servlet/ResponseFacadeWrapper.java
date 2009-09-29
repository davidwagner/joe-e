package org.joe_e.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;

public class ResponseFacadeWrapper extends ResponseFacade {
	
	private BufferedPrintWriter writer = null;
	
	public ResponseFacadeWrapper(Response res) {
		super(res);
	}
	
	/**
	 * This is a huge hack. I'm using reflection to get a reference to the response object
	 * held within the ResponseFacade (implementation of HttpServletResponse). Also this
	 * now becomes specific of the servlet container which is bad.
	 * @param rf
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static ResponseFacadeWrapper getNewWrapper(ResponseFacade rf) throws NoSuchFieldException, IllegalAccessException  {
		Class a = rf.getClass();
		Field[] fs = a.getDeclaredFields();
		Field responseField = null;
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getName().equals("response")) {
				responseField = f;
			}
		}
		Response res = (Response) responseField.get(rf);
		return new ResponseFacadeWrapper(res);
	}
	
	public void setWriter(BufferedPrintWriter p) {
		writer = p;
	}
	
	public PrintWriter getWriter() {
		return writer;
	}
	
	public void flushOutput() throws IOException {
		super.getWriter().write(this.writer.getText());
		this.writer.clear();
	}
	
}
