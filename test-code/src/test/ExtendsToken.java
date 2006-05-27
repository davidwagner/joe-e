package test;

import org.joe_e.Token;
import org.joe_e.Immutable;

public class ExtendsToken extends Token implements Immutable {
	final int id;
	ExtendsToken(int id) {
		this.id = id;
	}
}
