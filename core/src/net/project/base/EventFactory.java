package net.project.base;

import net.project.events.ApplicationEvent;
import net.project.events.BlogEvent;
import net.project.events.DocumentEvent;
import net.project.events.EventType;
import net.project.events.FormEvent;
import net.project.events.NewsEvent;
import net.project.events.ProjectEvent;
import net.project.events.TaskEvent;
import net.project.events.WikiEvent;
import net.project.util.StringUtils;

/**
 *
 */
public class EventFactory {
	
	public static ApplicationEvent getEvent(String objectType, EventType eventType) {
		
		if(StringUtils.isEmpty(objectType)) 
			return null;
		
		ApplicationEvent event = null;
		
		if(objectType.equalsIgnoreCase(ObjectType.BLOG_ENTRY)){
			event = new BlogEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
			event = new BlogEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.DOCUMENT)){
			event = new DocumentEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.CONTAINER)){
			event = new DocumentEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.FORM)){
			event = new FormEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.FORM_DATA)){
			event = new FormEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.NEWS)){
			event = new NewsEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.TASK)){
			event = new TaskEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.WIKI)){
			event = new WikiEvent(eventType);
		} else if(objectType.equalsIgnoreCase(ObjectType.PROJECT)){
			event = new ProjectEvent(eventType);
		}
		return event;
	}

}
