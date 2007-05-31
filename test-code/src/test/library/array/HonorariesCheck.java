package test.library.array;

import org.joe_e.array.*;
import org.joe_e.Token;
import org.joe_e.Struct;
import org.joe_e.Powerless;

public class HonorariesCheck {
    public static void test() {
        try {
            ImmutableArray<Object> iao = ImmutableArray.array(new Object());
            assert false;
        } catch(ClassCastException cce) { }
        
        try {
            PowerlessArray<Token> pao = PowerlessArray.array(new Token());
            assert false;
        } catch(ClassCastException cce) { }
        
        try {
            ImmutableArray<char[]> iao = ImmutableArray.array(new char[]{'a', 'b'});
            assert false;
        } catch(ClassCastException cce) { }
            
        class FooStruct extends Struct implements Powerless {}
        class BooStruct extends Struct {}
        
        try {
            PowerlessArray<Struct> iao = PowerlessArray.array((Struct[]) new BooStruct[] {new BooStruct()});
            assert false;
        } catch(ClassCastException cce) { }
        
        PowerlessArray<Struct> iao = PowerlessArray.array((Struct[]) new FooStruct[] {new FooStruct()});
        try {
            iao = iao.with(new BooStruct());
            assert false;
        } catch(ClassCastException cce) { }     
        
        PowerlessArray<Object> pao = PowerlessArray.array((Object[]) new String[]{"hello"});
        try {
            pao = pao.with(new Object());
            assert false;
        } catch(ClassCastException cce) {}
        
        try {
            pao = pao.with(ImmutableArray.array(new Token()));
            assert false;
        } catch(ClassCastException cce) {}
        
        ImmutableArray<Token> iat = ImmutableArray.array(new Token());
        try {
            iat = ((ImmutableArray) iat).with(new Object());
            assert false;
        } catch(ClassCastException cce) {}
    }
}
