package org.joe_e.testlib;

public enum EnumSomeEntriesDisabled {
    FOO("foo"), BAR(new java.io.File("/"));

    Object obj;
    
    EnumSomeEntriesDisabled(Object obj) {
        this.obj = obj;
    }
    
    Object getObject() {
        return obj;
    }
}
