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
|   $Revision: 18403 $
|       $Date: 2008-11-23 11:17:42 -0200 (dom, 23 nov 2008) $
|     $Author: vivana $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;


/**  
    This class represents a sort on a one or more FormFields. The FormList class uses this class to sort the list being displayed.  .
    This class determines the FormField to sort by, the sequence of in which this sort is applied, and whether the sort in ascending or descending order.
*/
public class FormSort
implements java.io.Serializable
{
    /** the form list this sort belongs to. */
    protected FormList m_list = null;

    /** the fields to sort by. */
    protected IndexedArrayList m_fields; 

    /** The comparator class for sorting an ArrayList of FormData. */
    protected FormListComparator m_dataComparator;

    /** the number of sort fields to allow */
    protected static final int NUM_SORT_FIELDS = 3;

    /** 
       Sole constructor
    */
    public FormSort()
    {
        m_fields = new IndexedArrayList(NUM_SORT_FIELDS);
        m_dataComparator = new FormListComparator();
    }

    /** set the FormList that this sort is for. */
    public void setFormList(FormList list)
    {
        m_list = list;
    }

    /** get the FormList that the sort is for. */
    public FormList getFormList()
    {
        return m_list;
    }

    /** clear the fields from this FormSort */
    public void clear()
    {
        m_fields.clear();
        m_fields.clearMap();
    }

    /**
         Add a FormField to the sort.  The sort order will be the order that fields are added.
         @param field the FormField to add to the sort, or null to clear the sort position.
         @param order the this field should be sorted by, vis-a-vis other fields on this sort.
         @param isAscending true is this field should be sorted by ascending values, false for descending.
         @throws FormFieldException if the passed value of order is outside the allowed range. 
     */
    public void setSortField(FormField field, int order, boolean isAscending)
    throws FormSortException
    {
        if ((order < 0) || (order > NUM_SORT_FIELDS))
            throw new FormSortException("FormSort range exception.  Sort field order must be between 0 and" + NUM_SORT_FIELDS + ".");

        // Ok to set to null field to clear the field from the sort list.
        // NOTE: order is ignored for now, order determined by call order.
        m_fields.addNew(order,field);

        if (field != null)
        {
            ListFieldProperties fieldProperties = m_list.getListFieldProperties(field.getID());
            fieldProperties.setSortAscending(isAscending);
        }
    }

    /**
        Have any sort fields been specified.
        @return true if one or more fields have been set to sort by.
    */
    public boolean hasSortFields()
    {
        return(m_fields.size() > 0);
    }

    /**
        The number of sort fields set.
        @return  The number of sort fields set.
    */
    public int size()
    {
        return m_fields.size();
    }

    /**
        The order in the list determines their sort order ( the order they should be listed in an SQL order by clause).
        @param order Nth sort field to return 0, 1, 2,...
         @returns the specified sort field., null if the sort field is not found.

    */
    public FormField getSortField(int order)
    {
        //if ((order < 0) || (order > NUM_SORT_FIELDS))
        //    throw new FormSortException("FormSort range exception.  Sort field order must be between 0 and" + NUM_SORT_FIELDS + ".");

        if ((m_fields != null) && (m_fields.size() > order))
        {
            //System.out.println("FormSort.getSortField(" + order + ") returned: "+ ((FormField)m_fields.get(order)).getFieldLabel());    
            return(FormField) m_fields.get(order);
        }
        else
        {
            //System.out.println("FormSort.getSortField(" + order + ") returned: null");
            return null;
        }
    }

    /**
           The order in the list determines their sort order ( the order they should be listed in an SQL order by clause).
           @param field the soft field with field_id matching the specified field, null if the field is not one of the current sort fields.
           @returns the specified sort field.  
   */
    public FormField getSortField(FormField field)
    {
        int order;

        if ((order = getSortFieldOrder(field)) != -1)
            return(FormField)m_fields.get(order);
        else
            return null;
    }

    /**
        Get the order of the specified sort field.
        @return the int sort order of the the field, -1 if the field was not found on the sort list or passed field is null. 
    */
    public int getSortFieldOrder(FormField field)
    {
        FormField sortField;

        if (field == null)
            return -1;

        for (int i=0; i<NUM_SORT_FIELDS; i++)
        {
            sortField = (FormField) m_fields.get(i);

            if ((sortField != null) && (sortField.getID().equals(field.getID())))
                return i;
        }
        return -1;
    }

    /**
      Is the specified sort field set to ascending?
      @return true is the specified field is set to ascending, false if desending, field was not found on the sort list, or the passed field is null.
    */
    public boolean isSortFieldAscending(FormField field)
    {

        if (field != null)
        {
            ListFieldProperties fieldProperties = m_list.getListFieldProperties(field.getID());
            return fieldProperties.isSortAscending();
        }
        else
            return false;
    }

    /**
        Set the sort mode for the field to ascending or descending.
        @param field the FormField to set the mode for.
        @param isAscending true if the field should be sorted in ascending order, false for descending.
    */ 
    public void setSortFieldAscending(FormField field, boolean isAscending)
    {
        if (field != null)
        {
            ListFieldProperties fieldProperties = m_list.getListFieldProperties(field.getID());
            fieldProperties.setSortAscending(isAscending);
        }
    }

    /**
       @returns a ArrayList of FormFields.  
       The order in the list determines their sort order ( the order they should be listed in an SQL order by clause).
    */
    public ArrayList getSortFields( )
    {
        return m_fields;
    }

    /**
       Sorts the FormList dataset (ArrayList of FormData) using the current sort settings. 
       @param dataList an ArrayList of FormData to be sorted. 
   */
    public void sortData(ArrayList dataList)
    {
        m_dataComparator.setFormList(m_list);
        m_dataComparator.setSortFields(m_fields);
        Collections.sort(dataList, m_dataComparator);
    }

    /**
    Converts the object to XML representation without the XML version tag.
    This method returns the object as XML text.
    @return XML representation
    */
    public String getXMLBody()
    {
        StringBuffer xml = new StringBuffer(200);
        FormField field;
        ListFieldProperties fieldProperties;

        // form list properties
        xml.append("<FormSort>\n");
        xml.append("<form_list_id>" + m_list.m_list_id + "</form_list_id>\n");
        xml.append("<num_sort_fields>" + m_fields.size() + "</num_sort_fields>\n");

        for (int i=0; i<m_fields.size(); i++)
        {
            field = (FormField) m_fields.get(i);
            fieldProperties = m_list.getListFieldProperties(field.getID());
            field.getXMLBody();
            fieldProperties.getXMLBody();
        }
        xml.append("</FormSort>\n");

        return xml.toString();
    }

    /**
        Converts the object to XML representation.
        This method returns the object as XML text.
        @return XML representation
     */
    public String getXML()
    {
        return( "<?xml version=\"1.0\" ?>\n" + getXMLBody() );
    } 

    /**
     * This class extends the behaviour of <TT>ArrayList</TT> class to maintain 
     * the <tt>List</tt> in the sequence 
     * as per their index no. ( sort order specified by the user ) . For that , 
     * it maintains an internal <TT>TreeMap</TT> object .
     */
    private class IndexedArrayList extends ArrayList {

        TreeMap treeMap = new TreeMap();
    
        /**
         * Constructor
         * 
         * @param size
         */
        IndexedArrayList(int size){
            super(size);
        }

        /**
         * Add a new <TT>FormField</TT> object to the List and adjusts the List as per the index number
         * 
         * @param index  The Index no. or sort order
         * @param object The FormField Object which is being added to the List
         */
        void addNew ( int index , Object object) {
            this.treeMap.put(new Integer(index) , object);
            this.clear();
            this.addAll(treeMap.values());
        }
        
        /**
         * Clears the <TT>TreeMap</TT>
         */
        void clearMap(){
            this.treeMap.clear();
        }
    }        
    
}






