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

 package net.project.form;

import net.project.util.Conversion;


/**  
    Properties for a field on a FormList. 
    Field properties defined only for fields which are part of a FormList.     
*/
public class ListFieldProperties implements java.io.Serializable
{

    // Field formatting properties for field display on a FormList.
    protected boolean m_is_list_field = false;
    protected String m_field_width = null;
    protected String m_field_order  = null;
    protected boolean m_wrap_mode  = false;    
    protected boolean m_is_subfield  = false; 
    protected boolean m_is_calculateTotal = false;
    //protected boolean m_is_selected  = false;

    // List sort field properties
    protected boolean m_is_sort_field = false;
    protected int m_sort_order = -1;
    protected boolean m_sort_ascending = true;



    public void setIsListField(boolean value)
    {
        m_is_list_field = value;
    }

    public boolean isListField()
    {
        return m_is_list_field;
    }

    public String getListFieldCheck()
    {
        if (m_is_list_field)
            return "checked";
        else
            return "";
    }


    public void setFieldWidth(String width)
    {
        m_field_width = width;
    }

    public String getFieldWidth()
    {
        return m_field_width;
    }


    public void setFieldOrder(String order)
    {
        m_field_order = order;
    }

    public String getFieldOrder()
    {
        return m_field_order;
    }


    /** 
        Set this field not to wrap.
        @param value true to set "nowrap" on this field, false allow default wrapping behavior.
   */
    public void setNoWrap(boolean value)
    {
        m_wrap_mode = value;
    }


    public boolean isNoWrap()
    {
        return m_wrap_mode;
    }


    /** return the String "wrap" is the wrap mode is set.  For use in setting HTML TD attribute directly in bean */
    public String getWrapMode()
    {
        if (m_wrap_mode)
            return "nowrap";
        else
            return "";
    }


    public String getNoWrapCheck()
    {
        if (m_wrap_mode)
            return "checked";
        else
            return "";
    }



    public void setIsSubfield(boolean value)
    {
        m_is_subfield = value;
    }


    public boolean isSubfield()
    {
        return m_is_subfield;
    }


    public String getSubfieldCheck()
    {
        if (m_is_subfield)
            return "checked";
        else
            return "";
    }
    
    public void setCalculateTotal(boolean value){
       m_is_calculateTotal = value;
       
    }


    public boolean isCalculateTotal()
    {
        return m_is_calculateTotal;
    }

    public String getCalculateTotalCheck(){
	if (m_is_calculateTotal)
            return "checked";
	else
            return "";
   }

    /*  replaced by m_is_list_field 
     public void setIsSelected(boolean value)
     {
         m_is_selected = value;
     }
 
     public boolean isSelected()
     {
         return m_is_selected;
     }
 
 
     public String getSelectedCheck()
     {
         if (m_is_selected)
             return "checked";
         else
             return "";
     }
 
   */



    /** 
      Set this field as an active sort field. This is a property of the actual field instance.  
      To see if this field type is sortable use the isSortable() method. 
  */
    public void setIsSortField(boolean isSortField)
    {
        m_is_sort_field = isSortField;
    }

    /** 
        Is this field an active sort field. This is a property of the actual field instance.  
        To see if this field type is sortable use the isSortable() method. 
   */
    public boolean isSortField()
    {
        return m_is_sort_field;
    }



    /** Sets the order of this field in the form list sort. */
    public void setSortOrder(int order)
    {
        m_sort_order = order;
    }

    /** Gets the order of this field in the form list sort. */
    public int getSortOrder()
    {
        return m_sort_order;
    }



    /**
        Set the sort mode for the field to ascending or descending.
        @param isAscending true if the field should be sorted in ascending order, false for descending.
    */ 
    public void setSortAscending(boolean isAscending)
    {
        m_sort_ascending = isAscending;
    }


    /** 
       set the field's SortAscending using a String representation of a boolean.
       @param value "1" or "true"  is true, "0", "false" or other is false. 
    */
    public void setSortAscending(String value)
    {
        m_sort_ascending = Conversion.toBoolean(value);
    }



    /**
        Is the specified sort field set to ascending? 
        @return true is the field is set to ascending, false if desending.
    */
    public boolean isSortAscending()
    {
        return m_sort_ascending;
    }



    /** 
        get the field's SortAscending property in String format.
        @return "1" for true, "0" for false.
   */
    public String getSortAscending()
    {
        if (m_sort_ascending)
            return "1";
        else
            return "0" ;
    }


    /** Toggle the sort direction for this field */
    public void toggleSortDirection()
    {
        m_sort_ascending = !m_sort_ascending;
    }




    /**
      Converts the object to XML representation without the XML version tag.
      This method returns the object as XML text. 
      @return XML representation
   */
    public String getXMLProperties()
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<field_width>" + m_field_width + "</field_width>\n");
        xml.append("<field_order>" + m_field_order + "</field_order>\n");
        xml.append("<wrap_mode>" + m_wrap_mode + "</wrap_mode>\n");
        xml.append("<is_subfield>" + m_is_subfield + "</is_subfield>\n");
        xml.append("<is_list_field>" + m_is_list_field + "</is_list_field>\n");
        xml.append("<is_sort_field>" + m_is_sort_field + "</is_sort_field>\n");
        xml.append("<sort_order>" + m_sort_order + "</sort_order>\n");
        xml.append("<sort_ascending>" + m_sort_ascending + "</sort_ascending>\n");
        return xml.toString();
    }





    /**
       Converts the object to XML node representation without the XML version tag.
       This method returns the object as XML text. 
       @return XML representation
    */
    public String getXMLBody()
    {
        return "<ListFieldProperties>\n" + getXMLProperties() + "</ListFieldProperties>\n";
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



}

