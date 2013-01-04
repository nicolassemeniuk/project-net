/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.view.services;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.internal.util.Defense;
import org.apache.tapestry5.services.URLEncoder;

/**
 *  PnetURLEncoder for Tapestry pages to support non English characters in Tapestry URL
 *  This will override the Tapestry URLEncoder service with PnetURLEncoder service 
 */
public class PnetURLEncoderImpl implements URLEncoder {
	
	static final String ENCODED_NULL = "$N";

	static final String ENCODED_BLANK = "$B";

	/**
	 * Bit set indicating which character are safe to pass through (when encoding or decoding) as-is.  All other
	 * characters are encoded as a kind of unicode escape.
	 */
	private final BitSet safe = new BitSet(128);

	{
		markSafe("abcdefghijklmnopqrstuvwxyz");
		markSafe("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		markSafe("01234567890-_.:");
	}

	private void markSafe(String s) {
		for (char ch : s.toCharArray()) {
			safe.set((int) ch);
		}
	}

	public String encode(String input) {
		if (input == null)
			return ENCODED_NULL;

		if (input.equals(""))
			return ENCODED_BLANK;

		boolean dirty = false;

		int length = input.length();

		StringBuilder output = new StringBuilder(length * 2);

		for (int i = 0; i < length; i++) {
			char ch = input.charAt(i);

			if (ch == '$') {
				output.append("$$");
				dirty = true;
				continue;
			}

			int chAsInt = (int) ch;

			if (safe.get(chAsInt)) {
				output.append(ch);
				continue;
			}

			output.append(String.format("$%04x", chAsInt));
			dirty = true;
		}

		return dirty ? output.toString() : input;
	}

	public String decode(String input) {
		Defense.notNull(input, "input");

		if (input.equals(ENCODED_NULL))
			return null;

		if (input.equals(ENCODED_BLANK))
			return "";

		boolean dirty = false;

		int length = input.length();

		StringBuilder output = new StringBuilder(length * 2);

		for (int i = 0; i < length; i++) {
			char ch = input.charAt(i);

			if (ch == '$') {
				dirty = true;

				if (i + 1 < length && input.charAt(i + 1) == '$') {
					output.append('$');
					i++;

					dirty = true;
					continue;
				}

				if (i + 4 < length) {
					String hex = input.substring(i + 1, i + 5);

					try {
						int unicode = Integer.parseInt(hex, 16);

						output.append((char) unicode);
						i += 4;
						dirty = true;
						continue;
					} catch (NumberFormatException ex) {
						// Ignore.
					}
				}

				throw new IllegalArgumentException(
						String.format(
										"Input string '%s' is not valid; the '$' character at position %d should be followed by another '$' or a four digit hex number (a unicode value).",
										input, i + 1));
			}

			String encodedChar = null;

			if (!safe.get((int) ch)) {
				try {
					encodedChar = java.net.URLEncoder.encode("" + ch, "UTF-8");
				} catch (UnsupportedEncodingException pnetEx) {
					Logger.getLogger(PnetURLEncoderImpl.class).error(
							"Error occurred while encoding unsupported characters : " + pnetEx.getMessage());
				}
			}

			output.append(StringUtils.isNotEmpty(encodedChar) ? encodedChar : ch);
		}

		return dirty ? output.toString() : input;
	}
}
