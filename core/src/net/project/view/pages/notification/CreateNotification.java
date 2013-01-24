package net.project.view.pages.notification;

import net.project.notification.DomainListBean;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * create notification page
 * 
 * @author umesh
 */
public class CreateNotification extends BasePage{

	@Inject
	private Request request;
	
	/**
	 * Method to get notification type checkbox list
	 */
	Object onActivate(String action) {
		String notificationTypes="";
		DomainListBean domainListBean =new DomainListBean();	

		if(StringUtils.isNotEmpty(action) && action.equals("loadEvents")){
			String objectType = request.getParameter("objectType");
			if(StringUtils.isNotEmpty(objectType)){
				notificationTypes = domainListBean.getNotificationTypesCheckList(objectType, null);
			}
		}
		return new TextStreamResponse("text/html", notificationTypes);
	}
}
