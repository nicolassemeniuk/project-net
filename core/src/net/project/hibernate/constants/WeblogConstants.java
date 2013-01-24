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
/**
 * 
 */
package net.project.hibernate.constants;

/**
 * @author
 *
 */
public class WeblogConstants {
	
	public static final Integer BLOG_ENABLED = 1;
	
	public static final Integer BLOG_DISABLED = 0;
	
	public static final Integer BLOG_ACTIVE = 1;
	
	public static final Integer BLOG_NOT_ACTIVE = 0;
	
	public static final Integer YES_PUBLISH_ENTRY = 1;

	public static final Integer DONT_PUBLISH_ENTRY = 0;
	
	public static final Integer ALLOW_EMAIL_COMMENTS = 1;
	
	public static final Integer DONT_ALLOW_EMAIL_COMMENTS = 0;
		
	public static final Integer YES_ALLOW_COMMENTS = 1;

	public static final Integer DONT_ALLOW_COMMENTS = 0;

	public static final Integer DEFAULT_COMMENT_DAYS = 7;

	public static final Integer YES_RIGHT_TO_LEFT = 1;

	public static final Integer NOT_RIGHT_TO_LEFT = 0;

	public static final String STATUS_PUBLISHED = "PUBLISHED";

	public static final String STATUS_DRAFT = "DRAFT";
	
	public static final String STATUS_DELETED = "DELETED";
	
	public static final Integer YES_IMPORTANT = 1;

	public static final Integer NOT_IMPORTANT = 0;

	public static final Integer YES_NOTIFY = 1;
	
	public static final Integer DONT_NOTIFY = 0;

	public static final String COMMENT_APPROVED_STATUS = "APPROVED";
	
    public static final String COMMENT_DISAPPROVED_STATUS = "DISAPPROVED";
    
    public static final String COMMENT_SPAM_STATUS = "SPAM";
    
    public static final String COMMENT_PENDING_STATUS = "PENDING";
    
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    
    // no of links to be displayed for archives, team members, categories etc.
    public static final Integer NO_OF_LINKS = 5;
    
    public static final Integer DATE_RANGE_FOR_ARCHIVES = 6;
    
    public static final String WEBLOG_DATE_FORMAT_PATTERN = "EEE, MMM dd, yyyy";

}
