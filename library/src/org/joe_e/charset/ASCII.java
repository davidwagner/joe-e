// Copyright 2006-2007 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
package org.joe_e.charset;

import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * ASCII I/O.
 */
public final class ASCII {
    private static final Charset charset = Charset.forName("US-ASCII");
    
    private ASCII() {}

    /**
     * Encodes a string in US-ASCII.
     * @param text  The text to encode.
     * @return The ASCII bytes.
     */
    static public byte[] encode(final String text) {
        final ByteBuffer bytes = charset.encode(text);
        final int off = bytes.arrayOffset();
        final int len = bytes.limit();
        final byte[] v = bytes.array();
        final byte[] r;
        if (0 == off && len == v.length) {
            r = v;
        } else {
            System.arraycopy(v, off, r = new byte[len], 0, len);
        }
        return r;
    }
    
    /**
     * Decodes a US-ASCII string. Each byte not corresponding to a US-ASCII
     * character decodes to the Unicode replacement character U+FFFD.  This
     * method is equivalent to <code>decode(buffer, 0, buffer.length)</code>.
     * @param buffer    the ASCII-encoded string to decode
     * @return The corresponding string
     * @throws java.lang.IndexOutOfBoundsException
     */
    static public String decode(byte[] buffer) {
        return decode(buffer, 0, buffer.length);
    }
    
    /**
     * Decodes a US-ASCII string. Each byte not corresponding to a US-ASCII
     * character decodes to the Unicode replacement character U+FFFD.
     * @param buffer    the ASCII-encoded string to decode
     * @param off       where to start decoding
     * @param len       how many bytes to decode
     * @return  the corresponding string
     * @throws java.lang.IndexOutOfBoundsException
     */
    static public String decode(byte[] buffer, int off, int len) {
        return charset.decode(ByteBuffer.wrap(buffer, off, len)).toString();
    }
        
    /**
     * Constructs an ASCII reader.
     * @param in    The binary input stream
     * @return  the ASCII character reader.
     */
    static public Reader input(final InputStream in) {
        return new InputStreamReader(in, charset);
    }

    /**
     * Constructs an ASCII writer.
     * @param out the output stream.
     */
    static public Writer output(final OutputStream out) {
        return new OutputStreamWriter(out, charset);
    }
}
