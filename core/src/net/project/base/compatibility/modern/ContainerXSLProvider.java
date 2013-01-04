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
package net.project.base.compatibility.modern;

import java.io.InputStream;

import net.project.base.compatibility.IXSLProvider;

import org.apache.log4j.Logger;

/**
 * Provides access to XSL content from a specified location in the
 * servlet container classpath.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ContainerXSLProvider implements IXSLProvider {

    private static final Logger logger = Logger.getLogger(ContainerXSLProvider.class);

    /** The default sub-directory from which to find xsl content. */
    private static final String RESOURCE_LOCATION = "/config/xsl/";

    /**
     * Returns a content located in the <code>/config/xsl</code> path
     * in <code>WEB-INF/classes</code>.
     *
     * @param path the relative path to the resource
     * @return an inputstream to the resource
     */
    public InputStream getInputStream(String path) {

        String relativePath = path.replace('\\', '/');

        // Chop off a leading separator since the repository path
        // might have one, and we'll make sure it does anyway
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        String fullPath = RESOURCE_LOCATION + relativePath;

        if (logger.isDebugEnabled()) {
            logger.debug("Fetching XSL resource from container classes at: " + fullPath);
        }

        return getClass().getResourceAsStream(fullPath);
    }

}
