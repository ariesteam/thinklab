/**
 * CurrencyPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCurrencyPlugin.
 * 
 * ThinklabCurrencyPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCurrencyPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.currency;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.integratedmodelling.currency.commands.CConvert;
import org.integratedmodelling.currency.cpi.CpiConversionFactory;
import org.integratedmodelling.sql.SQLPlugin;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.sql.hsql.HSQLMemServer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.value.BooleanValue;
import org.w3c.dom.Node;

public class CurrencyPlugin extends ThinklabPlugin  {

	CpiConversionFactory convFactory = null;
	
	URL cpiDataURL = null;
	URL exchangeDataURL = null;

	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(CurrencyPlugin.class);
	
	static final public String ID = "Currency";

	
	public void initialize() throws ThinklabException {

		String db = getProperties().getProperty("currency.database", 
												"hsqlmem://sa@localhost/currency");
												
		logger().info("currency database is " + db);
		
		/**
		 * Create a database to hold the currency data using the given
		 * database from property, defaulting to HSQLDB in-memory. Any db-relevant properties
		 * can be put in Currency.properties.
		 */
		SQLServer sqls = SQLPlugin.get().createSQLServer(db, CurrencyPlugin.get().getProperties());
				
		convFactory = new CpiConversionFactory();
		convFactory.initialize(
				sqls,
				new File(exchangeDataURL.getFile()), 
				new File(cpiDataURL.getFile()));
		
	}
	
	public CpiConversionFactory getConverter() {
		return convFactory;
	}
	
	public static CurrencyPlugin get() {
		return (CurrencyPlugin) getPlugin(ID);
	}

	public static Logger logger() {
		return log;
	}
	
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {

		try {
			new CConvert().install(km);
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}
	}

	public void notifyResource(String name, long time, long size)
			throws ThinklabException {

		if (name.endsWith("cpidata.txt"))
			cpiDataURL = this.exportResourceCached(name);
		else if (name.endsWith("exchrates.txt"))
			exchangeDataURL = this.exportResourceCached(name);

	}

	public void unload(KnowledgeManager km) throws ThinklabPluginException {
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
