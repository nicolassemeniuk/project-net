/*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 13835 $
|        $Date: 2005-01-29 03:13:48 +0530 (Sat, 29 Jan 2005) $
|      $Author: matt $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility.test;

import java.io.IOException;
import java.io.InputStream;
import net.project.base.compatibility.IXSLProvider;

/**
 * Provides a stream for a file located in the JSP hierarchy.
 *
 * @author Tim Morrow
 * @since Version 7.6.4
 */
class TestContentProvider implements IXSLProvider {

    public InputStream getInputStream(String path) throws IOException {
        throw new RuntimeException("TestContentProvider not implemented");
    }

}
