// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * An immutable array of char.
 */

// BUG: Should extend DataArray<Character>
public class CharArray extends PowerlessArray<Character> {
	private final char[] charArr;

	/**
	 * Construct an immutable char array with a copy of an existing char array as
	 * backing store.
	 * 
	 * @param charArr the array to make an unmodifiable duplicate of
	 */
	public CharArray(char... charArr) {
	    // Unless I find a more clever way, making a Character[] copy is 
        // necessary for equals() to work properly.  This is a bit of a bummer,
        // as it removes some of the advantage of a char-backed array.
        // Note that the following code is *NOT* thread-safe (the boxed array
        // isn't made from the same clone as charArr), but it doesn't matter 
        // for Joe-E code.  To make this threadsafe, we would need to copy
        // the values back out of the boxed array.
        super(boxCharArray(charArr));
        
        this.charArr = charArr.clone();
   	}
    
    /**
     * Helper method to create a boxed Character array given a char array.
     * 
     * @param charArr
     * 
     * @return a new Character array whose contents are the same as charArr
     */
    private static Character[] boxCharArray(char[] charArr) {
        Character[] boxedArray = new Character[charArr.length];
        for (int i = 0; i < charArr.length; ++i) {
            boxedArray[i] = charArr[i];
        }
        return boxedArray;
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
     * Return a mutable copy of the char array
     * 
     * @return a mutable copy of the array
     */
	public char[] toCharArray() {
		return charArr.clone();
	}

	/*
	 *  Character versions of above methods required by inheritance
	 *  No longer needed
    
    /**
     * Return the length of the array
     * 
     * @return the length of the array
     *
    public int length() {
        return charArr.length;
    }

    
    /**
     * Return a Character containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Character containing the char at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     *
	public Character at(int pos) {
		return charArr[pos];
	}

    /**
     * Return a mutable Character array copy of the char array
     * 
     * @return a mutable Character array copy of the array
     *
	public Character[] toArray() {
		Character[] boxedArray = new Character[charArr.length];
		for (int i = 0; i < charArr.length; ++i) {
			boxedArray[i] = charArr[i];
		}
		return boxedArray;
	}
    
    */
    
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
