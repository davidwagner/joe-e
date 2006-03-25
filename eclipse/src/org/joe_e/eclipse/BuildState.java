// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ISourceRange;

import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;

class BuildState {
	// contains list of dependencies and initializer invocations in order to detect
	// evil, nasty initialization cycles
	class InitializerRef {
		final IType target;
		final ISourceRange location;  // or ints, start and length?
		InitializerRef(IType target, ISourceRange location) {
			this.target = target;
			this.location = location;
		}
	}
	
	HashMap<IType, LinkedList<InitializerRef>> init;
	HashMap<IType, HashSet<IType>> ref;
	
	void clear(IType source) {
		init.get(source).clear();
		ref.get(source).clear();
	}
	
	void addInit(IType source, IType target, ISourceRange location) {
		init.get(source).add(new InitializerRef(target, location));
	}
	
	void addRef(IType source, IType target) {
		ref.get(source).add(target);
	}
	
	LinkedList<Problem> findCircularity() {
		LinkedList<Problem> problems = new LinkedList<Problem>();
		for (IType source : init.keySet()) {
			for (InitializerRef initRef : init.get(source)) {
				LinkedList<IType> path = doBFS(initRef.target, source);
				if (path != null) {
					problems.add(new Problem("", initRef.location));
				}
			}
		}
		
		return problems;
	}
	
	LinkedList<IType> doBFS(IType source, IType target) {
		
		return new LinkedList<IType>();
	}
	
	/*
	 * If more types of edges are added, I may want these
	 
	class Edge {
		final EdgeType type;
		final IType source;
		final IType target;
		
		Edge(EdgeType type, IType source, IType target) {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	enum EdgeType {
		INIT, REF;
	}
	*/
}
