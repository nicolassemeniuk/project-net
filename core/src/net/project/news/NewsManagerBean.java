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

 package net.project.news;

import java.util.Iterator;

import net.project.code.TableCodeDomain;
import net.project.persistence.PersistenceException;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.xml.XMLFormatter;

public class NewsManagerBean extends NewsManager {

    private XMLFormatter xmlFormatter;

    /**
      * Creates a new NewsManagerBean
      */
    public NewsManagerBean() {
        this.xmlFormatter = new XMLFormatter();
    }

    /**
      * Set the stylesheet to use
      * @param stylesheet the stylesheet path
      */
    public void setStylesheet(String stylesheetFileName) {
        xmlFormatter.setStylesheet(stylesheetFileName);
    }

    /**
      * Return news list
      */
    public String getNewsItemsPresentation() throws PersistenceException {
        NewsList newsList = getAllNewsItems();
        return xmlFormatter.getPresentation(newsList.getXML());
    }

    /**
      * Return a list of priority values
      * @return list of priority codes in HTML form
      */
    public String getPriorityOptionList() {
        TableCodeDomain dom = new TableCodeDomain();
        dom.setTableName("pn_news");
        dom.setColumnName("priority_id");
        dom.load();
        return dom.getOptionList();
    }

    public String getNotificationGroupOptionList() throws PersistenceException {
        Iterator it = null;
        Group group = null;
        StringBuffer options = new StringBuffer();

        GroupCollection groupList = new GroupCollection();
        groupList.setSpace(getSpace());
        groupList.loadAll();

        it = groupList.iteratorPrincipalGroups();
        while (it.hasNext()) {
            group = (Group) it.next();
            options.append ("<option value=\"" + group.getID() + "\">" + group.getDescription() + "</option>");
        }
        return options.toString();
    }

}
