package test.verifier;

import org.joe_e.Powerless;

public class ExtendsHonoraryPowerless extends Exception {
	  // error : doesn't redeclare Powerless
	int foo; // if it did, this would be an error
}
