// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

public class CharArray extends PowerlessArray<Character> {
	private final char[] charArr;

	/**
	 * Construct an immutable char array with a copy of an existing char array as
	 * backing store.
	 * 
	 * @param charArr the array to make an unmodifiable duplicate of
	 */
	public CharArray(char... charArr) {
		// dummy array to make superclasses happy.  Could add protected safety-check-bypassing
		// constructors as a minor optimization.
		super(new Character[]{});	
		
		this.charArr = charArr.clone();
	}
    
    /**
     * Return the char located at a specified position
     * 
     * @param pos the position whose char to return
     * 
     * @return the char at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public char charAt(int pos) {
		return charArr[pos];
	}

    /**
     * Return the length of the array
     * 
     * @return the length of the array
     */
	public int length() {
		return charArr.length;
	}

    /**
     * Return a mutable copy of the char array
     * 
     * @return a mutable copy of the array
     */
	public char[] toCharArray() {
		return charArr.clone();
	}

	/*
	 *  Character versions of above methods required by inheritance
	 */	
    
    /**
     * Return a Character containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Character containing the char at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public Character at(int pos) {
		return charArr[pos];
	}

    /**
     * Return a mutable Character array copy of the char array
     * 
     * @return a mutable Character array copy of the array
     */
	public Character[] toArray() {
		Character[] boxedArray = new Character[charArr.length];
		for (int i = 0; i < charArr.length; ++i) {
			boxedArray[i] = charArr[i];
		}
		return boxedArray;
	}
    
    /*
     * NOT PART OF AN ENDORSED STABLE INTERFACE! ... (yet)
     * 
     * Return a new CharArray containing a specified additional char
     * 
     * @return a new CharArray containing a specified additional char
     */
    public CharArray with(char newChar) {
        char[] newArr = new char[charArr.length + 1];
        System.arraycopy(charArr, 0, newArr, 0, charArr.length);
        newArr[charArr.length] = newChar;
        return new CharArray(newArr);
    }
    
    /*
     * NOT PART OF AN ENDORSED STABLE INTERFACE! ... (yet)
     * 
     * Return a new CharArray containing a specified additional Character
     * 
     * @return a new CharArray containing a specified additional Character
     */
	public CharArray with(Character newChar) {
		return with(newChar.charValue());
	}
}
