// Copyright 2007 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.safej;

import java.util.List;

public interface SafeJConsumer {
    public enum Kind {
        CONSTRUCTOR, METHOD, FIELD;
    }
    
    public class Member {       
        public final Kind kind;
        public final boolean allowed;
        public final String identifier;
        public final String comment;
        
        public Member(Kind kind, boolean allowed, String identifier, 
                      String comment) {
            this.kind = kind;
            this.allowed = allowed;
            this.identifier = identifier;
            this.comment = comment;
        }
    }
    
    void consumeClass(String className, String comment, List<String> honoraries,
                      List<Member> staticMembers, List<Member> instanceMembers);
    
    void disabledClass(String className, String comment);
}
