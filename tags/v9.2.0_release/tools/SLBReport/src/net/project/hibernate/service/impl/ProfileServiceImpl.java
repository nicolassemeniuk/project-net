package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.constants.NotificationConstants;
import net.project.hibernate.model.PnAddress;
import net.project.hibernate.model.PnCalendar;
import net.project.hibernate.model.PnCountryLookup;
import net.project.hibernate.model.PnDirectory;
import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPersonNotificationAddress;
import net.project.hibernate.model.PnPersonProfile;
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;
import net.project.hibernate.service.IDocumentService;
import net.project.hibernate.service.IPnAddressService;
import net.project.hibernate.service.IPnCalendarService;
import net.project.hibernate.service.IPnDirectoryHasPersonService;
import net.project.hibernate.service.IPnDirectoryService;
import net.project.hibernate.service.IPnDocContainerService;
import net.project.hibernate.service.IPnDocSpaceHasContainerService;
import net.project.hibernate.service.IPnModuleService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnPersonNotificationAddressService;
import net.project.hibernate.service.IPnPersonProfileService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnPortfolioService;
import net.project.hibernate.service.IPnSpaceHasCalendarService;
import net.project.hibernate.service.IPnSpaceHasModuleService;
import net.project.hibernate.service.IPnSpaceHasPersonService;
import net.project.hibernate.service.IPnSpaceHasPortfolioService;
import net.project.hibernate.service.IProfileService;
import net.project.hibernate.service.ISecurityService;
import net.project.hibernate.service.ServiceFactory;

public class ProfileServiceImpl implements IProfileService {

	/**
	 * 
	 */
	public BigDecimal createPersonStub(String email, String firstName, String lastName, String displayName, String userStatus) {
		BigDecimal personObjectId = new BigDecimal(0);
		try {
			// get PnObject service
			IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
			// create object for person
			PnObject personObject = new PnObject("person", new BigDecimal(1), new Date(System.currentTimeMillis()), "A");
			// save person object in database
			personObjectId = objectService.saveObject(personObject);
			// create object for portfolio
			PnObject portfolioObject = new PnObject("portfolio", new BigDecimal(1), new Date(System.currentTimeMillis()), "A");
			// save portfolio object into database
			BigDecimal portfolioObjectId = objectService.saveObject(portfolioObject);
			// get PnPerson service
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			// create person
			PnPerson pnPerson = new PnPerson(personObjectId, email, firstName, lastName, displayName, userStatus, portfolioObjectId, new Date(System.currentTimeMillis()), "A");
			// save person
			personService.savePerson(pnPerson);
			// get PnPortfolio service
			IPnPortfolioService portfolioService = ServiceFactory.getInstance().getPnPortfolioService();
			// create portfolio
			PnPortfolio pnPortfolio = new PnPortfolio(portfolioObjectId, "Personal Portfolio", "Personal Portfolio");
			// saves portfolio
			portfolioService.savePortfolio(pnPortfolio);
			// get PnSpaceHasPortfolio service
			IPnSpaceHasPortfolioService pnSpaceHasPortfolioService = ServiceFactory.getInstance().getPnSpaceHasPortfolioService();
			// create PnSpaceHasPortfolio object
			PnSpaceHasPortfolio pnSpaceHasPortfolio = new PnSpaceHasPortfolio(new PnSpaceHasPortfolioPK(personObjectId, portfolioObjectId), 1);
			// saves pnSpaceHasPortfolio
			pnSpaceHasPortfolioService.saveSpaceHasPortfolio(pnSpaceHasPortfolio);
			// get PnSpaceHasPerson service
			IPnSpaceHasPersonService pnSpaceHasPersonService = ServiceFactory.getInstance().getPnSpaceHasPersonService();
			// create PnSpaceHasPerson
			PnSpaceHasPerson pnSpaceHasPerson = new PnSpaceHasPerson(new PnSpaceHasPersonPK(personObjectId, personObjectId), "Person''s Personal Space", "A");
			// saves PnSpaceHasPerson
			pnSpaceHasPersonService.saveSpaceHasPerson(pnSpaceHasPerson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personObjectId;
	}

	public void createPersonProfile(BigDecimal personId, String firstName, String lastName, String displayName, String username, String email, String alternateEmail1,
			String alternateEmail2, String alternateEmail3, String prefixName, String middleName, String secondLastName, String suffixName, String localeCode, String languageCode,
			String timezoneCode, String verificationCode, String address1, String address2, String address3, String address4, String address5, String address6, String address7,
			String city, String cityDistrict, String region, String stateProvence, String countryCode, String zipcode, String officePhone, String faxPhone) {
		try {
			// get PnObject service
			
			IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
			// creates address object
			PnObject addressObject = new PnObject("address", new BigDecimal(1), new Date(System.currentTimeMillis()), "A");
			BigDecimal addressObjectId = objectService.saveObject(addressObject);
			// get PnAddress service
			IPnAddressService addressService = ServiceFactory.getInstance().getPnAddressService();
			// save address
			PnAddress address = new PnAddress(addressObjectId, address1, address2, address3, address4, address5, address6, address7, city, cityDistrict, region,
					stateProvence, new PnCountryLookup(countryCode), zipcode, officePhone, faxPhone, "A");
			addressService.saveAddress(address);
			// get PnPersonProfile service
			IPnPersonProfileService personProfileService = ServiceFactory.getInstance().getPnPersonProfileService();
			// save PnPersonProfile
			PnPersonProfile personProfile = new PnPersonProfile(personId, prefixName, middleName, secondLastName, suffixName, localeCode, languageCode, timezoneCode,
					displayName, verificationCode, addressObjectId, alternateEmail1, alternateEmail2, alternateEmail3);
			personProfileService.savePersonProfile(personProfile);
			// get PnPerson service
			IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
			// update PnPerson
			PnPerson person = personService.getPerson(personId);
			person.setFirstName(firstName);
			person.setLastName(lastName);
			person.setDisplayName(displayName);
			personService.updatePerson(person);
			// get PnDirectory service
			IPnDirectoryService directoryService = ServiceFactory.getInstance().getPnDirectoryService();
			// get List of default directories
			Iterator directoryIterator = directoryService.getDefaultDirectory().iterator();
			// get PnDirectoryHasPerson service
			IPnDirectoryHasPersonService directoryHasPersonService = ServiceFactory.getInstance().getPnDirectoryHasPersonService();
			// saves PnDirectoryHasPerson for all default directories
			while(directoryIterator.hasNext()){
				PnDirectory pnDirectory = (PnDirectory)directoryIterator.next();
				directoryHasPersonService.saveDirectoryHasPerson(new PnDirectoryHasPerson(new PnDirectoryHasPersonPK(pnDirectory.getDirectoryId(), personId), 1));
			}
			// get PnPersonNotificationAddress service
			IPnPersonNotificationAddressService pnPersonNotificationAddressService = ServiceFactory.getInstance().getPnPersonNotificationAddressService();
			// save new PnPersonNotificationAddress              
			pnPersonNotificationAddressService.savePersonNotificationAddress(new PnPersonNotificationAddress(personId, NotificationConstants.DELIVERY_TYPE_EMAIL, email, 1));
			// get PnModule service
			IPnModuleService moduleService = ServiceFactory.getInstance().getPnModuleService();
			// get PnModules
			Iterator moduleIterator = moduleService.getModuleIds().iterator();
			// get PnSpaceHasModule service
			IPnSpaceHasModuleService pnSpaceHasModuleService = ServiceFactory.getInstance().getPnSpaceHasModuleService();
			// save PnSpaceHasModules objects
			while(moduleIterator.hasNext()){
				PnModule pnModule = (PnModule)moduleIterator.next();
				// saves all PnSpaceHasModule objects
				pnSpaceHasModuleService.saveSpaceHasModule(new PnSpaceHasModule(new PnSpaceHasModulePK(personId, pnModule.getModuleId()), 1));				
			}
			// get Security service
			ISecurityService securityService = ServiceFactory.getInstance().getSecurityService();
			// get spaceAdminGroupId from security service
			BigDecimal spaceAdminGroupId = securityService.createSpaceAdminGroup(personId,personId, "Personal Space Admin");
			// get Document service
			IDocumentService documentService = ServiceFactory.getInstance().getDocumentService();
			// now create a default doc space for this personal space
			BigDecimal documentObjectId = documentService.createDocumentSpace(personId, personId);
			// create doc_space object
			BigDecimal docSpaceObject = objectService.saveObject(new PnObject("doc_container", new BigDecimal(1), new Date(System.currentTimeMillis()), "A"));
			// get PnDocContainer service
			IPnDocContainerService docContainerService = ServiceFactory.getInstance().getPnDocContainerService();
			// save PnDocContainer object
			docContainerService.saveDocContainer(new PnDocContainer(docSpaceObject, "@prm.document.container.topfolder.name", "Top level document folder",  new Date(System.currentTimeMillis()), new BigDecimal(1), 0,  new Date(System.currentTimeMillis()), "A"));
			// get Security service
			securityService.createSecurityPermissions(docSpaceObject, "doc_container", personId, new BigDecimal(1));
			// get PnDocSpaceHasContainer Service
			IPnDocSpaceHasContainerService docSpaceHasContainerService = ServiceFactory.getInstance().getPnDocSpaceHasContainerService();
			// save PnDocSpaceHasContainer object 
			docSpaceHasContainerService.saveDocSpaceHasContainer(new PnDocSpaceHasContainer(new PnDocSpaceHasContainerPK(documentObjectId, docSpaceObject), 1));
			// cretes new calendar object
			BigDecimal calendarObjectId = objectService.saveObject(new PnObject("calendar", new BigDecimal(1), new Date(System.currentTimeMillis()), "A"));
			// get PnCalendar service
			IPnCalendarService calendarService = ServiceFactory.getInstance().getPnCalendarService();
			// save new PnCalendar
			calendarService.saveCalendar(new PnCalendar(calendarObjectId, 1, "Personal Calendar", "Main Personal Calendar", "A"));
			// create security permissions for calendar
			securityService.createSecurityPermissions(calendarObjectId, "calendar", personId, new BigDecimal(1));
			// get PnSpaceHasCallendar service
			IPnSpaceHasCalendarService spaceHasCalendarService = ServiceFactory.getInstance().getPnSpaceHasCalendarService();
			// save new PnSpaceHasCalendar object
			spaceHasCalendarService.saveSpaceHasCalendar(new PnSpaceHasCalendar(new PnSpaceHasCalendarPK(personId, calendarObjectId)));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
