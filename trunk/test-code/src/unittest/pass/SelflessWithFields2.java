package unittest.pass;

import org.joe_e.*;
import java.io.File;

public class SelflessWithFields2 {
	static void f() {
		new Selfless() {
			class B {}
			
			final int f = 4;
			final String s = "";
			final File[] fs = new File[]{null, null};
			
			public boolean equals(Object o) {
				return false;
			}
			
			public int hashCode() {
				return f + s.hashCode() + fs.length;
			}
		};
	}
}
