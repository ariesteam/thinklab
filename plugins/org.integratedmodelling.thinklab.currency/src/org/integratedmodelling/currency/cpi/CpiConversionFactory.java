/**
 * CpiConversionFactory.java
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
package org.integratedmodelling.currency.cpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.integratedmodelling.corescience.exceptions.ThinklabInexactConversionException;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.sql.hsql.HSQLMemServer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.time.literals.TimeValue;

public class CpiConversionFactory {

	SQLServer server = null;
	
	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(CpiConversionFactory.class);
	
	public void initialize(SQLServer server, File ratesFile, File cpiFile) throws ThinklabException {

		int exchCount = 0;
		int cpiCount = 0;

		this.server = server;

		/* initialize database if necessary */
		if (!server.haveTable("rates")) {

			log.info("Currency plugin: creating exchange rate and CPI database... ");

			/* create main rate table */
			server.execute("CREATE TABLE rates (\n" + 
					"			    currency varchar(3),\n" + 
					"			    datefrom date,\n" + 
					"			    usdratio float\n" + 
			"			)");

			server.execute("CREATE TABLE cpi (\n" + 
					"			    datefrom date PRIMARY KEY,\n" + 
					"			    cpindex  float\n" + 
			"			)");
			
			// TODO build indexes to speed up operations

			try {
				
				BufferedReader reader = new BufferedReader(new FileReader(ratesFile));

				String line = null;
				while ((line = reader.readLine()) != null) {

					String[] ss = line.split(",");

					if (ss.length == 3) {
						server.execute ("INSERT INTO rates VALUES ('" + 
								ss[1] +
								"', '" + 
								ss[0] +
								"', " +
								ss[2] +
						")");					
						exchCount ++;
					}
				}
				reader.close();


				reader = new BufferedReader(new FileReader(cpiFile));

				while ((line = reader.readLine()) != null) {

					String[] ss = line.split(",");

					if (ss.length == 2) {
						server.execute ("INSERT INTO cpi VALUES ('" + 
								ss[0] +
								"', " +
								ss[1] +
						")");
						cpiCount++;
					}
				}
				reader.close();

				
				log.info(
						"read " + 
						exchCount + 
						" historic exchange rates and " + 
						cpiCount + 
						" historic CPI data points into database.");


			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
	}
	
	public double getDollarToCurrencyConversionFactor(String currencyCode, TimeValue date)
	throws ThinklabInexactConversionException {

		double ret = 1.0;
		
		if (!currencyCode.equals("USD")) {

			String sql = 
				"SELECT AVG(usdratio) FROM rates WHERE currency = '" +
				currencyCode + 
				"' AND datefrom ";

			sql += getDateRestriction(date);

			try {
				ret = server.getResultAsDouble(sql, -1.0);
			} catch (ThinklabStorageException e) {
				throw 
				new ThinklabInexactConversionException("database error: " + 
						e.getMessage());
			}

			if (ret < 0.0) {
				throw new ThinklabInexactConversionException(
						"cannot convert currency " + 
						currencyCode + 
						" to USD: no rates available for " +
						date);
			}
		}
		return ret;
	}

	public double getCurrencyToDollarConversionFactor(String currencyCode, TimeValue date)
		throws ThinklabInexactConversionException {

		double ret = 1.0;
		if (!currencyCode.equals("USD"))
			ret = 1.0/getDollarToCurrencyConversionFactor(currencyCode, date);
		return ret;
	}

	private String getDateRestriction(TimeValue date) {

		String sql;
		if (date.getPrecision() == TimeValue.Precision.YEAR) {
			sql = 
				"BETWEEN '" 
				+ date + 
				"-01-01' AND '" +
				date + 
				"-12-31'";			
		} else {
			sql = "= '" + date.getYear() + "-" + date.getMonth() + "-01'";
		}
		return sql;
	}
	
	public double getConversionFactor(String currencyFrom, TimeValue dateFrom, String currencyTo, TimeValue dateTo) throws ThinklabInexactConversionException {
		
		double r = 1.0;
		
		/* convert currency to USD at original date */
		r = getCurrencyToDollarConversionFactor(currencyFrom, dateFrom);
		
		/* adjust USD amount to match purchasing power at target date */
		r *= getDollarInflationCorrection(dateFrom, dateTo);
		
		/* convert USD to target currency at target date*/
		r *= getDollarToCurrencyConversionFactor(currencyTo, dateTo);
		
		return r;

	}

	public double getDollarInflationCorrection(TimeValue date, TimeValue dateTo) throws ThinklabInexactConversionException {

		/*
		 * Waiting for a working TimeValue.equals(), this is the best way to
		 * compare both time and precision.
		 */
		if (date.toString().equals(dateTo.toString())) {
			return 1.0;
		}

		String sql = "SELECT AVG(cpindex) FROM cpi WHERE datefrom ";
		
		double cfrom = -1.0;
		double cto = -1.0;
		
		try {
			cfrom = 
				server.getResultAsDouble(sql + getDateRestriction(date), -1.0);
			cto = 
				server.getResultAsDouble(sql + getDateRestriction(dateTo), -1.0);
		} catch (ThinklabStorageException e) {
		}

		if (cfrom < 0.0 || cto < 0.0)
			throw new ThinklabInexactConversionException(
					"cannot calculate inflation correction for " +
					date + 
					" vs. " +
					dateTo +
					": no CPI data available");
		
		// FIXME CHECK!
		return cto/cfrom;
	}
	
	
	public static void main(String[] args) {

		CpiConversionFactory cpi = new CpiConversionFactory();
		
		try {
			SQLServer sqls = new HSQLMemServer("currency");
			cpi.initialize(sqls, new File("data/exchrates.txt"), new File("data/cpidata.txt"));
			
			
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double convert(double value, String currencyFrom, TimeValue dateFrom,
			String currencyTo, TimeValue dateTo) throws ThinklabInexactConversionException {

		return 
			value *
			getConversionFactor(currencyFrom, dateFrom, currencyTo, dateTo);
	}
	
}