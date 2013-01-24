/**
 * 
 */
package net.project.view.pages.blog;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnAssignmentWork;
import net.project.hibernate.service.IPnAssignmentWorkService;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class MultipleWorkSubmittedEntries extends BasePage{
	
	private static Logger log;

	@Persist
	@Property
	private List<PnAssignmentWork> multipleWorkSubmited;
	
	@Property
	private PnAssignmentWork pnAssignmentWork;
	
	private String multipleWorkSubmittedCSV;
	
	private enum MultipleWorkActions {
		GET_MULTIPLE_WORKSUBMITTED;
	}
	
	void intializeValues(){
		log = Logger.getLogger(MultipleWorkSubmittedEntries.class);
		multipleWorkSubmited = new ArrayList<PnAssignmentWork>();
	}
	
	Object onActivate(String action) {
		intializeValues();
		if(action.equalsIgnoreCase(MultipleWorkActions.GET_MULTIPLE_WORKSUBMITTED.toString())) {
			multipleWorkSubmittedCSV = getRequest().getParameter("multipleWorkSubmitted");
			String[] multipleWorkSubmittedArray = getMultipleWorkSubmittedCSV().split(","); 
			try{	
				for(int index = 0; index < multipleWorkSubmittedArray.length; index++) {
					if(StringUtils.isNotEmpty(multipleWorkSubmittedArray[index])) {
						if((index % 2) == 0) {
							pnAssignmentWork = getPnAssignmentWorkService().getWorkDetailsById(Integer.parseInt(multipleWorkSubmittedArray[index]));
						} else {
							pnAssignmentWork.setObjectName(getPnObjectNameService().getNameFofObject(Integer.parseInt(multipleWorkSubmittedArray[index])));
							pnAssignmentWork.setWorkStartDate(getUtilService().calculateDate(pnAssignmentWork.getWorkStart(), DateFormat.getInstance()));
							multipleWorkSubmited.add(pnAssignmentWork);
						}
					}
				}
			} catch (Exception e) {
				log.error("Error Occured while printing Multiple Work Table: " +e.getMessage());
			}
		}
		return null;
	}

	/**
	 * @return the multipleWorkSubmittedCSV
	 */
	public String getMultipleWorkSubmittedCSV() {
		return multipleWorkSubmittedCSV;
	}

	/**
	 * @param multipleWorkSubmittedCSV the multipleWorkSubmittedCSV to set
	 */
	public void setMultipleWorkSubmittedCSV(String multipleWorkSubmittedCSV) {
		this.multipleWorkSubmittedCSV = multipleWorkSubmittedCSV;
	}
}
