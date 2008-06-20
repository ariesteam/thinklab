/**
 * KBoxManager.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 25, 2008
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
 * @date      Apr 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.kbox;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.plugin.PluginRegistry;
import org.integratedmodelling.utils.Polylist;

/**
 * A singleton that manages KBoxes. One of these is created by and accessed through
 * the Knowledge manager. All the KBox functions are not here yet, but will be
 * moved here with time.
 * 
 * @author Ferdinando Villa
 *
 */
public class KBoxManager {

	private static final String METADATA_KBOX_PROPERTY = "kbox.metadata.schema";
	
	/**
	 * Properties starting with this prefix declare a metadata field in
	 * their name (after the prefix) and its semantic type. Plugins can
	 * add as many metadata fields as they want, which kboxes can 
	 * declare and set into each object's metadata fields. 
	 */
	public final String KBOX_METADATA_PREFIX = "kbox.metadata.type";
	
	Logger log = Logger.getLogger(PluginRegistry.class);

	HashMap<String, IConcept> metadataTypes = new HashMap<String, IConcept>();
	
	public KBoxManager() throws ThinklabException {
		
		/* add the default metadata fields */
		metadataTypes.put(IQueryResult.ID_FIELD_NAME, KnowledgeManager.get().getTextType());
		metadataTypes.put(IQueryResult.LABEL_FIELD_NAME, KnowledgeManager.get().getTextType());
		metadataTypes.put(IQueryResult.DESCRIPTION_FIELD_NAME, KnowledgeManager.get().getTextType());
		metadataTypes.put(IQueryResult.CLASS_FIELD_NAME, KnowledgeManager.get().getTextType());
		
	}
	
	/**
	 * Get the only instance of the plugin registry.
	 * 
	 * @return the plugin registry
	 * @throws ThinklabNoKMException
	 *             if no knowledge manager was initialized.
	 */
	static public KBoxManager get() throws ThinklabNoKMException {
		return KnowledgeManager.get().getKBoxManager();
	}
	
	/*
	 * Called by the plugin registry
	 */
	public void defineMetadataTypes(Properties properties) throws ThinklabException {
		
		for (Object p : properties.keySet()) {
			
			if (p.toString().startsWith(KBOX_METADATA_PREFIX)) {
				String cid = properties.getProperty(p.toString());
				
				String[] ss = p.toString().split("\\.");
				String metadataName = ss[ss.length - 1];
				
				IConcept cc = KnowledgeManager.get().requireConcept(cid);
				metadataTypes.put(metadataName, cc);
			}
		}
	}
	
	public void validateSchema(Polylist schema) {
		
	}
	
	public Polylist getDefaultSchema() {
		return Polylist.list(
				IQueryResult.ID_FIELD_NAME, 
				IQueryResult.ID_FIELD_NAME, 
				IQueryResult.LABEL_FIELD_NAME, 
				IQueryResult.DESCRIPTION_FIELD_NAME);
	}
	
	public HashMap<String, IValue> createResult(Polylist schema, Polylist results) throws ThinklabException {
		
		HashMap<String, IValue> ret = new HashMap<String, IValue>();

		Object[] oo = schema.array();
		Object[] ov = results.array();
		
		for (int i = 0; i < oo.length; i++) {
			if (ov[i] != null) {
				if (ov[i] instanceof IValue)
					ret.put(oo[i].toString(), (IValue)ov[i]);
				else {
					
					String sv = ov[i].toString();
					IConcept c = metadataTypes.get(oo[i].toString());
					
					if (c == null) 
						throw new ThinklabValidationException(
								"kbox: metadata field " + 
								oo[i] + 
								" has not been defined by any plugins");
					
					IValue val = KnowledgeManager.get().validateLiteral(c, sv, null);
					
					ret.put(oo[i].toString(), val);
				}
			}
		}
		
		return ret;
	}

	public Polylist parseSchema(Properties properties) throws ThinklabValidationException {
		
		Polylist ret = getDefaultSchema();
		
		if (properties != null) {

			String pmd = properties.getProperty(METADATA_KBOX_PROPERTY);
			if (pmd != null) {
				String[] ss = pmd.split(",");
				for (String mdf : ss) {
					/* validate */
					if (metadataTypes.get(mdf) == null) 
						throw new ThinklabValidationException(
								"kbox: metadata field " + 
								mdf + 
								" has not been defined by any plugins");
					
					ret = ret.cons(mdf);
				}
			}
		}
		
		return ret;
	}
	
}
