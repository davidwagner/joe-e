package test;

public class Subclass extends Superclass {
    int a;
    
    Subclass() {
        super();
    }
    
    void foo(Object o) {
        System.out.println("Subclass.foo(Object) called");
    }
}
