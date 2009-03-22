package org.joe_e.servlet.test;

import java.io.IOException;
import java.util.ArrayList;

import org.joe_e.servlet.Cloner;

import junit.framework.TestCase;

public class DeepCopy extends TestCase {

	public class Unserializeable {
		String s;
		public Unserializeable (String st) {
			this.s = st;
		}
	}
	
/*	public void testIsSerializeable() {
		try {
			String s = "hello";
			assertTrue(Cloner.isSerializeable(s));
			Unserializeable u = new Unserializeable("blah");
			assertFalse(Cloner.isSerializeable(u));
			ArrayList<Unserializeable> list = new ArrayList<Unserializeable> ();
			list.add(u);
			list.add(new Unserializeable("hello"));
			assertFalse(Cloner.isSerializeable(list));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}
	*/
	
	public void testDeepCopy() {
		try {
			String s = "hello";
			String t = (String) Cloner.deepCopy(s);
			assertTrue(s.equals(t));
			assertFalse(s == t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ArrayList<Unserializeable> list = new ArrayList<Unserializeable> ();
			list.add(new Unserializeable("blah"));
			list.add(new Unserializeable("hello"));
			ArrayList<Unserializeable> l2 = (ArrayList<Unserializeable>) Cloner.deepCopy(list);
			assertFalse(l2 == list);
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(true);
		} catch (ClassNotFoundException e) {
			assertTrue(true);
		}
		
		
	}
}
