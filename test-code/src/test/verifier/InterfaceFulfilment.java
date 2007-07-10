package test.verifier;

public class InterfaceFulfilment<T> implements Comparable<T> {
    public int compareTo(Object o) {
        if (o instanceof Integer) {
            return 0;
        } else {
            return -42;
        }
    }
    
    public boolean equals(Object o) {
        return (o instanceof Integer);
    }
    
}
