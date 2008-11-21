package test.library.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.joe_e.Token;
import org.joe_e.array.ConstArray;
import org.joe_e.reflect.Reflection;

public class Invocation {
    public int dummy = 5;
    
    public Invocation() {
        // dummy constructor
    }

    static class NotPublic {
        public NotPublic() {
            
        }
        
        public void foo() {
            
        }
        
        public int goo = 5;
    }
    
    public static void test() {
        // TODO: add tests of more cases that consult taming data
        
        // get()          
        try {
            Field publicField = Reflection.field(Invocation.class, "dummy");
            int value = (Integer) Reflection.get(publicField, new Invocation());
        } catch (NoSuchFieldException nsfe) {
            assert false;
        } catch (IllegalAccessException iae) {
            assert false;
        }
        
        try {
            Field notReallyPublic = 
                Reflection.field(NotPublic.class, "goo");
            int value = (Integer) Reflection.get(notReallyPublic, new NotPublic());
            assert false;
        } catch (NoSuchFieldException nsfe) {
            assert false;
        } catch (IllegalAccessException iae) { 
            
        }
                
        // set()
        try {
            Field publicField = Reflection.field(Invocation.class, "dummy");
            Reflection.set(publicField, new Invocation(), 7);
        } catch (NoSuchFieldException nsme) {
            assert false;
        } catch (IllegalAccessException iae) {
            assert false;
        }
        
        try {
            Field notReallyPublic = 
                Reflection.field(NotPublic.class, "goo");
            Reflection.set(notReallyPublic, new NotPublic(), 7);
            assert false;
        } catch (NoSuchFieldException nsme) {
            assert false;
        } catch (IllegalAccessException iae) { 
            
        }
        

        // construct()
        try {
            Constructor<Token> publicCtor = 
                Reflection.constructor(Token.class, new Class[] {});
            Token t = Reflection.construct(publicCtor, new Object[] {});
        } catch (Exception e) {
            assert false;
        }

        try {
            Constructor<NotPublic> notReallyPublic = 
                Reflection.constructor(NotPublic.class, new Class[] {});
            Reflection.construct(notReallyPublic, new Object[] {});
            assert false;
        } catch (NoSuchMethodException nsme) {
            assert false;
        } catch (IllegalAccessException iae) { 
            
        } catch (Exception e) {
            assert false;
        }
        
        // invoke()
        try {
            Method publicMethod = 
                Reflection.method(ConstArray.class, "length", new Class[] {});
            ConstArray<String> ca = ConstArray.array("high", "dry");
            int length = (Integer) Reflection.invoke(publicMethod, ca, new Object[] {});
        } catch (Exception e) {
            assert false;
        }
        
        try {
            Method notReallyPublic = 
                Reflection.method(NotPublic.class, "foo", new Class[] {});
            Reflection.invoke(notReallyPublic, new Object[] {});
            assert false;
        } catch (NoSuchMethodException nsme) {
            assert false;
        } catch (IllegalAccessException iae) { 
            
        } catch (Exception e) {
            assert false;
        }
    }
}