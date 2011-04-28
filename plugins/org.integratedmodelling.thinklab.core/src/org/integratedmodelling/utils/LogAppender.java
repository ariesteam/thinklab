package org.integratedmodelling.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

public class LogAppender {

	private File fname;

	public LogAppender(String file, boolean append) throws ThinklabIOException {
		
		this.fname = new File(file);
		
		if (!append && this.fname.exists()) {
			this.fname.delete();
		}
	}
	
	public void print(String s)  {

		try {
			FileWriter writer = new FileWriter(fname, true);
			writer.write(s + System.getProperty("line.separator"));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
}
