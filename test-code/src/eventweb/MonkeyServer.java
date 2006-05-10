package eventweb;

//
// has only per-HTTP session state -- no server-global or per-user state.
//

public class MonkeyServer implements Service {
	public MonkeySession getSession(String user) {
		return new MonkeySession();
	}	
}
