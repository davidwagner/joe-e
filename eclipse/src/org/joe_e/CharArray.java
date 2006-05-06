package org.joe_e;

public class CharArray extends IncapableArray<Character> implements Incapable {
	char[] charArr;
	
	public CharArray(char... charArr) {
		super(new Character[]{});	
		// dummy array to make superclasses happy.  Could add protected safety-check bypassing
		// constructors as a minor optimization.
		this.charArr = charArr;
	}
	
	public char charAt(int pos) {
		return charArr[pos];
	}

	public int length() {
		return charArr.length;
	}

	public char[] toCharArray() {
		return charArr.clone();
	}
	
	public CharArray with(char newChar) {
		char[] newArr = new char[charArr.length + 1];
		System.arraycopy(charArr, 0, newArr, 0, charArr.length);
		newArr[charArr.length] = newChar;
		return new CharArray(newArr);
	}
	
	// Character versions of above methods required by inheritance
	
	public Character at(int pos) {
		return charArr[pos];
	}
	

	public Character[] toArray() {
		Character[] boxedArray = new Character[charArr.length];
		for (int i = 0; i < charArr.length; ++i) {
			boxedArray[i] = charArr[i];
		}
		return boxedArray;
	}
	
	public CharArray with(Character newChar) {
		return with(newChar.charValue());
	}
}
