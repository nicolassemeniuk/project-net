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

 package net.project.taglibs.xml;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.xml.XMLFormatException;
import net.project.xml.XMLFormatter;

/**
 * Provides ability to transform XML content using a stylesheet.
 * <p/>
 * Note: We are deprecating ProjectXML style.  Now you can use an "xml" parameter OR
 * XML body content.  This is the new way:<pre><code>
 *     &lt;xml:transform stylesheet="/path-to/stylesheet.xsl" xml="&lt;%=bean.getXML()%&gt;" /&gt;
 * or
 *     &lt;xml:transform stylesheet="/path-to/stylesheet.xsl"&gt;
 *         &lt;xml:param name="<i>SomeParameter</i>" value="&lt;%=<i>someValue</i>%&gt;" /&gt;
 *         &lt;jsp:getProperty name="<i>bean</i>" property="XML" /&gt;
 *     &lt;/xml:transform&gt;
 * </code></pre>
 * <p/>
 * To utilize a parameter passed in the above example, declare it in your stylesheet: <code><pre>
 *     &lt;xsl:param name="<i>SomeParameter</i>" /&gt;
 * </pre></code>
 * <p/>
 * Old way: <br>
 * XML may come from an attribute or bean that supports the {@link net.project.persistence.IXMLPersistence}
 * interface.  For example:<br>
 * <pre>
 * &lt;xml:transform name="document" scope="session" stylesheet="xsl/document-list.xsl" /&gt;
 * </pre>
 * or
 * <pre>
 * &lt;xml:transform stylesheet="xsl/my-stylesheet.xsl" content="&lt;?xml version=\"1.0\" ?&gt;&lt;myxml /&gt;" />
 * </pre>
 * <p/>
 */
public class TransformTag extends TagSupport {

    /** Stylesheet path used for transformation */
    private String stylesheet = null;
    /** Content to transform */
    private String content = null;
    /** Name of bean which contains content to transform */
    private String name = null;
    /** Scope of bean containing content to transform */
    private String scope = null;

    /**
     * Complete XML content to transform; will not be wrapped by "ProjectXML".
     */
    private String xml = null;

    /** Properties to append to xml content being transformed */
    private final TransformTag.Properties properties = new TransformTag.Properties();

    /** Parameters to pass to the transformer. */
    private final Map parameters = new HashMap();

    //
    // Attribute Setters
    //

    /**
     * Sets path to stylesheet to use for transformation.
     * This path is relative to the JSP page containing the transform tag.
     * @param stylesheet the path to the stylesheet
     */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Sets the XML content to transform.
     * <p>
     * This unnecessary if a bean name has been provided.
     * <b>Note:</b> The content will be wrapped with <code>&lt;ProjectXML&gt; ... &lt;/ProjectXML&gt;</code>
     * as such it must not contain an XML processing instruction (e.g. <code>&lt;?xml version="1.0" ?&gt;</code>)
     * @param content the XML content to transform (not a path to an XML file)
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets a bean name to use for getting XML content.
     * The bean named here must be in <code>page</code> scope, unless an
     * alternate scope is specified with {@link #setScope}.  The bean must
     * implement {@link net.project.persistence.IXMLPersistence}.
     * @param name bean name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the scope of the bean.
     * The scope must be one of: <code>application, session, request, page</code>
     * The default is <code>page</code>.  The named bean is accessed from the
     * page context at this scope.
     * @param scope the scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * SPecifies plain XML to transform.
     * The XML will not be modified before transformation.  As such it
     * <b>must</b> contain a processing instruction (<code>&lt;?xml version="1.0" ?&gt;</code>)
     * When specified, content will be ignored.
     * @param xml the XML to transform
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    //
    // Overriding TagSupport methods
    //

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Outputs transformed XML.
     * @throws JspTagException if there is a problem transforming the XML
     */
    public int doEndTag() throws JspTagException {
        try {
            JspWriter out;
            String transformXML;

            if (this.xml != null) {
                transformXML = this.xml;
            } else {
                // Construct project transformXML structure from content and additional properties
                transformXML = constructProjectXML(getXMLContent(), this.properties.getXMLElements());
            }

            out = pageContext.getOut();

            // Format the combined XML using the set stylesheet
            // and print it out
            XMLFormatter formatter = new XMLFormatter();
            formatter.setStylesheet(stylesheet);
            formatter.setXML(transformXML);
            formatter.setParameters(this.parameters);
            out.print(formatter.getPresentation());

        } catch (XMLFormatException xfe) {
            JspTagException transformException = new JspTagException("Error transforming XML: " + xfe);
            transformException.initCause(xfe);
            throw transformException;

        } catch (IOException ioe) {
            JspTagException transformException = new JspTagException("Error transforming XML: " + ioe);
            transformException.initCause(ioe);
            throw transformException;

        } catch (JspTagException je) {
			// TODO Auto-generated catch block
        	JspTagException transformException = new JspTagException("Error transforming XML: " + je);
            transformException.initCause(je);
            throw transformException;
		} catch (SQLException se) {
			// TODO Auto-generated catch block
			JspTagException transformException = new JspTagException("Error transforming XML: " + se);
            transformException.initCause(se);
            throw transformException;
		} finally {
            clear();

        }
        
        return EVAL_PAGE;
    }


    //
    // Additional utility methods
    //

    /**
     * Adds a new property to XML properties.
     * @param key the property key
     * @param value the property value
     */
    void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    /**
     * Clears out all properties.
     * This is should be called when the tag is finished to avoid lingering
     * values when this tag object is reused.
     */
    private void clear() {
        this.stylesheet = null;
        this.content = null;
        this.name = null;
        this.scope = null;
        this.properties.clear();
        this.xml = null;
        this.parameters.clear();
    }

    /**
     * Returns either specified content or the specified bean's XML content.
     * The bean content is always used if the bean name has been set, regardless
     * of whether the content was specified through any other means.
     * @return the XML string
     * @throws JspTagException if there is a problem getting the content
     * @throws SQLException 
     */
    private String getXMLContent() throws JspTagException, SQLException {
        String xmlContent;

        if (this.name != null) {
            xmlContent = getBeanXML();
            
        } else {
            xmlContent = this.content;

        }
        return xmlContent;
    }

    /**
     * Returns the bean's XML (excluding XML version tag).  Gets the bean for
     * specified name and specified scope.  Default scope is
     *
     * {@link javax.servlet.jsp.PageContext#PAGE_SCOPE}.
     * @throws JspTagException if the bean is not of type {@link net.project.persistence.IXMLPersistence}
     * or if an invalid scope was specified or if the bean is not found in the specified scope
     * @return XML string
     * @throws SQLException 
     */
    private String getBeanXML() throws JspTagException, SQLException {
        Object bean;
        int beanScope;

        // Determine correct scope
        if (this.scope != null) {
            if (this.scope.equals("page")) {
                beanScope = PageContext.PAGE_SCOPE;
            } else if (this.scope.equals("request")) {
                beanScope = PageContext.REQUEST_SCOPE;
            } else if (this.scope.equals("session")) {
                beanScope = PageContext.SESSION_SCOPE;
            } else if (this.scope.equals("application")) {
                beanScope = PageContext.APPLICATION_SCOPE;
            } else {
                throw new JspTagException("Error in transform tag: Invalid scope '" + 
                    this.scope + "'.  Must be one of application, session, request, page.");
            }
        } else {
            beanScope = PageContext.PAGE_SCOPE;
        }

        // Grab the bean from that scope
        bean = pageContext.getAttribute(this.name, beanScope);

        // Check that the bean was actually found in the specified scope
        if (bean == null) {
            throw new JspTagException("Error in transform tag: Bean '" + this.name + "' not found in " + this.scope + " scope.");
        }

        // Bean must be of type IXMLPersistence in order to get XML content
        if (!(bean instanceof net.project.persistence.IXMLPersistence)) {
            throw new JspTagException("Error transforming XML: bean '" + this.name + "' is not of type net.project.persistence.IXMLPersistence");
        }

        // Return body content (excludes XML version tag)
        return ((net.project.persistence.IXMLPersistence) bean).getXMLBody();
    }

    /**
     * Wraps XML and translation properties in ProjectXML schema structure.
     * @param content the XML content
     * @param translationProperties the translation properties to be included
     * @return the XML adhering to ProjectXML structure
     */
    private String constructProjectXML(String content, String translationProperties) {
        StringBuffer xml = new StringBuffer();

        final String PROJECTXML_ELEMENT = "ProjectXML";
        final String PROPERTIES_ELEMENT = "Properties";
        final String SCHEMA_ELEMENT = "Schema";
        final String TRANSLATION_ELEMENT = "Translation";
        final String CONTENT_ELEMENT = "Content";
        
        // Combine content and properties xml
        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<" + PROJECTXML_ELEMENT + ">");
        xml.append("<" + PROPERTIES_ELEMENT + ">");
        xml.append("<" + SCHEMA_ELEMENT + " />");
        xml.append("<" + TRANSLATION_ELEMENT + ">");
        xml.append(translationProperties);
        xml.append("</" + TRANSLATION_ELEMENT + ">");
        xml.append("</" + PROPERTIES_ELEMENT + ">");
        xml.append("<" + CONTENT_ELEMENT + ">");
        xml.append(content);
        xml.append("</" + CONTENT_ELEMENT + ">");
        xml.append("</" + PROJECTXML_ELEMENT + ">");

        return xml.toString();
    }

    /**
     * Sets a parameter to pass to the XSL stylesheet.
     * @param name the parameter name
     * @param value the parameter value
     */
    void addParameter(String name, Object value) {
        if (name == null || value == null) {
            throw new NullPointerException("Missing required parameter name or value");
        }

        this.parameters.put(name, value);
    }

    /**
     * Properties adds functionality to get entries as XML string.
     */
    private static class Properties extends java.util.HashMap {

        /**
         * Returns this HashMap's properties as XML in the form:<br>
         * <pre>
         *     <property name="prop1" value="somevalue" />
         *     <property name="prop2" value="someothervalue" />
         * </pre>
         * @return the xml properties
         */
        String getXMLElements() {
            String name;
            String value;

            StringBuffer xml = new StringBuffer();

            for (Iterator it = this.keySet().iterator(); it.hasNext(); ) {
                name = (String) it.next();
                value = (String) this.get(name);

                xml.append("<property name=\"" + name + "\">" + net.project.xml.XMLUtils.escape(value) + "</property>");

            }

            return xml.toString();
        }

    }
}
