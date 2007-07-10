package unittest.pass;

import org.joe_e.testlib.ContainsImmutable;

public class ImmutableInheritsFields1 extends ContainsImmutable 
										implements org.joe_e.Immutable {
	ImmutableInheritsFields1(int id, String name) {
		super(new org.joe_e.Token(), name, id);
	}
	
	final Exception e = null;
	final String s = null;
}
