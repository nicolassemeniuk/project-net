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

import java.util.ArrayList;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;


/**  
    Manages version history for a Form data instance.
*/
public class FormDataVersionHistory
extends ArrayList
implements  IXMLPersistence
{
    /** the Form this data belongs to. */
    protected Form m_form = null; 

    /** the object_id of the data we will store. */
    protected String m_data_object_id = null;

    // db access bean
    protected DBBean db = new DBBean();
    protected boolean m_isLoaded = false;


    /**  
       Bean constructor.
   */
    public FormDataVersionHistory()
    {
        super();
    }

    /**  
    Create an empty FormData object.
    @param form the form context.
    */
    public FormDataVersionHistory(Form form)
    {
        super();
        m_form = form;
    }




    /** set the class_id of the version history */
    public void setID(String id)
    {
        m_data_object_id = id;
    }


    /** get the class_id of the form */
    public String getID()
    {
        return m_data_object_id;
    }

    /** 
       Set the Form context for this data.  
       The form that owns and presents this data. 
   */
    public void setForm(Form form)
    {
        m_form = form;
    }




    /************************************************************************************************
     *   Implementing IXMLPersistence Interface
     *************************************************************************************************
       
    /**
      Converts the version history to XML representation without the XML version tag.
      This method returns the From as XML text. 
      @return XML representation
   */
    public String getXMLBody()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<FormDataVersionHistory>");
        sb.append("<id>" + m_data_object_id + "</id>");
        sb.append("<versionCnt>" + this.size() + "</versionCnt>");

        for (int i=0; i< this.size(); i++)
            sb.append( ((FormData)this.get(i)).getXMLBody() );

        sb.append("</FormDataVersionHistory>");
        return sb.toString();
    }



    /**
        Converts the object to XML representation of the FormData
        This method returns the Form as XML text. 
        @return XML representation of the form
     */
    public String getXML()
    {
        return( "<?xml version=\"1.0\" ?>\n" + getXMLBody() );
    }





    /** 
         Get the version history from the database for this data_object_id.
         @param data_object_id The object_id to retreive data for.
  
    public void load(String data_object_id)
    throws PersistenceException
    {
        m_data_object_id = data_object_id;   
        load();
    }

        */     

    /**
         Load the version history for the form instance.
    public void load()
    {
        FormField field;
        FormData form_data;
        FieldData field_data;
        ArrayList values;
        int i, r, c;
        int num_fields;
        int num_forms;
        int table_count = 0;
        String query = "";
        String select_string = "";
        String from_string = " from ";
        String where_string = " where ";
        String current_table_name = null;

        if (m_data !=null)
            m_data.clear();

        if ((num_fields = m_fields.size()) <= 0)
            return;

        // build a query joining all the data tables in the users scope that are needed for the field in the list.
        // the fields are ordered by table.

        for (i=0; i<num_fields; i++)
        {
            field = (FormField) m_fields.get(i);

            if ((field.m_data_table_name != null) && ( ! field.m_data_table_name.equals(current_table_name)) )
            {
                if (current_table_name != null)
                {
                    from_string += ", ";
                    where_string += " and ";
                }
                else
                {
                    select_string = "select " + field.m_data_table_name + ".object_id, " + field.m_data_table_name + ".version_id, " + field.m_data_table_name + ".seq_num, ";
                }

                table_count++;
                from_string = from_string + field.m_data_table_name;
                where_string += current_table_name + ".object_id=" + field.m_data_table_name + ".object_id";
                current_table_name = field.m_data_table_name;
            }

            select_string +=  field.getSQLName();

            if (i < num_fields-1)
                select_string += ", ";
        }


        query = select_string + from_string;

        // add where clause for multi-table joins and filters.
        if (table_count > 1)
            query += where_string + " and is_current=1 ";
        else
            query += " where is_current=1 ";


        db.setQuery(query);
        db.executeQuery();

        if ((num_forms = db.getNumRows()) <= 0)
            return;


        // GET DATA
        // Save the data in memory. Ddata for the fields of one form on the list

        // For each Form data record
        for (r=0; r<num_forms; r++)
        {
            form_data = new FormData(m_form, db.getItem(r, 0));
            form_data.setVersionID(db.getItem(r, 1));
            form_data.setSeqNum(Conversion.toInt(db.getItem(r, 2)));

            // For each field in the form data record.
            // only put the field data into the field_data array.
            // c+3 skips the three columns return by the query above which are not fields.
            for (c=0; c<num_fields; c++)
            {
                field = (FormField) m_fields.get(c);
                field_data = (FieldData) new FieldData();
                field_data.add(db.getItem(r, c+3));
                form_data.put(field.getSQLName(), field_data);
            }
            m_data.add(form_data);
        }
    }
        */


}






