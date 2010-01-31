package org.joe_e.servlet.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import org.joe_e.array.ConstArray;
import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;

public class ReadOnlyFile {

	private File file = null;
	
	public ReadOnlyFile (File f) {
		file = f;
	}
	public String getName() {
		return file.getName();
	}
	public ConstArray<ReadOnlyFile> list() throws IOException {
		ConstArray<File> files = Filesystem.list(file);
		ReadOnlyFile[] readOnlyFiles = new ReadOnlyFile[files.length()];
		for (int i = 0; i < files.length(); i++) {
			readOnlyFiles[i] = new ReadOnlyFile(files.get(0));
		}
		return ConstArray.array(readOnlyFiles);
	}
	
	public ReadOnlyFile getChild(String name) {
		return new ReadOnlyFile(Filesystem.file(file, name));
	}
	
	public Reader getReader() throws FileNotFoundException {
		return ASCII.input(Filesystem.read(file));
	}
}
