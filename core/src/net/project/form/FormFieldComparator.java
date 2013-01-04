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


/**
   The comparator for sorting a FormField using the standard Java Collections class.
   */

class FormFieldComparator implements java.util.Comparator
{

    /** 
    compares two fields by their row, column locations.
    Equal if row and column of both object are are the same.
    */
    public int compare(Object field1, Object field2)
    {
        if (field1.equals(field2))
            return 0;

        if (  ( ((FormField)field1).m_row_num >= ((FormField)field2).m_row_num )
              && ( ((FormField)field1).m_column_num > ((FormField)field2).m_column_num) )
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

}
