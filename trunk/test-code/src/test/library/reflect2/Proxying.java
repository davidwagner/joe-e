package test.library.reflect2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.joe_e.Powerless;
import org.joe_e.Immutable;
import org.joe_e.Equatable;
import org.joe_e.Selfless;
import org.joe_e.reflect.Proxies;
import org.joe_e.reflect.Reflection;

public class Proxying {
    static class Foo implements InvocationHandler, Powerless {
        public Object invoke(Object thiz, Method m, Object[] os) throws Throwable {              
            return null;
        }
    }

    static class Bar implements InvocationHandler, Immutable {
        public Object invoke(Object thiz, Method m, Object[] os) throws Throwable {              
            return null;
        }
    }
    
    static class Baz implements InvocationHandler {
        boolean state;
        
        public Object invoke(Object thiz, Method m, Object[] os) throws Throwable {
            state = !state;
            return state;
        }
    }
    
    public static void test() {     
        // Runnable
        Runnable runnable = (Runnable) Proxies.proxy(new Foo(), new Class[] {Runnable.class});
           
        // Immutable
        Immutable imm1 = (Immutable) Proxies.proxy(new Foo(), new Class[] {Immutable.class});
        Immutable imm2 = (Immutable) Proxies.proxy(new Bar(), new Class[] {Immutable.class});      
        
        try {
           Immutable imm3 = (Immutable) Proxies.proxy(new Baz(), new Class[] {Immutable.class});
           assert false;
        } catch (ClassCastException cce) {
            
        }
           
        // Powerless
        Powerless pow1 = (Powerless) Proxies.proxy(new Foo(), new Class[] {Powerless.class});
        
        try {
           Powerless pow2 = (Powerless) Proxies.proxy(new Bar(), new Class[] {Powerless.class});      
           assert false;
        } catch (ClassCastException cce) {
            
        }
        
        try {
           Powerless pow3 = (Powerless) Proxies.proxy(new Baz(), new Class[] {Powerless.class,
                                                                              Runnable.class});
           assert false;
        } catch (ClassCastException cce) {
            
        }      
        
        // Equatable, Selfless
        Equatable equatable = (Equatable) Proxies.proxy(new Baz(), new Class[] {Equatable.class, Runnable.class});
        Selfless sefless = (Selfless) Proxies.proxy(new Foo(), new Class[] {Selfless.class, Powerless.class});
        try {
            Object both = Proxies.proxy(new Baz(), new Class[] {Equatable.class, Selfless.class});
            assert false;
        } catch (ClassCastException cce) {
            
        }  
        
        // Try to cheat by calling Constructor for a Proxy type directly via
        // reflection.  Doing so can allow one to specify an InvocationHandler
        // that does not meet the specification of the associated marker
        // interfaces.
        try {
            Constructor c = Reflection.constructor(pow1.getClass(), 
                                                   InvocationHandler.class);
            Reflection.construct(c, new Bar());
            // One or the other of the above should fail to prevent unsafety
            assert false;
        } catch (NoSuchMethodException nsme) {
            
        } catch (IllegalAccessException iae) {
            
        } catch (Exception e) {
            assert false;
        }
    }
}
