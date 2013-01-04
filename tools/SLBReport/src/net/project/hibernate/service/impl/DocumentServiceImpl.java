package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.service.IDocumentService;
import net.project.hibernate.service.IPnDocProviderHasDocSpaceService;
import net.project.hibernate.service.IPnDocProviderService;
import net.project.hibernate.service.IPnDocSpaceService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasDocSpaceService;
import net.project.hibernate.service.ServiceFactory;

public class DocumentServiceImpl implements IDocumentService {

	public BigDecimal createDocumentSpace(BigDecimal spaceId, BigDecimal personId) {
		BigDecimal documentObjectId = null;
		try {
			// get PnObject service
			IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
			// create document object
			documentObjectId = objectService.saveObject(new PnObject("doc_space", new BigDecimal(1), new Date(System.currentTimeMillis()), "A"));
			// get PnDocSpace service
			IPnDocSpaceService docSpaceService = ServiceFactory.getInstance().getPnDocSpaceService();
			// save PnDocSpace object
			docSpaceService.saveDocSpace(new PnDocSpace(documentObjectId, "default", new Date(System.currentTimeMillis()), "A"));
			// get PnSpaceHasDocSpace service
			IPnSpaceHasDocSpaceService spaceHasDocSpace = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService();
			// save PnSpaceHasDocSpace object
			spaceHasDocSpace.saveSpaceHasDocSpace(new PnSpaceHasDocSpace(new PnSpaceHasDocSpacePK(spaceId, documentObjectId), 1));
			// get PnDocProvider service
			IPnDocProviderService docProviderService =  ServiceFactory.getInstance().getPnDocProviderService();
			// select default docProviderIds from PnDocProvider
			Iterator docProviderIdsIterator = docProviderService.getDocProviderIds().iterator();
			// get PnDocProviderHasDocSpaceService service
			IPnDocProviderHasDocSpaceService docProviderHasDocSpaceService = ServiceFactory.getInstance().getPnDocProviderHasDocSpaceService();
			// saves PnDocProviderHasDocSpace objects
			while(docProviderIdsIterator.hasNext()){
				PnDocProvider pnDocProvider = (PnDocProvider)docProviderIdsIterator.next();
				docProviderHasDocSpaceService.saveDocProviderHasDocSpace(new PnDocProviderHasDocSpace(new PnDocProviderHasDocSpacePK(pnDocProvider.getDocProviderId(), documentObjectId)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentObjectId;
	}

}
