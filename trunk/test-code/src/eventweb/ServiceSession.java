package eventweb;

import java.io.PrintStream;

//
// Used to contain session-local state; this may include user local state for authenticated sessions
//

public interface ServiceSession {
	public HTTPResponse serve(String fileName, PrintStream debugOut);
}
