/**
 * 
 */
package net.project.view.components;

import java.text.MessageFormat;

import net.project.activity.ActivityLogManager;
import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnActivityLog;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ShowActivityTextForBlogComment {
	
	@Property
	@Parameter(required = true)
	private PnActivityLog pnActivityLog;
	
	@Parameter
	@Property
	private String activityTime;
	
	@Parameter
	@Property
	private String personName;
	
	@Parameter
	@Property
	private String personLink;
	
	@Parameter
	@Property
	private String personImg;
	
	@Parameter
	@Property
	private String unMarkedToken;
	
	@Property
	private String topersonName;
	
	@Property
	private String toPersonLink;
	
	@Property
	private String jspRootURL;
	
	@Property
	private String entryTitle;
	
	@Property
	private String fullCommentContent;

	@Property
	private Integer activityLogId;
	
	@Property
	private String shortCommentContent;
	
	@Property
	private boolean isfullView;
	
	@Property
	private String expand;
	
	@BeginRender
	public void renderMessage() {
		try {
			expand = PropertyProvider.get("prm.project.activity.expand.lable");
			activityLogId = pnActivityLog.getActivityLogId();
			pnActivityLog.setDescription(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription()));
			shortCommentContent = pnActivityLog.getDescription().contains("<beginOfentryText>") ? pnActivityLog.getDescription().substring(pnActivityLog.getDescription().indexOf("<beginOfentryText>")) : pnActivityLog.getDescription();
			shortCommentContent = shortCommentContent.replace("<beginOfentryText>", "");
			pnActivityLog.setDescription(pnActivityLog.getDescription().contains("<beginOfentryText>") ? pnActivityLog.getDescription().substring(0, pnActivityLog.getDescription().indexOf("<beginOfentryText>")) : pnActivityLog.getDescription());
			
			if (StringUtils.isNotEmpty(pnActivityLog.getDescription())){
				pnActivityLog.setDescription(MessageFormat.format(pnActivityLog.getDescription(),
						new Object[] {  
					SessionManager.getJSPRootURL()+"/images/activitylog/blog_comment.gif",
					"", "blogPersonGreen", 
					"gray",
					pnActivityLog.getObjectName(),
					"leftpadding_titleText",
					SessionManager.getJSPRootURL()+"/images/activitylog/expand.gif",
					"javascript:checkAndRedirect('"+pnActivityLog.getTargetObjectId() + "','" + pnActivityLog.getTargetObjectType() + "','" + pnActivityLog.getActivityLogId() + "');",
					SessionManager.getJSPRootURL()+"/blog/view/"+pnActivityLog.getActivityBy()+"/"+pnActivityLog.getActivityBy()+"/person/"+Module.PERSONAL_SPACE+"?module="+Module.PERSONAL_SPACE,
					pnActivityLog.getPersonName(),
					SessionManager.getJSPRootURL(),
					activityTime,
					PropertyProvider.get("prm.project.activity.blogcomment.title"),
					"activityBodyText",
					pnActivityLog.getActivityLogId().toString()
				}));
			}
		} catch (Exception e) {
			Logger.getLogger(ShowActivityTextForBlogComment.class).error("Error occurred while setting description of blog comment activity("+ activityLogId +"): "+e.getMessage());
		}
	}	

}
