package test.verifier;

import org.joe_e.Powerless;

public class CustomException extends java.lang.Exception implements Powerless {
    java.lang.String f; // not final
}
