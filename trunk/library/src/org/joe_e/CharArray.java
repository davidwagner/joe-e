// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

public class CharArray extends PowerlessArray<Character> implements Powerless {
	private final char[] charArr;

	/**
	 * Constructs an immutable char array with a copy of an existing char array as
	 * backing store.
	 * 
	 * @param charArr the array to make an unmodifiable duplicate of
	 */
	public CharArray(char... charArr) {
		// dummy array to make superclasses happy.  Could add protected safety-check bypassing
		// constructors as a minor optimization.
		super(new Character[]{});	
		
		this.charArr = charArr.clone();
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
	
	/*
	 *  Character versions of above methods required by inheritance
	 */	
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
