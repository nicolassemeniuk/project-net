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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.project.base.compatibility.Compatibility;

import org.apache.log4j.Logger;

/**
 * Provides a cache of compiled stylesheets.
 * <p>
 * This cache maintains a separate entry for each absolute resolved stylesheet based
 * on configuration settings / customized stylesheets.
 * It uses the normal search hierarchy to locate a customized stylesheet.
 * If, at runtime, any changes are made to the properties or settings that control
 * customized stylesheets, this will be reflected by a different key being generated
 * for the same stylesheet path and thus a different stylesheet returned.
 * </p>
 * <p>
 * This cache grows forever; it will eventually contain a copy of every stylesheet
 * ever requested.  Consider a customer with the product translated into 3 languages
 * (or 2 languages plus our default English stylesheets).  Since we have 206 stylesheets,
 * there may be up to 618 entries in this cache if every stylesheet has been requested
 * in every language.  If the same customer renamed one of the configuration directories
 * and modified a property without restarting the application server, those stylesheets
 * would have to be reloaded.
 * </p>
 * <p>
 * This cache currently checks for modified stylesheet files every time ones is requested
 * so
 * @author Tim Morrow
 * @since Version 7.6.3
 */
class StylesheetCache extends Cache {

    private static final Logger logger = Logger.getLogger(StylesheetCache.class);

    /**
     * Determines whether we should check for modified XSL files or not.
     * Currently this appears to take an insignificant amount of time, so it
     * is not necessarily worth turning off; doing so will require application server
     * restarts every time an XSL file changes
     */
    private static final boolean IS_CHECK_MODIFIED_FILES = true;

    /**
     * The singleton cache.
     */
    private static final StylesheetCache CACHE = new StylesheetCache(new StylesheetLoader());

    /**
     * Returns a singleton instance of the cache.
     * @return
     */
    static synchronized StylesheetCache getInstance() {
        return CACHE;
    }

    /**
     * Constructs a key based on the specified stylesheet path.
     * @param path the path to the stylesheet
     * @return the key that uniquely identifies the cache entry for the stylesheet on that path
     */
    static Cache.IKey makeKey(String path) {

        // The key to the cache
        Cache.IKey key = null;

        // Get the pathFromServletRoot to the file from the servlet root
        String pathFromServletRoot = path.replace('/', File.separatorChar).replace('\\', File.separatorChar);

        if (XSLFileResolver.isLookInCustomXSLPath()) {
            XSLFileResolver.CustomXSLFile xslFile = XSLFileResolver.getCustomXSLFile(pathFromServletRoot);
            if (xslFile != null) {
                // We found a custom file
                key = new CustomKey(pathFromServletRoot, xslFile.getCustomXSLRootPath(), xslFile.getConfigDir());
                logger.debug("XSLFileResolver.getXSLFile - Found custom XSL file.");
            }
        }

        if (key == null) {
            // If we still haven't got an XSL file either because we didn't look, or
            // couldn't find any, grab it from the default location
            key = new ResourceKey(pathFromServletRoot);
        }

        return key;
    }

    //
    // Instance Members
    //

    /**
     * Creates a new stylesheet cache for the specified loader.
     * @param cacheLoader the loader to use to load entries
     */
    private StylesheetCache(StylesheetLoader cacheLoader) {
        super(cacheLoader, IS_CHECK_MODIFIED_FILES);
    }

    /**
     * Returns the templates for the specified stylesheet path,
     * loading from the cache if available or cacheing if not already
     * in the cache.
     * <p>
     * This method returns the appropriate templates, whether default or
     * overridden templates.
     * </p>
     * @param path the path to the stylesheet
     * @return the templates for the stylesheet
     * @throws XMLFormatException if there is a problem locating the stylesheet
     * or producing the templates
     */
    Templates getTemplates(String path) throws XMLFormatException {
        try {
            return ((StylesheetEntry) getEntry(makeKey(path))).getTemplates();
        } catch (CacheLoadException e) {
            throw new XMLFormatException("Error loading stylesheet for path " + path + " into cache: " + e, e);
        }
    }

    //
    // Nested top-level classes
    //

    /**
     * Implements a cache loader that creates Templates from a
     * stylesheet file.
     */
    private static class StylesheetLoader implements ICacheLoader {
        /**
         * Loads the entry for the specified key where the key
         * must be a <code>FileKey</code>.
         * @param key <code>FileKey</code> for which to load the entry
         * @return the entry which is a <code>StylesheetEntry</code>
         * @throws CacheLoadException if there is a problem loading the item;
         * the causing Throwable should be set to the original exception (if any)
         */
        public Cache.IEntry load(Cache.IKey key) throws CacheLoadException {
            IStylesheetKey stylesheetKey = (IStylesheetKey) key;

            Templates templates;
            try {
                // Read the XSL file, fix the namespace and create templates from it
                templates = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(
                                XMLFormatter.fixNamespaceForModernXSL(XMLFormatter.readContents(stylesheetKey.getInputStream()))
                        )));
            } catch (IOException e) {
                throw new CacheLoadException("Error reading XSL stream for file " + stylesheetKey.getName() + ": " + e, e);
            } catch (TransformerException e) {
                throw new CacheLoadException("Error compiling stylesheet from file " + stylesheetKey.getName() + ": " + e, e);
            } catch (XMLFormatException e) {
                throw new CacheLoadException("Error reading stylesheet " + stylesheetKey.getName() + ": " + e, e);
            }

            return new StylesheetEntry(templates, stylesheetKey.getModificationTime());
        }

    }

    static interface IStylesheetKey extends Cache.IKey {

        InputStream getInputStream() throws IOException;

        String getName();

        long getModificationTime();

        boolean isChangedSince(long sinceTime);


    }


    /**
     * Provides a key that is simply a resource on the application server.
     */
    private static class ResourceKey implements IStylesheetKey {

        private String pathFromServletRoot;

        /**
         * Creates a new resource key for the specified stylesheet path.
         * @param pathFromServletRoot the path to the stylesheet
         */
        ResourceKey(String pathFromServletRoot) {
            this.pathFromServletRoot = pathFromServletRoot;
        }

        public boolean isChangedSince(long sinceTime) {
            return false;
        }

        public InputStream getInputStream() throws IOException {
            return Compatibility.getXSLProvider().getInputStream(this.pathFromServletRoot);
        }

        public long getModificationTime() {
            return 0;  //To change body of implemented methods use Options | File Templates.
        }

        public String getName() {
            return this.pathFromServletRoot;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ResourceKey)) return false;

            final ResourceKey resourceKey = (ResourceKey) o;

            if (pathFromServletRoot != null ? !pathFromServletRoot.equals(resourceKey.pathFromServletRoot) : resourceKey.pathFromServletRoot != null) return false;

            return true;
        }

        public int hashCode() {
            return (pathFromServletRoot != null ? pathFromServletRoot.hashCode() : 0);
        }

        public String toString() {
            return pathFromServletRoot;
        }
    }

    /**
     * Provides a key that is a custom-defined stylesheet.
     */
    private static class CustomKey implements IStylesheetKey {

        /**
         * The path determined by a property in which all custom stylesheets
         * are located.
         */
        private final String customXSLRootPath;

        /**
         * The configuration directory in which the stylesheet for this
         * key is located.
         */
        private final String configDir;

        /** The XSLFile which is the stylesheet. */
        private final File xslFile;

        /**
         * Creates a new key based on the specified stylesheet path and
         * path to custom files.
         * @param pathFromServletRoot the path to the stylesheet as passed from the
         * JSP page
         * @param customXSLRootPath the file path to custom stylesheets
         * @param configDir the configuration directory below which the stylesheet
         * is located; when null, the customXSLRootPath is used on its own
         */
        CustomKey(String pathFromServletRoot, String customXSLRootPath, String configDir) {
            this.customXSLRootPath = customXSLRootPath;
            this.configDir = configDir;
            this.xslFile = resolveFile(pathFromServletRoot, customXSLRootPath, configDir);
        }

        /**
         * Resolves the actual file based on the parameters.
         * @param pathFromServletRoot the path to the stylesheet as passed from the
         * JSP page
         * @param customXSLRootPath the file path to custom stylesheets
         * @param configDir the configuration directory below which the stylesheet
         * is located; when null, the customXSLRootPath is used on its own
         * @return the file for that stylesheet from the custom stylesheet location
         */
        private static File resolveFile(String pathFromServletRoot, String customXSLRootPath, String configDir) {
            File file;
            if (configDir == null) {
                file = new File(customXSLRootPath + File.separator + pathFromServletRoot);
            } else {
                file = new File(XSLFileResolver.resolveCustomXSLPath(customXSLRootPath, configDir) + File.separator + pathFromServletRoot);
            }

            return file;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CustomKey)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            final CustomKey customKey = (CustomKey) o;

            if (!configDir.equals(customKey.configDir)) {
                return false;
            }
            if (!customXSLRootPath.equals(customKey.customXSLRootPath)) {
                return false;
            }

            return true;
        }

        public InputStream getInputStream() throws IOException {
            return new FileInputStream(xslFile);
        }

        public String getName() {
            return xslFile.getAbsolutePath();
        }

        public long getModificationTime() {
            return xslFile.lastModified();
        }

        public boolean isChangedSince(long sinceTime) {
            return (xslFile.lastModified() > sinceTime);
        }

        public int hashCode() {
            int result = super.hashCode();
            result = 29 * result + customXSLRootPath.hashCode();
            result = 29 * result + configDir.hashCode();
            return result;
        }

    }

    /**
     * Provides an entry in the stylesheet cache that holds the cached
     * Templates object.
     */
    private static class StylesheetEntry implements Cache.IEntry {
        private final Templates templates;
        private final long lastModified;

        /**
         * Creates a new entry for the specified file and compiled templates.
         * @param templates the parsed XSL templates
         * @param lastModified the date of last modification of the stylesheet
         * that the entry represents
         */
        StylesheetEntry(Templates templates, long lastModified) {
            this.templates = templates;
            this.lastModified = lastModified;
        }

        /**
         * Returns the compiled templates for the stylesheet
         * @return the compiled templates
         */
        Templates getTemplates() {
            return this.templates;
        }

        /**
         * Indicates whether this entry has expired based on the last modified
         * date of the current entry and the last modified specified by the key.
         * @param cacheKey the <code>IStylesheetKey</code>
         * @return true if this entry is older than the resource represented
         * by the key
         */
        public boolean isExpired(Cache.IKey cacheKey) {
            return ((IStylesheetKey) cacheKey).isChangedSince(this.lastModified);
        }
    }

}
