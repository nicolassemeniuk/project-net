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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

import net.project.base.IDownloadable;

/**
 * A CSVErrorFileExport class exports the Error CSV File.
 * @author Deepak   
 * @since emu
 */
public class CSVErrorFileExport implements IDownloadable {
    
    private  byte[] buf = null;
    

    /**
     * Creates a new Error Export object from the errors in the specified
     * CSV object.
     */
    public CSVErrorFileExport(CSV csv){
        this.buf = makeCsvFromErrors(csv).getBytes();
    }
    

    /**
     * Returns the default file name to which to download the csv errors.
     * @return the file name <code>Errors.csv</code>
     */
    public String getFileName(){
         return "Errors.csv";
    }
     

    /**
     * Returns the content type of this error object.
     * @return the content type <code>application/x-excel</code>
     */
    public String getContentType(){
        return "application/x-excel";
    }

    public InputStream getInputStream() throws IOException{
        return new ByteArrayInputStream(this.buf);
    }
    
    public long getLength(){
        return this.buf.length;
    }


    /**
     * Turns the csv object errors into a string.
     * @param csv the csv object whose errors to turn into a csv file
     * @return string representation of csv object
     */
    private String makeCsvFromErrors(CSV csv) {
        StringBuffer sbuffer = new StringBuffer();
        
        CSVColumns csvColumns = csv.getCSVColumns();
        Iterator itr = csvColumns.iterator();

        while(itr.hasNext()){
            CSVColumn csvColumn =(CSVColumn)itr.next();
            sbuffer.append(csvColumn.getColumnName());
            sbuffer.append(",");
        }
        
        sbuffer.append("\n");
        
        CSVErrorCellCollection csvErrorCellCollection = csv.getCSVErrorCellCollection();
        HashSet hset = csvErrorCellCollection.getErrorRowNumbers();
        Iterator itrErrorRows = hset.iterator();

        while(itrErrorRows.hasNext()){
            
            CSVRowNumber csvRowNumber = (CSVRowNumber)itrErrorRows.next();
            Iterator itrCSV = csv.getCSVRows().getCSVRowForRowNumber(csvRowNumber).getRowCellValues().iterator();

            while(itrCSV.hasNext()) {

                CSVCell csvCell = (CSVCell)itrCSV.next();
                sbuffer.append(csvCell.getCSVDataValue().getValue());
                sbuffer.append(",");
            }
        
            sbuffer.append("\n");
        }

        return sbuffer.toString();
    }

}
