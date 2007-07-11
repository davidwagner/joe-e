package test.verifier;

import org.joe_e.Powerless;
import org.joe_e.Struct;

// This caused a bug for Tyler: the incriminating local variable relevance
// check had to be generalized.  (definedOutside() rather than ==)
public final class
Factory2 extends Struct implements Powerless {

    interface F {
        void
        run(Struct a);
    }

    public F
    build(final Factory2 next) {
        return new F() {
            public void
            run(final Struct a) {
                new Runnable() {
                    public void
                    run() { a.equals(a); }
                };
            }
        };
    }
}
