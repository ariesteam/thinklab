/**
 * Shell.java
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
package org.integratedmodelling.thinklab.shell;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class Shell {

	
    public static void main(String[] args) {
        
                
        try {
        	// TODO substitute args with preferences or command-line parameters
            KnowledgeManager km = 
            	new KnowledgeManager(new FileKnowledgeRepository(), new CLInterface(args));

            // load it all
            km.initialize();

            new Help().install(km);
            new List().install(km);
            new Load().install(km);
            new Clear().install(km);
            new Is().install(km);
            new Import().install(km);
            new Eval().install(km);
            new Hierarchy().install(km);
            new Query().install(km);
            new KExport().install(km);
            new KCopy().install(km);
            new KImport().install(km);
            new CMap().install(km);
            new Find().install(km);
            
            // go
            km.run();

			/* be nice */
			km.shutdown();
			
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
