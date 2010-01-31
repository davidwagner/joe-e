package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;

import org.joe_e.array.ConstArray;
import org.joe_e.file.Filesystem;

public class DeleteOnlyFile {

	private File file = null;
	
	public DeleteOnlyFile(File f) {
		file = f;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public ConstArray<DeleteOnlyFile> list() throws IOException {
		ConstArray<File> files = Filesystem.list(file);
		DeleteOnlyFile[] deleteOnlyFiles = new DeleteOnlyFile[files.length()];
		for (int i = 0; i < files.length(); i++) {
			deleteOnlyFiles[i] = new DeleteOnlyFile(files.get(0));
		}
		return ConstArray.array(deleteOnlyFiles);
	}
	
	public DeleteOnlyFile getChild(String name) {
		return new DeleteOnlyFile(Filesystem.file(file, name));
	}
	
	public boolean delete() {
		return file.delete();
	}
}
