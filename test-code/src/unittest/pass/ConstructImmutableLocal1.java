package unittest.pass;

import org.joe_e.Immutable;
import org.joe_e.array.ImmutableArray;

public class ConstructImmutableLocal1 {
	static void foop() {
		final ImmutableArray<Integer> garbage = ImmutableArray.array(1, 2, 3);
		class Gorbachev {
			public String toString() {
				return garbage.get(0).toString();
			}
		}
		
		class Mikhail implements Immutable {
			{
				new Gorbachev();
			}
		}
	}
}
