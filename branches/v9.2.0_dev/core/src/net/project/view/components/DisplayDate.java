/**
 * 
 */
package net.project.view.components;

import java.util.Calendar;
import java.util.Date;

import net.project.activity.ActivityLogManager;
import net.project.base.property.PropertyProvider;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;

/**
 *
 */
public class DisplayDate {
	
	@Parameter(required = true)
	private Date date;
	
	@Parameter
	private Date continuedDate;
	
	@BeginRender
	void renderMessage(MarkupWriter writer) {
		String dateToDisplay = ActivityLogManager.getDisplayDate(date);
		Calendar currentDate = Calendar.getInstance();
		Calendar dateToCheck = Calendar.getInstance();
		dateToCheck.setTime(date);
		if(continuedDate != null){
			currentDate.setTime(continuedDate);
			if(currentDate.get(Calendar.DATE) == dateToCheck.get(dateToCheck.DATE)
				&& currentDate.get(Calendar.MONTH) == dateToCheck.get(dateToCheck.MONTH)
				&& currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)){
				dateToDisplay += " " + PropertyProvider.get("prm.activity.displaydate.message") + " ";
			} 
		}
		writer.write(dateToDisplay);
	}
}
