package test.library.file;

import org.joe_e.array.ConstArray;
import java.io.File;
import org.joe_e.file.Filesystem;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class WriteRead {
    public static void test() {      
        byte[] asciiHello = {104, 101, 108, 108, 111, 10};
        
        File root = new File("/tmp");
        File tempfile = Filesystem.file(root, "joe-e-lib-test");
        try {
            OutputStream out = Filesystem.writeNew(tempfile);
            out.write(asciiHello);
            out.close();
            
            InputStream in = Filesystem.read(tempfile);
            byte[] buf = new byte[20];
            int index = 0;
            int bytesRead = 0;
            do {
                index += bytesRead;
                bytesRead = in.read(buf, index, 20 - index);
            } while (bytesRead > 0);
            in.close();
            
            assert index == 6;
            for (int i = 0; i < 6; ++i) {
                assert buf[i] == asciiHello[i];
            }
            
            ConstArray<File> contents = Filesystem.list(root);
            boolean found = false;
            for (File f : contents) {
                if (f.equals(tempfile)) {
                    found = true;
                    assert (Filesystem.length(f) == 6);
                    break;
                }
            }
            
            assert found;         
            tempfile.delete();
            
            contents = Filesystem.list(root);
            found = false;
            for (File f : contents) {
                if (f.equals(tempfile)) {
                    found = true;
                    break;
                }
            }
            
            assert !found;
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
