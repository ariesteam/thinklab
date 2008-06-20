/**
 * TableGenerator.java
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
package org.integratedmodelling.currency.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;

public class TableGenerator {

	
	static final String dataDir =  "data";
	static final String[] currencyFiles  = {
		"d1.txt", "d2.txt", "d3.txt", "d4.txt", "d5.txt"
	};
	static final String currencyOutputFile = "exchrates.txt";
	
	static final String[] cpiFiles = {
		"cpiUS.txt"
	};
	static final String cpiOutputFile = "cpidata.txt";
	
	static HashMap<String, String> monthnames = new HashMap<String,String>();
	
	static {
		monthnames.put("Jan", "01");
		monthnames.put("Feb", "02");
		monthnames.put("Mar", "03");
		monthnames.put("Apr", "04");
		monthnames.put("May", "05");
		monthnames.put("Jun", "06");
		monthnames.put("Jul", "07");
		monthnames.put("Aug", "08");
		monthnames.put("Sep", "09");
		monthnames.put("Oct", "10");
		monthnames.put("Nov", "11");
		monthnames.put("Dec", "12");
	}
	
	public static void main(String[] args) {

		File outfile = new File(dataDir + "/" + currencyOutputFile);
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(outfile));

			for (String fn : currencyFiles) {

				File infile = new File(dataDir + "/" + fn);
				FileInputStream input = null;
				input = new FileInputStream(infile);

				LabeledCSVParser parser = new LabeledCSVParser(new CSVParser(
						input));

				String[] labels = parser.getLabels();				
				String[] row = null;

				while ((row = parser.getLine()) != null) {
					/* each row contains the date and one or more factors for the currency correspondent
					 * to the currency symbol in the label  */
					
					/* process date, from format "Jan 1976" to ODBC canonical */
					String date = row[0];
					date = date.substring(4) + "-" + monthnames.get(date.substring(0,3)) + "-01";
					
					for (int i = 1; i < row.length; i++) {
						
						if (row[i].equals(""))
							continue;
						
						/* extract currency ID in form "CAD/USD" */
						String currency = labels[i].substring(0,3);
	
						System.out.println(date + "," + currency + "," + row[i]);
						output.append(date + "," + currency + "," + row[i] + "\n");
					}
				}

				parser.close();

			}

			output.close();
			
			outfile = new File(dataDir + "/" + cpiOutputFile);
			output = new BufferedWriter(new FileWriter(outfile));
			
			for (String s : cpiFiles) {
				
				BufferedReader reader = 
					new BufferedReader(new FileReader(new File(dataDir + "/" + s)));
				String line = null;
				int count = 0;
				int currentyear = 0;
				
				while ((line = reader.readLine()) != null) {

					line = line.trim();
					if (line.equals(""))
						continue;
										
					if ((count % 2) == 0) {
						
						System.out.println("YEAR IS " + line);
						/* should be a year line */
						currentyear = Integer.valueOf(line);
						
						
					} else {
						
						/* a value line, one per month */
						System.out.println(line);
						
						String[] ls = line.split(" \t");
						for (int i = 1; i <= ls.length && i <= 12; i++)
							output.append(
									currentyear + "-" + (i < 10 ? ("0"+i) : String.valueOf(i)) + "-01" +
									"," +
									ls[i-1].trim() +
									"\n");
					}
					
					count ++;
				}
			}
			output.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
