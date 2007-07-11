package test.verifier;

import org.joe_e.Powerless;
import org.joe_e.Struct;

public final class
LocalClassRelevance extends Struct implements Powerless {  
    void f() {
        class A {
            void party() {
                final int[] foo = {0};
                class B {
                    void up() {
                        foo[0]++;
                    }
                }
                new B();
            }
        }        
        new A();
    } 
}

