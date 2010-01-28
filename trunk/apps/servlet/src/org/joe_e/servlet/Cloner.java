package org.joe_e.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * This class provides a method for deep-copying an object via
 * serialization. After ensuring that the object (and it's 
 * object graph) are Serializable, we can construct a deep
 * copy by first serializing the object and then deserializing it.
 * 
 * @see http://www.javaworld.com/javaworld/javatips/jw-javatip76.html
 * @author akshay
 * @deprecated
 *
 */
public class Cloner {

	private Cloner() {}
	
	public static Object deepCopy(Object o) throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		oos.flush();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ois = new ObjectInputStream(bis);
		Object ret = ois.readObject();
		ois.close();
		oos.close();
		return ret;
	}
	
	/**
	 * Walk the object graph reachable from o and check that
	 * each object implements the Serializable interface. returns
	 * true of this object can be successfully deep-copied using the 
	 * serialize/de-serialize technique 
	 * @deprecated we don't need this since serializing throws and exception
	 * if fields are not serializable. 
	 * 
	 * @param o
	 * @return boolean
	 */
	public static boolean isSerializeable(Object o) throws IllegalAccessException {
		Class<?>[] classes = o.getClass().getInterfaces();
		for (Class<?> cl : classes) {
			System.out.println(cl.getName());
			if (cl.getName().equals("java.io.Serializable")){
				System.out.println("hello");
				for (Field f : o.getClass().getFields()) {
					System.out.println(f.getName());
					if (f.isAccessible() && !f.isSynthetic() && !isSerializeable(f.get(o))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
