package test.library.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.joe_e.Token;
import org.joe_e.array.ConstArray;
import org.joe_e.array.PowerlessArray;
import org.joe_e.reflect.Reflection;

/*
 * This class is not Joe-E verifiable since it uses java.io.ObjectOutputStream.class
 */
public class Introspection {
    public int dummy = 5;
    
    public Introspection() {
        // dummy constructor
    }

    public static void test() {
        // field() and fields()
        try {
            Reflection.field(ConstArray.class, "arr");
            assert false;
        } catch (NoSuchFieldException nsme) {
            
        }
        
        
        try {
            Reflection.field(Introspection.class, "dummy");
        } catch (NoSuchFieldException nsme) {
            assert false;
        }
                
        PowerlessArray<Field> constArrayFields = 
            Reflection.fields(ConstArray.class);
        assert constArrayFields.length() == 0;
        
        PowerlessArray<Field> thisClassFields = 
            Reflection.fields(Introspection.class);
        assert thisClassFields.length() == 1;
        assert thisClassFields.get(0).getName().equals("dummy");
        
        // constructor() and constructors()
        try {
            Reflection.constructor(ConstArray.class,
                                       new Class[] {Object[].class});
            assert false;
        } catch (NoSuchMethodException nsme) {
            
        }       
        
        try {
            Reflection.constructor(Token.class, new Class[]{});
        } catch (NoSuchMethodException nsme) {
            assert false;
        }
        
        try {
            Reflection.constructor("foo".getClass(), new Class[]{});
        } catch (NoSuchMethodException nsme) {
            assert false;
        }
    
        PowerlessArray<Constructor<?>> constArrayCtors = 
            Reflection.constructors(ConstArray.array("a", "b").getClass());
        assert constArrayCtors.length() == 0;
        
        PowerlessArray<Constructor<?>> thisClassCtors = 
            Reflection.constructors(Introspection.class);
        assert thisClassCtors.length() == 1;
        assert thisClassCtors.get(0).getParameterTypes().length == 0;
        
        // method() and methods()
        try {
            Reflection.method(ConstArray.class, "readObject", 
                              new Class[] {java.io.ObjectInputStream.class});
            assert false;
        } catch (NoSuchMethodException nsme) {
            
        }
        
        try {
            Reflection.method(ConstArray.class, "length",
                                             new Class[] {});
        } catch (NoSuchMethodException nsme) {
            assert false;
        }        
    }

}
