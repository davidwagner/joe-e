package test;

import org.joe_e.Token;
import org.joe_e.DeepFrozen;

public class ExtendsToken extends Token implements DeepFrozen {
	final int id;
	ExtendsToken(int id) {
		this.id = id;
	}
}
