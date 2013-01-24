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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.gui.pager;

/**
 * Provides methods for displaying page index.
 * <p>
 * This index is returned as XML; it may be rendered in many ways but could
 * look like: <code><pre>
 * &lt; Previous 1 <a href="">2</a> 3 Next &gt;
 * </pre></code>
 * </p>
 * <p>
 * The XML structure is fixed, but the elements may vary depending on the
 * number of index links to include;  Including additional index links
 * may required additional results to be returned from the <code>IPageable</code>
 * that the pager is based on.
 * </p>
 * <p>
 * Example XML (where second page is current): <code><pre>
 * &lt;PageIndex&gt;
 *     &lt;Previous isAvailable="1"&gt;
 *         &lt;PageStart&gt;0&lt;/PageStart&gt;
 *         &lt;DisplayNumber&gt;1&lt;/DisplayNumber&gt;
 *         &lt;Href&gt;http://MyPage.jsp?module=120&page_start=0&lt;/Href&gt;
 *     &lt;/Previous&gt;
 *     &lt;Next isAvailable="1"&gt;
 *         &lt;PageStart&gt;20&lt;/PageStart&gt;
 *         &lt;DisplayNumber&gt;3&lt;/DisplayNumber&gt;
 *         &lt;Href&gt;http://MyPage.jsp?module=120&page_start=20&lt;/Href&gt;
 *     &lt;/Next&gt;
 *     &lt;Page isCurrent="0"&gt;
 *         &lt;PageStart&gt;0&lt;/PageStart&gt;
 *         &lt;DisplayNumber&gt;1&lt;/DisplayNumber&gt;
 *         &lt;Href&gt;http://MyPage.jsp?module=120&page_start=0&lt;/Href&gt;
 *     &lt;/Page&gt;
 *     &lt;Page isCurrent="1"&gt;
 *         &lt;PageStart&gt;10&lt;/PageStart&gt;
 *         &lt;DisplayNumber&gt;2&lt;/DisplayNumber&gt;
 *         &lt;Href&gt;http://MyPage.jsp?module=120&page_start=10&lt;/Href&gt;
 *     &lt;/Page&gt;
 *     &lt;Page isCurrent="0"&gt;
 *         &lt;PageStart&gt;20&lt;/PageStart&gt;
 *         &lt;DisplayNumber&gt;3&lt;/DisplayNumber&gt;
 *         &lt;Href&gt;http://MyPage.jsp?module=120&page_start=20&lt;/Href&gt;
 *     &lt;/Page&gt;
 * &lt;/PageIndex&gt;
 */
public class PageIndex implements net.project.persistence.IXMLPersistence {

    /**
     * The default maximum number of links to display, currently <code>0</code>.
     */
    public static final int DEFAULT_MAX_INDEX_LINK_COUNT = 0;

    /**
     * The pager providing the page results.
     */
    private PagerBean pagerBean = null;

    /**
     * The Href to include in links.
     */
    private String href = null;
    
    /**
     * The maximum number of links to display.
     */
    public int maxIndexLinks = DEFAULT_MAX_INDEX_LINK_COUNT;

    /**
     * Creates an empty PageIndex.
     */
    public PageIndex() {
        super();
    }

    /**
     * Sets the PagerBean that provides the results from which to build the
     * index.
     * @param pageBean the pager
     */
    public void setPagerBean(PagerBean pagerBean) {
        this.pagerBean = pagerBean;
    }

    /**
     * Specifies the Href to include in all links.
     * The page start parameter (named by <code>{@link PagerBean#getPageStartParameterName}</code>
     * is appended to this Href.
     * @param the Href; this can be any value
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Specifies the maximum number of links to return in this index.
     * <p>
     * <b>Caution:</b> Specifying more index links may require more results
     * to be fetched from the <code>IPageable</code> in order to determine
     * whether a particular page is available; however, the actual implementation
     * if up to the <code>IPageable</code>.
     * </p>
     * <p>
     * This PageIndex will not return more than the value specified here; however,
     * it may return less.  It works similar to Google: early pages in the results
     * show fewer index pages; once you get to a page number that is greater or
     * equal to the half max link count, then the max links are displayed with
     * the current page centered.
     * </p>
     * <p>
     * For example: <br>
     * maxIndexLinks = 20, current page = 1: <code><b>1</b> 2 3 4 5 6 7 8 9 10</code> <br>
     * maxIndexLinks = 20, current page = 5: <code>1 2 3 4 <b>5</b> 6 7 8 9 10 11 12 13 14</code> <br>
     * maxIndexLinks = 20, current page = 15: <code>5 6 7 8 9 10 11 12 13 14 <b>15</b> 16 17 18 19 20 21 22 23 24</code>
     * </p>
     * @param count the maximum number of links to return
     * @see #DEFAULT_MAX_INDEX_LINK_COUNT
     */
    public void setMaxIndexLinks(int count) {
        this.maxIndexLinks = count;
    }

    /**
     * Returns the XML rendition of this PageIndex, including the XML version tag.
     * @return the XML
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML rendition of this PageIndex, excluding the XML version tag.
     * @return the XML
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XMLDocument built from this PageIndex.
     * @return the XMLDocument
     */
    private net.project.xml.document.XMLDocument getXMLDocument() {
        
        // Grab the page start parameter name (e.g. "page_Start")
        String pageStartParameterName = this.pagerBean.getPageStartParameterName();

        net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();
        
        try {

            xml.startElement("PageIndex");
            
            // Add <Previous> element
            xml.startElement("Previous");
            xml.addAttribute("isAvailable", new Boolean(this.pagerBean.hasPreviousPage()));
            if (this.pagerBean.hasPreviousPage()) {
                xml.addElement("PageStart", String.valueOf(this.pagerBean.getPageStartForPage(this.pagerBean.getPreviousPageNumber())));
                xml.addElement("Href", makeHref(this.href, pageStartParameterName, this.pagerBean.getPageStartForPage(this.pagerBean.getPreviousPageNumber())));
                xml.addElement("DisplayNumber", new Integer(this.pagerBean.getPreviousPageNumber() + 1));
            }
            xml.endElement();
            
            // Add <Next> element
            xml.startElement("Next");
            xml.addAttribute("isAvailable", new Boolean(this.pagerBean.hasNextPage()));
            if (this.pagerBean.hasNextPage()) {
                xml.addElement("PageStart", String.valueOf(this.pagerBean.getPageStartForPage(this.pagerBean.getNextPageNumber())));
                xml.addElement("Href", makeHref(this.href, pageStartParameterName, this.pagerBean.getPageStartForPage(this.pagerBean.getNextPageNumber())));
                xml.addElement("DisplayNumber", new Integer(this.pagerBean.getNextPageNumber() + 1));
            }
            xml.endElement();

            // Add page elements; the number depends on maxIndexLinks
            // It will attempt to display maxIndexLinks, centered
            // on the current page number
            int currentPageNumber = this.pagerBean.getCurrentPage();
            // The max number of links to display to the left of the current page
            int leftNumber = this.maxIndexLinks / 2;
            // The starting page number is either zero or the some number greater
            // than zero that ensures only "leftNumber" of links are displayed
            int start = Math.max(0, (currentPageNumber - leftNumber));
            // The end number is such that (maxIndexLinks/2) links are displayed
            // after the current page number.
            int end = currentPageNumber + ((this.maxIndexLinks /2) - 1);
            
            for (int i = start; i < end && this.pagerBean.hasPage(i); i++) {
                xml.startElement("Page");
                xml.addAttribute("isCurrent", new Boolean(i == currentPageNumber));
                xml.addElement("PageStart", String.valueOf(this.pagerBean.getPageStartForPage(i)));
                xml.addElement("Href", makeHref(this.href, pageStartParameterName, this.pagerBean.getPageStartForPage(i)));
                xml.addElement("DisplayNumber", new Integer(i + 1));
                xml.endElement();
            }

            xml.endElement();
        
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Return empty XML document

        }

        return xml;
    }

    /**
     * Constructs an Href given the specified href, the parameter name and starting number.
     * Adds the request parameter, ensuring to include ? or & as appropriate.
     * @param href the href to append to
     * @param pageStartParameterName the parameter name
     * @param pageStart the parameter value
     */
    private String makeHref(String href, String parameterName, int parameterValue) {

        StringBuffer result = new StringBuffer();
        result.append(href);
        
        // If there is no ? then there must be no parameters
        // Otherwise, there is at least one parameter
        if (href.indexOf("?") < 0) {
            result.append("?");
        } else {
            result.append("&");
        }

        // Add parameter=value
        result.append(parameterName).append("=").append(parameterValue);

        return result.toString();
    }
}
