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
package net.project.portfolio.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import net.project.xml.document.XMLDocument;

import org.jdom.Element;


public class MetaColumnList {
    private List<MetaColumn> metaColumns = new Vector<MetaColumn>();

    public void addMetaColumn(MetaColumn metaColumn) {
        metaColumns.add(metaColumn);
    }

    private void replaceMetaColumn(MetaColumn metaColumn) {
        for (MetaColumn mc : metaColumns) {
            if (mc.equals(metaColumn)) {
                int i = metaColumns.indexOf(mc);
                metaColumns.set(i, metaColumn);
                return;
            }
        }
    }

    public void saveToXML(XMLDocument doc) {
        doc.startElement("MetaColumnList");
        doc.startElement("MetaColumns");
        for (MetaColumn metaColumn : metaColumns) {
            metaColumn.saveToXML(doc);
        }
        doc.endElement();
        doc.endElement();
    }

    public void loadFromXML(Element element) {
        for (Object o : element.getChild("MetaColumns").getChildren()) {
            Element metaColumnElement = (Element) o;
            MetaColumn metaColumn = new MetaColumn();
            metaColumn.loadFromXML(metaColumnElement);
            replaceMetaColumn(metaColumn);
        }
    }

    public List<MetaColumn> getSortedIncludedColumns() {
        List<MetaColumn> result = new Vector<MetaColumn>();
        result.addAll(metaColumns);
        for (int i = result.size() - 1; i >= 0; i--) {
            if (!result.get(i).isInclude())
                result.remove(i);
        }
        Collections.sort(result, new Comparator<MetaColumn>() {
            public int compare(MetaColumn o1, MetaColumn o2) {
                // columns with zero column order (not set) are placed after ordered columns
                if (o1.getColumnOrder() == 0 && o2.getColumnOrder() == 0) return 0;
                if (o1.getColumnOrder() == 0) return 1;
                if (o2.getColumnOrder() == 0) return -1;
                return o1.getColumnOrder() - o2.getColumnOrder();
            }
        });
        return result;
    }

    public String getHTML() {
        StringBuffer result = new StringBuffer();
        result.append("<table>");
        result.append("<tr class=\"tableHeader\">");
        result.append("<td width=\"100\">Include Field</td>\n");
        result.append("<td width=\"100\">Column Order</td>\n");
        result.append("<td width=\"100\">Field Name</td>\n");
        result.append("<td width=\"100\">Column Width</td>\n");
        result.append("<td width=\"100\">No Wrap</td>\n");
        result.append("</tr>");
        int id = 0;
        for (MetaColumn metaColumn : metaColumns) {
        	metaColumn.setOrderId(""+id);
            result.append("<tr>");
            result.append(metaColumn.getHTML());
            result.append("</tr>\n\n");
            id++;
        }
        result.append("</table>");
        return result.toString();
    }

    public void updateFromRequest(HttpServletRequest request) {
        for (MetaColumn metaColumn : metaColumns) {
            metaColumn.updateFromRequest(request);
        }
    }
    
    public List<MetaColumn> getAllColumns() {
    	return this.metaColumns;
    }

    /**
     * To update column properties from project portfolio two pane view
     * @param projectColumnList
     */
    public void updateFromProjectPortfolio(List<MetaColumn> projectColumnList) {
        for (MetaColumn metaColumn : metaColumns) {
            for (MetaColumn projectColumn : projectColumnList) {
            	if(projectColumn.getPropertyName().equals(metaColumn.getPropertyName()))
            		metaColumn.updateFromProjectPortfolio(projectColumn);
            }
        }
    }
}
