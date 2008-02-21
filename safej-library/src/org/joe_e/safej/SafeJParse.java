// Copyright 2007-08 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.safej;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import org.joe_e.safej.SafeJConsumer.Kind;
import org.joe_e.safej.SafeJConsumer.Member;

import static java.io.StreamTokenizer.TT_WORD;

public class SafeJParse {   
    static class ParseFailure extends Exception {
        static final long serialVersionUID = 1;
    }

    final String filename;
    final PrintStream err;
    final SafeJConsumer consumer;
    
    final List<String> honoraries = new ArrayList<String>();
    final List<Member> staticMembers = new ArrayList<Member>();
    final List<Member> instanceMembers = new ArrayList<Member>();

    StreamTokenizer st;
    int nextToken;
    String comment = null; // the class comment
    
    public SafeJParse(File f, PrintStream err, SafeJConsumer consumer) {
        this.err = err;
        this.consumer = consumer;
        this.filename = f.getName();
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                new SafeJParse(child, err, consumer);
            }
        } else if (filename.endsWith(".safej")){
            parse(f);
        }
    }
    
    /**
     * Parse a safej file, invoking the safej consumer
     * @param f a safej file
     */
    private void parse(final File f) {
        try {
            FileReader in = new FileReader(f);
            st = new StreamTokenizer(in);             
            st.commentChar('#');
            st.quoteChar('"');
            nextToken = st.nextToken();
            
            boolean safe = true;
            
            // Parse mandatory components of safej file
            assertNextWordIs("class");
            assertNextTokenIs('(');
            if (nextWordIs("safe")) {
                assertNextTokenIs(',');
            } else if (nextWordIs("unsafe")) {
                assertNextTokenIs(',');
                safe = false;
            }
            String className = getNextString();
                
            // Parse optional components
            parseBody();
               
            assertNextTokenIs(')');
            if (safe) {
                consumer.consumeClass(className, comment, honoraries, 
                                      staticMembers, instanceMembers);
            } else {
                consumer.disabledClass(className, comment);
            }             
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace(err);
        } catch (IOException ioe) {
            ioe.printStackTrace(err);
        } catch (ParseFailure pf) {
         
        }
    }
    
    /**
     * Parse the optional components of a safej file
     * @throws IOException
     * @throws ParseFailure
     */
    private void parseBody() throws IOException, ParseFailure {
        if (!nextTokenIs(',')) {
            return;
        }
            
        if (nextWordIs("comment")) {
            assertNextTokenIs('(');
            comment = getNextString();
            assertNextTokenIs(')');
            if (!nextTokenIs(',')) {
                return;
            }
        }

        if (nextWordIs("makerSugaredBy")) {
            assertNextTokenIs('(');
            getNextString();
            assertNextTokenIs(')');
            if (!nextTokenIs(',')) {
                return;
            }
        }
        
        if (nextWordIs("sugaredBy")) {
            assertNextTokenIs('(');
            getNextString();
            assertNextTokenIs(')');
            if (!nextTokenIs(',')) {
                return;
            }
        }
        
        if (nextWordIs("honorary")) {
            assertNextTokenIs('(');
            do {
                honoraries.add(getNextString());
            } while (nextTokenIs(','));
            assertNextTokenIs(')');
            if (!nextTokenIs(',')) {
                return;
            }
        }
        
        if (nextWordIs("static")) {          
            parseMembers(staticMembers);
            if (!nextTokenIs(',')) {
                return;
            }          
        }

        if (nextWordIs("instance")) {          
            parseMembers(instanceMembers);
            if (!nextTokenIs(',')) {
                return;
            }          
        }
    }

    private void parseMembers(List<Member> members) throws IOException, ParseFailure {
        assertNextTokenIs('(');
        do {
            Kind kind;
            boolean allowed = true;
            String identifier;
            String comment = null;

            if (nextWordIs("constructor")) {
                kind = Kind.CONSTRUCTOR;
            } else if (nextWordIs("method")) {
                kind = Kind.METHOD;              
            } else {
                assertNextWordIs("field");
                kind = Kind.FIELD;
            }
            assertNextTokenIs('(');

            if (nextWordIs("allow")) {
                assertNextTokenIs(',');
            } else if (nextWordIs("suppress")) {
                allowed = false;
                assertNextTokenIs(',');
            }
            
            identifier = getNextString();
            
            if (nextTokenIs(',')) {
                assertNextWordIs("comment");
                assertNextTokenIs('(');
                comment = getNextString();
                assertNextTokenIs(')');
            }
            assertNextTokenIs(')');
        
            members.add(new Member(kind, allowed, identifier, comment));
        } while (nextTokenIs(','));
        
        assertNextTokenIs(')');
    }
    
    private void failParsing(String expectation) throws ParseFailure {
        err.println(filename + ":" + st.lineno() + ": " + expectation);
        throw new ParseFailure();
    }
    
    private void assertNextWordIs(String word) 
                                        throws ParseFailure, IOException {
        if (nextToken != TT_WORD || !st.sval.equals(word)) {
            failParsing("\"" + word + "\" expected, found " + 
                        (nextToken == TT_WORD ? "\"" + st.sval + "\"" 
                                              : "'" + (char) nextToken + "'"));
        }
        nextToken = st.nextToken();
    }
    
    private void assertNextTokenIs(char token) 
                                        throws ParseFailure, IOException {
        if (nextToken != token) {
            failParsing("'" + token + "' expected, found " + 
                    (nextToken == TT_WORD ? "\"" + st.sval + "\"" 
                                          : "'" + (char) nextToken + "'"));
        }
        nextToken = st.nextToken();
    }
    
    /**
     * Test if the next word matches the argument given.  Consumes the next
     * token only if it matches the given word.  Returns true if the token
     * matched and was consumed, false otherwise
     * @param word
     * @return
     * @throws IOException  if the token matched and st.nextToken() threw an
     *          IOException
     */
    private boolean nextWordIs(String word) throws IOException {
        boolean match = 
            nextToken == TT_WORD && st.sval.equals(word);
        if (match) {
            nextToken = st.nextToken();
        }
        return match;
    }
    
    private boolean nextTokenIs(char token) throws IOException {
        boolean match = nextToken == token;
        if (match) {
            nextToken = st.nextToken();
        }
        return match;
    }

    /**
     * Assert that the next token is a quoted string and return the string's
     * contents
     * @return the contents of the quoted string
     * @throws ParseFailure if the next token is not a quoted string
     * @throws IOException if nextToken() throws it
     */
    private String getNextString()
        throws ParseFailure, IOException {
        if (nextToken != '"') {
            failParsing("quoted string expected");
        }
        String sval = st.sval;
        nextToken = st.nextToken();
        return sval;
    }
}
