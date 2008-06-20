/**
 * SimplePluginTest.java
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
package org.integratedmodelling.thinklab.test;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.impl.APIOnlyKnowledgeInterface;
import org.integratedmodelling.thinklab.impl.Session;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IOntology;

public class SimplePluginTest {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		FileKnowledgeRepository kr = new FileKnowledgeRepository();
		KnowledgeManager km = new KnowledgeManager(kr, new APIOnlyKnowledgeInterface());
		km.initialize();
		IOntology onto =  kr.requireOntology("thinklab-core");
		Session ses = (Session) km.requestNewSession();
		
//		kr.printDetails();
		
		IInstance i = ses.createObject("kati",new SemanticType("thinklab-core:StorageType"));
		System.out.println("-------" + i.toString());
		
		i.getDirectType();
		
//		kr.printDetails();
//		kr.printResources();
		
//		kr.saveAll();


	}

}
