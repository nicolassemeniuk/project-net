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
package net.project.portfolio;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.form.FormListCSVDownload;
import net.project.portfolio.view.MetaColumn;
import net.project.project.ProjectPortfolioRow;
import net.project.util.ParseString;
import net.project.util.ProjectNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * To create a CVS file of project portfolio 
 *
 */
public class ProjectPortfolioCSVDownload implements net.project.base.IDownloadable {

	/** The bytes representing the project portfolio data. */
    private byte[] data = null;

    /** The character set that the CSV file will be streamed in. */
    private String characterSetID = "ISO-8859-1";
    
    public ProjectPortfolioCSVDownload() {
        // Do nothing
    }

    /**
     * Constructs a new ProjectList based on the specified project list.
     * @param projectList
     * @param projectColumnList
     */
    public ProjectPortfolioCSVDownload(List<ProjectNode> projectList,List<MetaColumn> projectColumnList) {
        this();
        setProjectList(projectList,projectColumnList);
    }
    
    public void setProjectList(List<ProjectNode> projectList,List<MetaColumn> projectColumnList) {
    	String csv = getProjectPortfolioCSV(projectList,projectColumnList);
    	
    	try {
            CharsetEncoder ce = Charset.forName(characterSetID).newEncoder();
            this.data = ce.encode(CharBuffer.wrap(csv)).array();
        } catch (CharacterCodingException e) {
        	Logger.getLogger(FormListCSVDownload.class).debug("Unable to convert CSV file to character " +
                "set \"" + characterSetID + "\".  Defaulting to \"ISO-8859-1\".");
            characterSetID = "ISO-8859-1";
            this.data = csv.getBytes();
        }
    	
    }
    
    /**
     * Returns the input stream for this project portfolio csv data.
     * @return the stream
     */
    public java.io.InputStream getInputStream() throws java.io.IOException {
        return new java.io.ByteArrayInputStream(this.data);
    }

    /**
     * Returns the content type of the project portfolio data.
     * @return <code>application/x-excel</code>
     */
    public String getContentType() {
        return "application/x-excel;charset=" +characterSetID;
    }


    /**
     * Returns the length of this data.
     * @return always <code>-1</code> to indicate that the length is unknown
     */
    public long getLength() {
        return -1;
    }
    
    /**
     * Returns the default filename for the project portfolio data.
     * @return the default filename <code>Portfolio.csv</code>
     */
    public String getFileName() {
        return "Portfolio.csv";
    }

    /**
     * This is the character set id that corresponds to the character set in
     * which we are going to stream the CSV file.
     *
     * We need to do this because some programs (like Microsoft Excel) don't
     * process CSV files in UTF-8 format.  If this is set as the default
     * character set for an installation of Project.net before we had this
     * setting, CSV wouldn't work.
     *
     * @return a <code>String</code> containing the unique id for the character
     * set in which we are going to stream the CSV file.
     */
    public String getCharacterSetID() {
        return characterSetID;
    }

    /**
     * Set the id of the character set that we stream the project list in when we download it.
     *
     * @param characterSetID a <code>String</code> containing the unique id for
     * the character set in which we are going to stream the CSV file.
     */
    public void setCharacterSetID(String characterSetID) {
        this.characterSetID = characterSetID;
    }
    
	/**
     * Get a comma separated data file for this project list.
     * The CSV String returned is compatable with microsoft excel and other tools that can import CSV files.
     */
    public String getProjectPortfolioCSV(List<ProjectNode> projectList,List<MetaColumn>  projectColumnList) {
      
        StringBuffer csv = new StringBuffer(200);
      
		boolean parentProjectColumnSelected = false;

		// check check parent project name column is selected or not  
		for(MetaColumn projectColumn: projectColumnList)
			if(("SubprojectOf").equals(projectColumn.getPropertyName())){
				parentProjectColumnSelected = true;
				break;
			}

		for(MetaColumn projectColumn: projectColumnList){
			// if parent project name column is not selected 
			// then add new column to csv file to display name of parentproject  
			if(!parentProjectColumnSelected && ("name").equalsIgnoreCase(projectColumn.getPropertyName())){
	        	csv.append("\""+ projectColumn.getDescription()+"\"," +
	        			PropertyProvider.get("prm.project.create.wizard.subprojectof")+",");
			} else {
	        	csv.append("\""+ projectColumn.getDescription()+"\",");
			}
	    }

        csv.append("\r\n");
        
        for(ProjectNode project: projectList){
        	List<ProjectPortfolioRow> projectListRow = project.getSequensedProject();

        	//Set the CSV column data by comma separated list
			if(!parentProjectColumnSelected){
	        	csv.append("\""+ formatFieldDataCSV(project.getProject().getProjectName()) +"\"," +
	        			formatFieldDataCSV(project.getProject().getSubProjectOf()) +",");
			} else {
    			csv.append("\"" + formatFieldDataCSV(project.getProject().getProjectName()) + "\",");
			}
        	for(ProjectPortfolioRow projectData: projectListRow){
        		if(StringUtils.equalsIgnoreCase(projectData.getType(),"OverallStatus") || StringUtils.equalsIgnoreCase(projectData.getType(),"FinancialStatus") 
        				|| StringUtils.equalsIgnoreCase(projectData.getType(),"ScheduleStatus") || StringUtils.equalsIgnoreCase(projectData.getType(),"ResourceStatus") 
        				|| StringUtils.equalsIgnoreCase(projectData.getType(),"percent_complete")){
        			csv.append("\"" +formatFieldDataCSV(projectData.getActualValue())+ "\",");
        			
        		} else {
        			csv.append("\"" +formatFieldDataCSV(projectData.getDisplayValue())+ "\",");
        		}
        	}
			csv.append("\r\n");
        }
        return csv.toString();
    }
    
	/**
     * Formats the field data and returns it in a project suitable for using in a CSV file.
     * @param fieldData the field data to be formatted and returned.
     * @return a comma separated list representation of the field_data formatted correctly for this type of field.
     */
    private String formatFieldDataCSV(String projectData) {
        if (projectData != null)
            return (ParseString.escapeDoubleQuotes(projectData));
        else
            return "";
    }

 
}
