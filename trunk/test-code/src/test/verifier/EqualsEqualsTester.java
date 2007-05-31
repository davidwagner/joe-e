package test.verifier;

import org.joe_e.Token;


public class EqualsEqualsTester { 
    static void foo() {
	// primitive typed expressions are OK
	if (3 == 4);
	if (3 != 4);
	if (3.0 != 3.4);
	if (3.0 == 3.4);
	if (true == false);
	if (false != true);
	if ('c' != 'd');
	if ('e' == 34);
	if (3 == 3.0);
	if (0 != -0.0);
	if (0xdeadbeef != 45f * Math.sqrt(3.4));
	// comparing with null is also OK
	if (null == new ExtendsToken(0));
	if (null != new ExtendsToken(56));
	if (null == new String());
	if (null != new String());
	if (null == new IsIncapable(34));
	if (null != new IsIncapable(12));
	if (null == new Integer(34));
	if (null != new Integer(12));
	if (new ExtendsToken(0)  == null);
	if (new ExtendsToken(56) != null);
	if (new String()         == null);
	if (new String()         != null);
	if (new IsIncapable(34)  == null);
	if (new IsIncapable(12)  != null);
	if (new Integer(34)      == null);
	if (new Integer(12)      != null);
	
	// Tokens are OK to compare
	if (new ExtendsToken(32) == new ExtendsToken(32));
	if (new ExtendsToken(32) != new ExtendsToken(32));
	if (new Token() != new ExtendsToken(32));
	if (new Token() == new ExtendsToken(32));
	if (new ExtendsToken(32) != new Token());
	if (new ExtendsToken(32) == new Token());
	if (new Token() == new Token());
	if (new Token() != new Token());
	// How to handle these? It should be safe because we can always
	// rewrite a == b (a is Token, b is Object) as
	// b instanceof Token ? false : a == (Token) b
	if (new Token() == new Object());
	if (new Object() == new Token());
	if (new ExtendsToken(23) == new Object());
	if (new Object() != new ExtendsToken(23));
	
	// Other stuff isn't
	if (new String()         == new String());
	if (new String()         != new String());
	if (new IsIncapable(34)  == new IsIncapable(34));
	if (new IsIncapable(34)  != new IsIncapable(34));
	if (new Integer(34)      == new Integer(34));
	if (new Integer(34)      != new Integer(34));
	if (new Integer(34)      == new Object());
	if (new Object()         != new IsIncapable(44));
	
	// test autoboxing: these should be OK
	if (34 == new Integer(34));
	if (34 != new Integer(34));
	if (new Integer(34)      == 34);
	if (new Integer(34)      != 34);
    }
}

