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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;


/** An collection of Forms. **/
public class FormCollection 
extends java.util.ArrayList 
implements IXMLPersistence
{

    // db access bean
    protected DBBean db = new DBBean();
    private boolean isLoaded = false;
    protected XMLFormatter xml;


    /* -------------------------------  Constructors  ------------------------------- */

    public FormCollection() {
        xml = new XMLFormatter();
    }


    /**
     *  Loads all the forms from the database irresptive of users , spaces  
     * @exception PersistenceException
     */
    public void loadAll()
    throws PersistenceException
    {        
        this.clear();        
        try
        {
            String query = "select class_id ,class_name ,owner_space_id from pn_class where class_type_id='100'" +
                            " and  record_status='A' ";
            db.executeQuery(query);
            while(db.result.next())
            {
                String classID = db.result.getString("class_id");
                String ownerSpaceID = db.result.getString("owner_space_id");
                Form form =new Form();
                form.setID(classID);
                form.setOwnerSpaceID(ownerSpaceID);
                this.add(form);
            }
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(FormCollection.class).error("Form Collection.loadAll() threw a PersistenceException: " + sqle);
        }
        finally
        {
            db.release();
        }
    }


    /**
     *  Loads all the forms from the database irresptive of users , spaces  
     * @exception PersistenceException
     */
    public void loadFiltered(String filter)
    throws PersistenceException
    {   
        this.clear();
        filter = "%"+filter.toUpperCase() + "%";

        try
        {
            String query = "select class_id ,class_name ,owner_space_id from pn_class where class_type_id='100'" +
                            " and  record_status='A'  and UPPER(class_name) like '"+filter+"'";
            db.executeQuery(query);
            while(db.result.next())
            {
                String classID = db.result.getString("class_id");
                String ownerSpaceID = db.result.getString("owner_space_id");
                Form form =new Form();
                form.setID(classID);
                form.setOwnerSpaceID(ownerSpaceID);
                this.add(form);
            }
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(FormCollection.class).error("Form Collection.loadFiltered() threw a PersistenceException: " + sqle);
        }
        finally
        {
            db.release();
        }
    }

    /**
     *  Loads all the forms from the database irresptive of users , spaces  
     * @exception PersistenceException
     */
    public void loadFilteredType(String type)
    throws PersistenceException
    {   
        this.clear();
        try
        {
            String query = "select class_id ,class_name ,owner_space_id from pn_class where class_type_id='100'" +
                            " and  record_status='A' ";
            db.executeQuery(query);
            while(db.result.next())
            {
                String classID = db.result.getString("class_id");
                String ownerSpaceID = db.result.getString("owner_space_id");
                Form form =new Form();
                form.setID(classID);
                form.setOwnerSpaceID(ownerSpaceID);
                if(form.ownerSpace.getType().equals(type))
                    this.add(form);
            }
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(FormCollection.class).error("Form Collection.loadFilteredType() threw a PersistenceException: " + sqle);
        }
        finally
        {
            db.release();
        }
    }

    /** 
     * Are the collection properties loaded.  
     *@return boleean
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }


    /** 
     *  Set whether the colection is loaded.
     *@param boolean  
     */
    public void setLoaded(boolean value) {
        this.isLoaded = value;
    }


    /* -------------------------------  Implementing IXMLPersistence  ------------------------------- */

    /**
     *  Get an XML representation of the Collection.
     *   @return a complete XML document representation of this Collection.
     */

    public String  getXML()
    {
        return("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }



    /**
     *   Get an XML node representation of this Collection.      
     *   @return an XML node representation of this Collection.
     */
    public String  getXMLBody()
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<FormDesignerCollection>\n");

        Iterator itr =this.iterator();
        while(itr.hasNext()){
            Form form =(Form)itr.next();
            xml.append(form.getXMLBody());
        }

        xml.append("</FormDesignerCollection>\n");
        return xml.toString();
    }



    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet (String styleSheetFileName)
    {
        xml.setStylesheet(styleSheetFileName);
    }

    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation () 
    {
        return( xml.getPresentation( getXML()) );
        } 

    
}


