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
|   $Revision: 18440 $
|       $Date: 2008-11-29 22:06:02 -0200 (sáb, 29 nov 2008) $
|     $Author: vivana $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.ArrayList;

/**
   The comparator for sorting a FormList.  The class can order form lists by several fields.
 */
class FormListComparator 
implements java.util.Comparator, java.io.Serializable
{

    private ArrayList m_sortFields = null;
    private FormList m_list = null;
    
    /** 
        Set the fields to be used in sorting the form list.
        @param sortFields an ArrayList of FormFields to sort by.
    */
    public void setSortFields(ArrayList sortFields)
    {
        m_sortFields = sortFields;
    }

    /** 
        Set the FormList the sort is being performed for..
    */
    public void setFormList(FormList list)
    {
        m_list = list;
    }

    /** 
        Compares two FormData records using the sort field settings.
    */
    public int compare(Object data1, Object data2)
    {
        int numFields = 0;
        int fieldCompareValue = -1;
        FormField field = null;
        FieldData fieldData1 = null;
        FieldData fieldData2 = null;
        ListFieldProperties fieldProperties;


        // bad data.
        if ((data1 == null) || (data2 == null))
			return 0;

        // see if they are the same object.
       /* if (data1.equals(data2))
            return 0;
       */

        // Nothing to compare by.
        if ((m_sortFields == null) || ((numFields = m_sortFields.size()) < 1)){
            return 0;
        }

        // SEQ NUMBER SORT.
        // deal with special case of sorting by the form sequence number.
        if ((m_sortFields.get(0) != null) && (((FormField)m_sortFields.get(0)).getID().equals("0")))
        {
            fieldProperties = m_list.getListFieldProperties("0");
            // sort seq_num ascending
            if (fieldProperties.isSortAscending())
            {
                if (((FormData)data1).getSeqNum() > ((FormData)data2).getSeqNum()){
                    return 1;
                }
                else{
                    return -1;
                }
            }
            // sort seq_num descending
            else
            {
                if (((FormData)data1).getSeqNum() < ((FormData)data2).getSeqNum()){
                    return 1;
                }  else {
                    return -1;
                }
            }
        }

        // Sort by the sort fields
        else
        {
            // for each sort field.
            for (int i=0; i<numFields; i++)
            {
                // compare the data1 and data2 values for the ith sort field. 
                // as soon as a field have non-matching values, return the comparison.
                if (m_sortFields.get(i) != null)
                {
                    field = (FormField)m_sortFields.get(i);
                    fieldData1 = ((FormData) data1).getFieldData(field);
                    fieldData2 = ((FormData) data2).getFieldData(field);
                     
                    //String fieldData1String = String.valueOf(field.formatFieldDataListView(fieldData1));
                    //String fieldData2String = String.valueOf(field.formatFieldDataListView(fieldData2));
                    
                    fieldProperties = m_list.getListFieldProperties(field.getID());

                    if (fieldProperties.isSortAscending())  {

                        if (field instanceof FormID){
                            if (((FormData)data1).getSeqNum() > ((FormData)data2).getSeqNum()){
                                return 1;
                            }   else{
                                return -1;  
                            }
                        }else {
	                        if ((fieldCompareValue = field.compareData(fieldData1, fieldData2)) != 0)
	                            return fieldCompareValue;
                        }
                    }  else  {
                         if (field instanceof FormID){
                             if (((FormData)data1).getSeqNum() < ((FormData)data2).getSeqNum()){
                                 return 1;
                             }   else {
                                 return -1;
                             }
                         }else {
                        	 if ((fieldCompareValue = field.compareData(fieldData2, fieldData1)) != 0)
                        		 return fieldCompareValue;
                         }
                    }
                    

                }
            }
            // all sort fields were equal
            return 0;
        }

    }

}




