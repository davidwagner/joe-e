package org.joe_e.testlib;

import org.joe_e.Token;
import org.joe_e.array.ImmutableArray;

public class ContainsImmutable {
    // An arbitrary-dimension point class
    public final Token tok;
    public final ImmutableArray<Token> iat = null;
    final int id;
    final String name;
    
    public ContainsImmutable(Token tok, String name, int id) {
        this.tok = tok;
        this.name = name;
        this.id = id;
    }    
}
