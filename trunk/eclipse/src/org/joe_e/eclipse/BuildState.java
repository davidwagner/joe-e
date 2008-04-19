// Copyright 2005-08 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.eclipse;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.ITypeBinding;

import java.util.*;

class BuildState {
    static final int IMPL_SELFLESS =   0x0001;
    static final int IMPL_IMMUTABLE =  0x0002;
    static final int IMPL_POWERLESS =  0x0004; 
	
    static final int IS_EQUATABLE =    0x0010;

    static final int UNVERIFIED = 0x8000;
    
    static boolean isSelfless(int tags) {
        return ((tags & IMPL_SELFLESS) != 0);
    }
    static boolean isImmutable(int tags) {
        return ((tags & IMPL_IMMUTABLE) != 0);
    }
    static boolean isPowerless(int tags) {
        return ((tags & IMPL_POWERLESS) != 0);
    }    
    
    /*
    static final int VERIFY_SELFLESS =   0x0100;
    static final int VERIFY_IMMUTABLE =  0x0200;
    static final int VERIFY_POWERLESS =  0x0400;
    */
    
    final IJavaProject ijp;
    final Map<IType, ITypeState> typeStates;
    final Map<ICompilationUnit, ICUState> icuStates;
    
	BuildState(IJavaProject ijp) {
		this.ijp = ijp;
        typeStates = new HashMap<IType, ITypeState>();
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
				ITypeState referencedState = typeStates.get(i);
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
	 * @param dependedOn the class depended on to implement some marker interface
	 *
	void addFlagDependency(ICompilationUnit current, IType dependedOn) {
		if (!dependedOn.isBinary()) {
			ITypeState dependedState = classStates.get(dependedOn);
			if (dependedState == null) {
				// create a node with an unitialized flags state
				dependedState = new ITypeState();
				classStates.put(dependedOn, dependedState);
			}
			dependedState.addFlagDependent(current);
			
			ICUState currentState = icuStates.get(current);
			currentState.references.add(dependedOn);
		}
	}
	*/
    
    private boolean isFromProject(ITypeBinding itb) {
        return itb.getJavaElement().getJavaProject().equals(ijp);
    }
    
    void addFlagDependency(ICompilationUnit current, ITypeBinding dependedOn) {
        if (dependedOn.isFromSource() && !dependedOn.isTypeVariable()) {
            IType type = (IType) dependedOn.getJavaElement();
            if (!type.getJavaProject().equals(ijp)) {
                return; // we don't handle cross-project dependencies yet
            }
            
            ITypeState dependedState = typeStates.get(type);
            if (dependedState == null) {
                // create a node with an unitialized flags state
                dependedState = new ITypeState();
                typeStates.put(type, dependedState);
            }
            dependedState.addFlagDependent(current);
            
            ICUState currentState = icuStates.get(current);
            currentState.references.add(type);
        }
    }
    
	/**
	 * Adds a deep (source based) dependency.  Such a dependency causes
	 * recompilation whenever the source is reverified.  Assumes that there is
	 * a valid ICUState object for the current compilation unit.  This can be
	 * ensured by calling prebuild() before calling this method. 
	 * 
	 * @param current the compilation unit to which to add the dependency
	 * @param dependedOn the class depended on to have some property
	 */
	void addDeepDependency(ICompilationUnit current, ITypeBinding dependedOn) {
		if (dependedOn.isFromSource() && !dependedOn.isTypeVariable()) {
            IType type = (IType) dependedOn.getJavaElement();
			ITypeState dependedState = typeStates.get(type);
			if (dependedState == null) {
				dependedState = new ITypeState();
				typeStates.put(type, dependedState);
			}
			dependedState.addDeepDependent(current);
			
			ICUState currentState = icuStates.get(current);
			currentState.references.add(type);
		}
	}	
	
    /**
     * Compute the set of compilation units that must be rebuilt given a new
     * set of tags for a newly-rebuilt type.
     * 
     * @param type
     *              the type that has just been rebuilt.
     * @param newTags
     *              the tags that have been computed for the type, reflecting
     *              its new version
     * @return
     *              the set of compilation units that must be rebuilt in
     *              response to the new tags
     */
	Collection<ICompilationUnit> updateTags(IType type, int newTags) {
		ITypeState typeState = typeStates.get(type);
        
		// if state node doesn't already exist, create a new one with flags
		// initialized
        if (typeState == null) {
			typeState = new ITypeState();
			typeStates.put(type, typeState);
        }
		
		int oldTags = typeState.tags;
		typeState.tags = newTags;

		if (oldTags < 0 || newTags == oldTags) { // Flags unchanged
			// Only rebuild deep dependents
			return typeState.deepDependents;
		} else { 
			return typeState.allDependents;
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
    
    public String toString() {
       StringBuilder b = new StringBuilder("typeStates:\n");
       for (IType type: typeStates.keySet()) {
           b.append("  " + type.getFullyQualifiedName() + " " 
                    + typeStates.get(type));
       }
       b.append("icuStates:\n");
       for (ICompilationUnit icu: icuStates.keySet()) {
           b.append("  " + icu.getElementName() + " " 
                    + icuStates.get(icu));
       }
       return b.toString();
    }
}
