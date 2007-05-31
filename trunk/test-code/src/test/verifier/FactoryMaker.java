package test.verifier;

import org.joe_e.Immutable;
import org.joe_e.Token;

interface Product { 
	int getValue();
	void take(Product x);
}
interface Factory extends Immutable {
	Product create();
}

public class FactoryMaker {
    static Factory
    make(final Token brand) {
        class ProductX implements Product {
            private int value;
            public Token getBrand() { return brand; }
            public int getValue() { return value; }
            public void take(Product x) {
            	ProductX xx = (ProductX) x;
                if (brand != xx.getBrand()) { 
                	throw new IllegalArgumentException(); 
                }
                value += xx.value;
                xx.value = 0;
            }
        }
        class FactoryX implements Factory {
            public Product create() { return new ProductX(); }
        }
        return new FactoryX();
    }
}
