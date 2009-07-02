package unittest.broken;

/*
 * This file defines a class and interface to be not easily locatable, as they
 * are "hidden" in an unexpected compilation unit.  This can cause problems
 * when analyzing classes that reference them.  The files
 * 
 * AnonymousExtendsSuperclass
 * AnonymousImplementsSuperclass
 * ImplementsSuperinterface
 * UsesSuperclass
 * 
 * should fail verification due to Joe-E violations but sometimes would pass
 * before a check was added to flag a verifier error when a supertype was
 * resolved to null unexpectedly.
 */

class Foo implements org.joe_e.Immutable {}

interface Foo2 extends org.joe_e.Immutable {}

class DefinesSuperclass {}