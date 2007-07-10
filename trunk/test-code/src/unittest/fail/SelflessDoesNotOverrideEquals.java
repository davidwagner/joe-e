package unittest.fail;

public class SelflessDoesNotOverrideEquals implements org.joe_e.Selfless {
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(SelflessDoesNotOverrideEquals sdnoe) {
		return true;
	}
}
