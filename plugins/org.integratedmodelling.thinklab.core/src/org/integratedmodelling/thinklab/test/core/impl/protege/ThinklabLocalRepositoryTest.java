/**
 * ThinklabLocalRepositoryTest.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.test.core.impl.protege;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.impl.protege.ThinklabLocalRepository;

public class ThinklabLocalRepositoryTest extends AllTestsSetup {
	
	private ThinklabLocalRepository rep;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.IMALocalRepository.IMALocalRepository(URL, boolean)'
	 */
	public void testIMALocalRepositoryURLBoolean() throws URISyntaxException, MalformedURLException {
		boolean flag = true;
		try {
			rep = new ThinklabLocalRepository(pizzafile.toURL(),true);
		} catch (ThinklabIOException e) {
			flag = false;
		} catch (MalformedURLException e) {
			flag = false;
		}
		
		URI uri = new URI(pizzaURI);
		assertEquals(true,flag);
		assertEquals(pizzaFilename,rep.getFileName());
		assertEquals(true,rep.contains(uri));
		assertEquals(pizzafile.getParent().toString(),new File(rep.getDirectory()).toString());
		assertEquals(pizzaURI+"#",rep.getNamespace());
		assertEquals(pizzafile.toURL().toString(),rep.getFileURL().toString());

		assertEquals(uri,rep.getName());
		assertEquals(1,rep.getOntologies().size());
		assertEquals(true,rep.getOntologies().contains(uri));
		assertEquals(false,rep.isSystem());
		assertEquals(true,rep.isWritable());
		assertEquals(true,rep.isWritable(uri));
		assertEquals(false,rep.isWritable(new URI("http://test/something")));
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.IMALocalRepository.IMALocalRepository(URL, URI, boolean)'
	 */
	public void testIMALocalRepositoryURLURIBoolean() throws URISyntaxException {
		URI uri = new URI("http://test/something");

		boolean flag = true;
		try {
			rep = new ThinklabLocalRepository(pizzafile.toURL(),uri,true);
		} catch (ThinklabIOException e) {
			flag = false;
		} catch (MalformedURLException e) {
			flag = false;
		}
		
		assertEquals(pizzaFilename,rep.getFileName());
		assertEquals(true,rep.contains(uri));
		assertEquals(pizzafile.getParent().toString(),new File(rep.getDirectory()).toString());
		assertEquals(uri+"#",rep.getNamespace());
		try {
			assertEquals(pizzafile.toURL().toString(),rep.getFileURL().toString());
		} catch (MalformedURLException e) {
			flag = false;
		}

		assertEquals(uri,rep.getName());
		assertEquals(1,rep.getOntologies().size());
		assertEquals(true,rep.getOntologies().contains(uri));
		assertEquals(false,rep.isSystem());
		assertEquals(true,rep.isWritable());
		assertEquals(true,rep.isWritable(uri));
		assertEquals(true,rep.isWritable(new URI("http://test/something")));
		assertEquals(false,rep.isWritable(new URI("http://test/somethingElse")));
		assertEquals(true,flag);
	}



	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.IMALocalRepository.set*()'
	 */
	public void testRefresh() throws URISyntaxException {
		boolean flag = true;
		try {
			rep = new ThinklabLocalRepository(pizzafile.toURL(),true);
		} catch (ThinklabIOException e) {
			flag = false;
		} catch (MalformedURLException e) {
			flag = false;
		}
		assertEquals(true,flag);
		try {
			rep.setFileURL(example.toURL());
		} catch (MalformedURLException e) {
			flag= false;
		}
		assertEquals(true,flag);
		try {
			assertEquals(example.toURL(),rep.getFileURL());
		} catch (MalformedURLException e) {
			flag= false;
		}
		assertEquals(true,flag);
		
		rep.refresh();
		assertEquals(new URI(exampleURI),rep.getName());
		

	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.IMALocalRepository.getInputStream(URI)'
	 */
	public void testGetInputStream() throws IOException {
		boolean flag = true;
		try {
			rep = new ThinklabLocalRepository(pizzafile.toURL(),true);
		} catch (ThinklabIOException e) {
			flag = false;
		} catch (MalformedURLException e) {
			flag = false;
		}
		assertEquals(true,flag);
		
		InputStream is = rep.getInputStream(pizzafile.toURI());
		assertEquals(pizzafile.length(),is.available());
	}

	/*
	 * Test method for 'org.integratedmodelling.ima.core.impl.protege.IMALocalRepository.getOutputStream(URI)'
	 */
	public void testGetOutputStream() throws IOException {
		boolean flag = true;
		try {
			rep = new ThinklabLocalRepository(pizzafile.toURL(),true);
		} catch (ThinklabIOException e) {
			flag = false;
		} catch (MalformedURLException e) {
			flag = false;
		}
		assertEquals(true,flag);
		
		
//		FileOutputStream is = (FileOutputStream) rep.getOutputStream(pizzafile.toURI());
		
		rep.getOutputStream(pizzafile.toURI());

	}

}
