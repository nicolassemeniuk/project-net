/**
 * 
 */
package net.project.business.report;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.apache.log4j.Logger;

import net.project.form.FormList;
import net.project.form.FormListCSVDownload;

/**
 *
 */
public class TimeSummaryCSVDownLoad implements net.project.base.IDownloadable {
	
	/** The bytes representing the time summary data. */
    private byte[] data = null;

    /** The character set that the CSV file will be streamed in. */
    private String characterSetID = "ISO-8859-1";
    
    public TimeSummaryCSVDownLoad() {
        // Do nothing
    }
    
    /**
     * Constructs a new AssignmentList based on the specified assignment list.
     * @param formList the assignment list to download
     */
    public TimeSummaryCSVDownLoad(List<AssignmentList> assignmentList) {
        this();
        setAssignMentList(assignmentList);
    }
    
    public void setAssignMentList(List<AssignmentList> assignmentList) {
    	AssignmentList list = new AssignmentList();
    	String csv = list.getTimeSummaryCSV(assignmentList);
    	
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
     * Returns the input stream for this time summary csv data.
     * @return the stream
     */
    public java.io.InputStream getInputStream() throws java.io.IOException {
        return new java.io.ByteArrayInputStream(this.data);
    }

    /**
     * Returns the content type of the time summary data.
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
     * Returns the default filename for the time summaryt data.
     * @return the default filename <code>TimeSummary.csv</code>
     */
    public String getFileName() {
        return "TimeSummary.csv";
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
     * Set the id of the character set that we stream the time summary in when we
     * download it.
     *
     * @param characterSetID a <code>String</code> containing the unique id for
     * the character set in which we are going to stream the CSV file.
     */
    public void setCharacterSetID(String characterSetID) {
        this.characterSetID = characterSetID;
    }
}
