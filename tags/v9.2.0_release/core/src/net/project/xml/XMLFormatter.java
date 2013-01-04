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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * XMLFormatter provides convenience methods for formatting XML.
 * <p/>
 * Currently, a number of top-level parameter values are included in the stylesheet:
 * <ul>
 * <li><code>JspRootUrl</code> - the JSP Root URL as gotten from SessionManager
 * </ul>
 * These can be used in a stylesheet by declaring the parameter (optionally with a default value).
 * For example:<pre><code>
 *     &lt;xsl:param name="JspRootUrl" /&gt;
 * </code></pre>
 * <p/>
 * <h4>Custom XSL Stylesheet Searching</h4>
 * Normally, an XSL file is referenced relatively from the current JSP page
 * (for example "xsl/mystylesheet.xsl") or absolutely from the servlet root
 * (for example "/document/xsl/mystylesheet.xsl").  The XSL file is located
 * within the application file path specified in the "apserver.txt" file.<br>
 * Now it is possible to locate XSL files in an alternate directory in addition
 * to this one.  This allows custom XSL files to be defined that <i>override</i>
 * the normal XSL files.  The location of the custom XSL files is partially
 * specified by a property; hence the location can be customized on a per-configuration
 * basis.<br>
 * The location of custom XSL files is determined by:
 * <li>The <code>customXSLRootPath</code> configuration file setting</li>
 * <li>The {@link XSLFileResolver#CUSTOM_XSL_SUB_DIRECTORY_PROPERTY_NAME} property value</li><br>
 * For example, if configuration setting is:<br>
 * <code>customXSLRootPath=c:\prm\customxsl</code><br>
 * the property for the default configuration is <code>default</code> and the
 * specified stylesheet is <code>/document/xsl/mystylesheet.xsl</code><br>
 * then the XMLFormatter will first look in:<br>
 * <code>c:\prm\customxsl\default\document\xsl\mystylesheet.xsl</code><br>
 * If the stylesheet is not found there, then the normal location is used.
 * Note that this supports hierarchical searching of directories, based on the
 * user's current configuration.
 * <p/>
 * <h4>Usage</h4>
 * Typical usage in a JSP page is as follows:
 * <code><pre>
 *    &lt;jsp:useBean id="xmlFormatter" name="net.project.xml.XMLFormatter" scope="page" /&gt;
 *    ...
 *    &lt;jsp:setProperty name="xmlFormatter" property="stylesheet" value="mystylesheet.xsl" /&gt;
 *    &lt;jsp:setProperty name="xmlFormatter" property="xml" value="&lt;%=somebean.getXML()%&gt;" /&gt;
 *    &lt;jsp:getProperty name="xmlFormatter" property="presentation" /&gt;
 * </pre></code>
 * <p/>
 * Uses Java JAXP APIs, so the transformation engine is not specified here.
 * Any JAXP 1.1 compliant engine my be used by including its implementation
 * in the application classpath.
 * @since emu
 * @author Tim
 */
public class XMLFormatter implements java.io.Serializable {
    transient Logger logger = Logger.getLogger(XMLFormatter.class);

    /**
     * Turns on use of the stylesheet cache.
     * 10/29/2003 - Tim
     * !!!! It turns out the compiling transformer's support of
     * extension functions is immature.  Specifically, none of our XSL
     * files that use the XSLFormat.formatNumber(String) method work;
     * there appears to be some sort of parameter type mismatch.
     * <b>DO NOT TURN ON THE CACHEING FEATURE UNLESS ALL XSL FILES ARE TESTED
     * TO WORK WITH THE CACHE ON</b>
     * There is a Unit test that is currently disabled that will test
     * all files: {@link StylesheetTransformTest#testCompilingTransform()}
     * Only if it succeeds do we know that the compiling transformer will
     * work for all our files. !!!!
     * */
    private static final boolean IS_USE_CACHE = true;
    
    public static final String XML_HEADER_PATTERN = "<?xml version=\"1.0\" ?>";

    static {
        //Check for token caching being turned off through a system property
        String useCaching = System.getProperty("use.compiled.stylesheets");
        boolean bUseCaching = ((useCaching != null) && (useCaching.equals("false")));

        // To use the cache we must use Xalan's compiling transformer
        // This requires a separate Xalan instance; not the one that ships
        // with Java 1.4.2 JDK
        if (IS_USE_CACHE && !bUseCaching) {
            System.out.println("Using stylesheet compilation");
            System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
        } else {
            System.out.println("Stylesheet compilation disabled");
        }
    }

    /** Path to stylesheet */
    private String stylesheetPath = null;

    /** The XML presentation to be transformed. */
    private String xml = null;

    /** The parameters to pass to stylesheet. */
    private final Map parameters = new HashMap();

    /**
     * Creates a new XMLFormatter.
     */
    public XMLFormatter() {
        super();
    }


    /**
     * Sets the stylesheet to use for transformation.
     * @param stylesheetPath path to the stylesheet
     * @see #getStylesheet
     */
    public void setStylesheet(String stylesheetPath) {
        this.stylesheetPath = stylesheetPath;
    }


    /**
     * Returns the current stylesheet path
     * @return the current stylesheet path
     * @see #setStylesheet
     */
    public String getStylesheet() {
        return this.stylesheetPath;
    }

    /**
     * Sets the XML to be transformed.
     * @param xml the XML to be transformed
     */
    public void setXML(String xml) {
        this.xml = xml;
    }

    /**
     * Specifies parameters to pass to the stylesheet.
     * <p/>
     * Clears out the current parameters first.
     * Thse are in addition to default parameters, which are always passed.
     * @param parameters the parameters to pass where keys are <code>String</code>
     * parameter names and values are <code>Object</code> parameter values.
     */
    public void setParameters(Map parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }

    /**
     * Returns the presentation of of the transformed XML.
     * Ignores any XML that might be set by {@link #setXML}.
     * @param xml the xml to transform
     * @return the transformed XML
     * @throws IllegalStateException if there is a problem getting the
     * presentation; for example, the XML is invalid and cannot be parsed
     */
    public String getPresentation(String xml) {
        try {
            setXML(xml);
            return getPresentation();

        } catch (XMLFormatException xfe) {
            throw (IllegalStateException) new IllegalStateException("Error getting presentation: " + xfe.getMessage()).initCause(xfe);

        }
    }


    /**
     * Returns the presentation of of the transformed XML.
     * Assumes XML has already been specified with {@link #setXML}.  The stylesheet
     * used is specified by calling {@link #setStylesheet}.  The stylesheet
     * may be physically located in a number of places:
     * <li>If the setting <code>customXSLRootPath</code> is specified in the configuration file
     * then the path to the stylesheet is constructed by concatenating that value
     * and the property value given by <code>CUSTOM_XSL_SUB_DIRECTORY_PROPERTY_NAME</code>.
     * For example, if <code>customXSLRootPath</code> is <code>c:\myxsl</code>,
     * the property is set to <code>pnet</code> and the stylesheet path is <code>/document/xsl/myfile.xsl</code>
     * then the file is looked for as <code>c:\myxsl\pnet\document\xsl\myfile.xsl</code>
     * <li>If that fails, or if <code>customXSLRootPath</code> is not set then the standard
     * location is used.  This is currently provided by Bluestone APIs.
     * @return the transformed XML
     * @throws XMLFormatException if no stylesheet has been specified or
     * there is a problem reading the stylesheet
     */
    public String getPresentation() throws XMLFormatException {

        if(getStylesheet() == null) {
            throw new XMLFormatException("No stylesheet specified");
        }
      
        final StringBuffer xmlStringEditBuffer = new StringBuffer(this.xml);
        int indexOfHeader = xmlStringEditBuffer.indexOf(XML_HEADER_PATTERN);
        
		while (indexOfHeader!=-1) {
			xmlStringEditBuffer.delete(indexOfHeader, indexOfHeader+XML_HEADER_PATTERN.length());
			indexOfHeader = xmlStringEditBuffer.indexOf(XML_HEADER_PATTERN);
        }
        
		setXML(xmlStringEditBuffer.toString());
        
        if(!this.xml.startsWith("<?xml version")) {
            this.xml = addXMLDirective(xml);
        }
        
        // Now perform the transformation
        StringReader reader = new StringReader(this.xml);
        StringWriter writer = new StringWriter();

        long start = System.currentTimeMillis();
        if (IS_USE_CACHE) {
            Templates templates = StylesheetCache.getInstance().getTemplates(getStylesheet());
            transform(templates, reader, writer);
            if (logger.isDebugEnabled()) {
                logger.debug("XMLFormatter used compiled transform, elapsed: " + (System.currentTimeMillis() - start));
            }
        } else {
            transform(new XSLFileResolver().getXSLFileAsStream(getStylesheet()), reader, writer);
            if (logger.isDebugEnabled()) {
                logger.debug("XMLFormatter used interpreted transform, elapsed: " + (System.currentTimeMillis() - start));
            }
        }
        return writer.toString();

    }


    /**
     * Adds an XML Directive if it is missing from the XML.
     * An XML directive is present if the start of the XML is exactly equal
     * to <code>&lt;?xml version=</code>
     * @param xmlBody the XML to check
     * @return the XML prepended with an XML Directive
     */
    private String addXMLDirective(String xmlBody) {
        if(!xmlBody.startsWith("<?xml version=")) {
            xmlBody = "<?xml version=\"1.0\"?>" + xmlBody;
        }
        return xmlBody;
    }

    /**
     * Reads the contents of the file.
     * @param input the inputstream to read
     * @return the contents
     * @throws XMLFormatException if there is a problem reading the file
     */
    static String readContents(InputStream input) throws XMLFormatException {
        String content = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            in = new BufferedInputStream(input, 4096);
            out = new ByteArrayOutputStream();

            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            content = out.toString();

        } catch (IOException ioe) {
            throw new XMLFormatException("Error reading XSL content: " + ioe);

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // Do nothing
            }

            try {
                out.close();
            } catch (IOException e) {
                // Do nothing
            }

        }

        return content;
    }


    /**
     * Fixes the namespace to replace the old XSLT Draft namespace with the
     * XSLT Recommendation namespace, also adding the version= attribute.
     * <p>
     * Our previous transformer was based on Lotus XSL and was VERY old technology.
     * It required the OLD namespace as specified in the XSLT Draft.
     * If the incorrect namespace was present, it would simply fail to
     * transform with no reported errors.
     * </p>
     * <p>
     * This method replaces the old Draft &lt;xsl:stylesheet ...&gt; attribute with
     * the version 1.0 attribute: <br>
     * <code>&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0" xmlns="http://www.w3.org/TR/REC-html40"&gt;</code><br>
     * with this: <br>
     * <code>&lt;xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"&gt;</code><br>
     * Note:  It drops the "REC-html40" part too, due to graphical anomolies when it is present.
     * </p>
     * <p>
     * In the future, we should always use the 1.0 standard version.
     * </p>
     * <p>
     * Known limitations / points of interest:
     * <li>Assumes all tags in question are lowercase
     * <li>Assumes that after locating <code>&lt;xsl:stylesheet</code> the first
     * &gt; character is the terminator for that tag
     * <li>Assumes that after locating <code>xmlns:xsl=&quot;</code> the first
     * &quot; character is the terminator for that attribute
     * </p>
     * <p>
     * Tests indicate that on a single processor Pentium III Mobile 1.2 Ghz
     * this routines takes 0ms.
     * </p>
     * @param xsl the XSL content in which to fix the namespace
     * @return the xsl content with the namespace fixed, or the original
     * content if the most recent namespace could not be found
     */
    static String fixNamespaceForModernXSL(String xsl) {

        final String stylesheetElementMatch = "<xsl:stylesheet";
        final String oldXSLAttribute = "xmlns:xsl=\"http://www.w3.org/XSL/Transform/1.0\"";
        final String newXSLAttribute = "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"";
        final String versionAttribute = "version=\"1.0\"";
        final String htmlAttributeMatch = "xmlns=\"http://www.w3.org/TR/REC-html40\"";

        StringBuffer result = new StringBuffer(xsl);

        // Look for a stylesheet tag
        int stylesheetTagStartPosition = result.toString().indexOf(stylesheetElementMatch);
        if (stylesheetTagStartPosition > 0) {

            // Found a stylesheet tag

            // Now figure out the position of the first ">" after the "<xsl:stylesheet" start position
            int stylesheetTagEndPosition = result.toString().indexOf(">", stylesheetTagStartPosition);

            // Look for old-style xmlns attribute
            // Grab the entire <xsl:stylesheet ...> string and look for the old attribute
            int oldXSLAttributePosition =  result.substring(stylesheetTagStartPosition, stylesheetTagEndPosition).indexOf(oldXSLAttribute);
            if (oldXSLAttributePosition > 0) {

                // We found an old-style attribute

                // Replace old-style attribute region with new-style attribute
                // The area to replace bounded by the beginning of the oldXSLAttribute
                // and the character position of the last character of the oldXSLAttribute
                int replaceStart = stylesheetTagStartPosition + oldXSLAttributePosition;
                int replaceEnd = replaceStart + oldXSLAttribute.length();
                result.replace(replaceStart, replaceEnd, newXSLAttribute);

                // Locate the end position since it as moved
                stylesheetTagEndPosition = result.toString().indexOf(">", stylesheetTagStartPosition);

                // Look for presence of version attribute somewhere
                // within the <xsl:stylesheet start and end positions
                int versionAttributePosition = result.substring(stylesheetTagStartPosition, stylesheetTagEndPosition).indexOf(versionAttribute);
                if (versionAttributePosition < 0) {

                    // We need to add the version tag
                    int insertStart = stylesheetTagStartPosition + stylesheetElementMatch.length();
                    StringBuffer versionToInsert = new StringBuffer(" ").append(versionAttribute);
                    result.insert(insertStart, versionToInsert.toString());

                }

                // Now look for the xmlns="http://www.w3.org/TR/REC-html40" attribute
                // We remove this, because it causes the generate HTML to be different
                // We only remove it when we've already found an old-style xmlns:xsl attribute
                // We're assuming that newer stylesheets have been tested to
                // work correctly even if they have the "html40" value

                // Locate the end position since it may have moved again
                stylesheetTagEndPosition = result.toString().indexOf(">", stylesheetTagStartPosition);

                int htmlStartPosition = result.substring(stylesheetTagStartPosition, stylesheetTagEndPosition).indexOf(htmlAttributeMatch);
                if (htmlStartPosition > 0) {
                    int deleteStart = stylesheetTagStartPosition + htmlStartPosition;
                    int deleteEnd = deleteStart + htmlAttributeMatch.length();
                    result.delete(deleteStart, deleteEnd);
                }

            }

        }

        return result.toString();
    }

    /**
     * Transforms the source XML into the result using the compiled templates.
     * @param templates the templates compiled from an XSL file.
     * @param source the xml source
     * @param result the result of the transformation
     * @throws XMLFormatException if there is a problem transforming; for example,
     * the xsl file could not be parsed or the xml could not be transformed
     */
    private void transform(Templates templates, Reader source, Writer result) throws XMLFormatException {

        try {
            Transformer transformer = templates.newTransformer();
            applyParameters(transformer);
            transformer.transform(new javax.xml.transform.stream.StreamSource(source),
                                  new javax.xml.transform.stream.StreamResult(result));

        } catch (TransformerConfigurationException e) {
            Logger.getLogger(XMLFormatter.class).debug("Unable to transform xsl:" + e, e);
            // Problem parsing source xsl
            throw new XMLFormatException("XSL Transform operation failed.  Problem parsing XSL content: " + e, e);

        } catch (TransformerException e) {
            Logger.getLogger(XMLFormatter.class).debug("Unable to transform xsl:" + e, e);

            // Problem transforming xml
            throw new XMLFormatException("XSL Transform operation failed. Problem transforming XML content: " + e, e);

        }
    }

    /**
     * Transforms the xml source to the result using the specified xsl file.
     * @param input xsl stream to use to transform xml source
     * @param source the xml source
     * @param result the result of the transformation
     * @throws XMLFormatException if there is a problem transforming; for example,
     * the xsl file could not be parsed or the xml could not be transformed
     */
    protected void transform(InputStream input, Reader source, Writer result)
            throws XMLFormatException {

        try {

            // Instantiate a TransformerFactory
            // Assuming we have not defined the property javax.xml.transform.TransformerFactory
            // it will use the default of org.apache.xalan.processor.TransformerFactoryImpl
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            // Create a Transformer based for our XSL file
            // 08/15/2002 - Tim
            // Ideally we'd pass it the File directly.  However, since we
            // are currently processing the <xsl:stylesheet...> tag on-the-fly
            // we need to read it in first

            // Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslFile));
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer(
                        new javax.xml.transform.stream.StreamSource(
                            new StringReader(fixNamespaceForModernXSL(readContents(input)))));
            applyParameters(transformer);


            // Transform the source XML and write it to the result writer
            transformer.transform(new javax.xml.transform.stream.StreamSource(source),
                                  new javax.xml.transform.stream.StreamResult(result));

        } catch (TransformerConfigurationException e) {
            // Problem parsing source xsl
            throw new XMLFormatException("XSL Transform operation failed.  Problem parsing XSL content: " + e, e);

        } catch (TransformerException e) {
            // Problem transforming xml
            throw new XMLFormatException("XSL Transform operation failed. Problem transforming XML content: " + e, e);

        }

    }

    /**
     * Sets any top-level parameters we want to pass into the stylesheet, including defaults.
     * @param transformer the transformer on which to set parameters
     */
    private void applyParameters(Transformer transformer) {
        // First add the defaults
        transformer.setParameter("JspRootUrl", SessionManager.getJSPRootURL());

        // Now add any custom parameters
        for (Iterator iterator = this.parameters.keySet().iterator(); iterator.hasNext();) {
            String parameterName = (String) iterator.next();
            transformer.setParameter(parameterName, this.parameters.get(parameterName));
        }

    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        logger = Logger.getLogger(XMLFormatter.class);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
}
