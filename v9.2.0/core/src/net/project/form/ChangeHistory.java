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


/**
    A listing of change on a particular form instance. */
public class ChangeHistory
{
   
    /** list of FormData that was extracted for this list view.   Kept for "next 25" functionality. */
    protected ArrayList m_data = new ArrayList();

    /** the Form that this changeHistory belongs to. */
    protected Form m_form = null;

    // db access bean
    protected DBBean db = new DBBean();
    protected boolean m_isLoaded = false;


    /**
       Construct a FormList for the specified Form.
    */
    public ChangeHistory()
    {
        m_form = null;
    }

  

    /** set the form context for this form ChangeHistory. */
    public void setForm(Form form)
    {
        m_form = form;
    }


         
/**
    Extract the needed data for all the forms in the form list.  Applies sorts and filters to the extraction.  Must be called prior to display().
    @returns true if the Data was retrieved from the database OK.
    public void load()
    {
        FormField field;
        FormData form_data;
        FieldData field_data;
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

        m_isLoaded = false;

        // build a query joining all the data tables in the users scope that are needed for the field in the list.
        // the fields are ordered by table.

        System.out.println("loadData() List Dump----------------\n" + this.toString());

        if ((num_fields = m_fields.size()) <= 0)
            return;

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

        try
        {
            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                return;


            // GET DATA
            // Save the data in memory.
            do
            {
                form_data = new FormData(m_form, db.result.getString(1));
                form_data.setVersionID(db.result.getString(2));
                form_data.setSeqNum(db.result.getInt(3));

                // For each field in the form data record.
                // only put the field data into the field_data array.
                // c+3 skips the three columns return by the query above which are not fields.
                for (c=0; c<num_fields; c++)
                {
                    field = (FormField) m_fields.get(c);
                    field_data = (FieldData) new FieldData();
                    field_data.add(db.result.getString(c+4));
                    form_data.put(field.getSQLName(), field_data);
                }
                m_data.add(form_data);
            } while (db.result.next());
        }
        catch (SQLException sqle)
        {
            ApplicationLogger.logEvent("FormList.loadData failed " + sqle, ApplicationLogger.HIGH);
        }
        finally
        {
            db.release();
        }

        m_isLoaded = true;
    }
    */



    /** Has ChangeHistory data been loaded from database . */
    public boolean isLoaded()
    {
        return m_isLoaded;
    }

/*
    public String getXML()
    {
    }

    public String getXMLBody()
    {
    }
*/

}




