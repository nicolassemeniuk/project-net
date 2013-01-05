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

 package net.project.discussion;



/**  
    An image used to annotate a discussion group or post..
*/
public class ImageDecoration
{
    public String m_image_id = null;
    public String m_image_URI = null;
    

 
    /** 
        Sets the ID of this image.
        @param value the URI used to access the image.
    */
    public void setImageID(String value)
    {
        m_image_id = value;
    }

   
    /** 
       Get the ID of this image.
       @return the image_id for this image.
    */
    public String getImageID()
    {
        return m_image_id;
    }



    /** 
        Sets the URI used to access the image.
        @param value the URI used to access the image.
    */
    public void setImageURI(String value)
    {
        m_image_URI = value;
    }

   
    /** 
       Get the URI used to access the image.
       @return the URI used to access the image.
    */
    public String getImageURI()
    {
        return m_image_URI;
    }



}



