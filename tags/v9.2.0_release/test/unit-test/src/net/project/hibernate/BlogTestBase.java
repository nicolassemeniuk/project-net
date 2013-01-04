package net.project.hibernate;

import java.util.Calendar;

import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.space.Space;
import junit.framework.TestCase;


public class BlogTestBase extends TestCase{
		
	public BlogTestBase() {
		
	}	

	/**
	 * Creating weblog instance for test
	 * 
	 * @param userId
	 * @param spaceId
	 * @param spaceType
	 * @param person
	 * @return PnWeblog instance
	 */
	public PnWeblog getWeblog(int userId, int spaceId, String spaceType, PnPerson person) {
		PnWeblog pnWeblog = new PnWeblog();
		pnWeblog.setWeblogId(new Integer(1));
		
		// setting blog name and description as per space type
		if (spaceType != null) {
			if (spaceType.equals(Space.PERSONAL_SPACE)) {
				pnWeblog.setName("Display Name_" + userId);
				pnWeblog.setDescription("Display Name" + "'s Personal Blog");
			} else if (spaceType.equals(Space.PROJECT_SPACE)) {
				pnWeblog.setName("Project Name_" + spaceId);
				pnWeblog.setDescription("Project Name" + " Project Blog");
			}
		}
		pnWeblog.setEmailAddress("user@email.com");
		pnWeblog.setCreatedDate(Calendar.getInstance().getTime());
		pnWeblog.setPnPerson(person);
		pnWeblog.setSpaceId(spaceId);
		pnWeblog.setIsActive(WeblogConstants.BLOG_ACTIVE);
		pnWeblog.setIsEnabled(WeblogConstants.BLOG_ENABLED);
		pnWeblog.setDefaultAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
		pnWeblog.setDefaultCommentDays(WeblogConstants.DEFAULT_COMMENT_DAYS);
		pnWeblog.setAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
		pnWeblog.setEmailComments(WeblogConstants.DONT_ALLOW_EMAIL_COMMENTS);
		pnWeblog.setLocale("en");
		pnWeblog.setTimezone("IST");
		return pnWeblog;
	}
	
	/**
	 * Creating weblog entry instance for test
	 * 
	 * @param userId
	 * @param spaceId
	 * @param spaceType
	 * @return PnWeblogEntry instance
	 */
	public PnWeblogEntry getWeblogEntry(int userId, int spaceId, String spaceType) {
		PnPerson person = new PnPerson();
		person.setDisplayName("Display Name");
		person.setFirstName("FirstName");
		person.setLastName("LastName");
		person.setPersonId(new Integer(userId));

		PnWeblogEntry pnWeblogEntry = new PnWeblogEntry();
		pnWeblogEntry.setWeblogEntryId(new Integer(1));
		PnWeblog weblog = getWeblog(userId, spaceId, spaceType, person);

		// creating weblog entry object from data provided from user to store in database
		pnWeblogEntry.setPnPerson(person);
		pnWeblogEntry.setAnchor("Entry from user_" + userId + "".replaceAll(" ", "_"));
		pnWeblogEntry.setTitle("Entry from user_" + userId);
		pnWeblogEntry.setText("Content for Entry form user_" + userId);
		pnWeblogEntry.setUpdateTime(Calendar.getInstance().getTime());
		pnWeblogEntry.setPnWeblog(weblog);
		pnWeblogEntry.setPublishEntry(WeblogConstants.YES_PUBLISH_ENTRY);
		pnWeblogEntry.setAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
		pnWeblogEntry.setCommentDays(WeblogConstants.DEFAULT_COMMENT_DAYS);
		pnWeblogEntry.setRightToLeft(WeblogConstants.NOT_RIGHT_TO_LEFT);
		pnWeblogEntry.setLocale("en");
		pnWeblogEntry.setStatus(WeblogConstants.STATUS_PUBLISHED);
		pnWeblogEntry.setPubTime(Calendar.getInstance().getTime());
		return pnWeblogEntry;
	}
	
	/**
	 * Creating weblog comment instance for test
	 * @param userId
	 * @param spaceId
	 * @param spaceType
	 * @return PnWeblogComment instance
	 */
	public PnWeblogComment getWeblogComment(int userId, int spaceId, String spaceType) {
		PnWeblogComment pnWeblogComment = new PnWeblogComment();
		pnWeblogComment.setPnWeblogEntry(getWeblogEntry(userId, spaceId, spaceType));

		// creating weblog comment object from data provided from user to store in database
		pnWeblogComment.setCommentId(new Integer(1));
		pnWeblogComment.setName("FirstName");
		pnWeblogComment.setEmail("user@email.com");
		pnWeblogComment.setContent("Comment contents from user_"+userId);
		pnWeblogComment.setPostTime(Calendar.getInstance().getTime());
		pnWeblogComment.setNotify(WeblogConstants.DONT_NOTIFY);
		pnWeblogComment.setStatus(WeblogConstants.COMMENT_APPROVED_STATUS);
		pnWeblogComment.setContentType(WeblogConstants.CONTENT_TYPE_TEXT_PLAIN);
		return pnWeblogComment;
	}
	
	/**
	 * Creating weblog entry attribute instance for test
	 * @param attributeName
	 * @param attributeValue
	 * @param isImportant
	 * @return PnWeblogEntryAttribute instance
	 */
	public PnWeblogEntryAttribute getWeblogEntryAttribute(String attributeName, String attributeValue){
		// creating weblog entry attribute object to save		
		PnWeblogEntryAttribute pnWeblogEntryAttribute = new PnWeblogEntryAttribute();	
		pnWeblogEntryAttribute.setWeblogEntryAttributeId(new Integer(1));
		pnWeblogEntryAttribute.setName(attributeName);
		pnWeblogEntryAttribute.setValue(attributeValue);
		pnWeblogEntryAttribute.setPnWeblogEntry(getWeblogEntry(1, 11001, Space.PROJECT_SPACE));
		return pnWeblogEntryAttribute;
	}	
}
