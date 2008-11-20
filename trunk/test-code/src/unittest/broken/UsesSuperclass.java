package unittest.broken;

/*
 * This is broken nondeterministically; apparently depending on a race condition
 * bug with the indexer thread.  The current code aims to ensure that when the
 * bug happens, the verification fails with an error instead of certifying
 * potentially invalid code.
 */
class UsesSuperclass extends Foo {
	int f;
}