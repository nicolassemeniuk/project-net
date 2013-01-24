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
package net.project.soa.news;

import net.project.news.News;
import net.project.news.NewsManager;
import net.project.news.NewsManagerBean;
import net.project.security.SessionManager;

public class NewsManagerImpl extends NewsManagerBean implements INewsManager {
	public News[] getAllNews() throws Exception{
		NewsManager newsManager = new NewsManager();
		newsManager.setSpace(SessionManager.getUser().getCurrentSpace());
		//newsManager.setTruncatedPresentableMessageLength(390);
	    //newsManager.setTruncatedPresentableMessageMaxParagraphs(5);
	    Object[] arr = newsManager.getAllNewsItems().toArray();
		News[] news = new News[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	news[i] = (News)arr[i];
	    }
	    return news;
	}
}