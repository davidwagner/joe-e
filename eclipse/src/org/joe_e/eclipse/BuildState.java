// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ICompilationUnit;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

class BuildState {
	
	final Map<IType, ITypeState> classStates;
	final Map<ICompilationUnit, ICUState> icuStates;
	
	BuildState() {
		classStates = new HashMap<IType, ITypeState>();
		icuStates = new HashMap<ICompilationUnit, ICUState>();
	}
	
	/**
	 * Resets all outgoing dependencies for a compilation unit, and assigns a new
	 * ICUState to the unit.  Called before calculating a new set of dependencies
	 * for a new build.
	 */
	void prebuild(ICompilationUnit toRebuild) {
		ICUState oldState = icuStates.get(toRebuild);
		if (oldState != null) {
			Set<IType> typesReferenced = oldState.references;
			
			for (IType i : typesReferenced) {
				ITypeState referencedState = classStates.get(i);
				referencedState.resetDependencies(toRebuild);
			}
		}

		icuStates.put(toRebuild, new ICUState());
	}
    
	/**
	 * Adds a flag (marker interface) based dependency.  Such a dependency only
	 * causes recompilation if the flags change.  Assumes that there is
	 * a valid ICUState object for the current compilation unit.  This can be
	 * ensured by calling prebuild() before calling this method. 
	 * 
	 * @param current the compilation unit to which to add the dependency
	 * @param dependedOn the class depended on to implement a marker interface
	 */
	void addFlagDependency(ICompilationUnit current, IType dependedOn) {
		if (!dependedOn.isBinary()) {
			ITypeState dependedState = classStates.get(dependedOn);
			if (dependedState == null) {
				// create a node with an unitialized flags state
				dependedState = new ITypeState(-1);
				classStates.put(dependedOn, dependedState);
			}
			dependedState.addFlagDependent(current);
			
			ICUState currentState = icuStates.get(current);
			currentState.references.add(dependedOn);
		}
	}
	
	/**
	 * Adds a deep (source based) dependency.  Such a dependency causes
	 * recompilation whenever the source changes.  Assumes that there is
	 * a valid ICUState object for the current compilation unit.  This can be
	 * ensured by calling prebuild() before calling this method. 
	 * 
	 * @param current the compilation unit to which to add the dependency
	 * @param dependedOn the class depended on to implement a marker interface
	 */
	void addDeepDependency(ICompilationUnit current, IType dependedOn) {
		if (!dependedOn.isBinary()) {
			ITypeState dependedState = classStates.get(dependedOn);
			if (dependedState == null) {
				// create a node with an unitialized flags state
				dependedState = new ITypeState(-1);
				classStates.put(dependedOn, dependedState);
			}
			dependedState.addDeepDependent(current);
			
			ICUState currentState = icuStates.get(current);
			currentState.references.add(dependedOn);
		}
	}	
	
	
	/*
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
