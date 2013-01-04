/**
 * 
 */
package net.project.view.components;

import java.text.MessageFormat;

import net.project.activity.ActivityLogManager;
import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.events.EventType;
import net.project.hibernate.model.PnActivityLog;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 *
 */
public class ShowActivityTextForBlogEntry {
	
	@Property
	@Parameter(required = true)
	private PnActivityLog pnActivityLog;
	
	@Parameter
	@Property
	private String personName;
	
	@Parameter
	@Property
	private String activityTime;
	
	@Parameter
	@Property
	private String personLink;
	
	@Parameter
	@Property
	private String objectLink;
	
	@Parameter
	@Property
	private String personImg;
	
	@Parameter
	@Property
	private String unMarkedToken;
	
	@Property
	private String entryText;

	@Property
	private String fullText;
	
	@Property 
	private Integer activityLogId;
	
	@Property
	private String blogIdAndActivityId;
	
	@Property
	private String expand;
	
	@Property
	private boolean isValidForExpand;
	
	@BeginRender
	public void renderMessage() {
		try {
			expand = PropertyProvider.get("prm.project.activity.expand.lable");
			activityLogId = pnActivityLog.getActivityLogId();
			pnActivityLog.setDescription(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription()));
			entryText = pnActivityLog.getDescription().contains("<beginOfentryText>") ? pnActivityLog.getDescription().substring(pnActivityLog.getDescription().indexOf("<beginOfentryText>"), pnActivityLog.getDescription().length()) : pnActivityLog.getDescription();
			entryText = entryText.replace("<beginOfentryText>", "");
			isValidForExpand = entryText.length() > 150;
			pnActivityLog.setDescription(pnActivityLog.getDescription().contains("<beginOfentryText>") ? pnActivityLog.getDescription().substring(0, pnActivityLog.getDescription().indexOf("<beginOfentryText>")) : pnActivityLog.getDescription());
			
			if (StringUtils.isNotEmpty(pnActivityLog.getDescription())){
				pnActivityLog.setDescription(MessageFormat.format(pnActivityLog.getDescription(),
						new Object[] {  
					SessionManager.getJSPRootURL()
					+(pnActivityLog.getActivityType().equals(EventType.EDITED.getText()) ? "/images/activitylog/blog_entry_update.gif"
							: "/images/activitylog/blog_entry.gif"),
							"", "blogPersonGreen",
							"gray",
							(pnActivityLog.getObjectName().length() > 40 ? pnActivityLog.getObjectName().substring(0, 40)+"..." : pnActivityLog.getObjectName()),
							"leftpadding_titleText",
							SessionManager.getJSPRootURL()+"/images/activitylog/expand.gif",
							"javascript:checkAndRedirect('"+pnActivityLog.getTargetObjectId() + "','" + pnActivityLog.getTargetObjectType() + "','" + pnActivityLog.getActivityLogId() + "');",
							SessionManager.getJSPRootURL()+"/blog/view/"+pnActivityLog.getActivityBy()+"/"+pnActivityLog.getActivityBy()+"/person/"+Module.PERSONAL_SPACE+"?module="+Module.PERSONAL_SPACE,
							pnActivityLog.getPersonName(),
							pnActivityLog.getObjectName(),
							activityTime,
							PropertyProvider.get("prm.project.activity.blogentry.title"),
							"activityBodyText",
							pnActivityLog.getActivityLogId().toString()
				}));
				blogIdAndActivityId = ""+pnActivityLog.getTargetObjectId()+","+ activityLogId ;
				fullText = null;
			}
		} catch (Exception e) {
			Logger.getLogger(ShowActivityTextForBlogEntry.class).error("Error occurred while setting description of blog activity("+ activityLogId +"): "+e.getMessage());
		}
	}	
}
