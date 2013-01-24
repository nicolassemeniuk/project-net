/*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 17102 $
|        $Date: 2008-03-25 16:17:07 +0530 (Tue, 25 Mar 2008) $
|      $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.IResourceProvider;

/**
 * Provides web container resources for the Bluestone app server.
 * <p>
 * Resources are located in the directory specified by the <code>repositoryPath</code>
 * configuration settings.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.4
 */
class TestResourceProvider implements IResourceProvider {

    /**
     * Returns the specified resource from the <code>repositoryPath</code>
     * location.
     * <p>
     * The resource is located at <code>repositoryPath/resourcePath</code>.
     * </p>
     *
     * @param resourcePath the relative path to the resource
     * @return the resource inputstream
     * @throws java.io.FileNotFoundException if the physical file is not
     * found
     */
    public InputStream getResourceAsStream(String resourcePath) throws FileNotFoundException {

        String relativePath = resourcePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);

        // Chop off a leading separator since the repository path
        // might have one, and we'll make sure it does anyway
        if (relativePath.startsWith(File.separator)) {
            relativePath = relativePath.substring(1);
        }

        // First construct the entire path to the resource
        // Gets the path and ensures it has a trailing slash
        String repositoryPath = ((TestConfigurationProvider) Compatibility.getConfigurationProvider()).getFilePath();
        repositoryPath = repositoryPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
        repositoryPath = net.project.util.FileUtils.ensureTrailingSlash(repositoryPath);

        return new FileInputStream(new File(repositoryPath + relativePath));
    }

}
