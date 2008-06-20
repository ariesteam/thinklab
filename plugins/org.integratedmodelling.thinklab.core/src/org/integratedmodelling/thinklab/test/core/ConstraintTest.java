/**
 * ConstraintTest.java
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
package org.integratedmodelling.thinklab.test.core;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.test.SetupKM;

public class ConstraintTest extends SetupKM {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
    public void testConstraintString() {

        try {
            new Constraint("thinklab-core:Number");
          
        } catch (ThinklabException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    public void testConstraintPolylist() {
//        fail("Not yet implemented");
//    }
//
//    public void testConstraint() {
//        fail("Not yet implemented");
//    }
//
//    public void testAddOR() {
//        fail("Not yet implemented");
//    }
//
//    public void testAddAND() {
//        fail("Not yet implemented");
//    }
//
//    public void testDumpPrintStream() {
//        fail("Not yet implemented");
//    }
//
//    public void testAsList() {
//        fail("Not yet implemented");
//    }
//
//    public void testMatch() {
//        fail("Not yet implemented");
//    }
//
//    public void testMergeConstraint() {
//        fail("Not yet implemented");
//    }
//
//    public void testMergeConstraintLogicalConnector() {
//        fail("Not yet implemented");
//    }

}
