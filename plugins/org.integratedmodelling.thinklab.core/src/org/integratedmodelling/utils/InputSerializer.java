/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
public class InputSerializer {
	
	public static interface ObjectReader {
		public abstract Object readObject() throws ThinklabException;
	}

	private DataInputStream input;
	private File file;
	
	public InputSerializer(File f) throws ThinklabIOException {
		try {
			this.file = f;
			this.input = new DataInputStream (new FileInputStream (f));
		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		}
	}
	
	public InputSerializer(InputStream f) {
		this.input = new DataInputStream (f);
	}
	
	public int readInteger() throws ThinklabIOException {
		try {
			return input.readInt();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public long readLong() throws ThinklabIOException {
		try {
			return input.readLong();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public float readFloat() throws ThinklabIOException {
		try {
			return input.readFloat();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public double readDouble() throws ThinklabIOException {
		try {
			return input.readDouble();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public String readString() throws ThinklabIOException {
		try {
			int len = input.readInt();
			if (len == 0)
				return null;
			char[] bb = new char[len];
			for (int i = 0; i < len; i++)
				bb[i] = input.readChar();
			return new String(bb);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	public Object readObject(ObjectReader object) throws ThinklabException {
		return object.readObject();
	}
	
	public int[] readIntegers() throws ThinklabIOException {
		int len = readInteger();
		if (len == 0)
			return null;
		int[] ret = new int[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readInteger();
		return ret;
	}

	public long[] readLongs() throws ThinklabIOException {
		int len = readInteger();
		if (len == 0)
			return null;
		long[] ret = new long[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readLong();
		return ret;
	}

	public float[] readFloats() throws ThinklabIOException {
		int len = readInteger();
		if (len == 0)
			return null;
		float[] ret = new float[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readFloat();
		return ret;
	}

	public double[] readDoubles() throws ThinklabIOException {
		int len = readInteger();
		if (len == 0)
			return null;
		double[] ret = new double[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readDouble();
		return ret;
	}

	public String[] readStrings() throws ThinklabIOException {
		int len = readInteger();
		if (len == 0)
			return null;
		String[] ret = new String[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readString();
		return ret;
	}
	
	public Object[] readObjects(ObjectReader reader) throws ThinklabException {
		int len = readInteger();
		if (len == 0)
			return null;
		Object[] ret = new Object[len];
		for (int i = 0; i < ret.length; i++)
			ret[i] = readObject(reader);
		return ret;
	}

	public File close() {
		try {
			input.close();
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
		return file;
	}
}
