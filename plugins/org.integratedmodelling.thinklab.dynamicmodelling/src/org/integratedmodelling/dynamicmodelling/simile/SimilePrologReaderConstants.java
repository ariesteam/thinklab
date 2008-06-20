/**
 * SimilePrologReaderConstants.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary W. Johnson, Jr.
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.simile;

public interface SimilePrologReaderConstants {

  int EOF = 0;
  int EOL = 2;
  int OPEN_PAR = 3;
  int CLOSE_PAR = 4;
  int OPEN_SQR = 5;
  int CLOSE_SQR = 6;
  int OPEN_CUR = 7;
  int CLOSE_CUR = 8;
  int DOT = 9;
  int COMMA = 10;
  int DASH = 11;
  int EQUALS = 12;
  int SOURCE = 13;
  int ROOTS = 14;
  int PROPERTIES = 15;
  int NODE = 16;
  int ARC = 17;
  int LINKS = 18;
  int REFERENCES = 19;
  int IF = 20;
  int THEN = 21;
  int ELSEIF = 22;
  int ELSE = 23;
  int QTDSTRING = 24;
  int MATHSYM = 25;
  int RELSYM = 26;
  int WORD = 27;
  int LETTERS = 28;
  int NUMBER = 29;
  int DIGITS = 30;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "<EOL>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\".\"",
    "\",\"",
    "\"-\"",
    "\"=\"",
    "\"source\"",
    "\"roots\"",
    "\"properties\"",
    "\"node\"",
    "\"arc\"",
    "\"links\"",
    "\"references\"",
    "\"if\"",
    "\"then\"",
    "\"elseif\"",
    "\"else\"",
    "<QTDSTRING>",
    "<MATHSYM>",
    "<RELSYM>",
    "<WORD>",
    "<LETTERS>",
    "<NUMBER>",
    "<DIGITS>",
  };

}
