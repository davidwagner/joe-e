package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonImmutableField2 implements Powerless, Equatable {
	a, b, c {
		final int[] nums = new int[]{0, 1};
		public int[] getNums() {
			return nums;
		}
	};
	
	public int[] getNums() {
		return new int[]{1, 2};
	}
}
