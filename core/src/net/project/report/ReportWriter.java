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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.report;

import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter;
import com.lowagie.text.xml.XmlWriter;

/**
 * Sets up a reporting document with the appropriate listeners that will 
 * write the document to the output stream in an appropriate format.
 * 
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportWriter {
    /** The iText document object which will be used to construct the report. */
    private Document document = new Document();
    /**
     * Token pointing to: "Unable to create new report writer."
     */
    private String UNABLE_TO_CREATE_INSTANCE_TOKEN = "prm.report.reportwriter.unabletocreatenew.message";

    /**
     * Standard constructor.  This method does most of the work that this class will do.
     *
     * @param format a <code>ReportOutputType</code> value that specifies the format the
     * output stream should be written in.
     * @param out a <code>OutputStream</code> value to which the report will be written.
     * @throws ReportException if any error occurs while trying to create a new
     * report writer object.
     */
    public ReportWriter(ReportOutputType format, OutputStream out) throws ReportException {
        try {
            if (format.equals(ReportOutputType.PDF_REPORT_OUTPUT_TYPE)) {
                PdfWriter.getInstance(document, out);
            } else if (format.equals(ReportOutputType.HTML_REPORT_OUTPUT_TYPE)) {
                HtmlWriter.getInstance(document, out);
            } else if (format.equals(ReportOutputType.RTF_REPORT_OUTPUT_TYPE)) {
                RtfWriter.getInstance(document, out);
            } else if (format.equals(ReportOutputType.XML_REPORT_OUTPUT_TYPE)) {
                XmlWriter.getInstance(document, out);
            }
        } catch (DocumentException doce) {
            throw new ReportException(UNABLE_TO_CREATE_INSTANCE_TOKEN, doce);
        }
    }

    /**
     * Gets the document that this report writer is writing to.
     * 
     * @return a <code>Document</code> that information is being written to by a report writer.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Set a document that the report writer is writing to.
     *
     * @param document a <code>Document</code> that the report writing is
     * writing to.
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Signals that the reporting class is done writing to the document and is 
     * prepared to write out the outputstream.
     */
    public void close() {
        document.close();
    }
}
