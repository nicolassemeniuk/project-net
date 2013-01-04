/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.util;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests the functions in the {@link FileUtils} class.
 *
 * @author Tim Morrow
 * @since Version 7.5
 */
public class FileUtilsTest extends TestCase {

    public FileUtilsTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FileUtilsTest.class);
        
        return suite;
    }

    /**
     * Tests method {@link FileUtils#ensureTrailingSlash}.
     */
    public void testEnsureTrailingSlash() {

        assertEquals(null, FileUtils.ensureTrailingSlash(null));
        assertEquals("", FileUtils.ensureTrailingSlash(""));

        // Only a slash
        assertEquals("/", FileUtils.ensureTrailingSlash("/"));
        assertEquals("\\", FileUtils.ensureTrailingSlash("\\"));
        assertEquals(File.separator, FileUtils.ensureTrailingSlash(File.separator));

        // Got a trailing slash
        assertEquals("abc/def/", FileUtils.ensureTrailingSlash("abc/def/"));
        assertEquals("abc\\def\\", FileUtils.ensureTrailingSlash("abc\\def\\"));
        assertEquals("abc" + File.separator + "def" + File.separator, FileUtils.ensureTrailingSlash("abc" + File.separator + "def" + File.separator));

        // No trailing slash
        assertEquals("abc/def" + File.separator, FileUtils.ensureTrailingSlash("abc/def"));
        assertEquals("abc\\def" + File.separator, FileUtils.ensureTrailingSlash("abc\\def"));
        assertEquals("abc" + File.separator + "def" + File.separator, FileUtils.ensureTrailingSlash("abc" + File.separator + "def"));
        assertEquals("abc/def  " + File.separator, FileUtils.ensureTrailingSlash("abc/def  "));
        assertEquals("abc\\def  " + File.separator, FileUtils.ensureTrailingSlash("abc\\def  "));
        assertEquals("abc" + File.separator + "def  " + File.separator, FileUtils.ensureTrailingSlash("abc" + File.separator + "def  "));

        // Spaces
        assertEquals("/", FileUtils.ensureTrailingSlash("/  "));
        assertEquals("\\", FileUtils.ensureTrailingSlash("\\  "));
        assertEquals(File.separator, FileUtils.ensureTrailingSlash(File.separator + "  "));
        assertEquals("abc/", FileUtils.ensureTrailingSlash("abc/  "));
        assertEquals("abc\\", FileUtils.ensureTrailingSlash("abc\\  "));
        assertEquals("abc" + File.separator, FileUtils.ensureTrailingSlash("abc" + File.separator + "  "));
    }

    /**
     * Tests method {@link FileUtils#getFileExt}.
     */
    public void testGetFileExt() {

        // Empty string and no period
        assertEquals("", FileUtils.getFileExt(null));
        assertEquals("", FileUtils.getFileExt(""));
        assertEquals("", FileUtils.getFileExt(" "));
        assertEquals("", FileUtils.getFileExt("x"));

        // Period with no extension
        assertEquals("", FileUtils.getFileExt("."));
        assertEquals("", FileUtils.getFileExt("a."));
        assertEquals("", FileUtils.getFileExt("a.b.c.d."));

        // Period with extension
        assertEquals("x", FileUtils.getFileExt(".x"));
        assertEquals("xxx", FileUtils.getFileExt(".xxx"));
        assertEquals("x", FileUtils.getFileExt("a.x"));
        assertEquals("x", FileUtils.getFileExt("a.b.c.d.e.x"));

    }

    /**
     * Tests method {@link FileUtils#copy}.
     */
    public void testCopy() {

        File sourceFile = null;
        File destFile = null;

        try {

            // Test null string file paths
            try {
                FileUtils.copy((String) null, (String) null);
                fail();
            } catch (NullPointerException e) {
                // Success
            }

            // Test emptry string file paths
            try {
                FileUtils.copy("", "");
                fail();
            } catch (IllegalArgumentException e) {
                // Success
            }

            // Test null File paths
            try {
                FileUtils.copy((File) null, (File) null);
                fail();
            } catch (NullPointerException e) {
                // Success
            }

            // Test missing source file
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            sourceFile.delete();
            destFile.delete();
            try {
                FileUtils.copy(sourceFile, destFile);
                fail();
            } catch (IOException e) {
                // success
                assertTrue(!destFile.exists());
            }

            // Test present target file
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            try {
                FileUtils.copy(sourceFile, destFile);
                fail();
            } catch (IOException e) {
                // success
                assertTrue(sourceFile.canRead());
                assertTrue(destFile.canRead());
            }
            sourceFile.delete();
            destFile.delete();

            // Test successful copy
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            destFile.delete();
            try {
                FileUtils.copy(sourceFile, destFile);
                assertTrue(sourceFile.canRead());
                assertTrue(destFile.canRead());
            } catch (IOException e) {
                fail();
            }
            sourceFile.delete();
            destFile.delete();

        } catch (IOException e) {
            fail("Unexpected IOException executing tests: " + e);

        }

    }

    /**
     * Tests method {@link FileUtils#move}.
     */
    public void testMove() {

        File sourceFile = null;
        File destFile = null;

        try {

            // Test null File paths
            try {
                FileUtils.move(null, null);
                fail();
            } catch (NullPointerException e) {
                // Success
            }

            // Test missing source file
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            sourceFile.delete();
            destFile.delete();
            try {
                FileUtils.move(sourceFile, destFile);
                fail();
            } catch (IOException e) {
                // success
            }

            // Test present target file
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            try {
                FileUtils.move(sourceFile, destFile);
                // The move should be unsuccessful
                fail();
            } catch (IOException e) {
                // success
                // We should be able to continue to read the
                assertTrue(sourceFile.canRead());
            }
            sourceFile.delete();
            destFile.delete();

            // Test successful move
            sourceFile = File.createTempFile("test", null);
            destFile = File.createTempFile("test", null);
            destFile.delete();
            try {
                FileUtils.move(sourceFile, destFile);
                assertTrue(!sourceFile.exists());
                assertTrue(destFile.canRead());
            } catch (IOException e) {
                fail();
            }
            destFile.delete();


        } catch (IOException e) {
            fail("Unexpected IOException executing tests: " + e);

        }

    }

    public void testResolveNameFromPath() {

        try {
            FileUtils.resolveNameFromPath(null);
            fail("Unexpected success with null value");
        } catch (Exception e) {
            // Success
        }

        // Empty path is empty name
        assertEquals("", FileUtils.resolveNameFromPath(""));

        // No path separators means path is name
        assertEquals("x", FileUtils.resolveNameFromPath("x"));

        // Trailing separators dropped; then empty path is empty name
        assertEquals("", FileUtils.resolveNameFromPath("\\"));
        assertEquals("", FileUtils.resolveNameFromPath("/"));

        // Last slash is not a trailing separator
        assertEquals("/", FileUtils.resolveNameFromPath("\\/"));
        assertEquals("\\", FileUtils.resolveNameFromPath("/\\"));

        // Trailing separators dropped; then find name
        assertEquals("temp dir", FileUtils.resolveNameFromPath("c:\\temp dir\\"));
        assertEquals("temp dir", FileUtils.resolveNameFromPath("/temp dir/"));
        assertEquals("temp dir/file.txt", FileUtils.resolveNameFromPath("c:\\temp dir/file.txt\\"));
        assertEquals("temp dir\\file.txt", FileUtils.resolveNameFromPath("/temp dir\\file.txt/"));

        assertEquals("file.txt", FileUtils.resolveNameFromPath("c:\\temp dir\\file.txt"));
        assertEquals("temp dir/file.txt", FileUtils.resolveNameFromPath("c:\\temp dir/file.txt"));
        assertEquals("file.txt", FileUtils.resolveNameFromPath("c:/temp dir/file.txt"));
        assertEquals("temp dir\\file.txt", FileUtils.resolveNameFromPath("c:/temp dir\\file.txt"));
        assertEquals("file.txt", FileUtils.resolveNameFromPath("/temp dir/file.txt"));
        assertEquals("temp dir\\file.txt", FileUtils.resolveNameFromPath("/temp dir\\file.txt"));
        assertEquals("file.txt", FileUtils.resolveNameFromPath("\\temp dir\\file.txt"));
        assertEquals("temp dir/file.txt", FileUtils.resolveNameFromPath("\\temp dir/file.txt"));

    }

}
