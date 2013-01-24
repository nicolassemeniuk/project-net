package net.project.methodology.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.service.ServiceFactory;

public class LinkContainer {

	private LinkContainer() {
		objectLinks = new HashMap<String, List<ObjectLink>>();
	}

	private static LinkContainer linkContatiner;

	private static Map<String, List<ObjectLink>> objectLinks;

	public static final String DOCUMENT = "document";

	public static LinkContainer getInstance() {
		if (linkContatiner == null) {
			linkContatiner = new LinkContainer();
		}
		return linkContatiner;
	}

	public synchronized void updateLinks(Integer oldObjectId, Integer newObjectId) {
		// get all object links for a parent
		List<PnObjectLink> result = ServiceFactory.getInstance().getPnObjectLinkService().getObjectLinksByParent(oldObjectId);
		List<ObjectLink> existingParentObjectLinks = new ArrayList<ObjectLink>();
		List<ObjectLink> existingChildObjectLinks = new ArrayList<ObjectLink>();
		for (PnObjectLink p : result) {
			ObjectLink ol = new ObjectLink();
			checkLink(p.getComp_id().getFromObjectId(), p.getComp_id().getToObjectId());
			ol.setContext(p.getComp_id().getContext());
			ol.setFromObjectIdOld(p.getComp_id().getFromObjectId());
			ol.setFromObjectIdNew(newObjectId);
			ol.setToObjectIdOld(p.getComp_id().getToObjectId());
			existingParentObjectLinks.add(ol);
		}

		// get all object links as a child
		List<PnObjectLink> resultTo = ServiceFactory.getInstance().getPnObjectLinkService().getObjectLinksByChild(oldObjectId);
		for (PnObjectLink p : resultTo) {
			ObjectLink ol = new ObjectLink();
			ol.setContext(p.getComp_id().getContext());
			ol.setFromObjectIdOld(p.getComp_id().getFromObjectId());
			ol.setToObjectIdOld(p.getComp_id().getToObjectId());
			ol.setToObjectIdNew(newObjectId);
			existingChildObjectLinks.add(ol);
		}

		List<ObjectLink> documentLinks = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
		int numberOfLinks = 0;
		if(documentLinks != null){
			numberOfLinks = documentLinks.size();
		}
        int counter = 0;
        while(counter <  numberOfLinks){
        	ObjectLink o = documentLinks.get(counter);
        	Integer toObjectIdOld = o.getToObjectIdOld();
        	for(ObjectLink existing : existingChildObjectLinks)
        	if(toObjectIdOld.equals(Integer.valueOf(existing.getToObjectIdOld()))){
        		//documentLinks.remove(o);
        		o.setToObjectIdNew(Integer.valueOf(existing.getToObjectIdNew()));
        		//documentLinks.add(o);
        		//break;
        	}
        	counter++;
		}
        
		if(documentLinks != null){
			numberOfLinks = documentLinks.size();
		}
        counter = 0;
        while(counter <  numberOfLinks){
        	ObjectLink o = documentLinks.get(counter);
        	Integer fromObjectIdOld = o.getFromObjectIdOld();
        	for(ObjectLink existing : existingParentObjectLinks)
        	if(fromObjectIdOld.equals(Integer.valueOf(existing.getFromObjectIdOld()))){
        		//documentLinks.remove(o);
        		o.setFromObjectIdNew(Integer.valueOf(existing.getFromObjectIdNew()));
        		//documentLinks.add(o);
        		//break;
        	}
        	counter++;
		}
        
        if(documentLinks != null){
        	documentLinks.addAll(existingParentObjectLinks);
			documentLinks.addAll(existingChildObjectLinks);
        }
		LinkContainer.getInstance().setLinks(LinkContainer.DOCUMENT, documentLinks);
	}

	private boolean checkLink(Integer oldObjectId, Integer newObjectId) {
		boolean linkExists = false;
		try {
			List<ObjectLink> documentLinks = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
			for (ObjectLink o : documentLinks) {
				Integer toObjectIdOld = o.getToObjectIdOld();
				Integer fromObjectIdOld = o.getFromObjectIdOld();
				if (toObjectIdOld.equals(oldObjectId)) {
					//documentLinks.remove(o);
					o.setToObjectIdNew(Integer.valueOf(newObjectId));
					//documentLinks.add(o);
					linkExists = true;
					//break;
				}
				if (fromObjectIdOld.equals(Integer.valueOf(oldObjectId))) {
					//documentLinks.remove(o);
					o.setFromObjectIdNew(Integer.valueOf(newObjectId));
					//documentLinks.add(o);
					linkExists = true;
					//break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return linkExists;
	}

	public void setLinks(String key, List<ObjectLink> links) {
		try {
			List<ObjectLink> currentList = objectLinks.get(key);
			if (currentList != null) {
				//List<ObjectLink> temp = objectLinks.get(key);
				for (ObjectLink o : links){
					if(!currentList.contains(o)){
						currentList.add(o);
					}
					//temp.addAll(links);
				}
				
				objectLinks.put(key, currentList);
			} else {
				objectLinks.put(key, links);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<ObjectLink> getLinks(String key) {
		return objectLinks.get(key);
	}

}
