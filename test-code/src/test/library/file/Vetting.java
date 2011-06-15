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
        String[] depends = {"\\", "yes\\no", "..\\asdf", "T\\Y", "\\join"};
        
        File base = new File("blarg");
        
        for (String p : pass) {
            Filesystem.file(base, p);
        }
    
        for (String f : fail) {
            try {
                Filesystem.file(base, f);
                assert false;
            } catch (InvalidFilenameException ife) {
            
            }
        }
        
        for (String d : depends) {
            try {
                Filesystem.file(base, d);
                assert File.pathSeparatorChar != '\\';
            } catch (InvalidFilenameException ife) {
                assert File.pathSeparatorChar == '\\';
            }
        }
        
    }
}