package test;

public class Superclass {
    void foo(String s) {
        System.out.println("Superclass.foo(String) called.");
    }
    
    void foo(Object o) {
        System.out.println("Superclass.foo(Object) called.");
    }
}
