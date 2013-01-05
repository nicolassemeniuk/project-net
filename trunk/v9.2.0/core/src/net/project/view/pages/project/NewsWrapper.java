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
package net.project.view.pages.project;

import java.io.Serializable;

import net.project.base.Module;
import net.project.news.News;
import net.project.security.SessionManager;

public class NewsWrapper implements Serializable {
    
    private News news;
    
    public NewsWrapper() {
        
    }
    
    public NewsWrapper(News news) {
        this.news = news;
    }
    
    public String getId() {
        return this.news.getID();
    }
    
    public String getDate() {
        return SessionManager.getUser().getDateFormatter().formatDate(this.news.getPostedDatetime());
    }
    
    public String getPostedBy() {
    	if(this.news.getPostedByFullName().length()>30){
    		return this.news.getPostedByFullName().substring(0, 25)+"...";
    	}else{
    		return this.news.getPostedByFullName();
    	}
    }
    
    public String getMessage() {
            return news.getMessage();
    }
    
    public String getTopic() {
    	 return news.getTopic();
    }
    
    public String getUrl() {
        return "/news/NewsView.jsp?module=" + Module.NEWS + "&id=" + this.news.getID();
    }

	public News getNews() {
		return news;
	}

}
