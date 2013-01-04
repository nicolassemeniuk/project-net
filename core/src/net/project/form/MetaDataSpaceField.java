package net.project.form;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;

public class MetaDataSpaceField extends FormField {

	public static final int DATA_COLUMN_SIZE = 0; // static field, no storage.

	/**
	 * @param form
	 *            the Form that this field belongs to.
	 * @param field_id
	 *            the id of the field in the database.
	 */
	public MetaDataSpaceField(Form form, String field_id) {
		super(form, field_id);
		m_data_column_size = DATA_COLUMN_SIZE;
	}

	protected String getSQLSelectColumn() {
		return " pn_class_instance.space_id ";
	}

	public String getSQLName() {
		return getSQLSelectColumn();
	}

	/**
	 * @return the database storage type for the field; this field type is
	 *         static and does not store data.
	 */
	public String dbStorageType() {
		return null;
	}

	public boolean isDesignable() {
		return false;
	}

	/**
	 * "Space Field" is exportable field
	 */
	public boolean isExportable() {
		return true;
	}

	public String formatFieldDataCSV(FieldData field_data) {
		return formatFieldData(field_data);
	}

	@Override
	public String formatFieldData(FieldData fieldData) {
		HashMap<String, HashMap<String, String>> spaceIds = m_form.getSharedFormSpaceIds();
		String spaceId = (String)fieldData.get(0);
		if(spaceIds.get(spaceId) != null){
			return spaceIds.get(spaceId).get("spaceName");
		}else if(getForm() != null && SessionManager.getUser().getCurrentSpace().getID().equals(getForm().getSpace().getID())){
			// Display top business name for shared form records 
			return SessionManager.getUser().getCurrentSpace().getName();
		} else {
			return "";
		}
	}

	public boolean isFilterable() {
		return true;
	}

	public boolean isSearchable() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isSelectable() {
		return true;
	}

	public boolean isSortable() {
		return true;
	}

	public FieldFilter processFilterHttpPost(ServletRequest request) {
        String[] values;
        int num_values = 0;
        FieldFilter filter = null;
        FieldFilterConstraint constraint = null;

        values = request.getParameterValues("filter__" + m_field_id);

        // If we have filter values selected for this field and "All" is not selected.
        // "All" is the same as nothing selected, no filter.
        if ((values != null) && ((num_values = values.length) > 0) && !values[0].equals("All") && !values[0].equals("")) {
            filter = new FieldFilter(this);
            constraint = new FieldFilterConstraint(1);
            // support only the "=" operator.
            constraint.setOperator("=");

            for (int val = 0; val < num_values; val++) {
                constraint.add(values[val]);
            }

            filter.addConstraint(constraint);
            return filter;
        } else
            return null;
	}

	public void writeFilterHtml(FieldFilter filter, PrintWriter out)
			throws IOException {
        // the label
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\">" + HTMLUtils.escape(m_field_label) + "</td>");
        out.println("<td align=\"left\" width=\"1%\" nowrap=\"nowrap\"> &nbsp;&nbsp; = &nbsp;&nbsp; </td>");
        out.println("<td align=\"left\">");

        // the field tag
        out.print("<select");

        // field name.  set name="fieldXXXXfilter" in the tag.
        out.print(" name=\"filter__" + m_field_id + "\" ");

        // filters use multiple selection.
        out.print("multiple size=\"4\" ");
        out.print(">\n");
		
        // Create a list of the default values
        List<String> defaultValuesList = new ArrayList<String>();
        if (filter != null) {
            Iterator it = filter.getConstraintsIterator();
            while (it.hasNext()) {
                FieldFilterConstraint constraint = (FieldFilterConstraint)it.next();
                Iterator it2 = constraint.iterator();
                while (it2.hasNext()) {
                	defaultValuesList.add((String)it2.next());
                }
            }
        }
        
        if(defaultValuesList.size() == 0)
        	defaultValuesList.add("All");
 
        HashMap<String, HashMap<String, String>> spaceIds = m_form.getSharedFormSpaceIds();
        String spaceId = m_form.getSpace().getID();
        List<String> spaceIdsSet = new ArrayList<String>();
        if (spaceIds.get(spaceId) != null && spaceIds.get(spaceId).get("childIds") != null){
	        String childSpacesIds = spaceIds.get(spaceId).get("childIds") ;
	        String[] spaceIdsArray = childSpacesIds.split(",");
	        
	        for(int idx = 0; idx< spaceIdsArray.length; idx++){
	        	if (!spaceIdsSet.contains(spaceIdsArray[idx].trim()))
	        		spaceIdsSet.add(spaceIdsArray[idx].trim());
	        }
        }
        out.print("<option value=\"\"");
        out.print(">" + PropertyProvider.get("prm.form.designer.lists.create.filters.personlist.option.all.name") + "</option>\n");                
        for(String value : spaceIdsSet){
        	if (spaceIds.get(value) != null){
        		String display = spaceIds.get(value).get("spaceName");
        		String selected = defaultValuesList.contains(value) ? "selected" : "";
        		out.print("<option value=\"" + value + "\" " + selected + " >" + HTMLUtils.escape(display) + "</option>");
        	}
        }
        out.print("</select>\n");
        out.print("</td>\n");        
	}

	public void writeHtml(FieldData field_data, PrintWriter out)
			throws IOException {
	}

	
	 /**
     * Compare data from two PersonListFields. Compares the two person fields by
     * full name. Case-insensitive lexicographic compare is used. null fields
     * are considered "greater than" defined fields.
     *
     * @return 0 if equal, -1 if data1 < data2, 1 if data1 > data2, per contract
     * of java.util.Comparator.
     * @see java.util.Comparator
     */
    public int compareData(FieldData data1, FieldData data2) {
        String space1, space2;
        int compareValue;
        int data1Size, data2Size;

        data1Size = data1.size();
        data2Size = data2.size();
        HashMap<String, HashMap<String, String>> spaceIds = m_form.getSharedFormSpaceIds();
        for (int i = 0; i < data1Size; i++) {
            // end of data2 list, data1 > data2
            if (data2Size <= i)
                return 1;

            // field data may contain null values
            if ((data1.get(i) == null) || (data2.get(i) == null)) {
                // data1 if data1 is null, data1 > data2
                if (data1.get(i) == null)
                    return 1;
                // data2 if data2 is null, data1 < data2
                else
                    return -1;
            }
            // normal compare of full names.
            else {
                space1 = spaceIds.get((String)data1.get(i)).get("spaceName"); 
                space2 = spaceIds.get((String)data2.get(i)).get("spaceName");

                // if the if space is different, return the comparison, otherwise go on to the next space.
                if ((compareValue = space1.compareToIgnoreCase(space2)) != 0)
                    return compareValue;
            }
        }

        // if data2 still has more values, data1 < data2.
        // otherwise the fields are identical.
        if (data2Size > data1Size)
            return -1;
        else
            return 0;
    }	

    /**
     * Return an SQL representation of the field filter to be used in a WHERE
     * clause.
     *
     * The PersonList variety of this method differs from the standard FormField
     * version because it needs to be aware of the "current user" field type,
     * which it needs to change dynamically right before SQL is run.
     *
     * @param filter the filter values to use in generating the SQL.
     * @param joinOperator String containing the boolean operator to join this
     * field filter to the preceding filter. "and" or "or".
     */
    public String getFilterSQL(FieldFilter filter, String joinOperator) {
        FieldFilterConstraint constraint = null;
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
            for (int j = 0; j < num_values; j++) {
                if ((constraint.get(j) != null) && !(constraint.get(j)).equals("")) {
                    if (j != 0)
                        sb.append(" or ");

                    String constraintValue = (String)constraint.get(j);
                    sb.append("( "+  getSQLSelectColumn() + " " + constraint.getOperator() + " " + constraintValue + ")");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }        
    
}
