package org.joe_e.testlib;

public enum DisabledEnum {
    BAZ(System.out), QUX(new java.io.File("/"));

    Object obj;
    
    DisabledEnum(Object obj) {
        this.obj = obj;
    }
    
    Object getObject() {
        return obj;
    }
}
