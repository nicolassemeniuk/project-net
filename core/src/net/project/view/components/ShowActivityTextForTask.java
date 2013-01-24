/**
 * 
 */
package net.project.view.components;

import java.text.MessageFormat;

import net.project.activity.ActivityLogManager;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnActivityLog;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ShowActivityTextForTask {
	
	@Property
	@Parameter(required = true)
	private PnActivityLog pnActivityLog;
	
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
	private String unMarkedToken;
	
	@Property
	private String objectToken;
	
	@BeginRender
	public void renderMessage() {
		try {
			objectToken = PropertyProvider.get("prm.project.activity.task");
			pnActivityLog.setDescription(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription()));
			if (StringUtils.isNotEmpty(pnActivityLog.getDescription())){
				pnActivityLog.setDescription(MessageFormat.format(pnActivityLog.getDescription(),
						new Object[] { 
					"", pnActivityLog.getObjectName(),
					"green", 
					"gray",
					"javascript:checkAndRedirect('"+pnActivityLog.getTargetObjectId() + "','" + pnActivityLog.getTargetObjectType() + "','" + pnActivityLog.getActivityLogId() + "');",
					personLink,
					pnActivityLog.getPersonName(),
					activityTime,
					"activityBodyText",
					pnActivityLog.getActivityLogId().toString()
				}));
			}
		} catch (Exception e) {
			Logger.getLogger(ShowActivityTextForTask.class).error("Error occurred while setting description of task activity("+ pnActivityLog.getActivityLogId() +"): "+e.getMessage());
		}
	}
}
