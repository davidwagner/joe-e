package eventweb;

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;

// import org.joe_e.Incapable;

public class HTTPResponse {
	int code; // http status code
	
	byte[] headers;
	byte[] content;
	
	static String describe(int code) {
		switch(code) {
		case 200:
			return "OK";
		case 404:
			return "File not found.";
		default:
			return "Unknown";
		}
	}
	
	public HTTPResponse(int code, byte[] content) 
	{
		this.code = code;
		this.headers = new byte[]{};
		this.content = content;
	}
	
	public HTTPResponse(int code, CharSequence stringContent) throws CharacterCodingException
	{
		this.code = code;
		CharsetEncoder ce = Charset.forName("ISO-8859-1").newEncoder();
		CharBuffer cb = CharBuffer.wrap(stringContent);
		ByteBuffer out = ce.encode(cb);
		this.headers = new byte[]{};
		this.content = out.array();
	}
	
	public HTTPResponse(int code, CharSequence[] headers, CharSequence stringContent) throws CharacterCodingException
	{
		this.code = code;
		CharsetEncoder ce = Charset.forName("ISO-8859-1").newEncoder();
		CharBuffer headerCB = CharBuffer.wrap(headers);
		ByteBuffer headerBB = ce.encode(headerCB);
		this.headers = headerBB.array();
		CharBuffer cb = CharBuffer.wrap(stringContent);
		ByteBuffer out = ce.encode(cb);
		this.content = out.array();
	}	
}
