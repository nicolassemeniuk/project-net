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

package net.project.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to add/update copywrite notice for project.net opensource release
 *
 * @author Avinash Bhamare (avinash@avibha.com) for Project.Net
 */

public class Bfd3005 {

	private Bfd3005() {} // not instantiable :) strictly standalone utility

	private static final Pattern PATH_SEPARATOR = Pattern.compile("[\\s:;,]+");
	private static final Pattern MATCH_ALL      = Pattern.compile(".*");

	// trick to detect default platform charset
	private static final Charset DEFAULT_PLATFORM_CHARSET =
		Charset.forName(new InputStreamReader(new ByteArrayInputStream(new byte[0])).getEncoding());


	public static URI[] listFiles(
			String directory, boolean recurse, String includes, String excludes) {

		// prepare dir
		File dir = parsePath(directory);

		// prepare expressions
		Matcher[] includes2 = parseExpressions(includes);
		if (includes2.length == 0) includes2 = new Matcher[] {MATCH_ALL.matcher("")};
//		if (includes2.length == 0) includes2 = parseExpressions("#.*"); // match all
		Matcher[] excludes2 = parseExpressions(excludes);

		// do the real work
		List uris = new ArrayList();
		Set history = recurse ? new HashSet() : null;
		listFiles2(dir, includes2, excludes2, uris, history);

		// output results
		URI[] results = new URI[uris.size()];
		uris.toArray(results);
		return results;
	}

	/** Parses OS insensitive file path, stripping off leading URI scheme, if any */
	private static File parsePath(String path) {
		path = (path == null ? "" : path.trim());
		if (path.startsWith("file://"))  {
			path = path.substring("file://".length());
		} else if (path.startsWith("file:")) {
			path = path.substring("file:".length());
		}

		if (path.length() == 0 || path.equals(".")) {
			path = System.getProperty("user.dir", ".") ; // CWD
		} else {
			// convert separators to native format
			path = path.replace('\\', File.separatorChar);
			path = path.replace('/',  File.separatorChar);

			if (path.startsWith("~")) {
				// substitute Unix style home dir: ~ --> user.home
				String home = System.getProperty("user.home", "~");
				path = home + path.substring(1);
			}
		}

		return new File(path);
	}

	/**
	 * Parses wildcard expressions or regexes into regex matchers, splitting on
	 * one or more whitespace or ':' or ';' or ',' path separators.
	 */
	private static Matcher[] parseExpressions(String expressions) {
		expressions = (expressions == null ? "" : expressions.trim());
		if (expressions.length() == 0) return new Matcher[0]; // optimization

		String[] exprs = PATH_SEPARATOR.split(expressions);
//		String[] exprs = expressions.split("[\\s:;,]+");
		Matcher[] matchers = new Matcher[exprs.length];
		int size = 0;
		for (int i=0; i < exprs.length; i++) {
			if (exprs[i].length() > 0) {
				String regex = expression2Regex(exprs[i]);
				matchers[size++] = Pattern.compile(regex).matcher("");
			}
		}

		if (size == matchers.length) return matchers;
		Matcher[] results = new Matcher[size];
		System.arraycopy(matchers, 0, results, 0, size);
		return results;
	}

	/** translates a wildcard expression or regex to a regex */
	private static String expression2Regex(String expr) {
		if (expr.startsWith("#")) return expr.substring(1); // it's a regex

		expr = "*" + File.separatorChar + expr;             // anypath/expr

		// convert separators to native format:
		expr = expr.replace('\\', File.separatorChar);
		expr = expr.replace('/',  File.separatorChar);

		// escape all chars except wildcards, substitute wildcards with ".*" regex:
		StringBuffer buf = new StringBuffer(3 * expr.length());
		for (int i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i);
			if (c == '*') {
				buf.append(".*"); // wildcard --> regex
			} else if (c == '\\') {
				buf.append(c);
				buf.append(c); // escape backslash
			} else {
				buf.append("\\Q"); // quote begin
				buf.append(c);
				buf.append("\\E"); // quote end
			}
		}
		return buf.toString();
	}

	/** the work horse: recursive file system tree walker */
	private static void listFiles2(
			File dir, Matcher[] includes, Matcher[] excludes, List uris, Set history) {

		boolean recurse = (history != null);
		try { // avoid infinite cycles in directory traversal (cyclic symlinks etc.)
//			if (DEBUG) System.err.println(dir.getCanonicalPath());
			if (recurse && !history.add(dir.getCanonicalPath())) return;
		} catch (IOException e) {
			return; // harmless
		}

		File[] files = dir.listFiles();
		if (files == null) return; // dir does not exist

		// breadth-first search
		for (int i=0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				for (int j=0; j < includes.length; j++) {
					if (includes[j].reset(file.getPath()).matches()) {
						boolean exclude = false;
						for (int k=0; !exclude && k < excludes.length; k++) {
							exclude = excludes[k].reset(file.getPath()).matches();
						}
						if (!exclude) uris.add(file.toURI());
						break; // move to next file (mark as non-dir)
					}
				}

				// mark as non-directory, avoiding expensive isDirectory() calls below
				files[i] = null;
			}
		}

		// recurse into directories
		for (int i=0; recurse && i < files.length; i++) {
			if (files[i] != null) {
				listFiles2(files[i], includes, excludes, uris, history);
			}
		}
	}

	/**
	 * Reads until end-of-stream and returns all read bytes, finally closes the stream.
	 *
	 * @param input the input stream
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @return the bytes read from the input stream
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		try {
			if (input.getClass() == ByteArrayInputStream.class) { // fast path
				synchronized (input) { // better safe than sorry
					int avail = input.available();
					if (avail >= 0) { // better safe than sorry
						byte[] buffer = new byte[avail];
						input.read(buffer);
						return buffer;
					}
				}
			}

			// safe and fast even if input.available() behaves weird or buggy
			int size = java.lang.Math.max(256, input.available());
			byte[] buffer = new byte[size];
			byte[] output = new byte[size];

			size = 0;
			int n;
			while ((n = input.read(buffer)) >= 0) {
				if (size + n > output.length) { // grow capacity
					byte tmp[] = new byte[java.lang.Math.max(2 * output.length, size + n)];
					System.arraycopy(output, 0, tmp, 0, size);
					System.arraycopy(buffer, 0, tmp, size, n);
					buffer = output; // use larger buffer for future larger bulk reads
					output = tmp;
				} else {
					System.arraycopy(buffer, 0, output, size, n);
				}
				size += n;
			}

			if (size == output.length) return output;
			buffer = null; // help gc
			buffer = new byte[size];
			System.arraycopy(output, 0, buffer, 0, size);
			return buffer;
		} finally {
			if (input != null) input.close();
		}
	}

	/**
	 * Reads until end-of-stream and returns all read bytes as a string, finally
	 * closes the stream, converting the data with the given charset encoding, or
	 * the system's default platform encoding if <code>charset == null</code>.
	 *
	 * @param input the input stream
	 * @param charset the charset to convert with, e.g. <code>Charset.forName("UTF-8")</code>
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @return the bytes read from the input stream, as a string
	 */
	public static String toString(InputStream input, Charset charset) throws IOException {
		if (charset == null) charset = DEFAULT_PLATFORM_CHARSET;
		byte[] data = toByteArray(input);
		return charset.decode(ByteBuffer.wrap(data)).toString();
	}


	/*... Real stuff using all api's above ...*/
	public static void main(String[] parameters){

		String dirname = null;
		String licencefile =  null;
		String includesExpresion = "";
		String excludesExpression = "";
        String licString = "";
		try{
			dirname = parameters[0];
			licencefile = parameters[1];
			includesExpresion = (parameters.length > 2)? parameters[2] : "";
			excludesExpression = (parameters.length > 3)? parameters[3] : "";
			//System.out.println(includesExpresion + excludesExpression );

		}catch(Exception ex){
			System.out.println("Usage: " );
			System.out.println("java Bfd3005 dirname licencefile [includesExpresion] [excludesExpression]");
			String detailedHelp ="dirname ";
			detailedHelp += "           the path or URI of the directory to start at. Leading ";
			detailedHelp += "           <code>'file://'</code> and <code>'file:'</code> URI ";
			detailedHelp += "           prefixes are stripped off if present. If <code>null</code> ";
			detailedHelp += "           or <code>''</code> or <code>'.'</code> defaults to the ";
			detailedHelp += "           current working directory. ";
			detailedHelp += "           <p> ";
			detailedHelp += "           Absolute examples: <code>'/tmp/lib'</code>, ";
			detailedHelp += "           <code>'file:/tmp/lib'</code>, ";
			detailedHelp += "           <code>'file:///tmp\\lib'</code>,<code>'C:\\tmp\\lib'</code>, ";
			detailedHelp += "           <code>'file://C:\\tmp\\lib'</code>, Windows UNC ";
			detailedHelp += "           <code>'\\\\server\\share\\tmp\\lib'</code>, ";
			detailedHelp += "           <code>'file:\\\\server\\share\\tmp\\lib'</code> ";
			detailedHelp += "           <code>'file://\\\\server\\share\\tmp\\lib'</code>, ";
			detailedHelp += "           etc. ";
			detailedHelp += "           <p> ";
			detailedHelp += "           Relative examples: <code>'.'</code>, ";
			detailedHelp += "           <code>'/com/avibha/util/CVS'</code>,<code>'/lib/CVS'</code> ";
			detailedHelp += "licencefile ";
			detailedHelp += "	      filename contaning the license notice";
			detailedHelp += "includesExpresion ";
			detailedHelp += "           zero or more wildcard or regular expressions to match for ";
			detailedHelp += "           result set inclusion; as in ";
			detailedHelp += "           <code>File.getPath().matches(regex)</code>. If ";
			detailedHelp += "           <code>null</code> or an empty string defaults to matching ";
			detailedHelp += "           all files. Example: <code>'*.xml *.xsl'</code>. Example: ";
			detailedHelp += "           <code>'*.xml, *.xsl'</code>. ";
			detailedHelp += "excludesExpression ";
			detailedHelp += "           zero or more wildcard or regular expressions to match for ";
			detailedHelp += "           result set exclusion; as in ";
			detailedHelp += "           <code>File.getPath().matches(regex)</code>. If ";
			detailedHelp += "           <code>null</code> or an empty string defaults to matching ";
			detailedHelp += "           (i.e. excluding) no files. Example: <code>'*.xml *.xsl'</code>. ";
			detailedHelp += "           Example: <code>'*.xml, *.xsl'</code>. ";
			System.out.println(detailedHelp);


			ex.printStackTrace();
			}
		// all parametrs ok!!

		// read licence notice and cache as string/buffer?
        File licFile = new File(licencefile);
        if( !licFile.exists() || !licFile.canRead() ){
            System.out.println("Sorry, unable to read licence file.." + licFile.getAbsolutePath() );
            return;
        }else{
			try{
            licString = toString(new FileInputStream(licFile), null);
            licString = licString.endsWith("\n")?licString:licString+"\n";
			}catch(IOException licEx){System.out.println("Sorry, unable to read licence file.." + licFile.getAbsolutePath() );}
        }

		URI[] fileUris = listFiles(dirname, true, includesExpresion , excludesExpression);
		System.out.println(fileUris.length);
		for(int fileno = 0; fileno < fileUris.length; fileno++ ){
			try{
			System.out.println("Processing..."+ fileUris[fileno]);
			String fileString = toString(new FileInputStream(new File(fileUris[fileno])), null);
			FileOutputStream fot = new FileOutputStream(new File(fileUris[fileno]));
			// REPLACE the old licence notice...
			fot.write((licString  + ((fileString.indexOf("http://dev.project.net/licenses/PPL1.0") >0)? (fileString.substring(fileString.indexOf("*/")+2, fileString.length() )):	fileString)).getBytes());
			fot.close();
			}catch(Exception ourEx){
				System.out.println("Enable to update licence notice for file:" + (new File(fileUris[fileno])).getAbsolutePath() );
			}
		}
	}

}

