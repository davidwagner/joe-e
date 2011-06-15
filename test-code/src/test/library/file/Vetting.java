package test.library.file;

import java.io.File;

import org.joe_e.file.Filesystem;
import org.joe_e.file.InvalidFilenameException;

public class Vetting {
    public static void test() {
        String[] pass = {"ok", "longish alphanumeric", "didn't", "*\"funky",
                         "*.*", "jeepers", "stuff..", "ork.ext", ".hack", 
                         "hello..good-bye"};
        String[] fail = {"yes/no", ".", "..", "../asdf", "T/Y", "/join", "..\0", "\0", ".\0",
                         "..\0ok", "\0\1\2", "\0../.."};
        String[] withBackslashes = {"\\", "yes\\no", "..\\asdf", "T\\Y", "\\join"};
        
        for (String p : pass) {
            Filesystem.checkName(p);
        }
    
        for (String f : fail) {
            try {
                Filesystem.checkName(f);
                assert false;
            } catch (InvalidFilenameException ife) {
            
            }
        }
        
        for (String b : withBackslashes) {
            try {
                Filesystem.checkName(b);
                assert File.pathSeparatorChar != '\\';
            } catch (InvalidFilenameException ife) {
                assert File.pathSeparatorChar == '\\';
            }
        }
        
    }
}