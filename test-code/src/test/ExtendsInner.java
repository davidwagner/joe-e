package test;

/*
 * TEST: extend a static inner class; inherit permissions of that class.
 * Pass.
 */

public class ExtendsInner extends HasInner.StaticInner {
	int foo;  // not final
}
