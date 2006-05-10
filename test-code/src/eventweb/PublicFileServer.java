package eventweb;

import java.io.File;

public class PublicFileServer implements Service {
	File base;

	PublicFileServer(File base) {
		this.base = base;
	}
	
	public PublicFileSession getSession(String user) {
		return new PublicFileSession(base);
	}
}
