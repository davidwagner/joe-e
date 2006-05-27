package test;
import org.joe_e.Powerless;

public class StrawMan implements Powerless {
	private final String contents;
	
	public StrawMan(String contents) {
		this.contents = contents;
	}
	
	public String toString() {
		return contents;
	}	
}
