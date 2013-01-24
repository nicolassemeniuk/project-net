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
 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 16593 $
|        $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|      $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.test.util.TestProperties;

import org.apache.xalan.xsltc.compiler.XSLTC;

/**
 * Tests that all stylehseets can be parsed.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class StylesheetTransformTest extends TestCase {

    public StylesheetTransformTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(StylesheetTransformTest.class);

        return suite;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests that all XSL files can be transformed by the
     * interpretive transformation mechanism.
     */
    public void testInterpretiveTransform() {
        System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        recursePath(getXSLRootPath(), new InterpretiveTester());
    }

    /**
     * Tests that all XSL files can be transformed by the
     * compiling transformation mechanism.
     */
    public void testCompilingTransform() {
        // 10/29/2003 - Tim
        // Currently disabled because any XSL file that uses the
        // XSLFormat:formatNumber(String) extension function will fail
        // The compiling transformer seems to be immature
        // We're not using it in the application until this works
        System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
        recursePath(getXSLRootPath(), new CompilingTester());
    }

    /**
     * Recurses over XSL files starting at the specified path.
     * @param rootPath the XSL root path
     * @param tester the tester to use to test files
     */
    private void recursePath(String rootPath, IStylesheetTester tester) {

        File rootDir = new File(rootPath);
        if (!rootDir.isDirectory()) {
            fail("XSL File root path must be a directory: " + rootPath);
        }

        test(rootDir, tester);
    }

    /**
     * Tests the specified file or directory.
     * @param file the file or directory to test
     * @param tester the tester to use
     */
    private void test(File file, IStylesheetTester tester) {

        if (file.isDirectory()) {
            // Recursively test all directories and files
            for (Iterator it = Arrays.asList(file.listFiles()).iterator(); it.hasNext();) {
                File nextFile = (File) it.next();
                test(nextFile, tester);
            }

        } else {
            // It is a file; make sure it is a stylesheet
            if (file.getAbsolutePath().endsWith(".xsl")) {
                try {
                    tester.test(file);
                } catch (TransformException e) {
                    fail("Error testing file " + file + ": " + e);
                }
            }
        }
    }

    /**
     * Returns XML consisting of only a single element called <code>Test</code>.
     * @return the test XML
     */
    private static String getTestXML() {
        return "<?xml version=\"1.0\" ?><Test />";
    }

    private static String getXSLRootPath() {
        String rootPath = TestProperties.getInstance().getProperty("xslRootPath");
        if (rootPath == null) {
            fail("Missing setting 'xslRootPath' in test.properties file.  This setting should be the root at which to locate XSL files for testing transformation.");
        }
        return rootPath;
    }

    //
    // Helper classes
    //

    private static interface IStylesheetTester {
        void test(File xslFile) throws TransformException;
    }

    private static class TransformException extends Exception {

        public TransformException(Throwable cause) {
            super(cause);
        }

        public TransformException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Provides a Stylesheet Tester that interprets stylesheets
     * to test their transformation abilities.
     */
    private static class InterpretiveTester implements IStylesheetTester {

        public void test(File xslFile) throws TransformException {
            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer(new StreamSource(new FileInputStream(xslFile)));
                transformer.transform(
                        new StreamSource(new StringReader(getTestXML())),
                        new StreamResult(new StringWriter()));
            } catch (FileNotFoundException e) {
                throw new TransformException(e);
            } catch (TransformerException e) {
                throw new TransformException(e);
            }
        }
    }

    /**
     * Provides a Stylesheet Tester that compiles stylesheets
     * to test their transformation abilities.
     */
    private static class CompilingTester implements IStylesheetTester {

        public void test(File xslFile) throws TransformException {
            try {
                XSLTC compiler = new XSLTC();
                boolean compileOk = compiler.compile(new FileInputStream(xslFile), "abc");
                if (!compileOk) {
                    throw new TransformException(new Exception("Unable to compile " + xslFile));
                }
                //Templates templates = TransformerFactory.newInstance().newTemplates(new StreamSource(new FileInputStream(xslFile)));
                //Transformer transformer = templates.newTransformer();
                //transformer.transform(
                //        new StreamSource(new StringReader(getTestXML())),
                //        new StreamResult(new StringWriter()));
            } catch (FileNotFoundException e) {
                throw new TransformException(e);
            }
        }
    }

}
