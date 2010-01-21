package org.integratedmodelling.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

/**
 * Simple object to read and write stuff to file. Just call the write() functions in the
 * serializer and call the read() functions in the same order in the reader. Anything 
 * that is not a base type can be written by passing a specialized writer and reader 
 * object which uses the same functions.
 * 
 * @author Ferdinando Villa
 *
 */
public class OutputSerializer {

	public static interface ObjectWriter {
		public abstract void writeObject(Object o) throws ThinklabException;		
	}

	private DataOutputStream output;
	private File file;
	
	public OutputSerializer(File f) throws ThinklabIOException {
		try {
			this.file = f;
			this.output = new DataOutputStream (new FileOutputStream (f));
		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		}
	}
	
	public OutputSerializer(OutputStream f) {
			this.output = new DataOutputStream(f);
	}
	
	public void writeInteger(int b) throws ThinklabIOException {
		try {
			output.writeInt(b);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeLong(long b) throws ThinklabIOException {
		try {
			output.writeLong(b);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeFloat(float b) throws ThinklabIOException {
		try {
			output.writeFloat(b);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeDouble(double b) throws ThinklabIOException {
		try {
			output.writeDouble(b);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeString(String b) throws ThinklabIOException {
		try {
			output.writeInt(b == null ? 0 : b.length());
			if (b != null)
				for (int i = 0; i < b.length(); i++)
					output.writeChar(b.charAt(i));
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeObject(Object b, ObjectWriter writer) throws ThinklabException {
			writer.writeObject(b);
	}

	
	public void writeIntegers(int[] b) throws ThinklabIOException {
		try {
			output.writeInt(b == null ? 0 : b.length);
			if (b != null)
				for (int i = 0; i < b.length; i++)
					output.writeInt(b[i]);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeLongs(long[] b) throws ThinklabIOException {
		try {
			output.writeInt(b == null ? 0 : b.length);
			if (b != null)
				for (int i = 0; i < b.length; i++)
					output.writeLong(b[i]);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeFloats(float[] b) throws ThinklabIOException {
		try {
			output.writeInt(b == null ? 0 : b.length);
			if (b != null)
				for (int i = 0; i < b.length; i++)
					output.writeFloat(b[i]);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeDoubles(double[] b) throws ThinklabIOException {
		try {
			output.writeInt(b == null ? 0 : b.length);
			if (b != null)
				for (int i = 0; i < b.length; i++)
					output.writeDouble(b[i]);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public void writeStrings(String[] b) throws ThinklabIOException {
		writeInteger(b == null ? 0 : b.length);
		if (b != null)
			for (int i = 0; i < b.length; i++)
				writeString(b[i]);
	}

	public void writeObjects(Object[] b, ObjectWriter writer) throws ThinklabException {
		writeInteger(b == null ? 0 : b.length);
		if (b != null)
			for (int i = 0; i < b.length; i++)
				writer.writeObject(b[i]);
	}
	
	public void writeStrings(Collection<String> b) throws ThinklabIOException {
		writeInteger(b.size());
		for (Iterator<String> it = b.iterator(); it.hasNext(); )
			writeString(it.next());
	}

	public void writeObjects(Collection<Object> b, ObjectWriter writer) throws ThinklabException {
		writeInteger(b.size());
		for (Iterator<Object> it = b.iterator(); it.hasNext(); )
			writeObject(it.next(), writer);
	}
	
	public File close() {
		try {
			output.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
		return file;
	}
}
