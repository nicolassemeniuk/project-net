/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19845 $
|       $Date: 2009-08-24 13:18:07 -0300 (lun, 24 ago 2009) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.form.property.CustomPropertySheet;
import net.project.form.property.PropertySheetFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;

import org.apache.log4j.Logger;


/**
 * A form's date field.
 */
public class DateField extends FormField {

    /**
     * The Oracle format pattern to use in the <code>TO_DATE</code> function for
     * converting string dates to dates.
     */
    protected static final String DATABASE_DATETIME_FORMAT_PATTERN = "FMMM/DD/YYYY HH24:MI";

    /** Property name for the automatic default date checkbox */
    public static final String DEFAULT_DATE_ID = "use_default";
    /** Property name for the display mode radio group */
    public static final String DISPLAY_MODE_ID = "100";
    /** Radio button value for display of date only */
    public static final String MODE_DATE_ONLY = "10";
    /** Radio button value for display of date and time */
    public static final String MODE_DATE_TIME = "20";
    /** Radio button value for display of time only */
    public static final String MODE_TIME_ONLY = "30";

    // used to format dates and times.
    protected DateFormat m_dateFormatter = null;


    /**
     * Construct a date field.
     *
     * @param form the Form that this field belongs to.
     * @param field_id the id of the field in the database.
     */
    public DateField(Form form, String field_id) {
        super(form, field_id);
        m_dateFormatter = new net.project.util.DateFormat(SessionManager.getUser());
    }


    /**
     * Can this field be used for filtering.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isFilterable() {
        return true;
    }


    /**
     * Can this field be used for searching.
     *
     * @return true if the field can be used to filter form data.
     */
    public boolean isSearchable() {
        return true;
    }


    /**
     * Can this field be used for sorting.
     *
     * @return true if the field can be used to sort form data.
     */
    public boolean isSortable() {
        return true;
    }


    /**
     * @return true/false depending upon whether it is selectable for display in
     * the list.
     */
    public boolean isSelectable() {
        return true;
    }


    /**
     * @return the database storage type for the field. i.e. DATE
     */
    public String dbStorageType() {
        return (m_db_datatype);
    }


    /**
     * Format the datetime for display to the user.
     */
    public String formatFieldData(FieldData field_data) {
        String formatString = null;
        String dateString = null;

        // display nothing if field data is null
        //if (field_data == null && field_data.get(0) == null)
        if (field_data == null || field_data.get(0) == null) {
            return "";
        }
        java.util.Date tmpDate = null;
        // parse the date string into a date object so we can format it various ways
        try {
            tmpDate = m_dateFormatter.parseDateString((String)field_data.get(0), "M/d/yyyy H:mm");

        } catch (net.project.util.InvalidDateException ide) {
        	Logger.getLogger(DateField.class).debug("DateField.formatFieldData: InvalidDateException: " + ide);
            tmpDate = null;
        }

        // coudn't parse date.
        if (tmpDate == null)
            return "";

        ////////////////// DEBUG:
        // System.out.println("User.getDateFormat() = " +  SessionManager.getUser().getDateFormat());
        // System.out.println("User.getTimeFormat() = " +  SessionManager.getUser().getTimeFormat());
        //////////////////

        if (MODE_TIME_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {
            formatString = SessionManager.getUser().getTimeFormat();
            dateString = m_dateFormatter.formatDate(tmpDate, formatString);
        } else if (MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {
            formatString = SessionManager.getUser().getDateFormat() + " " + SessionManager.getUser().getTimeFormat();
            dateString = m_dateFormatter.formatDate(tmpDate, formatString);
        }
        // Default: MODE_DATE_ONLY
        else {
            formatString = SessionManager.getUser().getDateFormat();
            dateString = m_dateFormatter.formatDate(tmpDate, formatString);
        }

        return dateString;
    }


    /**
     * Returns the column to use in a SQL select statement. For example:
     * <code>TO_CHAR(tablename.columnname, 'FMMM/DD/YYYY HH24:MI')</code>
     *
     * @return the expression that is used to indentify a Date field in an SQL
     * select statement
     */
    protected String getSQLSelectColumn() {
        return ("TO_CHAR(" + getSQLName() + ", '" + DATABASE_DATETIME_FORMAT_PATTERN + "')");
    }


    /**
     * Outputs an HTML representation of this object to the specified stream.
     */
    public void writeHtml(FieldData field_data, java.io.PrintWriter out)
        throws java.io.IOException {
        FormFieldProperty property = null;
        ;
        String data_value = "";
        int num_properties;
        int prop_cnt;
        java.util.Date tmpDate = null;

        // field Label
        // out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_field_label + ":&nbsp;&nbsp;</td>\n");
        // Insert field label, flagging as an error if there is an error
        // for this field
        out.print("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + m_form.getFlagError(getDataColumnName(), HTMLUtils.escape(getFieldLabel())) + ":&nbsp;&nbsp;</td>\n");
        out.print("<td align=\"left\" nowrap=\"nowrap\" ");
        if (m_column_span > 1)
            out.print("colspan=\"" + ((m_column_span * 2) - 1) + "\"");
        out.print(">");

        // DATE PORTION
        if (getPropertyValue(this.DISPLAY_MODE_ID) == null || // for backwards compatibility with old date field
            MODE_DATE_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
            MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {
            out.print("<input type=\"text\" ");
            // field name.  set name="fieldXXXX" in the tag.
            out.print("name=\"" + m_data_column_name + "\" ");

            if (isValueRequired) {
                out.print("required=\"true\" ");
            }

            // field properties  (html tag attributes)
            num_properties = m_properties.size();

            for (prop_cnt = 0; prop_cnt < num_properties; prop_cnt++) {
                property = (FormFieldProperty)m_properties.get(prop_cnt);

                if (property.m_type.equals("in_tag")) {
                    if (property.m_name != null && !property.m_name.equals(""))
                        out.print(property.m_name);
                    if (property.m_value != null && !property.m_value.equals(""))
                        out.print("=\"" + property.m_value + "\" ");
                    else
                        out.print(" ");
                }
            }

            // default date value
            // Handle default date value if there if not data for this field
            if ((field_data == null) || (field_data.size() < 1)) {
                // format a date to today's date (intermediate value, not the user's format yet)
                if (useDefault()) {
                    data_value = m_dateFormatter.formatDate(new java.util.Date(), "M/d/yyyy");
                }
            }
            // write the data value in the field if there is one.
            else if (field_data != null) {
                try {
                    data_value = (String)field_data.get(0);        // only one value for text field
                } catch (IndexOutOfBoundsException e) {
                    data_value = "";
                }
            }
            if (data_value == null)
                data_value = "";
            try {
                tmpDate = m_dateFormatter.parseDateString(data_value, "M/d/yyyy");
            } catch (net.project.util.InvalidDateException ide) {
                // Since the date string is internally generated one
                //we don't expect this exception to be thrown and hence we ignore it.
                // Vishwajeet 06/19/2002
            }
            data_value = Conversion.dateToString(tmpDate);

            out.print(" value=\"" + data_value + "\">\n");
            out.print("<a href=\"javascript:popupDate('" + m_data_column_name + "')\" tabindex=\"-1\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" height=\"16\" border=\"0\"></a>");

            // Add appropriate script for indicating field is required
            if (isValueRequired()) {
                out.println(getRequiredValueJavascript());
            }
        }  // end date portion


        //
        // TIME PORTION
        //
        if (MODE_TIME_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
            MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {

            if (field_data != null &&
                    field_data.size() > 0 &&
                    field_data.get(0) != null) {

                try {
                    tmpDate = m_dateFormatter.parseDateString((String) field_data.get(0), "M/d/yyyy H:mm");

                } catch (net.project.util.InvalidDateException ide) {
                    // Since the date string is internally generated one
                    //we don't expect this exception to be thrown and hence we ignore it.
                    // Vishwajeet 06/19/2002
                }

                if (tmpDate == null) {
                    tmpDate = new Date();
                }

            } else {
                // New form, default data.

                // Note: Started to ignore useDefault() for time fields
                // With timefields as selection lists, it is not possible to
                // set them to "nothing"
                tmpDate = new Date();
            }

            TimeBean timeBean = new TimeBean();
            timeBean.setTag(m_data_column_name);
            timeBean.setDate(tmpDate);
            timeBean.setMinuteStyle(TimeBean.MINUTE_STYLE_NORMAL);
            out.print(timeBean.getPresentation());

        }
        // Now include any error message, if present
        out.print(m_form.getErrorMessage(getDataColumnName()));
        out.print("</td>\n");
    }

    public String writeHtmlReadOnly(FieldData field_data){
    
    StringBuffer fieldHtml = new StringBuffer();	

    String data_value = "";
    java.util.Date tmpDate = null;

    fieldHtml.append("<td align=\"left\" class=\"tableHeader\" >" + HTMLUtils.escape(m_field_label) + ": </td>");
    fieldHtml.append("<td align=\"left\" class=\"tableContent\">");
    // DATE PORTION
    if (getPropertyValue(this.DISPLAY_MODE_ID) == null || // for backwards compatibility with old date field
        MODE_DATE_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
        MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {
    	

        // default date value
        // Handle default date value if there if not data for this field
        if ((field_data == null) || (field_data.size() < 1)) {
            // format a date to today's date (intermediate value, not the user's format yet)
            if (useDefault()) {
                data_value = m_dateFormatter.formatDate(new java.util.Date(), "M/d/yyyy");
            }
        }
        // write the data value in the field if there is one.
        else if (field_data != null) {
            try {
                data_value = (String)field_data.get(0);        // only one value for text field
            } catch (IndexOutOfBoundsException e) {
                data_value = "";
            }
        }
        if (data_value == null)
            data_value = "";
        try {
            tmpDate = m_dateFormatter.parseDateString(data_value, "M/d/yyyy");
        } catch (net.project.util.InvalidDateException ide) {
            // Since the date string is internally generated one
            //we don't expect this exception to be thrown and hence we ignore it.
            // Vishwajeet 06/19/2002
        }
        data_value = Conversion.dateToString(tmpDate);

        //fieldHtml.append(" value=\"" + data_value + "\">\n");
        fieldHtml.append(data_value);

    }  

    //
    // TIME PORTION
    //
    if (MODE_TIME_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
        MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {

        if (field_data != null &&
                field_data.size() > 0 &&
                field_data.get(0) != null) {

            try {
                tmpDate = m_dateFormatter.parseDateString((String) field_data.get(0), "M/d/yyyy H:mm");

            } catch (net.project.util.InvalidDateException ide) {
                // Since the date string is internally generated one
                //we don't expect this exception to be thrown and hence we ignore it.
                // Vishwajeet 06/19/2002
            }

            if (tmpDate == null) {
                tmpDate = new Date();
            }

        } else {
            // New form, default data.

            // Note: Started to ignore useDefault() for time fields
            // With timefields as selection lists, it is not possible to
            // set them to "nothing"
            tmpDate = new Date();
        }

        TimeBean timeBean = new TimeBean();
        timeBean.setTag(m_data_column_name);
        timeBean.setDate(tmpDate);
        timeBean.setMinuteStyle(TimeBean.MINUTE_STYLE_NORMAL);
        fieldHtml.append(" " + timeBean.getHourSelectionValue()+ ":" + timeBean.getMinuteSelectionValue() + " " + (timeBean.getAmPmSelectionValue() == 1 ? "PM" : "AM"));

    }
    fieldHtml.append("</td>");
    
    //return field_data.toString();
    return fieldHtml.toString();
}    
    

    ////////  WARNING:  Date filters have not been fully tested and are not active yet.   More code may be needed.  -Roger  ////////////
    /**
     * Outputs an HTML representation of the field's filter to the specified
     * stream.
     */
    public void writeFilterHtml(FieldFilter filter, java.io.PrintWriter out)
        throws java.io.IOException {
        FieldFilterConstraint constraint = null;
        //String radioValue = null;
        String value1 = null;
        String value2 = null;
        //String data_value = "";
        int filterSize = 0;

        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");
                
        if ((filter != null) && ((filterSize = filter.size()) > 0)) {
        	
        	for(int idx=0; idx< filterSize; idx++){
        		constraint = filter.getConstraint(idx);
        		String value = null;
        		try{ //
        			Date dateValue = m_dateFormatter.parseDateString((String)constraint.get(0), "MM/dd/yyyy");
        			value = m_dateFormatter.formatDate(dateValue, SessionManager.getUser().getDateFormat());
        		}catch (Exception e) {

				}        		
        		if(constraint.getOperator().equals(">") ){
        			value1 = value;        		
        		}
        		else
        			value2 = value;
        	}
        	
/*            constraint = filter.getConstraint(0);
            value1 = (String)constraint.get(0);
            if (filterSize > 1){
            	constraint = filter.getConstraint(1);
            	value2 = (String)constraint.get(0);
            }*/
            
/*            if ((constraint != null) && (constraint.size() > 0))
                radioValue = "static";// (String)constraint.get(0);

            if ((radioValue != null) && radioValue.equals(""))
                radioValue = null;

            if (filterSize > 1)
                value1 = (String)constraint.get(0);
            if ((value1 == null) || (value1.equals("")))
                value1 = "All";

            if (filterSize > 2)
                value2 = (String)constraint.get(2);*/
        }
        
        // DATE RANGE radio
     /*   out.print("<input type=radio name=\"filter__radio" + m_field_id + "\" value=static");
        if (radioValue != null && radioValue.equals("static"))
            out.print(" checked");
        out.println(">");*/
        out.println("Between ");
        out.println("<input type=text name=\"filter__start" + m_field_id + "\"");
//        if (radioValue != null && radioValue.equals("static") && !value1.equals("All"))
         if(value1 != null)
            out.print(" value=\"" + value1 +"\"");  // Start Date
        out.println("/><a href=\"javascript:autoDate('filter__start" + m_field_id + "','"+SessionManager.getJSPRootURL()+"')\" tabindex=\"-1\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" height=\"16\" border=\"0\"></a>");
        out.println(" and ");
        out.println("<input type=text name=\"filter__end" + m_field_id + "\"");
        //if (radioValue != null && radioValue.equals("static"))
        if(value2 != null)
            out.print(" value=\"" + value2 +"\"");  // End Date
        out.print("/><a href=\"javascript:autoDate('filter__end" + m_field_id + "','"+SessionManager.getJSPRootURL()+"')\" tabindex=\"-1\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" height=\"16\" border=\"0\"></a>");

/*
        // DYNAMIC INTERVAL radio
        out.println("<input type=radio name=\"filter__radio" + m_field_id + "\" value=dynamic");
        if (radioValue != null && radioValue.equals("dynamic"))
            out.print(" checked");
        out.println(">");

        out.print("<select name=\"filter__interval" + m_field_id + "\"");
        out.println(" value=\"" + data_value + "\">");

        out.println("<option value=All");
        if (value1 != null &&  value1.equals("All"))
            out.print(" selected");
        out.println(">All</option>");

        out.println("<option value=today");
        if (value1 != null &&  value1.equals("today"))
            out.print(" selected");
        out.println(">Today</option>");

        out.println("<option value=yesterday");
        if (value1 != null &&  value1.equals("yesterday"))
            out.print(" selected");
        out.println(">Yesterday</option>");

        out.println("<option value=this_week");
        if (value1 != null &&  value1.equals("this_week"))
            out.print(" selected");
        out.println(">This Week</option>");

        out.println("<option value=last_week");
        if (value1 != null &&  value1.equals("last_week"))
            out.print(" selected");
        out.println(">Last Week</option>");

        out.println("<option value=next_week");
        if (value1 != null &&  value1.equals("next_week"))
            out.print(" selected");
        out.println(">Next Week</option>");

        out.println("<option value=this_month");
        if (value1 != null &&  value1.equals("this_month"))
            out.print(" selected");
        out.println(">This Month</option>");

        out.print("</select>");
        out.print("</td>\n");*/
    }


    public void processHttpPost(javax.servlet.ServletRequest request, FieldData fieldData) {
        String dateString = null;
        String hourString = null;
        String minuteString = null;
        String ampmString = null;

        // DATE portion
        if ((request.getParameter(this.m_data_column_name) != null) && !request.getParameter(this.m_data_column_name).equals("")) {
            dateString = request.getParameter(this.m_data_column_name);
        }
        fieldData.add(dateString);

        // hour portion
        hourString = request.getParameter(this.m_data_column_name + "_hour");
        fieldData.add(hourString);

        // minute portion
        minuteString = request.getParameter(this.m_data_column_name + "_minute");
        fieldData.add(minuteString);

        // AMPM
        ampmString = request.getParameter(this.m_data_column_name + "_ampm");
        fieldData.add(ampmString);
    }

    /**
     * Converts the field data into date time.
     *
     * @param data the field data list to be converted
     */
    public void processFieldData(FieldData data) {
        ArrayList newData = new ArrayList(data.size());

        String userDatePattern = SessionManager.getUser().getDateFormat();
        String parseDatePattern = null;
        String tmpDateString = null;
        int hour = 0;
        int minutes = 0;
        String ampmString = null;
        boolean isNullValue = false;

        // Grab the date portion
        tmpDateString = (String) data.get(0);
        if (tmpDateString != null) {
            // We got a date
            parseDatePattern = userDatePattern;

        } else {
            // Date string is null
            // if this is a date or date-time field, we want to store a null date in the database.
            // if this field contains time, we "zero" the date (set to January 1, 0001, 00:00:00 GMT).
            if (MODE_DATE_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
                MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID)) ||
                getPropertyValue(this.DISPLAY_MODE_ID) == null) {

                // Field value is null
                isNullValue = true;

            } else {
                // Date not needed, we'll continue to get the time value
                tmpDateString = "1/1/0001";
                parseDatePattern = "M/d/yyyy";
            }
        }

        // Now grab the time components
        //parse hour
        try {
            hour = Integer.parseInt((String) data.get(1));

        } catch (NumberFormatException nfe) {
            hour = 0;
            // user did no enter hour, if this is a time-only field
            if (MODE_TIME_ONLY.equals(getPropertyValue(this.DISPLAY_MODE_ID)) || MODE_DATE_TIME.equals(getPropertyValue(this.DISPLAY_MODE_ID))) {
                isNullValue = true;
            }

        }

        // parse MINUTES
        try {
            minutes = Integer.parseInt((String) data.get(2));

        } catch (NumberFormatException nfe) {
            // ok, we will let the user leave the minutes field empy and default it to :00.
            minutes = 0;

        }

        // parse AMPM
        ampmString = (String) data.get(3);
        if ((ampmString != null) && new Integer(Calendar.PM).toString().equals(ampmString)) {
            // ampmString is PM
        } else {
            ampmString = String.valueOf(Calendar.AM);
        }


        // Add parsed date to the new data
        // structure.
        if (isNullValue) {
            newData.add(null);

        } else {
            // First parse the date portion
            Date resultDate = null;
            try {
                resultDate = m_dateFormatter.parseDateTimeString(tmpDateString, parseDatePattern);
            } catch (net.project.util.InvalidDateException ide) {
                // We do not expect this to occur.
                resultDate = new Date();
                Logger.getLogger(DateField.class).debug("DateField.java : InvalidDateException thrown" + ide.getMessage());
            }

            // Now apply the time to the date
            TimeZone userTimeZone = SessionManager.getUser().getTimeZone();
            resultDate = TimeBean.parseTime(String.valueOf(hour), String.valueOf(minutes), ampmString, userTimeZone.getID(), resultDate);

            // Now format the date using the built-in format
            newData.add(m_dateFormatter.formatDate(resultDate, "M/d/yyyy H:mm"));
        }

        // Now replace the elements in existing data with
        // new data elements
        data.clear();
        data.addAll(newData);

    }


    /**
     * Process the HTTP request to extract the filter selections for this field.
     *
     * @return the FieldFilter containing the filter information for the field,
     * null if their was no filter information for the field in the request.
     */
    public FieldFilter processFilterHttpPost(javax.servlet.ServletRequest request) {
        String radioValue, startDate, endDate;
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        filter = new FieldFilter(this);

   //     radioValue = request.getParameter("filter__radio" + m_field_id);

   //     if (radioValue.equals("static")) {
            startDate = request.getParameter("filter__start" + m_field_id);
            endDate = request.getParameter("filter__end" + m_field_id);
            Date dateStart = null;
            Date dateEnd = null;
            try{	
            	dateStart = m_dateFormatter.parseDateString(startDate, SessionManager.getUser().getDateFormat());
            	dateEnd = m_dateFormatter.parseDateString(endDate, SessionManager.getUser().getDateFormat());            
            } catch (net.project.util.InvalidDateException ide) {
                // We don't expect this exception to be thrown at all since
                // the data being compared would already have been validated and hence we ignore it
            }
            
            if ((startDate == null) && (endDate == null))
                return filter;

			StringBuffer dateString = new StringBuffer();
			if(dateStart != null){
				dateString.append(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(dateStart));
	            constraint = new FieldFilterConstraint(1);
	            constraint.setOperator(">");
	            //constraint.add(startDate);
	            constraint.add(dateString.toString());
	            filter.addConstraint(constraint);
			}

			if(dateEnd != null){
	            dateString = new StringBuffer();
				dateString.append(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(dateEnd));            
	            constraint = new FieldFilterConstraint(1);
	            constraint.setOperator("<");
	            constraint.add(dateString.toString());
	            filter.addConstraint(constraint);
			}
        //}
        // Dynamic date ranges
    /*    else {
            constraint = new FieldFilterConstraint(1);
            constraint.setOperator("in");
            constraint.add(request.getParameter("filter__interval" + m_field_id));
            filter.addConstraint(constraint);
        }*/

        return filter;
    }


    /**
     * Compare data from two date fields. null fields are considered "greater
     * than" defined fields.
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        java.util.Date date1 = null;
        java.util.Date date2 = null;

        // data1 ith datam is null, data1 > data2
        if (data1.get(0) == null)
            return 1;
        // data2 ith datam is null, data1 < data2
        if (data2.get(0) == null)
            return -1;
        try {
            date1 = m_dateFormatter.parseDateString((String)data1.get(0), "M/d/yyyy H:mm");
            date2 = m_dateFormatter.parseDateString((String)data2.get(0), "M/d/yyyy H:mm");

        } catch (net.project.util.InvalidDateException ide) {
            // We don't expect this exception to be thrown at all since
            // the data being compared would already have been validated and hence we ignore it
        }

        // if the ith person is different, return the comparison, otherwise go on to the next person.
        if (date1.before(date2))
            return 1;
        else if (date1.after(date2))
            return -1;
        else
            return 0;
    }


    /**
     * Process the specified value for insertion into persistence storage.
     *
     * @param  value the date/time String in the java format "M/d/yyyy H:mm" .
     * @return <code>"NULL"</code> when value is null; otherwise the value
     * formatted for a date insert using the format
     */
    public String processDataValueForInsert(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return "TO_DATE('" + value + "', '" + DATABASE_DATETIME_FORMAT_PATTERN + "')";
        }
    }


    /**
     * Loads this field's properties. The loaded properties are accessible by
     * {@link #getProperties}.
     */
    public void loadProperties() {
        try {
            // Get this field's property sheet; this is assumed to be
            // a CustomPropertySheet
            CustomPropertySheet propertySheet = (CustomPropertySheet)PropertySheetFactory.getPropertySheetForDisplayClass(getElementDisplayClassID());
            // not needed anymore CustomPropertySheet propertySheet = (CustomPropertySheet) PropertySheetFactory.getCalculationFieldPropertySheet();
            propertySheet.setID(getElementDisplayClassID());
            propertySheet.setManagedField(this);
            propertySheet.setForm(getForm());

            // Load properties
            propertySheet.load();

            // Add custom properties to form field properties
            this.m_properties.addAll(propertySheet.getFormFieldProperties());
        } catch (PersistenceException pe) {
        	Logger.getLogger(DateField.class).debug("DateField.loadProperties(), PersistenceException: " + pe);
        }
    }

    /**
     * Validates that the specified FieldData contains legal date.
     *
     * @param data the field data to validate
     * @param errorMessagePatternBuffer the buffer into which the error message
     * pattern is written
     * @return true if the date in the field data is a legal date; false otherwise
     */
    public boolean isValidFieldData(FieldData data, StringBuffer errorValue, StringBuffer errorMessagePatternBuffer) {
        boolean isValid = true;
        String value = null;

        // There are always four parts of a date string, we will check each one  individually for correctness.
        //First, the date part
        try {
            value = (String)data.get(0);
            if (value != null && value.length() > 0) {
                net.project.util.DateFormat formatter = net.project.security.SessionManager.getUser().getDateFormatter();
                formatter.parseDateString(value);
            }

        } catch (net.project.util.InvalidDateException ide) {
            isValid = false;
            errorValue.append(value);
            errorMessagePatternBuffer.append(PropertyProvider.get("prm.form.date.error.cannotparse.message", new Object[] { value } ));
			
        }

        // Second, the hour part
        try {
            value = (String)data.get(1);
            if (value != null && value.length() > 0) {
                Integer.parseInt((String)data.get(1));
            }

        } catch (NumberFormatException nfe) {
            isValid = false;
            errorValue.append(value);
            errorMessagePatternBuffer.append(PropertyProvider.get("prm.form.date.error.invalidhour.message", new Object[] { value } ));
        }

        // Third, the minute part
        try {
            value = (String)data.get(2);
            if (value != null && value.length() > 0) {
                Integer.parseInt((String)data.get(2));
            }

        } catch (NumberFormatException nfe) {
            isValid = false;
            errorValue.append(value);
            errorMessagePatternBuffer.append(PropertyProvider.get("prm.form.date.error.invalidminute.message", new Object[] { value } ));
        }

        // Fourth, the AM-PM part, we always provide drop downs for these ones,
        // and therefore need not validate here.

        return isValid;
    }
    
    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * @param filter the filter values to use in generating the SQL.
     * @param joinOperator String containing the boolean operator to join this
     * field filter to the preceeding filter. "and" or "or".
     */
    public String getFilterSQL(FieldFilter filter, String joinOperator) {
        FieldFilterConstraint constraint;
        int num_constraints;
        int num_values;
        StringBuffer sb = new StringBuffer();

        sb.append(" " + joinOperator + " (");

        // For each constraint
        num_constraints = filter.m_constraints.size();
        for (int i = 0; i < num_constraints; i++) {
            constraint = (FieldFilterConstraint)filter.m_constraints.get(i);
            num_values = constraint.size();
            // For each constraint value
            if(i>0)
            	sb.append(" and ");
            for (int j = 0; j < num_values; j++) {
                if ((constraint.get(j) != null) && !(constraint.get(j)).equals("")) {
                    if (j != 0)
                        sb.append(" or ");
                    sb.append("(" + this.getDataColumnName() + " " + constraint.getOperator() + " TO_DATE('" + constraint.get(j) + "', 'MM/DD/YYYY HH24:MI'))");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    
}

