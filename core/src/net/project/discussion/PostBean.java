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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.discussion;

import java.io.Serializable;
import java.util.ArrayList;

import net.project.persistence.PersistenceException;

/**
 * Provides setter & getter methods for presentation items within the Post class.
 * The bean references an actual post object to make it possible to use an existing
 * post instance as a jsp:useBean
 *
 * @author AdamKlatzkin
 * @since 01/00
 */
public class PostBean implements Serializable {
    private Post m_reference = new Post();
    private ArrayList m_readers = null;

    /**
     * Constructor
     */
    public PostBean() {
    }

    /**
     * @param post   post object that this bean should reference
     */
    public void setReference(Post post) {
        m_reference = post;
        m_readers = null;
    }

    /**
     * @return Post    the Post object
     */
    public Post getPost() {
        return m_reference;
    }


    /**
     * @return String    the post's id
     * @deprecated Please use {@link #getID()} instead.
     */
    public String getId() {
        return m_reference.getID();
    }

    /**
     * @return String    the post's id
     */
    public String getID() {
        return m_reference.getID();
    }


    /**
     * @return String    the post's parent id
     */
    public String getParentId() {
        return m_reference.getParentPostID();
    }

    /**
     * @return String    the number of reader's who have viewed the post
     */
    public String getNumOfReaders() {
        if (m_reference.getNumberOfViews() != null)
            return m_reference.getNumberOfViews();
        else
            return "0";
    }

    /**
     * @return String    the post's subject
     */
    public String getSubject() {
        return m_reference.getSubject();
    }

    /**
     * @return String    the post's full name
     */
    public String getFullname() {
        return m_reference.getFullname();
    }

    /**
     * @return String    the post's urgency level
     */
    public String getUrgency() {
        return m_reference.getUrgency();
    }

    /**
     * @return String    the post's date
     */
    public String getPostDate() {
        return m_reference.getPostdate();
    }

    /**
     * @return String    the post's body
     */
    public String getPostBody() {
        return m_reference.getPostbody();
    }

    /**
     * @return String    the color a link to this post should be displayed in
     */
    public String getLinkColor() {
        return m_reference.getLinkColor();
    }

    /**
     * @param  reader_num    the index number of the reader to obtain.
     *                       0 <= reader_num < getNumOfReaders()
     * @return String    the post's full name
     */
    public PostReader getReader(int reader_num) throws PersistenceException {
        if (m_readers == null) {
            m_readers = m_reference.getReaderList();
        }

        return (PostReader)m_readers.get(reader_num);
    }
}
