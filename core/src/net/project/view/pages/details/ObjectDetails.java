package net.project.view.pages.details;

import net.project.base.ObjectFactory;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.calendar.Meeting;
import net.project.form.FormData;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.view.pages.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;

/**
 * @author
 */
public class ObjectDetails extends BasePage {

	private static Logger log = Logger.getLogger(ObjectDetails.class);

	/**
	 * 
	 */
	@Property
	private String presentation;

	/**
	 * @return
	 */
	Object onActivate(String param) {
		String objectId = getParameter("objectId");
		boolean isLoadTask = Boolean.valueOf(getParameter("isLoadTask")).booleanValue();
		String objectType = ObjectFactory.getObjectType(objectId);
		try {
			if (ObjectType.FORM_DATA.equals(objectType)) {
				FormData formData = new FormData();
				formData.load(objectId);
				presentation = formData.getForm().getDetails();
			} else if (ObjectType.TASK.equals(objectType)) {
				ScheduleEntry task = null;
				if(!isLoadTask) {
					Schedule schedule = (Schedule) getSessionAttribute("schedule");
					if (schedule != null) {
						task = schedule.getEntry(objectId);
					}
				}
				if (task == null) {
					task = new Task();
					task.setID(objectId);
					task.load();
				}
				presentation = task.getDetails(getServletContext());
			} else if (ObjectType.MEETING.equals(objectType)) {
				Meeting meeting = new Meeting();
				meeting.setID(objectId);
				meeting.load();
				presentation = meeting.getDetails(getServletContext());
			} else if (ObjectType.PROJECT.equals(objectType)) {
				ProjectSpace project = new ProjectSpace();
				project.setID(objectId);
				project.load();
				presentation = project.getDetails(getServletContext());
			} else if (ObjectType.BUSINESS.equals(objectType)) {
				BusinessSpace business = new BusinessSpace();
				business.setID(objectId);
				business.load();
				presentation = business.getDetails(getServletContext());
			} else {
				presentation = "<label class=\"two-pane-message\">" + PropertyProvider.get("prm.global.detailsnotavailablehere.message") + "</label>";
				log.error("Undefined object type: " + objectType);
			}
		} catch (PersistenceException pnetEx) {
			log.error("Eror occured while loading " + objectType);
			presentation = "";
		}
		return null;
	}
}
