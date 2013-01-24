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

 package net.project.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.compatibility.Compatibility;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * Provides methods to help resolves a physical XSL file from a servlet path.
 * The path to an xsl file may be specified in one of two ways:
 * <li>Relative to a JSP location, for example: <code>xsl/myfile.xsl</code> or
 * <code>../xsl/myfile.xsl</code>
 * <li>Absolute from the JSP root, for example: <code>/document/xsl/myfile.xsl</code>
 * <p>
 * Additionally, we provide a facility that first looks in a custom XSL directory
 * hierarchy before looking in the default application server location.  This is
 * to allow customers to override our standard XSL files with custom files, stored
 * by configuration.  That directory is given by {@link #CUSTOM_XSL_SUB_DIRECTORY_PROPERTY_NAME}.
 * </p>
 */
class XSLFileResolver implements java.io.Serializable {

    private static transient Logger logger = Logger.getLogger(XSLFileResolver.class);

    /**
     * The property that specifies the custom directory to look in for
     * XSL files, currently <code>prm.global.brand.xsl.customdirectory</code>
     */
    static final String CUSTOM_XSL_SUB_DIRECTORY_PROPERTY_NAME = "prm.global.brand.xsl.customdirectory";

    /**
     * Returns an InputStreamfor the XSL file with the specified path.
     * This first looks in our XSL directories, then uses the default
     * XSL file if there is no XSL file in our directories.
     * @param filePath the path to the file; this MUST be absolute from
     * the servlet root.  For example, <code>/document/xsl/myfile.xsl</code> is
     * acceptable but <code>xsl/myfile.xsl</code> is not even when called from
     * a page in the <code>/document</code> package.
     * @return the InputStream
     * @throws XMLFormatException if the file could not be located
     */
    InputStream getXSLFileAsStream(String filePath) throws XMLFormatException {

        InputStream input = null;

        // Get the path to the file from the servlet root
        String pathFromServletRoot = filePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);

        if (logger.isDebugEnabled()) {
            logger.debug("Fetching XSL from relative path: " + pathFromServletRoot);
        }

        try {

            if (isLookInCustomXSLPath()) {
                // Look in our custom XSL directory structure for an XSL file
                // that overrides the one in the normal location
                CustomXSLFile xslFile = getCustomXSLFile(pathFromServletRoot);
                if (xslFile != null) {

                    input = xslFile.getInputStream();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Found custom XSL file at path: " + xslFile.file.getAbsolutePath());
                    }

                }
            }

            if (input == null) {
                // If we still haven't got an XSL file either because we didn't look, or
                // couldn't find any, grab it from the default location
                input = Compatibility.getXSLProvider().getInputStream(pathFromServletRoot);
            }

            if (input == null) {
                // If we still haven't got XSL file, then a problem occurred.
                throw new XMLFormatException("Error getting XSL content for file: " + filePath);
            }

        } catch (java.io.IOException ioe) {
            throw new XMLFormatException("Error getting XSL content for file: " + filePath + ": " + ioe, ioe);

        }

        return input;
    }

    /**
     * Locates a custom XSL file by looking in each custom directory.
     * @param pathFromServletRoot the path to the XSL file from the servlet
     * root
     * @return the custom XSL File or null if there was no custom XSL file
     * at the specified path
     */
    static CustomXSLFile getCustomXSLFile(String pathFromServletRoot) {

        String configDir = null;
        File foundFile = null;

        // Get the custom XSL Root Path
        String customXSLRootPath = SessionManager.getCustomXSLRootPath();

        // Get all sub directories from the property hierarchy
        Collection subDirectoryHierarchy = PropertyProvider.getAll(CUSTOM_XSL_SUB_DIRECTORY_PROPERTY_NAME);

        if (subDirectoryHierarchy.isEmpty()) {
            // If there are no sub-directory properties, then we just use
            // the root directory
            configDir = null;

            File f = new File(customXSLRootPath + File.separator + pathFromServletRoot);
            if (f.exists()) {
                foundFile = f;
            }

        } else {
            // Iterate over each sub directory, building the entire custom XSL path
            // based on customXSLRootPath setting and sub-directory

            for (Iterator it = subDirectoryHierarchy.iterator(); it.hasNext();) {
                // Get the custom XSL directory based on the sub-directory
                String nextConfigDir = (String) it.next();
                String customXSLPath = resolveCustomXSLPath(customXSLRootPath, nextConfigDir);

                File f = new File(customXSLPath + File.separator + pathFromServletRoot);
                if (f.exists()) {
                    // We found it.  Stop processing other directories
                    configDir = nextConfigDir;
                    foundFile = f;
                    break;
                }

            }

        } //end if

        CustomXSLFile xslFile;
        if (foundFile == null) {
            xslFile = null;
        } else {
            xslFile = new CustomXSLFile(customXSLRootPath, configDir, foundFile);
        }

        return xslFile;
    }

    /**
     * Indicates whether we should look in the custom XSL root path for
     * custom XSL files.
     * @return true if a customXSLRootPath setting is present in the configuraiton
     * file; false otherwise
     */
    static boolean isLookInCustomXSLPath() {
        String customXSLRootPath = SessionManager.getCustomXSLRootPath();
        if (customXSLRootPath != null && customXSLRootPath.length() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Returns the custom XSL path as determined by the custom XSL root path
     * and sub-directory
     * @param customXSLRootPath the root path
     * @param subDir the sub-directory below the root path
     * @return the custom XSL path
     */
    static String resolveCustomXSLPath(String customXSLRootPath, String subDir) {
        
        if (subDir != null && subDir.length() > 0) {
            
            // Add a path separator if it does not start with one
            if (!subDir.startsWith("\\") && 
                !subDir.startsWith("/") && 
                !subDir.startsWith(File.separator)) {
                
                subDir = File.separator + subDir;
            }
            
            // path includes sub directory
            customXSLRootPath = customXSLRootPath + subDir;
        }

        return customXSLRootPath;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        logger = Logger.getLogger(XSLFileResolver.class);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a structure which maintains the components that are used
     * to locate a custom xsl file.
     */
    static class CustomXSLFile {
        private final String customXSLRootPath;
        private final String configDir;
        private final File file;

        /**
         * Creates a CustomXSLFIle for the specified root path and optional
         * configuration dir that points to the custom file.
         * @param customXSLRootPath the path to the root of custom files
         * @param configDir the configuration sub-directory; may be null
         * @param file the file the custom stylesheet file located in the custom configuration
         */
        CustomXSLFile(String customXSLRootPath, String configDir, File file) {
            this.customXSLRootPath = customXSLRootPath;
            this.configDir = configDir;
            this.file = file;
        }

        /**
         * Returns the path to the root of custom xsl files.
         * @return the path
         */
        String getCustomXSLRootPath() {
            return this.customXSLRootPath;
        }

        /**
         * Returns the sub-directory below which files for the current configuration
         * are found.
         * @return the sub-directory; may be null
         */
        String getConfigDir() {
            return this.configDir;
        }

        /**
         * Returns an InputStream to the custom stylesheet.
         * @return the input stream
         * @throws IOException if there is a problem locating the file
         */
        InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }

    }

}
