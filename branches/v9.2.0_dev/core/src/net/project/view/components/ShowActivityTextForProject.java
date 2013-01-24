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
import net.project.hibernate.model.PnProjectSpace;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ShowActivityTextForProject {

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
	private String unMarkedToken;

	@Property
	private String actionTaken;

	private boolean isDisabled;

	private PnProjectSpace pnProjectSpace;
	
	@Property
	private String objectToken;

	@BeginRender
	public void initializeValues() {
		try {
			objectToken = PropertyProvider.get("prm.project.activity.project.title");
			pnActivityLog.setDescription(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription()));
			if (StringUtils.isNotEmpty(pnActivityLog.getDescription())){
				pnActivityLog.setDescription(MessageFormat.format(pnActivityLog.getDescription(),
						new Object[] { 
					"", pnActivityLog.getObjectName(),
					"green", 
					"gray",
					"javascript:checkAndRedirect('"+pnActivityLog.getTargetObjectId() + "','" + pnActivityLog.getTargetObjectType() + "','" + pnActivityLog.getActivityLogId() + "');",
					SessionManager.getJSPRootURL() + "/blog/view/"+pnActivityLog.getActivityBy()+"/"+pnActivityLog.getActivityBy()+"/person/"+Module.PERSONAL_SPACE+"?module="+Module.PERSONAL_SPACE,
					pnActivityLog.getPersonName(),
					activityTime,
					"activityBodyText",
					pnActivityLog.getActivityLogId().toString()
				}));
			}
		} catch (Exception e) {
			Logger.getLogger(ShowActivityTextForProject.class).error("Error occurred while setting description of project activity("+ pnActivityLog.getActivityLogId() +"): "+e.getMessage());
		}
	}

	//Return activity type
	public String getActivityType() {
		if (pnActivityLog.getActivityType().equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "created by";
		} else if(pnActivityLog.getActivityType().equalsIgnoreCase(EventType.EDITED.getText())){
			actionTaken = "updated by";
		}
		return actionTaken;
	}

	//Return description text
	public String returnRssDescrText() {
		return StringUtils.capitalize(pnActivityLog.getTargetObjectType())+" "+pnProjectSpace.getProjectName()+" "+getActivityType();
	}
	
	public PnActivityLog getPnActivityLog() {
		return pnActivityLog;
	}

	public void setPnActivityLog(PnActivityLog pnActivityLog) {
		this.pnActivityLog = pnActivityLog;
	}

	//Return project is deleted or not
	public boolean getIsDisabled() {
		return isDisabled;
	}

	public PnProjectSpace getPnProjectSpace() {
		return pnProjectSpace;
	}
}
