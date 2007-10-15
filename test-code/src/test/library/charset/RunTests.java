package test.library.charset;

import org.joe_e.charset.*;
import java.util.Arrays;
import java.io.Reader;
import java.io.Writer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RunTests {
    public static void test() {
        byte[] helloBytes = {104, 101, 108, 108, 111};
        
        byte[] output = ASCII.encode("hello");      
        assert Arrays.equals(output, helloBytes);
        
        output = UTF8.encode("hello");
        assert Arrays.equals(output, helloBytes);
        
        String asciiDecode = ASCII.decode(helloBytes, 0, helloBytes.length);
        assert "hello".equals(asciiDecode);

        String utf8Decode = UTF8.decode(helloBytes, 0, helloBytes.length);
        assert "hello".equals(utf8Decode);
        
        // Reader: just make sure they can be called without
        // generating exceptions.
        Reader asciiReader = ASCII.input(System.in);
        Reader utf8Reader = UTF8.input(System.in);
        
        // Writer
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Writer asciiWriter = ASCII.output(stream);
        try {
            asciiWriter.write("hello");
            asciiWriter.flush();
            assert Arrays.equals(stream.toByteArray(), helloBytes);
        } catch (IOException ioe) {
            assert false;
        }
        stream.reset();
        Writer utf8Writer = UTF8.output(stream);
        try {
            utf8Writer.write("hello");
            utf8Writer.flush();
            assert Arrays.equals(stream.toByteArray(), helloBytes);
        } catch (IOException ioe) {
            assert false;
        }
                
        // URLEncode/decode
        String decoded = "hi ü@foo-bar";
        String encoded = "hi+%C3%BC%40foo-bar";
        assert URLEncoding.encode(decoded).equals(encoded);
        assert URLEncoding.decode(encoded).equals(decoded);    
    }
}
