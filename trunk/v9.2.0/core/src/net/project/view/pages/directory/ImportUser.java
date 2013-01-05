/**
 * 
 */
package net.project.view.pages.directory;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.project.base.property.PropertyProvider;
import net.project.datatransform.csv.CSV;
import net.project.datatransform.csv.CSVCell;
import net.project.datatransform.csv.CSVColumn;
import net.project.datatransform.csv.CSVColumns;
import net.project.datatransform.csv.CSVRow;
import net.project.datatransform.csv.CSVRows;
import net.project.resource.Invitee;
import net.project.resource.Person;
import net.project.schedule.importer.IScheduleImporter;
import net.project.schedule.importer.XMLImporter;
import net.project.soa.schedule.Project.Resources.Resource;
import net.project.util.FileUtils;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.sf.vcard4j.java.AddressBook;
import net.sf.vcard4j.java.VCard;
import net.sf.vcard4j.java.type.EMAIL;
import net.sf.vcard4j.java.type.FN;
import net.sf.vcard4j.parser.DomParser;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;


public class ImportUser extends BasePage {
	
	@Property
    private UploadedFile file;
	
	private static Logger log;
	
	@InjectPage
	private ImportUser importUser;
	
	@InjectPage
	private LoadMembers loadMember;
	
	private String type;
	
	private String tempFileLocation;
	
	private IScheduleImporter importer;
	
	private Invitee inviteeSearchResults;
	
	private ArrayList<Invitee> inviteeList;
	
	private ArrayList<Invitee> displayNameList;
	
	private Person person;
	
	@Property
	private String uploadFor;
	
	@Inject
	private Request request;
	
	private ArrayList<CSVCell> cvsRowList;
	
	private CSVRow csvRow;
	
	private CSVRows rows;
	
	private CSVColumns columns;
	
	private CSVColumn column;
	
	private CSV csv;
	
	private Pattern pattern;
	
	@Property
	private String xmlType;
	
	@Property
	private String csvType;
	
	@Property
	private String vcfType;
	
	@Property
	@Persist
	private String fileType;
    
	@Property
	private String uploadLabel;
	
	@Property
	private String cancelLabel;
	
	
	public void initialize(){
		log = Logger.getLogger(InviteMember.class);
		inviteeList = new ArrayList<Invitee>();
		displayNameList = new ArrayList<Invitee>();
		xmlType = PropertyProvider.get("prm.directory.directory.importuser.filesupported.xmltype");
		csvType = PropertyProvider.get("prm.directory.directory.importuser.filesupported.csvtype");
		vcfType = PropertyProvider.get("prm.directory.directory.importuser.filesupported.vcftype");
		uploadLabel = PropertyProvider.get("prm.personal.personalimageupload.uploadbutton.caption");
		cancelLabel = PropertyProvider.get("prm.personal.personalimageupload.cancelbutton.caption");
		pattern = Pattern.compile(".+@.+\\.[a-z]+");
	}
	
	Object onActivate(){
		initialize();
		return null;
	}
	
	Object onActivate(String action) {
		initialize();
		return null;
	}

	public void onSuccess() {
		initialize();
	}
	
	Object onAction() {
		initialize();
		try {

			String extension = file.getFilePath().toLowerCase().substring(file.getFilePath().lastIndexOf('.') + 1,
					file.getFilePath().length());
			tempFileLocation = FileUtils.commitUploadedFileToFileSystem(file);

			if (extension.equals("xml")) {
				getXMLFile(tempFileLocation);
			} else if (extension.equals("csv")) {
				getCSVFile(tempFileLocation);
			} else if (extension.equals("vcf")) {
				getVCardFile(tempFileLocation);
			}
			loadMember.setFromImportUser(true);
			for (Invitee inviteeSearchResult : inviteeList) {
				loadMember.createAndAddInvitee("", inviteeSearchResult.getFirstName(),inviteeSearchResult.getLastName() , inviteeSearchResult.getDisplayName(), inviteeSearchResult.getEmail());
			}

		} catch (Exception e) {
			log.error("Error occurred while loading import user page" + e.getMessage());
		}
		return loadMember;
	}

	/**
	 * Get users by import xml file
	 * 
	 * @param tempFileLocation
	 */
	public void getXMLFile(String tempFileLocation) {
		try {
			
			importer = new XMLImporter();
			importer.setFileName(tempFileLocation);
			importer.init();
			importer.loadResources();
			
			Iterator<Resource> iterator = importer.getResources().iterator();
			Resource resource;
			while (iterator.hasNext()) {
				resource = (Resource) iterator.next();
				person = new Person();
				inviteeSearchResults = new Invitee();
				if(validUserName(resource.getName()) && validEmailId(resource.getEmailAddress())){
					String[] userNameArray = StringUtils.split(resource.getName(),", ");
					inviteeSearchResults.setFirstName(userNameArray[0]);
					inviteeSearchResults.setLastName(userNameArray[1]);
					inviteeSearchResults.setDisplayName(resource.getName());
					inviteeSearchResults.setEmail(resource.getEmailAddress());
					inviteeSearchResults.setInvite("");
					inviteeList.add(inviteeSearchResults);
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while getXMLFile()" + e.getMessage());
		}
	}
	
	/**
	 * check validation of user by its name.
	 * @param userName
	 * @return
	 */
	private boolean validUserName(String userName){
		String[] userNameArray = StringUtils.split(userName,", ");
		
		if(userNameArray != null && userNameArray.length > 1 && StringUtils.isNotEmpty(userNameArray[0]) && StringUtils.isNotEmpty(userNameArray[1])){
			return true;
		}
		else{ 
			return false;
		}
	}
	
	private boolean validEmailId(String emailId){
		if (StringUtils.isNotEmpty(emailId)	&& pattern.matcher(emailId).matches()){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Get users by import excel file
	 * 
	 * @param tempFileLocation
	 */
	public void getCSVFile(String tempFileLocation) {
		try {
			csv = new CSV();
			File tempFile = new File(tempFileLocation);
			csv.setCharacterEncoding("UTF-8");
			csv.parse(tempFile);
			rows = csv.getCSVRows();
			columns = csv.getCSVColumns();
			Iterator rowIterator = rows.iterator();
			Iterator columnIterator = columns.iterator();

			while (columnIterator.hasNext()) {
				column = (CSVColumn) columnIterator.next();
				Iterator cellIterator = column.getColumnCellValues().iterator();
				if (column.getColumnName().equals("Name")) {
					while (cellIterator.hasNext()) {
						inviteeSearchResults = new Invitee();
						CSVCell cell = (CSVCell) cellIterator.next();
						String[] userName = StringUtils.split(cell.getCSVDataValue().getValue(),", ");
						if(validUserName(cell.getCSVDataValue().getValue())){
							inviteeSearchResults.setFirstName(userName[0]);
							inviteeSearchResults.setLastName(userName[1]);
							inviteeSearchResults.setDisplayName(cell.getCSVDataValue().getValue());
							displayNameList.add(inviteeSearchResults);
						}
					}
				} 
				if (column.getColumnName().equals("EmailAddress")) {
					Iterator listIterator = displayNameList.iterator();
					while (cellIterator.hasNext() && listIterator.hasNext()) {
						CSVCell cell = (CSVCell) cellIterator.next();
						Invitee invitee = (Invitee) listIterator.next();
						if (validEmailId(cell.getCSVDataValue().getValue())){
							invitee.setEmail(cell.getCSVDataValue().getValue());
							inviteeList.add(invitee);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while getCSVFile()" + e.getMessage());
		}
	}
	

	/**
	 * Get users by import outlook file
	 * 
	 * @param tempFileLocation
	 */
	public void getVCardFile(String tempFileLocation) {
		FileReader fileReader;
		try {
			Document document = new DocumentImpl();
			DomParser parser = new DomParser();
			parser.parse(file.getStream(), document);
			AddressBook addressBook = new AddressBook(document);
			EMAIL email = null;
			for (Iterator vcards = addressBook.getVCards(); vcards.hasNext();) {
				inviteeSearchResults = new Invitee();
				VCard vcard = (VCard) vcards.next();
				FN fullName = (FN) vcard.getTypes("FN").next();
				Iterator emails = vcard.getTypes("EMAIL");
				if (emails.hasNext()) {
					email = (EMAIL) emails.next();
				}
				if (email != null) {
					if (validUserName(fullName.get()) && validEmailId(email.get())) {
						String[] userName = StringUtils.split(fullName.get(), ", ");
						inviteeSearchResults.setFirstName(userName[0]);
						inviteeSearchResults.setLastName(userName[1]);
						inviteeSearchResults.setDisplayName(userName[0] +", "+ userName[1]);
						inviteeSearchResults.setEmail(email.get());
					}
					inviteeList.add(inviteeSearchResults);
				}
			}
		} catch (Exception e) {
			log.error("Error occurred while getVCardFile()" + e.getMessage());
		}
	}
	
}
