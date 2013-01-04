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

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;

import org.apache.log4j.Logger;

public class NewsManager {
    
    // Filtering constants
    public static final int FILTER_ALL = 0;
    public static final int FILTER_PAST_TWO_WEEKS = 1;
    public static final int FILTER_PAST_MONTH = 2;

    // Sorting constants
    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_PRIORITY_ID = 0;
    public static final int SORT_POSTED_ON_ID = 3;
    
    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_TOPIC = 1;
    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_POSTED_BY_ID = 2;
    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_POSTED_DATETIME = 3;

    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_ASCENDING = 0;
    /**
     * @deprecated As of 7.6.7; No replacement.
     * This constant will be made private.
     */
    public static final int SORT_DESCENDING = 1;

    private static final int DAYS_IN_WEEK = 7;

    /**
     * The length to which the "presentable message" is to be truncated to.
     */
    private int truncatedPresentableMessageLength = 150 ;

    /**
     * The maximum number of paragraphs to include in the "presentable message".
     */
    private int truncatedPresentableMessageMaxParagraphs = 5;

    /** Current space */
    private Space space;
    
    /** Database functions */
    private final DBBean db;

    /** view range */
    private int viewRange = FILTER_ALL;

    /** Sort */
    private int sortColumn = SORT_PRIORITY_ID;
    private int sortOrder = SORT_ASCENDING;

    /**
      * Creates new NewsManager
      */
    public NewsManager() {
        this.db = new DBBean();
    }

    /**
      * Set the current space, to be used by other methods.
      * @param space the space
      */
    public void setSpace(Space space) {
        this.space = space;
    }
    
    public Space getSpace() {
        return this.space;
    }
    /**
     * Set the current user, to be used by other methods.
     * @param user the user
     * @deprecated As of 7.6.7; no replacement
     * The value set by this method is not used.
     */
    public void setUser(User user) {
        // Do nothing since user member variable was used as
        // it was never accessed
    }

    public void setViewRange(int viewRange) {
        this.viewRange = viewRange;
    }

    public int getViewRange() {
        return this.viewRange;
    }

    /**
     * Sets the length for the truncated Presentable Message.
     * Defaults to 150.
     * 
     * @param length the length for the truncated Presentable Message
     */
    public void setTruncatedPresentableMessageLength(int length) {
        this.truncatedPresentableMessageLength = length;
    }

    /**
     * Sets the max number of paragraphs to show when presenting the news
     * message.
     * Defaults to 5.
     * This number of paragraphs simply count's linefeeds.
     * It is useful for ensuring that many short lines don't take up too much
     * screen real estate.
     * It is not based on wrapping since wrapping is performed by the
     * web browser.
     *
     * @param maxParagraphs the maximum number of paragraphs to display
     */
    public void setTruncatedPresentableMessageMaxParagraphs(int maxParagraphs) {
        this.truncatedPresentableMessageMaxParagraphs = maxParagraphs;
    }

    /**
     *
     * @param sortColumn
     * @deprecated As of 7.6.7; no replacement.
     * The ability to control the sort is being removed.
     * Calling this method will have no effect.
     */
    public void setSortColumn(int sortColumn) {
    	//TODO this is temporal, this will be removed in the future
        // Do nothing; no ability to control sort
        // The reason is that sorting will no longer be on a single column
        // It will be hardcoded
    	this.sortColumn = sortColumn;
    }

    /**
     * @return
     * @deprecated As of 7.6.7; no replacement.
     * This method is never used and serves no useful purpose.
     */
    public int getSortColumn() {
        return this.sortColumn;
    }

    /**
     * @return
     * @deprecated As of 7.6.7; no replacement.
     * This method is never used and serves no useful purpose.
     */
    public int getSortOrder() {
        return this.sortOrder;
    }

    /**
     * Gets a list of all news items in current space.<br>
     * <b>Preconditions:</b>
     * <li>setSpace must be called prior to this method</li>
     * @return list of news items filtered based on the current view range
     * and orderd by priority (high to low) then by date time (most recent to oldest)
     */
    public NewsList getAllNewsItems() throws PersistenceException {
        return getNewsItems(getNewsWhereClause());
    }

    /**
      * Constructs a SQL where clause (EXCLUDING "WHERE" statement) based on specified
      * news criteria.<br>
      * <b>Note</b><br>
      * Assumes the news table will have an alias of "n"
      * @return the where clause
      */
    private String getNewsWhereClause() {
        StringBuffer whereClause = new StringBuffer("");
        whereClause.append("n.record_status = 'A' ");
        whereClause.append("and n.space_id = " + this.space.getID() + " ");

        if (viewRange == FILTER_ALL) {
            // Do nothing
        
        } else if (viewRange == FILTER_PAST_TWO_WEEKS) {
            // Last two weeks : includes all tasks created since 2 weeks ago, on or after midnight on that day
            int days = 2 * DAYS_IN_WEEK;
            whereClause.append("and posted_datetime >= (trunc(sysdate) - " + days + ") ");
        
        } else if (viewRange == FILTER_PAST_MONTH) {
            // Last month : includes all tasks created since 1 month ago, on or after midnight on that day
            whereClause.append("and posted_datetime >= add_months(trunc(sysdate), -1) ");
        }

        return whereClause.toString();
    }

    /**
      * Return a list of news items
      * @return the news list
      */
    private NewsList getNewsItems(String whereClause) throws PersistenceException {
        NewsList newsList = new NewsList();
        News news;

        StringBuffer queryBuff = new StringBuffer();
        
        queryBuff.append("select n.space_id , n.news_id , n.topic , n.message_clob , n.priority_id, ");
        queryBuff.append("n.priority_description , n.priority_name , n.notification_id, n.posted_by_id , trunc(n.posted_datetime) posted_datetime, n.posted_by_full_name, ");
        queryBuff.append("n.created_by_id , n.created_datetime , n.created_by_full_name , ");
        queryBuff.append("n.modified_by_id , n.modified_datetime, n.modified_by_full_name , n.crc , n.record_status ");
        queryBuff.append("from pn_news_view n ");
        
        if (whereClause != null && !whereClause.equals("")) {
            queryBuff.append("where " + whereClause);
        }

        // Order by priority (high to low) then date time with most recent posts first
        switch(this.sortColumn) {
        case SORT_PRIORITY_ID :
        	queryBuff.append("order by n.priority_name asc, n.posted_datetime desc");
        	break;
        case SORT_POSTED_BY_ID:
        	queryBuff.append("order by n.posted_by_full_name asc, n.posted_datetime desc");
        	break;
        case SORT_POSTED_ON_ID:
        	queryBuff.append("order by n.posted_datetime desc");
        }

        // Execute the query
        try {
        	
            db.executeQuery(queryBuff.toString());

            while(db.result.next()) {
                news = new News();

                news.setID(db.result.getString("news_id"));
                news.setTopic(db.result.getString("topic"));
                news.setMessage(ClobHelper.read(db.result.getClob("message_clob")));
                news.setPriorityID(db.result.getString("priority_id"));
                news.setNotificationID(db.result.getString("notification_id"));
                news.setPostedByID(db.result.getString("posted_by_id"));
                news.setPostedDatetime(db.result.getTimestamp("posted_datetime"));
                news.setCreatedByID(db.result.getString("created_by_id"));
                news.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                news.setModifiedByID(db.result.getString("modified_by_id"));
                news.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                news.setCrc(db.result.getTimestamp("crc"));
                news.setRecordStatus(db.result.getString("record_status"));
                news.setSpaceID(db.result.getString("space_id"));

                news.setPriorityName(PropertyProvider.get(db.result.getString("priority_name")));
                news.setPriorityDescription(db.result.getString("priority_description"));
                news.setPostedByFullName(db.result.getString("posted_by_full_name"));
                news.setCreatedByFullName(db.result.getString("created_by_full_name"));
                news.setModifiedByFullName(db.result.getString("modified_by_full_name"));
                news.setTruncatedPresentableMessageLength(this.truncatedPresentableMessageLength);
                news.setTruncatedPresentableMessageMaxParagraphs(this.truncatedPresentableMessageMaxParagraphs);
                news.setLoaded(true);
                newsList.add(news);
            }


        } catch (SQLException sqle) {
        	Logger.getLogger(NewsManager.class).error("NewsManager.getNewsItems() threw an SQL exception: " + sqle);
            throw new PersistenceException("News manager get news items operation failed.", sqle);

        } finally {
            db.release();

        }
        
        return newsList;
    }

}
