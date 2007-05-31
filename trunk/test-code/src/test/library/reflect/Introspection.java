package test.library.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.joe_e.Token;
import org.joe_e.array.ConstArray;
import org.joe_e.array.PowerlessArray;
import org.joe_e.reflect.Reflection;

public class Introspection {
    public int dummy = 5;
    
    public Introspection() {
        // dummy constructor
    }

    public static void test() {
        // field() and fields()
        try {
            Field secret = 
                Reflection.field(ConstArray.class, "arr");
            assert false;
        } catch (NoSuchFieldException nsme) {
            
        }
        
        Field publicField = null;
        try {
            publicField = Reflection.field(Introspection.class, "dummy");
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
            Constructor secret = 
                Reflection.constructor(ConstArray.class,
                                       new Class[] {Object[].class});
            assert false;
        } catch (NoSuchMethodException nsme) {
            
        }
        
        Constructor publicCtor = null;
        try {
            publicCtor = Reflection.constructor(Token.class, new Class[]{});
        } catch (NoSuchMethodException nsme) {
            assert false;
        }
    
        PowerlessArray<Constructor> constArrayCtors = 
            Reflection.constructors(ConstArray.class);
        assert constArrayFields.length() == 0;
        
        PowerlessArray<Constructor> thisClassCtors = 
            Reflection.constructors(Introspection.class);
        assert thisClassCtors.length() == 1;
        assert thisClassCtors.get(0).getParameterTypes().length == 0;
        
        // method() and methods()
        try {
            Method secret =
                Reflection.method(ConstArray.class, "readObject", 
                              new Class[] {java.io.ObjectInputStream.class});
            assert false;
        } catch (NoSuchMethodException nsme) {
            
        }
        
        Method publicMethod = null;
        try {
            publicMethod = Reflection.method(ConstArray.class, "length",
                                             new Class[] {});
        } catch (NoSuchMethodException nsme) {
            assert false;
        }        
    }

}
