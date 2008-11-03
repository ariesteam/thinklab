package org.integratedmodelling.utils;
/*
 * Copyright (c) 2007 Cornel Mihaila (http://www.mihaila.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.package zutil;
 */


/**
 * Various functions related to string processing (in addition to the methods
 * found in <code>java.lang.String</code> and
 * <code>org.apache.commons.lang.StrUtils</code>).
 */
public class CamelCase {

	/**
	 * Converts a string to upper underscore case (Ex. "justASimple_String"
	 * becomes "JUST_A_SIMPLE_STRING"). This function is not idempotent (Eg.
	 * "abc" => "ABC" => "A_B_C").
	 * 
	 * @param value
	 * @return the upper case string with the chosen separator
	 */
	public static String toUpperCase(String value, char sep) {
		return toUnderscoreCaseHelper(value, true, sep);
	}

	/**
	 * Converts a string to lower underscore case (Ex. "justASimple_String"
	 * becomes "just_a_simple_string"). This function is idempotent.
	 * 
	 * @param value
	 * @return the lower case string with the chosen separator
	 */
	public static String toLowerCase(String value, char sep) {
		return toUnderscoreCaseHelper(value, false, sep);
	}

	/**
	 * Converts a string to lower came case (Ex. "justASimple_String" becomes
	 * "JustASimpleString"). This function is idempotent.
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toUpperCamelCase(String value, char sep) {
		return toCamelCaseHelper(value, true, sep);
	}

	/**
	 * Converts a string to lower came case (Ex. "justASimple_String" becomes
	 * "justASimpleString"). This function is idempotent.
	 * 
	 * @param value
	 * @return the upper underscore case string
	 */
	public static String toLowerCamelCase(String value, char sep) {
		return toCamelCaseHelper(value, false, sep);
	}

	/**
	 * Helper function for underscore case functions.
	 * 
	 * @param value
	 * @param upperCase
	 * @return
	 */
	private static String toUnderscoreCaseHelper(String value, boolean upperCase, char sep) {
		if (value == null) {
			return null;
		}
		// 10% percent increase estimation, minimum 8
		int estimatedSize = value.length() * 11 / 10;
		if (value.length() < 8) {
			estimatedSize = 8;
		}
		StringBuilder result = new StringBuilder(estimatedSize);
		boolean underscoreWritten = true;
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if ((ch >= 'A') && (ch <= 'Z')) {
				if (!underscoreWritten) {
					result.append(sep);
				}

			}
			result.append((upperCase) ? CharUtil.toAsciiUpperCase(ch)
					: CharUtil.toAsciiLowerCase(ch));
			underscoreWritten = (ch == sep);
		}
		return result.toString();
	}

	/**
	 * Helper function for camel case functions.
	 * 
	 * @param value
	 * @param upperCase
	 * @return
	 */
	private static String toCamelCaseHelper(String value, boolean upperCase, char sep) {
		if (value == null) {
			return null;
		}
		if (value.length() == 0) {
			return "";
		}
		char firstChar = value.charAt(0);
		char firstCharCorrected = (upperCase) ? CharUtil
				.toAsciiUpperCase(firstChar) : CharUtil
				.toAsciiLowerCase(firstChar);
		if (value.indexOf(sep) == -1) {
			if (firstChar != firstCharCorrected) {
				return firstCharCorrected + value.substring(1);
			} else {
				return value;
			}
		}
		StringBuilder result = new StringBuilder(value.length());
		result.append(firstCharCorrected);
		boolean nextIsUpperCase = false;
		for (int i = 1; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (ch == sep) {
				nextIsUpperCase = true;
			} else {
				if (nextIsUpperCase) {
					result.append(CharUtil.toAsciiUpperCase(ch));
					nextIsUpperCase = false;
				} else {
					result.append(ch);
				}
			}
		}
		return result.toString();
	}
}
