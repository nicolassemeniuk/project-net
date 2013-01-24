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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.gui.history;

/**
  * Represents a HistoryLevel item in the history.
  */
public class HistoryLevel implements java.io.Serializable {
    
    /**
      * This simply defines the various levels with a number.
      */
    static class Level implements java.io.Serializable {
        private int level = 0;
        private Level(int level) {
            this.level = level;
        }    

        /**
          * Returns the actual number for this level
          * @return the level number
          */
        int getLevelNumber() {
            return this.level;
        }

        /**
          * Levels are equal if their level numbers are equal.
          */
        public boolean equals(Object obj) {
            if (obj != null &&
                obj instanceof Level &&
                ((Level) obj).level == this.level) {
                return true;
            }
            return false;
        }

    }

    /** Defines the BUSINESS level constant. */
    public static final Level BUSINESS = new Level(0);

    /** Defines the ENTERPRISE level constant. */
    public static final Level ENTERPRISE = new Level(0);

    /** Defines the PROJECT level constant. */
    public static final Level PROJECT = new Level(1);
    
    /** Defines the MODULE level constant. */
    public static final Level MODULE = new Level(2);

    /** Defines the PAGE level constant. */
    public static final Level PAGE = new Level(3);
    
    /** The HistoryLevels actual level.  This is set by sub-classes in their
        constructor. */
    private Level level = null;

    /** The text to display at this history level */
    private String display = null;

    /** The token for the text to display at this history level */
    private String displayToken = null;

    /** The URI of the jspPage to add to an active history item */
    private String jspPage = null;

    /** The query string to pass with the URI on an active history item */
    private String queryString = null;

    /** Indicates whether the history item is active (i.e. represented with an anchor) */
    private boolean isActive = false;

    /** Indicates whether to show the history item or not */
    private boolean isShow = false;

    /**
      * Creates a new history level with the specified level.
      * This is called from a sub-class's constructor to lock in the actual
      * level.
      */
    public HistoryLevel(Level level) {
        this.level = level;
    }

    /**
      * Two history levels are equal if their actual levels are equal.
      */
    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof HistoryLevel &&
            ((HistoryLevel) obj).level == this.level) {
            return true;
        }
        return false;
    }

    /** 
      * Sets the display attribute.
      * @param display the text to display
      */
    public void setDisplay(String display) {
        this.display = display;
    }

    /** 
      * Sets the display token attribute.
      * @param displayToken the token for the text to display
      */
    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    /**
      * Sets the jspPage attribute.
      * @param jspPage the jsp page URI
      */
    public void setJspPage(String jspPage) {
        this.jspPage = jspPage;
    }

    /**
      * sets the queryString attribute
      * @param queryString the query string value
      */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
      * Sets the active attribute
      * @param isActive true means the history item will be displayed as an
      * anchor with the HREF constructed from the jspPage and queryString
      */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
      * Sets the show attribute.
      * @param isShow true means the history item will be displayed.
      */
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    /*
        Getters
     */

    String getDisplay() {
        return this.display;
    }

    String getDisplayToken() {
        return this.displayToken;
    }

    String getJspPage() {
        return this.jspPage;
    }

    String getQueryString() {
        return this.queryString;
    }

    boolean isActive() {
        return this.isActive;
    }

    boolean isShow() {
        return this.isShow;
    }

    /**
      * Returns the display value or display token value.
      */
    String getResolvedDisplay() {
	return getProperty(getDisplayToken(), getDisplay());
    }

    /**
      * Returns the target URL for the history level.
      * This is constructed from the jspPage and queryString
      * @return jspPage?queryString
      */
    String getTargetURL() {
        String url = null;
        if (jspPage != null) {
            url = jspPage;
        }
        if (queryString != null) {
            url += "?" + queryString;
        }
        return url;
    }

    /**
      * Return this history level's actual level
      * @return the level
      */
    Level getLevel() {
        return this.level;
    }

    /**
      * Return this history levels level number (which is the number of
      * the level.
      * This is overridden by PageLevel to add in its page number.
      * @return the level number
      */
    int getLevelNumber() {
        return this.level.getLevelNumber();
    }

    /**
      * Returns the absolute value if not null or the value for propertyName.
      * @param propertyName the property name to get the value for
      * @param defaultValue the value to return if present
      */
    private String getProperty(String propertyName, String absoluteValue) {
	String value = null;
	
	if (absoluteValue != null) {
	    value = absoluteValue;
	
	} else {
	    // Lookup up the property name if specified
	    if (propertyName != null) {
		value = net.project.base.property.PropertyProvider.get(propertyName);
	    }
	
	}

	return value;
    }

}
