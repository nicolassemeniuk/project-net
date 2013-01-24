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

 package net.project.datatransform.csv;

import java.io.Serializable;


/**
 * A CSV class represents a CSV File.
 * @author Deepak
 * @since emu
 */

public class CSVRowNumber implements Serializable{
    private String rowNumber;
    
    public CSVRowNumber(String rowNumber){
        this.rowNumber=rowNumber;
    }
    
    /**
     * Returns the Value for the Row Number
     * @return String   
    */

    public String getCSVRowNumberValue(){
        return rowNumber;
    }

}


