package eventweb;

//
// Used to contain server-local state and references.
//

public interface Service {
	ServiceSession getSession(String user);
}
