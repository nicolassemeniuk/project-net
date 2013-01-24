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
package net.project.hibernate.service.impl;


import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.model.PnBookmark;
import net.project.hibernate.model.PnDocBySpaceView;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocContainerHasObject;
import net.project.hibernate.model.PnDocContainerHasObjectPK;
import net.project.hibernate.model.PnDocContentElement;
import net.project.hibernate.model.PnDocHistory;
import net.project.hibernate.model.PnDocHistoryPK;
import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.model.PnDocVersion;
import net.project.hibernate.model.PnDocVersionHasContent;
import net.project.hibernate.model.PnDocVersionHasContentPK;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.model.PnTmpDocument;
import net.project.hibernate.service.IDocumentService;
import net.project.hibernate.service.IPnDocProviderHasDocSpaceService;
import net.project.hibernate.service.IPnDocProviderService;
import net.project.hibernate.service.IPnDocSpaceService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasDocSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.filters.IPnDocBySpaceViewFilter;
import net.project.hibernate.service.filters.IPnDocContainerFilter;
import net.project.hibernate.service.filters.IPnDocContainerHasObjectFilter;
import net.project.hibernate.service.filters.IPnDocSpaceHasContainerFilter;
import net.project.hibernate.service.filters.IPnDocVersionFilter;
import net.project.hibernate.service.filters.IPnDocVersionHasContentFilter;
import net.project.hibernate.service.filters.IPnDocumentFilter;
import net.project.hibernate.service.filters.IPnObjectLinkFilter;
import net.project.hibernate.service.filters.IPnSpaceHasDocSpaceFilter;
import net.project.hibernate.service.impl.utilities.CreateDiscussionGroupClass;
import net.project.hibernate.service.impl.utilities.DebugMethodUtilites;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="documentService")
public class DocumentServiceImpl implements IDocumentService {
    private Logger logger;

    public Logger getLogger() {
        if (logger == null)
            logger = Logger.getLogger(getClass());
        return logger;
    }


    public Integer createDocumentSpace(Integer spaceId, Integer personId) {
        Integer documentObjectId = null;
        String methodName = "createDocumentSpace";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            // get PnObject service
            IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
            // create document object
            documentObjectId = objectService.saveObject(new PnObject("doc_space", new Integer(1), new Date(System.currentTimeMillis()), "A"));
            // get PnDocSpace service
            IPnDocSpaceService docSpaceService = ServiceFactory.getInstance().getPnDocSpaceService();
            // save PnDocSpace object
            docSpaceService.saveDocSpace(new PnDocSpace(documentObjectId, "default", new Date(System.currentTimeMillis()), "A"));
            // get PnSpaceHasDocSpace service
            IPnSpaceHasDocSpaceService spaceHasDocSpace = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService();
            // save PnSpaceHasDocSpace object
            spaceHasDocSpace.saveSpaceHasDocSpace(new PnSpaceHasDocSpace(new PnSpaceHasDocSpacePK(spaceId, documentObjectId), 1));
            // get PnDocProvider service
            IPnDocProviderService docProviderService = ServiceFactory.getInstance().getPnDocProviderService();
            // select default docProviderIds from PnDocProvider
            Iterator docProviderIdsIterator = docProviderService.getDocProviderIds().iterator();
            // get PnDocProviderHasDocSpaceService service
            IPnDocProviderHasDocSpaceService docProviderHasDocSpaceService = ServiceFactory.getInstance().getPnDocProviderHasDocSpaceService();
            // saves PnDocProviderHasDocSpace objects
            while (docProviderIdsIterator.hasNext()) {
                PnDocProvider pnDocProvider = (PnDocProvider) docProviderIdsIterator.next();
                docProviderHasDocSpaceService.saveDocProviderHasDocSpace(new PnDocProviderHasDocSpace(new PnDocProviderHasDocSpacePK(pnDocProvider.getDocProviderId(), documentObjectId)));
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return documentObjectId;
    }

    public Integer createBookmark(String name, String description, String url, Integer containerId, Integer statusId, Integer ownerId, String comments, Integer createdById) {
        Integer result = null;
        Integer documentObjectId = null;
        String methodName = "createBookmark";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        /*
        o_object_id := base.create_object (G_BOOKMARK_OBJECT_TYPE, createdById, 'A');
        */
        try {
            Date vSysDate = new Date();
            result = ServiceFactory.getInstance().getBaseService().createObject(G_BOOKMARK_OBJECT_TYPE, createdById, "A");
            PnBookmark newPnBookmark = new PnBookmark();
            newPnBookmark.setName(name);
            newPnBookmark.setBookmarkId(result);
            newPnBookmark.setDescription(description);
            newPnBookmark.setStatusId(statusId);
            newPnBookmark.setPnPersonByOwnerId(ServiceFactory.getInstance().getPnPersonService().getPerson(ownerId));
            newPnBookmark.setComments(comments);
            newPnBookmark.setModifiedDate(vSysDate);
            newPnBookmark.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(createdById));
            newPnBookmark.setRecordStatus(G_ACTIVE_RECORD_STATUS);
            newPnBookmark.setCrc(vSysDate);
            ServiceFactory.getInstance().getPnBookmarkService().save(newPnBookmark);
            Integer vSpaceId = getSpaceForContainerId(containerId);
            ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(result, containerId, G_BOOKMARK_OBJECT_TYPE,
                    vSpaceId, createdById);
            addObjectToContainer(result.toString(), containerId.toString());
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public Integer getSpaceForContainerId(final Integer containerId) {
        Integer result = null;
        String methodName = "getSpaceForContainerId";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = ServiceFactory.getInstance().getPnDocSpaceHasContainerService().findByFilter(new IPnDocSpaceHasContainerFilter() {
                public boolean isSuitable(PnDocSpaceHasContainer object) {
                    return object.getPnDocContainer().getDocContainerId().equals(containerId);
                }
            });
            /*OAR, original query:
                      select doc_space_id into v_doc_space_id
                from pn_doc_space_has_container where doc_container_id = containerId;
                */
            final PnDocSpaceHasContainer currentPnDocSpaceHasContainer = allPnDocSpaceHasContainers.get(0);

            List<PnSpaceHasDocSpace> allHasSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {

                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getPnDocSpace().getDocSpaceId().equals(currentPnDocSpaceHasContainer.getPnDocSpace().getDocSpaceId());
                }
            });
            result = new Integer(allHasSpaces.get(0).getComp_id().getSpaceId());
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public Integer getContainerForObjectId(final Integer objectId) {
        /*
        select distinct doc_container_id into v_container_id
    from pn_doc_container_has_object where object_id = objectId;
    */
        Integer result = null;
        String methodName = "getContainerForObjectId";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            List<PnDocContainerHasObject> list = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {
                public boolean isSuitable(PnDocContainerHasObject object) {
                    return object.getPnObject().getObjectId().equals(objectId);
                }
            });
            result = new Integer(list.get(0).getComp_id().getDocContainerId());
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public Integer getContainerForDocId(Integer docId) {
        return getContainerForObjectId(docId);
    }

    public Integer getDocSpaceForId(Integer id) {
        Integer result = null;
        String methodName = "getDocSpaceForId";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final Integer vContainerId = getContainerForObjectId(id);
            List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = ServiceFactory.getInstance().getPnDocSpaceHasContainerService().findByFilter(new IPnDocSpaceHasContainerFilter() {
                public boolean isSuitable(PnDocSpaceHasContainer object) {
                    return object.getComp_id().getDocContainerId().equals(vContainerId);
                }
            });
            result = allPnDocSpaceHasContainers.get(0).getComp_id().getDocSpaceId();
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public Integer getSpaceForId(Integer id) {
        Integer result = null;
        String methodName = "getSpaceForId";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final Integer vContainerId = getContainerForDocId(id);
            List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = ServiceFactory.getInstance().getPnDocSpaceHasContainerService().findByFilter(new IPnDocSpaceHasContainerFilter() {
                public boolean isSuitable(PnDocSpaceHasContainer object) {
                    return object.getComp_id().getDocContainerId().equals(vContainerId);
                }
            });
            final Integer vDocSpaceId = new Integer(allPnDocSpaceHasContainers.get(0).getComp_id().getDocSpaceId());
            List<PnSpaceHasDocSpace> pnSpaceHasSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {
                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getComp_id().getDocSpaceId().equals(vDocSpaceId);
                }
            });
            result = new Integer(pnSpaceHasSpaces.get(0).getComp_id().getSpaceId());
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(final Integer inSpaceId) {

        int result = 0;
        /*
        select count(doc_id) into v_count from pn_doc_by_space_view
            where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                                    where space_id = inSpaceId);
        */
        String methodName = "getCount";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final List<PnSpaceHasDocSpace> allPnSpaceHasDocSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {

                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getComp_id().getSpaceId().equals(inSpaceId);
                }
            });

            List<PnDocBySpaceView> views = ServiceFactory.getInstance().getPnDocBySpaceViewService().findByFilter(new IPnDocBySpaceViewFilter() {

                public boolean isSuitable(PnDocBySpaceView pnDocBySpaceView) {
                    boolean result = false;
                    for (int i = 0, n = allPnSpaceHasDocSpaces.size(); i < n; i++) {
                        PnSpaceHasDocSpace currentSpaceHasDocSpace = allPnSpaceHasDocSpaces.get(i);
                        result = pnDocBySpaceView.getDocSpaceId().equals(currentSpaceHasDocSpace.getComp_id().getDocSpaceId());
                        if (result)
                            break;
                    }
                    return result;
                }
            });
            result = views.size();
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void addObjectToContainer(String inObjectId, String inContainerId) {
        String methodName = "addObjectToContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            PnDocContainerHasObject currentPnDocContainerHasObject = new PnDocContainerHasObject();
            PnDocContainerHasObjectPK pnDocContainerHasObjectPK = new PnDocContainerHasObjectPK();
            currentPnDocContainerHasObject.setComp_id(pnDocContainerHasObjectPK);
            pnDocContainerHasObjectPK.setDocContainerId(new Integer(inContainerId));
            pnDocContainerHasObjectPK.setObjectId(new Integer(inObjectId));
            ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(currentPnDocContainerHasObject);
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public void modifyBookmark(Integer id, String name, String description, String url, Integer containerId, Integer statusId, Integer ownerId, String comments, Integer createdById) {
        String methodName = "modifyBookmark";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            PnBookmark currentBookmark = ServiceFactory.getInstance().getPnBookmarkService().getByPK(id);
            if (currentBookmark != null) {
                Date vSysdate = new Date();
                currentBookmark.setName(name);
                currentBookmark.setDescription(description);
                currentBookmark.setUrl(url);
                currentBookmark.setStatusId(statusId);
                currentBookmark.setPnPersonByOwnerId(ServiceFactory.getInstance().getPnPersonService().getPerson(ownerId));
                currentBookmark.setComments(comments);
                currentBookmark.setModifiedDate(vSysdate);
                currentBookmark.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(createdById));
                currentBookmark.setRecordStatus("A");
                currentBookmark.setCrc(vSysdate);
                ServiceFactory.getInstance().getPnBookmarkService().update(currentBookmark);
                long endTime = System.currentTimeMillis();
                long timeExecution = endTime - startTime;
                String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
                getLogger().debug(messageTimeExecution);
                getLogger().debug(methodIsFinished);
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public boolean fVerifyCrc(Date origCrc, Date currentCrc) {
        return (origCrc.equals(currentCrc));
    }

    public Integer hasLinks(final Integer id) {
        Integer result = new Integer(0);
        String methodName = "hasLinks";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            List<PnObjectLink> pnObjectLinks = ServiceFactory.getInstance().getPnObjectLinkService().findByFilter(new IPnObjectLinkFilter() {
                private boolean validateObject(PnObject object) {
                    return (object.getRecordStatus().equalsIgnoreCase("A"));
                }

                public boolean isSuitable(PnObjectLink object) {
                    PnObject fromObject = object.getPnObjectByFromObjectId();
                    PnObject toObject = object.getPnObjectByToObjectId();
                    boolean result = ((validateObject(fromObject)) && (validateObject(toObject)) && ((fromObject.getObjectId().equals(id)) || (toObject.getObjectId().equals(id))));
                    return result;
                }
            });
            if (pnObjectLinks.size() > 0) {
                result = new Integer(1);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void removeObjectFromContainer(final String inObjectId, final String inContainerId) {
        String methodName = "removeObjectFromContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            ServiceFactory.getInstance().getPnDocContainerHasObjectService().deleteByFilter(new IPnDocContainerHasObjectFilter() {

                public boolean isSuitable(PnDocContainerHasObject object) {
                    return ((object.getComp_id().getDocContainerId().equals(new Integer(inContainerId))) &&
                            (object.getComp_id().getObjectId().equals(new Integer(inObjectId))));
                }
            });
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public boolean fVerifyUniqueName(final String newName, final Integer containerId, final String objectType) {
        boolean result = true;
        String methodName = "fVerifyUniqueName";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            String vName = null;
            Integer vFoo = new Integer(-1);
            List<PnDocContainerHasObject> containerObjects = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {
                public boolean isSuitable(PnDocContainerHasObject object) {
                    return ((object.getComp_id().getDocContainerId().equals(containerId)) && (object.getPnObject().getPnObjectType().getObjectType().equalsIgnoreCase(objectType)));
                }
            });

            for (int i = 0, n = containerObjects.size(); i < n; i++) {
                final PnDocContainerHasObject currentPnDocContainerHasObject = containerObjects.get(i);
                //f (objectType = G_DOCUMENT_OBJECT_TYPE) then
                if (objectType.equalsIgnoreCase(G_DOCUMENT_OBJECT_TYPE)) {
                    List<PnDocument> documents = ServiceFactory.getInstance().getPnDocumentService().findByFilter(new IPnDocumentFilter() {
                        public boolean isSuitable(PnDocument object) {
                            return ((object.getDocId().equals(currentPnDocContainerHasObject.getComp_id().getObjectId())) && (object.getRecordStatus().equalsIgnoreCase("A")));
                        }
                    });
                    if (documents.size() == 0) {
                        vFoo = new Integer(1);
                    } else {
                        vName = documents.get(0).getDocName();
                        if (vName.toLowerCase().equalsIgnoreCase(newName.toLowerCase())) {
                            result = false;
                        }

                    }
                } else {
                    //
                    if (objectType.equalsIgnoreCase(G_CONTAINER_OBJECT_TYPE)) {
                        List<PnDocContainer> containers = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {
                            public boolean isSuitable(PnDocContainer object) {
                                return ((object.getDocContainerId().equals(currentPnDocContainerHasObject.getComp_id().getObjectId())) && (object.getRecordStatus().equalsIgnoreCase("A")));
                            }
                        });
                        if (containers.size() == 0) {
                            vFoo = new Integer(1);
                        } else {
                            vName = containers.get(0).getContainerName();
                            if (vName.toLowerCase().equalsIgnoreCase(newName.toLowerCase())) {
                                result = false;
                            }
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public Integer isParentContainer(final String parentContainerId, final String childContainerId) {
        String methodName = "isParentContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        Integer result = new Integer(0);
        try {
            final Integer vChildId = new Integer(childContainerId);
            final Integer vParentId = new Integer(parentContainerId);
            List<PnDocContainerHasObject> docContainerHasObjects = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {
                public boolean isSuitable(PnDocContainerHasObject object) {
                    return (object.getPnObject().getObjectId().equals(vChildId));
                }
            });
            PnDocContainerHasObject currentPnDocContainerHasObject = docContainerHasObjects.get(0);
            if (vParentId.equals(currentPnDocContainerHasObject.getComp_id().getDocContainerId())) {
                result = new Integer(1);
            }
            while ((result.equals(new Integer(0))) && (currentPnDocContainerHasObject != null)) {
                final PnDocContainerHasObject tempCurrentPnDocContainerHasObject = docContainerHasObjects.get(0);
                docContainerHasObjects = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {
                    public boolean isSuitable(PnDocContainerHasObject object) {
                        return (object.getPnObject().getObjectId().equals(tempCurrentPnDocContainerHasObject.getComp_id().getDocContainerId()));
                    }

                });
                currentPnDocContainerHasObject = docContainerHasObjects.get(0);
                if (vParentId.equals(currentPnDocContainerHasObject.getComp_id().getDocContainerId())) {
                    result = new Integer(1);
                }
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void moveObject(final String inObjectId, final String inContainerId) {
        String methodName = "moveObject";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            List<PnDocContainerHasObject> all = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {
                public boolean isSuitable(PnDocContainerHasObject object) {
                    return (object.getComp_id().getObjectId().equals(new Integer(inObjectId)));
                }
            });

            final PnDocContainerHasObject currentPnDocContainerHasObject = all.get(0);
            ServiceFactory.getInstance().getPnDocContainerHasObjectService().deleteByFilter(new IPnDocContainerHasObjectFilter() {
                public boolean isSuitable(PnDocContainerHasObject object) {
                    return ((object.getComp_id().getObjectId().equals(new Integer(inObjectId))) && (object.getComp_id().getDocContainerId().equals(currentPnDocContainerHasObject.getComp_id().getDocContainerId())));
                }
            });
            PnDocContainerHasObject newPnDocContainerHasObject = new PnDocContainerHasObject();
            PnDocContainerHasObjectPK pnDocContainerHasObjectPK = new PnDocContainerHasObjectPK();
            newPnDocContainerHasObject.setComp_id(pnDocContainerHasObjectPK);
            pnDocContainerHasObjectPK.setDocContainerId(new Integer(inContainerId));
            pnDocContainerHasObjectPK.setObjectId(new Integer(inObjectId));
            ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(newPnDocContainerHasObject);
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public Integer modifyContainer(String parentId, String myContainerId, String folderName, String description, String whoami, String isHidden, Date origCrc) {
        Integer result = null;
        String methodName = "modifyContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final Integer vParentId = new Integer(parentId);
            final Integer vContainerId = new Integer(myContainerId);
            final Integer vWhoami = new Integer(whoami);
            final Date startDate = new Date();
            List<PnDocContainer> all = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {
                public boolean isSuitable(PnDocContainer object) {
                    return object.getDocContainerId().equals(vContainerId);
                }
            });
            PnDocContainer vCurrentCrc = all.get(0);
            if (fVerifyCrc(origCrc, vCurrentCrc.getCrc())) {
                /*
                IF (   (folderName = v_current_name)
             OR document.f_verify_unique_name (folderName,
                                               vParentId,
                                               g_container_object_type
                                              )
            )
            */
                if ((folderName.equals(vCurrentCrc.getContainerName())) || (fVerifyUniqueName(folderName, vParentId, G_CONTAINER_OBJECT_TYPE))) {
                    /*
                    UPDATE pn_doc_container
               SET container_name = folderName,
                   container_description = description,
                   isHidden = isHidden,
                   modified_by_id = vWhoami,
                   date_modified = SYSDATE
             WHERE doc_container_id = vContainerId;
               */
                    List<PnDocContainer> allContainers = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {
                        public boolean isSuitable(PnDocContainer object) {
                            return object.getDocContainerId().equals(vContainerId);
                        }
                    });
                    for (int i = 0, n = allContainers.size(); i < n; i++) {
                        PnDocContainer container = allContainers.get(0);
                        container.setContainerName(folderName);
                        container.setContainerDescription(description);
                        container.setIsHidden(new Integer(isHidden));
                        container.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                        container.setDateModified(startDate);
                        ServiceFactory.getInstance().getPnDocContainerService().updateDocContainer(container);
                    }
                    result = new Integer(0);
                } else {
                    if ((!folderName.equals(vCurrentCrc.getContainerName())) && (folderName.toLowerCase().equals(vCurrentCrc.getContainerName().toLowerCase()))) {
                        /*
                             UPDATE pn_doc_container
                        SET container_name = folderName,
                            container_description = description,
                            isHidden = isHidden,
                            modified_by_id = vWhoami,
                            date_modified = SYSDATE
                      WHERE doc_container_id = vContainerId;
                        */
                        List<PnDocContainer> allContainers = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {
                            public boolean isSuitable(PnDocContainer object) {
                                return object.getDocContainerId().equals(vContainerId);
                            }
                        });
                        for (int i = 0, n = allContainers.size(); i < n; i++) {
                            PnDocContainer container = allContainers.get(0);
                            container.setContainerName(folderName);
                            container.setContainerDescription(description);
                            container.setIsHidden(new Integer(isHidden));
                            container.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                            container.setDateModified(startDate);
                            ServiceFactory.getInstance().getPnDocContainerService().updateDocContainer(container);
                        }
                        result = new Integer(0);
                    }
                }
            } else {
                result = new Integer(1000);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void logEvent(String docId, String whoami, String action, String actionName, String notes) {
        /*
        nsert into pn_doc_history (
         docId,
         doc_history_id,
         action,
         actionName,
         action_by_id,
         action_date,
         action_comment)
     values (
         v_document_id,
         v_history_id,
         v_action,
         v_action_name,
         v_whoami,
         SYSDATE,
         v_action_comment
     );
     */
        String methodName = "logEvent";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer vHistoryId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
            PnDocHistory object = new PnDocHistory();
            PnDocHistoryPK pk = new PnDocHistoryPK();
            object.setComp_id(pk);
            pk.setDocHistoryId(vHistoryId);
            pk.setDocId(new Integer(docId));
            object.setPnDocActionLookup(ServiceFactory.getInstance().getPnDocActionLookupService().getById(action));
            object.setActionName(actionName);
            object.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(new Integer(whoami)));
            object.setActionDate(new Date());
            object.setActionComment(notes);
            ServiceFactory.getInstance().getPnDocHistoryService().save(object);
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public Integer copyContainer(final Integer fromContainerId, final Integer newParentContainerId, final Integer creatorId) {
        Integer result = null;
        String methodName = "copyContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            List<PnDocContainer> allContainers = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {
                public boolean isSuitable(PnDocContainer object) {
                    return ((object.getDocContainerId().equals(fromContainerId)) && (object.getRecordStatus().toLowerCase().equals("a")));
                }
            });
            PnDocContainer container = allContainers.get(0);
            if (container.getIsHidden() == 0) {
                /*
                 if (f_verify_unique_name (
                     v_container_rec.container_name,
                     newParentContainerId,
                     g_container_object_type
                 )
                )
                */
                if (fVerifyUniqueName(container.getContainerName(), newParentContainerId, G_CONTAINER_OBJECT_TYPE)) {
                    /*
                    base.create_object (
                                          g_container_object_type,
                                          creatorId,
                                          g_active_record_status
                                      );
                                      */
                    result = ServiceFactory.getInstance().getBaseService().createObject(G_CONTAINER_OBJECT_TYPE, creatorId, G_ACTIVE_RECORD_STATUS);
                    /*
                 insert into pn_doc_container
                         (doc_container_id, container_name, container_description,
                          record_status, modified_by_id, date_modified, crc)
                  values (v_new_container_id, v_container_rec.container_name,
                          v_container_rec.container_description,
                          v_container_rec.record_status, creatorId,
                          v_timestamp, v_timestamp);
                    */
                    PnDocContainer newContainer = new PnDocContainer();
                    newContainer.setDocContainerId(result);
                    newContainer.setContainerName(container.getContainerName());
                    newContainer.setContainerDescription(container.getContainerDescription());
                    newContainer.setRecordStatus(container.getRecordStatus());
                    newContainer.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(creatorId));
                    Date currentDate = new Date();
                    newContainer.setDateModified(currentDate);
                    newContainer.setCrc(currentDate);
                    ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(newContainer);
                    /*
                          security.applyDocumentPermissions (
                        v_new_container_id,
                        newParentContainerId,
                        g_container_object_type,
                        get_space_for_container_id (v_container_rec.doc_container_id),
                        creatorId

                    );
                    */
                    ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(result, newParentContainerId, G_CONTAINER_OBJECT_TYPE, getSpaceForContainerId(new Integer(container.getDocContainerId())), creatorId);
                    List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = ServiceFactory.getInstance().getPnDocSpaceHasContainerService().findByFilter(new IPnDocSpaceHasContainerFilter() {
                        public boolean isSuitable(PnDocSpaceHasContainer object) {
                            return (object.getComp_id().getDocContainerId().equals(newParentContainerId));
                        }
                    });
                    PnDocSpaceHasContainer currentPnDocSpaceHasContainer = allPnDocSpaceHasContainers.get(0);
                    /*
                    insert into pn_doc_space_has_container
                                         (doc_space_id, doc_container_id, is_root)
                                  values (v_dshc_rec.doc_space_id, v_new_container_id, 0);
                   */
                    PnDocSpaceHasContainer newPnDocSpaceHasContainer = new PnDocSpaceHasContainer();
                    PnDocSpaceHasContainerPK pk = new PnDocSpaceHasContainerPK();
                    newPnDocSpaceHasContainer.setComp_id(pk);
                    pk.setDocSpaceId(currentPnDocSpaceHasContainer.getComp_id().getDocSpaceId());
                    pk.setDocContainerId(result);
                    newPnDocSpaceHasContainer.setIsRoot(0);
                    ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(newPnDocSpaceHasContainer);
                    PnDocContainerHasObject newPnDocContainerHasObject = new PnDocContainerHasObject();
                    PnDocContainerHasObjectPK objectPK = new PnDocContainerHasObjectPK();
                    newPnDocContainerHasObject.setComp_id(objectPK);
                    objectPK.setDocContainerId(newParentContainerId);
                    objectPK.setObjectId(result);
                    ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(newPnDocContainerHasObject);
                    /*
                    insert into pn_doc_container_has_object
                            (doc_container_id, object_id)
                     values (newParentContainerId, v_new_container_id);
                    */
                }
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void modifyProperties(String tmpDoc, String whoami, Date origCrc) {
        String methodName = "modifyProperties";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            String gActiveRecordStatus = "A";
            String gAction = "modifyProperties";
            String gActionComment = "Modified Document Properties: ";
            Integer vTmpDocId = new Integer(tmpDoc);
            Integer vWhoami = new Integer(whoami);
            Integer vHistoryId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
            final PnTmpDocument vTmpDocRec = ServiceFactory.getInstance().getPnTmpDocumentService().getByPk(vTmpDocId);
            PnDocument vDocument = ServiceFactory.getInstance().getPnDocumentService().findByPk(vTmpDocRec.getDocId());
            Date vCurrentCrc = vDocument.getCrc();
            String vName = vDocument.getDocName();
            /*IF (document.f_verify_crc (origCrc, vCurrentCrc)) THEN

                */
            if (fVerifyCrc(origCrc, vCurrentCrc)) {
                List<PnDocContainerHasObject> listPnDocContainerHasObject = ServiceFactory.getInstance().getPnDocContainerHasObjectService().findByFilter(new IPnDocContainerHasObjectFilter() {

                    public boolean isSuitable(PnDocContainerHasObject object) {
                        return object.getComp_id().getObjectId().equals(vTmpDocRec.getDocId());
                    }
                });
                PnDocContainerHasObject vContainerId = listPnDocContainerHasObject.get(0);
                if ((vTmpDocRec.getDocName().equals(vName)) || (fVerifyUniqueName(vName, new Integer(vContainerId.getComp_id().getDocContainerId()), G_DOCUMENT_OBJECT_TYPE))) {
                    List<PnDocument> allDocuments = ServiceFactory.getInstance().getPnDocumentService().findByFilter(new IPnDocumentFilter() {

                        public boolean isSuitable(PnDocument pnDocument) {
                            return pnDocument.getDocId().equals(vTmpDocRec.getDocId());
                        }
                    });
                    Date crcDate = new Date();
                    for (int i = 0, n = allDocuments.size(); i < n; i++) {
                        PnDocument currentDocument = allDocuments.get(i);
                        currentDocument.setDocName(vTmpDocRec.getDocName());
                        currentDocument.setDocDescription(vTmpDocRec.getDocDescription());
                        currentDocument.setPnDocType(ServiceFactory.getInstance().getPnDocTypeService().findById(vTmpDocRec.getDocTypeId()));
                        currentDocument.setDocStatusId(vTmpDocRec.getDocStatusId());
                        currentDocument.setCrc(crcDate);
                        ServiceFactory.getInstance().getPnDocumentService().update(currentDocument);
                    }
                    /*
                    update pn_document
            SET
                doc_name = vTmpDocRec.doc_name,
                doc_description = vTmpDocRec.doc_description,
                doc_type_id = vTmpDocRec.doc_type_id,
                doc_status_id = vTmpDocRec.doc_status_id,
                record_status = G_ACTIVE_RECORD_STATUS,
                crc = SYSDATE
            WHERE doc_id = vTmpDocRec.doc_id;
            */
                }
                /*
                IF ((vTmpDocRec.doc_name = vName) OR
                            document.f_verify_unique_name (vTmpDocRec.doc_name, vContainerId, G_DOCUMENT_OBJECT_TYPE)) THEN
                */

                List<PnDocVersion> docVersions = ServiceFactory.getInstance().getPnDocVersionService().findByFilter(new IPnDocVersionFilter() {

                    public boolean isSuitable(PnDocVersion pnDocVersion) {
                        return ((pnDocVersion.getDocVersionId().equals(vTmpDocRec.getDocVersionId())) && (pnDocVersion.getPnDocument().getDocId().equals(vTmpDocRec.getDocId())));
                    }
                });
                for (int i = 0, n = docVersions.size(); i < n; i++) {
                    PnDocVersion currentPnDocVersion = docVersions.get(i);
                    currentPnDocVersion.setPnPersonByDocAuthorId(ServiceFactory.getInstance().getPnPersonService().getPerson(vTmpDocRec.getActionId()));
                    currentPnDocVersion.setDocDescription(vTmpDocRec.getDocDescription());
                    currentPnDocVersion.setDateModified(new Date());
                    currentPnDocVersion.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                    currentPnDocVersion.setDocComment(vTmpDocRec.getDocComment());
                    currentPnDocVersion.setCrc(new Date());
                    currentPnDocVersion.setRecordStatus(gActiveRecordStatus);
                    ServiceFactory.getInstance().getPnDocVersionService().update(currentPnDocVersion);
                }
                /*
                update pn_doc_version
            SET
                doc_author_id = vTmpDocRec.doc_author_id,
                doc_description = vTmpDocRec.doc_description,
                date_modified = SYSDATE,
                modified_by_id = vWhoami,
                doc_comment = vTmpDocRec.doc_comment,
                crc = SYSDATE,
                record_status = G_ACTIVE_RECORD_STATUS
            WHERE
                doc_version_id = vTmpDocRec.doc_version_id
            and
                doc_id = vTmpDocRec.doc_id;
                */
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public void removeContainer(final String containerId, final String recordStatus, final String whoami) {
        String methodName = "removeContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final Integer vContainerId = new Integer(containerId);
            final Integer vWhoami = new Integer(whoami);
            List<PnDocContainer> allContainers = ServiceFactory.getInstance().getPnDocContainerService().findByFilter(new IPnDocContainerFilter() {

                public boolean isSuitable(PnDocContainer pnDocContainer) {
                    return pnDocContainer.getDocContainerId().equals(vContainerId);
                }
            });
            for (int i = 0, n = allContainers.size(); i < n; i++) {
                PnDocContainer container = allContainers.get(i);
                /*
                update
            pn_doc_container
        set
            recordStatus = v_record_status,
            modified_by_id = vWhoami,
            date_modified = SYSDATE
        where
            doc_container_id = vContainerId;
            */
                container.setRecordStatus(recordStatus);
                container.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                container.setDateModified(new Date());
                ServiceFactory.getInstance().getPnDocContainerService().updateDocContainer(container);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public void removeDoc(String docId, Date lastModified, String recordStatus, String whoami) {
        String methodName = "removeDoc";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer vDocId = new Integer(docId);
            Integer vWhoami = new Integer(whoami);
            PnDocument currentDocument = ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocId);
            currentDocument.setRecordStatus(recordStatus);
            ServiceFactory.getInstance().getPnDocumentService().update(currentDocument);
            PnDocVersion currentPnDocVersion = ServiceFactory.getInstance().getPnDocVersionService().findByPk(currentDocument.getCurrentVersionId());
            currentPnDocVersion.setDateModified(lastModified);
            currentPnDocVersion.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
            ServiceFactory.getInstance().getPnDocVersionService().update(currentPnDocVersion);
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public Integer createContainer(String inContainerId, String whoami, String folderName, String description, String spaceId, String isHidden) {
        Integer result = null;
        String methodName = "createContainer";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            final Integer vInContainer = new Integer(inContainerId);
            final Integer vWhoami = new Integer(whoami);
            final Integer vSpaceId = new Integer(spaceId);
            result = ServiceFactory.getInstance().getPnObjectService().generateNewId();
            List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = ServiceFactory.getInstance().getPnDocSpaceHasContainerService().findByFilter(new IPnDocSpaceHasContainerFilter() {
                public boolean isSuitable(PnDocSpaceHasContainer object) {
                    return object.getComp_id().getDocContainerId().equals(vInContainer);
                }
            });
            PnDocSpaceHasContainer currentPnDocSpaceHasContainer = allPnDocSpaceHasContainers.get(0);
            /*
            IF (document.f_verify_unique_name (v_folder_name, vInContainer, G_CONTAINER_OBJECT_TYPE)) THEN
            */
            if (fVerifyUniqueName(folderName, vInContainer, G_CONTAINER_OBJECT_TYPE)) {
                /*
                        -- register new container in pn_object

              insert into pn_object (
                  object_id,
                  date_created,
                  object_type,
                  created_by,
                  record_status )
              values (
                  v_new_id,
                  SYSDATE,
                  G_CONTAINER_OBJECT_TYPE,
                  vWhoami,
                  G_ACTIVE_RECORD_STATUS);
                */
                Date currentDate = new Date();
                PnObject newPnObject = new PnObject();
                newPnObject.setDateCreated(currentDate);
                newPnObject.setPnObjectType(ServiceFactory.getInstance().getPnObjectTypeService().getObjectType(G_CONTAINER_OBJECT_TYPE));
                newPnObject.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newPnObject.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                result = ServiceFactory.getInstance().getPnObjectService().saveObject(newPnObject);

                /*
            -- create new document container

            insert into pn_doc_container (
                doc_container_id,
                container_name,
                container_description,
                record_status,
                isHidden,
                modified_by_id,
                date_modified,
                crc)
            values (
                v_new_id,
                v_folder_name,
                v_description,
                G_ACTIVE_RECORD_STATUS,
                v_is_hidden,
                vWhoami,
                SYSDATE,
                SYSDATE);
                */
                PnDocContainer newPnDocContainer = new PnDocContainer();
                newPnDocContainer.setDocContainerId(result);
                newPnDocContainer.setContainerName(folderName);
                newPnDocContainer.setContainerDescription(description);
                newPnDocContainer.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                newPnDocContainer.setIsHidden(new Integer(isHidden));
                newPnDocContainer.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newPnDocContainer.setDateModified(currentDate);
                newPnDocContainer.setCrc(currentDate);
                ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(newPnDocContainer);
                /*
                SECURITY.APPLY_DOCUMENT_PERMISSIONS (v_new_id, vInContainer, G_DOCUMENT_VERSION_OBJECT_TYPE, vSpaceId, vWhoami);
                */

                ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(result, vInContainer, G_DOCUMENT_VERSION_OBJECT_TYPE, vSpaceId, vWhoami);
                /*
                insert into pn_doc_space_has_container (
          doc_space_id,
          doc_container_id,
          is_root)
      values (
          v_doc_space_id,
          v_new_id,
          0);
          */
                PnDocSpaceHasContainer newPnDocSpaceHasContainer = new PnDocSpaceHasContainer();
                PnDocSpaceHasContainerPK pnDocSpaceHasContainerPK = new PnDocSpaceHasContainerPK();
                newPnDocSpaceHasContainer.setComp_id(pnDocSpaceHasContainerPK);
                pnDocSpaceHasContainerPK.setDocSpaceId(currentPnDocSpaceHasContainer.getComp_id().getDocSpaceId());
                pnDocSpaceHasContainerPK.setDocContainerId(result);
                newPnDocSpaceHasContainer.setIsRoot(0);
                ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(newPnDocSpaceHasContainer);
                /*
                -- make the new folder an object of the exiting folder

      insert into pn_doc_container_has_object (
          doc_container_id,
          object_id)
      values (
          vInContainer,
          v_new_id);*/
            }

            PnDocContainerHasObject newPnDocContainerHasObject = new PnDocContainerHasObject();
            PnDocContainerHasObjectPK pnDocContainerHasObjectPK = new PnDocContainerHasObjectPK();
            newPnDocContainerHasObject.setComp_id(pnDocContainerHasObjectPK);
            pnDocContainerHasObjectPK.setDocContainerId(vInContainer);
            pnDocContainerHasObjectPK.setObjectId(result);
            ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(newPnDocContainerHasObject);
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void checkOut(String docId, String whoami, Date ckoDue, String notes, Date origCrc) {
        String methodName = "checkOut";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer vDocumentId = new Integer(docId);
            final Integer vWhoami = new Integer(whoami);
            final Date currentDate = new Date();
            final Integer vHistoryId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
            final PnDocument vCurrentCrc = ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocumentId);
            if (fVerifyCrc(origCrc, vCurrentCrc.getCrc())) {
                List<PnDocVersion> listPnDocVersion = ServiceFactory.getInstance().getPnDocVersionService().findByFilter(new IPnDocVersionFilter() {
                    public boolean isSuitable(PnDocVersion pnDocVersion) {
                        return ((pnDocVersion.getPnDocument().getDocId().equals(vCurrentCrc.getDocId())) && (pnDocVersion.getDocVersionId().equals(vCurrentCrc.getCurrentVersionId())));
                    }
                });
                PnDocVersion currentPnDocVersion = listPnDocVersion.get(0);
                currentPnDocVersion.setIsCheckedOut(1);
                currentPnDocVersion.setPnPersonByCheckedOutById(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                currentPnDocVersion.setCheckoutDue(ckoDue);
                currentPnDocVersion.setDocComment(notes);
                currentPnDocVersion.setDateCheckedOut(currentDate);
                currentPnDocVersion.setCrc(currentDate);
                ServiceFactory.getInstance().getPnDocVersionService().update(currentPnDocVersion);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public void undoCheckOut(String docId, String whoami, Date origCrc) {
        String methodName = "undoCheckOut";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer vDocId = new Integer(docId);
            final PnDocument vCurrentCrc = ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocId);
            if (fVerifyCrc(origCrc, vCurrentCrc.getCrc())) {
                List<PnDocVersion> listPnDocVersion = ServiceFactory.getInstance().getPnDocVersionService().findByFilter(new IPnDocVersionFilter() {
                    public boolean isSuitable(PnDocVersion pnDocVersion) {
                        return ((pnDocVersion.getPnDocument().getDocId().equals(vCurrentCrc.getDocId())) && (pnDocVersion.getDocVersionId().equals(vCurrentCrc.getCurrentVersionId())));
                    }
                });
                PnDocVersion currentPnDocVersion = listPnDocVersion.get(0);
                currentPnDocVersion.setIsCheckedOut(0);
                currentPnDocVersion.setPnPersonByCheckedOutById(null);
                currentPnDocVersion.setCheckoutDue(null);
                currentPnDocVersion.setDateCheckedOut(null);
                currentPnDocVersion.setCrc(new Date());
                ServiceFactory.getInstance().getPnDocVersionService().update(currentPnDocVersion);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }

    }

    public Integer copyBookmark(Integer fromBookmarkId, Integer toContainerId, Integer creatorId) {
        Integer result = null;
        String methodName = "copyBookmark";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Date vSysdate = new Date();
            PnBookmark vBookmarkRec = ServiceFactory.getInstance().getPnBookmarkService().getByPK(fromBookmarkId);
            result = ServiceFactory.getInstance().getBaseService().createObject(G_BOOKMARK_OBJECT_TYPE, creatorId, "A");

            /*
            v_new_bookmark_id :=
                              base.create_object (g_bookmark_object_type, creatorId, 'A');
            */
            /*
            insert into pn_bookmark
                            (bookmark_id, name, description, url, status_id, owner_id,
                             comments, modified_date, modified_by_id, record_status, crc)
                     values (v_new_bookmark_id, vBookmarkRec.name,
                             vBookmarkRec.description, vBookmarkRec.url,
                             vBookmarkRec.status_id, creatorId, vBookmarkRec.comments,
                             vSysdate, creatorId, g_active_record_status, vSysdate);
            */
            PnBookmark newPnBookmark = new PnBookmark();
            newPnBookmark.setBookmarkId(result);
            newPnBookmark.setName(vBookmarkRec.getName());
            newPnBookmark.setDescription(vBookmarkRec.getDescription());
            newPnBookmark.setUrl(vBookmarkRec.getUrl());
            newPnBookmark.setStatusId(vBookmarkRec.getStatusId());
            PnPerson creatorPerson = ServiceFactory.getInstance().getPnPersonService().getPerson(creatorId);
            newPnBookmark.setPnPersonByOwnerId(creatorPerson);
            newPnBookmark.setComments(vBookmarkRec.getComments());
            newPnBookmark.setModifiedDate(vSysdate);
            newPnBookmark.setPnPersonByModifiedById(creatorPerson);
            newPnBookmark.setRecordStatus(G_ACTIVE_RECORD_STATUS);
            newPnBookmark.setCrc(vSysdate);
            ServiceFactory.getInstance().getPnBookmarkService().save(newPnBookmark);
            Integer vSpaceId = getSpaceForContainerId(toContainerId);
            /*
            security.applyDocumentPermissions (
        v_new_bookmark_id,
        toContainerId,
        g_bookmark_object_type,
        vSpaceId,
        creatorId
    );
    */
            ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(result, toContainerId,
                    G_BOOKMARK_OBJECT_TYPE, vSpaceId, creatorId);
            /*
            addObjectToContainer (v_new_bookmark_id, toContainerId);
            */
            addObjectToContainer(result.toString(), toContainerId.toString());
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void checkIn(String tmpDoc, String whoami, String spaceId, Date origCrc) {
        String methodName = "checkIn";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer vTmpDocId = new Integer(tmpDoc);
            Integer vWhoami = new Integer(whoami);
            Integer vSpaceId = new Integer(spaceId);
            /*
             SELECT pn_object_sequence.nextval INTO vVersionId FROM dual;
             SELECT pn_object_sequence.nextval INTO vContentId FROM dual;
                */
            Integer vVersionId = null;
            Integer vContentId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
            /*
           select * INTO vTmpDocRec from pn_tmp_document where tmp_doc_id = vTmpDocId;
             vDocId := vTmpDocRec.doc_id;
                */
            PnTmpDocument vTmpDocRec = ServiceFactory.getInstance().getPnTmpDocumentService().getByPk(vTmpDocId);
            final Integer vDocId = vTmpDocRec.getDocId();
            /*
            select crc into vCurrentCrc from pn_document where doc_id = vTmpDocRec.doc_id;
                */
            PnDocument vCurrentCrc = ServiceFactory.getInstance().getPnDocumentService().findByPk(vTmpDocRec.getDocId());
            /*
            IF (document.f_verify_crc (origCrc, vCurrentCrc)) THEN
                */
            if (fVerifyCrc(origCrc, vCurrentCrc.getCrc())) {
                /*
               -- get the properties for the document
                    select * INTO vDocProperties from pn_document where doc_id = vDocId;

                    -- get the current version info for the document
                    select * INTO vCurrentVersionRec from pn_doc_version
                        where doc_id = vDocId and doc_version_id = vDocProperties.current_version_id;
                */
                final PnDocument vDocProperties = ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocId);
                List<PnDocVersion> listPnDocVersion = ServiceFactory.getInstance().getPnDocVersionService().findByFilter(new IPnDocVersionFilter() {

                    public boolean isSuitable(PnDocVersion pnDocVersion) {
                        return ((pnDocVersion.getPnDocument().getDocId().equals(vDocId)) && (pnDocVersion.getDocVersionId().equals(vDocProperties.getCurrentVersionId())));
                    }
                });
                PnDocVersion vCurrentVersionRec = listPnDocVersion.get(0);
                //vVersionNum := vCurrentVersionRec.doc_version_num + 1;
                Integer vVersionNum = new Integer(vCurrentVersionRec.getDocVersionNum() + 1);
                Date currentDate = new Date();
                /*
                insert into pn_object (
              object_id,
              date_created,
              object_type,
              created_by,
              record_status )
          values (
              vVersionId,
              SYSDATE,
              G_DOCUMENT_VERSION_OBJECT_TYPE,
              vWhoami,
              G_ACTIVE_RECORD_STATUS);
              */
                PnObject newObject = new PnObject();
                newObject.setDateCreated(currentDate);
                newObject.setPnObjectType(ServiceFactory.getInstance().getPnObjectTypeService().getObjectType(G_DOCUMENT_VERSION_OBJECT_TYPE));
                newObject.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newObject.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                vVersionId = ServiceFactory.getInstance().getPnObjectService().saveObject(newObject);
                /*
    insert into pn_doc_version (
          doc_id, doc_version_id, doc_version_num, doc_version_label, doc_version_name, source_file_name,
          short_file_name, doc_author_id, doc_description, date_modified, modified_by_id, doc_comment,
          crc, record_status)
      values (vDocId,vVersionId, vVersionNum, NULL, vDocProperties.doc_name, vTmpDocRec.source_file_name,
          vTmpDocRec.short_file_name, vCurrentVersionRec.doc_author_id,
          vCurrentVersionRec.doc_description, SYSDATE, vWhoami, vTmpDocRec.doc_comment,
          SYSDATE, G_ACTIVE_RECORD_STATUS
      );
                */
                PnDocVersion newPnDocVersion = new PnDocVersion();
                newPnDocVersion.setPnDocument(ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocId));
                newPnDocVersion.setDocVersionId(vVersionId);
                newPnDocVersion.setDocVersionNum(vVersionNum);
                newPnDocVersion.setDocVersionLabel(null);
                newPnDocVersion.setDocVersionName(vDocProperties.getDocName());
                newPnDocVersion.setSourceFileName(vTmpDocRec.getSourceFileName());
                newPnDocVersion.setShortFileName(vTmpDocRec.getShortFileName());
                newPnDocVersion.setPnPersonByDocAuthorId(vCurrentVersionRec.getPnPersonByDocAuthorId());
                newPnDocVersion.setDocDescription(vCurrentVersionRec.getDocDescription());
                newPnDocVersion.setDateModified(currentDate);
                newPnDocVersion.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newPnDocVersion.setDocComment(vTmpDocRec.getDocComment());
                newPnDocVersion.setCrc(currentDate);
                newPnDocVersion.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                ServiceFactory.getInstance().getPnDocVersionService().save(newPnDocVersion);
                /*
                update pn_document set
             current_version_id = vVersionId,
             doc_status_id = vTmpDocRec.doc_status_id
         where doc_id = vDocId;
                */
                vDocProperties.setCurrentVersionId(vVersionId);
                vDocProperties.setDocStatusId(vTmpDocRec.getDocStatusId());
                ServiceFactory.getInstance().getPnDocumentService().update(vDocProperties);
                /*
                insert into pn_doc_content_element (
              doc_content_id,
              doc_format_id,
              record_status,
              file_size,
              file_handle,
              repository_id)
          values (
              vContentId,
              vTmpDocRec.doc_format_id,
              G_ACTIVE_RECORD_STATUS,
              vTmpDocRec.file_size,
              vTmpDocRec.file_handle,
              vTmpDocRec.repository_id
          );
            */
                PnDocContentElement newPnDocContentElement = new PnDocContentElement();
                newPnDocContentElement.setDocContentId(vContentId);
                newPnDocContentElement.setDocFormatId(vTmpDocRec.getDocFormatId());
                newPnDocContentElement.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                newPnDocContentElement.setFileSize(vTmpDocRec.getFileSize());
                newPnDocContentElement.setFileHandle(vTmpDocRec.getFileHandle());
                newPnDocContentElement.setPnDocRepositoryBase(ServiceFactory.getInstance().getPnDocRepositoryBaseService().findByPK(vTmpDocRec.getRepositoryId()));
                ServiceFactory.getInstance().getPnDocContentElementService().saveObject(newPnDocContentElement);
                /*
                    insert into pn_doc_version_has_content (
                 doc_id,
                 doc_version_id,
                 doc_content_id)
             values (
                 vDocId,
                 vVersionId,
                 vContentId
             );
                */
                PnDocVersionHasContent newPnDocVersionHasContent = new PnDocVersionHasContent();
                PnDocVersionHasContentPK pk = new PnDocVersionHasContentPK();
                newPnDocVersionHasContent.setComp_id(pk);
                newPnDocVersionHasContent.setDocId(vDocId);
                pk.setDocVersionId(vVersionId);
                pk.setDocContentId(vContentId);
                ServiceFactory.getInstance().getPnDocVersionHasContentService().saveObvect(newPnDocVersionHasContent);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public int getCountOpen(final Integer inSpaceId) {

        int result = 0;
        String methodName = "getCountOpen";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        /*
            select count(doc_id) into v_count from pn_doc_by_space_view
        where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                                where space_id = inSpaceId)
        and doc_status_id not in (400);
        */
        try {
            final List<PnSpaceHasDocSpace> allPnSpaceHasDocSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {

                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getComp_id().getSpaceId().equals(inSpaceId);
                }
            });

            List<PnDocBySpaceView> views = ServiceFactory.getInstance().getPnDocBySpaceViewService().findByFilter(new IPnDocBySpaceViewFilter() {

                public boolean isSuitable(PnDocBySpaceView pnDocBySpaceView) {
                    boolean result = false;
                    for (int i = 0, n = allPnSpaceHasDocSpaces.size(); i < n; i++) {
                        PnSpaceHasDocSpace currentSpaceHasDocSpace = allPnSpaceHasDocSpaces.get(i);
                        result = ((pnDocBySpaceView.getDocSpaceId().equals(currentSpaceHasDocSpace.getComp_id().getDocSpaceId())) && (pnDocBySpaceView.getDocStatusId() != 400));
                        if (result)
                            break;
                    }
                    return result;
                }
            });
            result = views.size();
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public int getCountClosed(final Integer inSpaceId) {

        int result = 0;
        String methodName = "getCountClosed";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);

        /*
            select count(doc_id) into v_count from pn_doc_by_space_view
        where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                                where space_id = inSpaceId)
        and doc_status_id = 400;
        */
        try {
            final List<PnSpaceHasDocSpace> allPnSpaceHasDocSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {

                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getComp_id().getSpaceId().equals(inSpaceId);
                }
            });

            List<PnDocBySpaceView> views = ServiceFactory.getInstance().getPnDocBySpaceViewService().findByFilter(new IPnDocBySpaceViewFilter() {

                public boolean isSuitable(PnDocBySpaceView pnDocBySpaceView) {
                    boolean result = false;
                    for (int i = 0, n = allPnSpaceHasDocSpaces.size(); i < n; i++) {
                        PnSpaceHasDocSpace currentSpaceHasDocSpace = allPnSpaceHasDocSpaces.get(i);
                        result = ((pnDocBySpaceView.getDocSpaceId().equals(currentSpaceHasDocSpace.getComp_id().getDocSpaceId())) && (pnDocBySpaceView.getDocStatusId() == 400));
                        if (result)
                            break;
                    }
                    return result;
                }
            });
            result = views.size();
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public int getCountClosedLastWeek(final Integer inSpaceId) {
        int result = 0;
        String methodName = "getCountClosedLastWeek";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);

        /*
            select count(doc_id) into v_count from pn_doc_by_space_view
        where doc_space_id = (select doc_space_id from pn_space_has_doc_space
                                where space_id = inSpaceId)
        and doc_status_id = 400;
        */
        try {
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            final Date newDate = calendar.getTime();
            final List<PnSpaceHasDocSpace> allPnSpaceHasDocSpaces = ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().findByFilter(new IPnSpaceHasDocSpaceFilter() {

                public boolean isSuitable(PnSpaceHasDocSpace object) {
                    return object.getComp_id().getSpaceId().equals(inSpaceId);
                }
            });

            List<PnDocBySpaceView> views = ServiceFactory.getInstance().getPnDocBySpaceViewService().findByFilter(new IPnDocBySpaceViewFilter() {

                public boolean isSuitable(PnDocBySpaceView pnDocBySpaceView) {
                    boolean result = false;
                    for (int i = 0, n = allPnSpaceHasDocSpaces.size(); i < n; i++) {
                        PnSpaceHasDocSpace currentSpaceHasDocSpace = allPnSpaceHasDocSpaces.get(i);
                        result = ((pnDocBySpaceView.getDocSpaceId().equals(currentSpaceHasDocSpace.getComp_id().getDocSpaceId())) && (pnDocBySpaceView.getDocStatusId() == 400) && (pnDocBySpaceView.getDateModified().after(newDate)));
                        if (result)
                            break;
                    }
                    return result;
                }
            });
            result = views.size();
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
        return result;
    }

    public void createDoc(String tmpDoc, String containerId, String whoami, String spaceId, Integer ignoreNameConstraint, String discussionGroupDescription) {
        String methodName = "createDoc";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);

        try {
            Integer tmpDocId = new Integer(tmpDoc);
            Integer vContainerId = new Integer(containerId);
            Integer vWhoami = new Integer(whoami);
            Integer vSpaceId = new Integer(spaceId);
            Integer vDocumentId = tmpDocId;
            Integer gInitialVersion = new Integer(1);
            String gInitialLabel = "Initial Version";
            String gAction = "create";
            String gActionComment = "Created new document: ";
            /*
                -- SELECT pn_object_sequence.nextval INTO vDocumentId FROM dual;
                SELECT pn_object_sequence.nextval INTO vVersionId FROM dual;
                SELECT pn_object_sequence.nextval INTO vContentId FROM dual;
                SELECT pn_object_sequence.nextval INTO vHistoryId FROM dual;
                */
            IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
            Integer vVersionId = objectService.generateNewId();
            Integer vContentId = objectService.generateNewId();
            Integer vHistoryId = objectService.generateNewId();
            PnTmpDocument vTmpDocRec = ServiceFactory.getInstance().getPnTmpDocumentService().getByPk(tmpDocId);
            /*
            IF ((ignoreNameConstraint = 1) OR
                (document.f_verify_unique_name (vTmpDocRec.doc_name, vContainerId, G_DOCUMENT_OBJECT_TYPE)))
                THEN*/
            if ((ignoreNameConstraint.intValue() == 1) || (fVerifyUniqueName(vTmpDocRec.getDocName(), vContainerId, G_DOCUMENT_OBJECT_TYPE))) {
                /*
                 insert into pn_object (
               object_id,
               date_created,
               object_type,
               created_by,
               record_status )
           values (
               vDocumentId,
               SYSDATE,
               G_DOCUMENT_OBJECT_TYPE,
               vWhoami,
               G_ACTIVE_RECORD_STATUS);
                */
                Date currentDate = new Date();
                PnObject newPnObject = new PnObject();
                newPnObject.setObjectId(vDocumentId);
                newPnObject.setDateCreated(currentDate);
                newPnObject.setPnObjectType(ServiceFactory.getInstance().getPnObjectTypeService().getObjectType(G_DOCUMENT_VERSION_OBJECT_TYPE));
                newPnObject.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newPnObject.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                objectService.saveObject(newPnObject);
                /*
                    -- insert the document object
              insert into pn_document
              (
                  doc_id,
                  doc_name,
                  doc_description,
                  doc_type_id,
                  doc_status_id,
                  current_version_id,
                  record_status,
                  crc
              ) values
              (
                  vDocumentId,
                  vTmpDocRec.doc_name,
                  vTmpDocRec.doc_description,
                  vTmpDocRec.doc_type_id,
                  vTmpDocRec.doc_status_id,
                  vVersionId,
                  G_ACTIVE_RECORD_STATUS,
                  SYSDATE
              );
                */
                PnDocument newDocument = new PnDocument();
                newDocument.setDocId(vDocumentId);
                newDocument.setDocName(vTmpDocRec.getDocName());
                newDocument.setDocDescription(vTmpDocRec.getDocDescription());
                newDocument.setPnDocType(ServiceFactory.getInstance().getPnDocTypeService().findById(vTmpDocRec.getDocTypeId()));
                newDocument.setDocStatusId(vTmpDocRec.getDocStatusId());
                newDocument.setCurrentVersionId(vVersionId);
                newDocument.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                newDocument.setCrc(currentDate);
                ServiceFactory.getInstance().getPnDocumentService().save(newDocument);

                ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(vDocumentId, vContainerId, G_DOCUMENT_OBJECT_TYPE, vSpaceId, vWhoami);

                /*
             -- register the "document_version" object
             insert into pn_object (
                 object_id,
                 date_created,
                 object_type,
                 created_by,
                 record_status )
             values (
                 vVersionId,
                 SYSDATE,
                 G_DOCUMENT_VERSION_OBJECT_TYPE,
                 vWhoami,
                 G_ACTIVE_RECORD_STATUS);
                */
                PnObject versionObject = new PnObject();
                versionObject.setObjectId(vVersionId);
                versionObject.setDateCreated(currentDate);
                versionObject.setPnObjectType(ServiceFactory.getInstance().getPnObjectTypeService().getObjectType(G_DOCUMENT_VERSION_OBJECT_TYPE));
                versionObject.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                versionObject.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                objectService.saveObject(versionObject);
                /*

                -- create a document version
          insert into pn_doc_version (
              doc_id,
              doc_version_id,
              doc_version_num,
              doc_version_label,
              doc_version_name,
              source_file_name,
              short_file_name,
              doc_author_id,
              doc_description,
              date_modified,
              modified_by_id,
              doc_comment,
              crc,
              record_status)
          values (
              vDocumentId,
              vVersionId,
              gInitialVersion,
              gInitialLabel,
              vTmpDocRec.doc_name,
              vTmpDocRec.source_file_name,
              vTmpDocRec.short_file_name,
              vTmpDocRec.doc_author_id,
              vTmpDocRec.doc_description,
              SYSDATE,
              vWhoami,
              vTmpDocRec.doc_comment,
              SYSDATE,
              G_ACTIVE_RECORD_STATUS
          );
          */
                PnDocVersion newPnDocVersion = new PnDocVersion();
                newPnDocVersion.setPnDocument(ServiceFactory.getInstance().getPnDocumentService().findByPk(vDocumentId));
                newPnDocVersion.setDocVersionId(vVersionId);
                newPnDocVersion.setDocVersionName(gInitialLabel);
                newPnDocVersion.setDocVersionNum(gInitialVersion);
                newPnDocVersion.setDocVersionName(vTmpDocRec.getDocVersionName());
                newPnDocVersion.setSourceFileName(vTmpDocRec.getSourceFileName());
                newPnDocVersion.setShortFileName(vTmpDocRec.getShortFileName());
                newPnDocVersion.setPnPersonByDocAuthorId(ServiceFactory.getInstance().getPnPersonService().getPerson(vTmpDocRec.getDocAuthorId()));
                newPnDocVersion.setDocDescription(vTmpDocRec.getDocDescription());
                newPnDocVersion.setDateModified(currentDate);
                newPnDocVersion.setPnPersonByModifiedById(ServiceFactory.getInstance().getPnPersonService().getPerson(vWhoami));
                newPnDocVersion.setDocComment(vTmpDocRec.getDocComment());
                newPnDocVersion.setCrc(currentDate);
                newPnDocVersion.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                ServiceFactory.getInstance().getPnDocVersionService().save(newPnDocVersion);
                /*
                 -- create the content element(s) for the document version

                insert into pn_doc_content_element (
              doc_content_id,
              doc_format_id,
              record_status,
              file_size,
              file_handle,
              repository_id)
          values (
              vContentId,
              vTmpDocRec.doc_format_id,
              G_ACTIVE_RECORD_STATUS,
              vTmpDocRec.file_size,
              vTmpDocRec.file_handle,
              vTmpDocRec.repository_id
          );
              */
                PnDocContentElement contentElement = new PnDocContentElement();
                contentElement.setDocContentId(vContentId);
                contentElement.setDocFormatId(vTmpDocRec.getDocFormatId());
                contentElement.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                contentElement.setFileSize(vTmpDocRec.getFileSize());
                contentElement.setFileHandle(vTmpDocRec.getFileHandle());
                contentElement.setPnDocRepositoryBase(ServiceFactory.getInstance().getPnDocRepositoryBaseService().findByPK(vTmpDocRec.getRepositoryId()));
                ServiceFactory.getInstance().getPnDocContentElementService().saveObject(contentElement);
                /*
                --link
                the content
                to the
                doc_version
                insert into
                pn_doc_version_has_content(
                        doc_id,
                        doc_version_id,
                        doc_content_id)
                values(
                        vDocumentId,
                        vVersionId,
                        vContentId
                );
                */

                PnDocVersionHasContent newPnDocVersionHasContent = new PnDocVersionHasContent();
                PnDocVersionHasContentPK pk = new PnDocVersionHasContentPK();
                newPnDocVersionHasContent.setComp_id(pk);
                pk.setDocVersionId(vVersionId);
                pk.setDocContentId(vContentId);
                newPnDocVersionHasContent.setDocId(vDocumentId);
                ServiceFactory.getInstance().getPnDocVersionHasContentService().saveObvect(newPnDocVersionHasContent);
                /*
                --insert
                the doc
                into the
                correct container
                insert into
                pn_doc_container_has_object(
                        doc_container_id,
                        object_id)
                values(
                        vContainerId,
                        vDocumentId
                );
                */
                PnDocContainerHasObject newPnDocContainerHasObject = new PnDocContainerHasObject();
                PnDocContainerHasObjectPK pkDocContainerHasObjectPK = new PnDocContainerHasObjectPK();
                newPnDocContainerHasObject.setComp_id(pkDocContainerHasObjectPK);
                pkDocContainerHasObjectPK.setDocContainerId(vContainerId);
                pkDocContainerHasObjectPK.setObjectId(vDocumentId);
                ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(newPnDocContainerHasObject);
                /*
                DISCUSSION.createDiscussionGroup(
            NULL,
            vSpaceId,
            vDocumentId,
            vTmpDocRec.doc_name,
            vWhoami,
            discussionGroupDescription ,
            0, -- do not create a welcome message
            NULL,
            v_discussion_group_id,
            v_welcome_message_post_clob,
            v_discussion_grp_charter_clob);
               */
                CreateDiscussionGroupClass createDiscussionGroupClass = ServiceFactory.getInstance().getDiscussionService().createDiscussionGroup(null, vSpaceId.toString(), vDocumentId.toString(),
                        vTmpDocRec.getDocName(), vWhoami, discussionGroupDescription, new Integer(0), null);
                /*
                -- apply the documents permissions to the discussion group
              SECURITY.APPLY_DOCUMENT_PERMISSIONS(
                v_discussion_group_id,
                vDocumentId,
                'discussion_group',
                vSpaceId,
                vWhoami);
                }
                */
                ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(createDiscussionGroupClass.getDiscussionGroupId(),
                        vDocumentId, "discussion_group", vSpaceId, vWhoami);
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }
    }

    public Integer copyDocument(Integer fromDocumentId, Integer toContainerId, Integer creatorId, String discussionGroupDescription) {
        Date vTimestamp = new Date();
        Integer result = null;
        String methodName = "copyDocument";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);

        /*
        select *
             into v_doc_record
             from pn_document
            where doc_id = fromDocumentId;
        */
        try {
            final PnDocument vDocRecord = ServiceFactory.getInstance().getPnDocumentService().findByPk(fromDocumentId);
            /*
             if (document.f_verify_unique_name (
                     vDocRecord.doc_name,
                     toContainerId,
                     g_document_object_type
                 )
                ) then
                */
            if (fVerifyUniqueName(vDocRecord.getDocName(), toContainerId, G_DOCUMENT_OBJECT_TYPE)) {
                /*
                vNewDocId := base.create_object (
                                            g_document_object_type,
                                            creatorId,
                                            g_active_record_status
                                        );
                        vNewVersionId := base.create_object (
                                                g_document_version_object_type,
                                                creatorId,
                                                g_active_record_status
                                            );
                        select pn_object_sequence.nextval
                          into vNewContentId
                          from dual;
                */
                Integer vNewDocId = ServiceFactory.getInstance().getBaseService().createObject(G_DOCUMENT_OBJECT_TYPE, creatorId,
                        G_ACTIVE_RECORD_STATUS);
                Integer vNewVersionId = ServiceFactory.getInstance().getBaseService().createObject(G_DOCUMENT_VERSION_OBJECT_TYPE, creatorId,
                        G_ACTIVE_RECORD_STATUS);
                Integer vNewContentId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
                /*
       insert into pn_document
                           (doc_id, doc_name, doc_type_id, doc_description,
                            current_version_id, doc_status_id, crc, record_status)
                    values (vNewDocId, vDocRecord.doc_name,
                            vDocRecord.doc_type_id, vDocRecord.doc_description,
                            vNewVersionId, vDocRecord.doc_status_id, vTimestamp,
                            g_active_record_status);
                */
                PnDocument newPnDocument = new PnDocument();
                newPnDocument.setDocId(vNewDocId);
                newPnDocument.setDocName(vDocRecord.getDocName());
                newPnDocument.setPnDocType(vDocRecord.getPnDocType());
                newPnDocument.setDocDescription(vDocRecord.getDocDescription());
                newPnDocument.setCurrentVersionId(vNewVersionId);
                newPnDocument.setDocStatusId(vDocRecord.getDocStatusId());
                newPnDocument.setCrc(vTimestamp);
                newPnDocument.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                ServiceFactory.getInstance().getPnDocumentService().save(newPnDocument);
                /*
                        -- apply default object permissions for the document
                        security.applyDocumentPermissions (
                            vNewDocId,
                            toContainerId,
                            g_document_object_type,
                            get_space_for_id (vDocRecord.doc_id),
                            creatorId
                        );
                        */
                ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(vNewDocId,
                        toContainerId, G_DOCUMENT_OBJECT_TYPE, getDocSpaceForId(vDocRecord.getDocId()),
                        creatorId);
                /*
                select *
                          into vVersionRec
                          from pn_doc_version
                         where doc_version_id = vDocRecord.current_version_id;
                         */
                PnDocVersion vVersionRec = ServiceFactory.getInstance().getPnDocVersionService().findByPk(vDocRecord.getCurrentVersionId());
                /*
                insert into pn_doc_version
                        (doc_version_id, doc_id, doc_version_name, source_file_name,
                         doc_description, date_modified, modified_by_id,
                         is_checked_out, checked_out_by_id, date_checked_out,
                         doc_comment, doc_version_num, doc_version_label,
                         checkout_due, doc_author_id, short_file_name, crc,
                         record_status)
                 values (vNewVersionId, vNewDocId,
                         vVersionRec.doc_version_name,
                         vVersionRec.source_file_name,
                         vVersionRec.doc_description, vVersionRec.date_modified,
                         vVersionRec.modified_by_id, 0, -- Will not carry checked out documents
                                                         null, -- see above
                                                              null,
                         'Copied Document', 1, -- will renumber as first version for new document
                                              null, null, creatorId,
                         vVersionRec.short_file_name, vTimestamp,
                         g_active_record_status);
                  */
                PnDocVersion newPnDocVersion = new PnDocVersion();
                newPnDocVersion.setDocVersionId(vNewVersionId);
                newPnDocVersion.setPnDocument(ServiceFactory.getInstance().getPnDocumentService().findByPk(vNewDocId));
                newPnDocVersion.setDocVersionName(vVersionRec.getDocVersionName());
                newPnDocVersion.setSourceFileName(vVersionRec.getSourceFileName());
                newPnDocVersion.setDocDescription(vVersionRec.getDocDescription());
                newPnDocVersion.setDateModified(vVersionRec.getDateModified());
                newPnDocVersion.setPnPersonByModifiedById(vVersionRec.getPnPersonByModifiedById());
                newPnDocVersion.setIsCheckedOut(0);
                newPnDocVersion.setPnPersonByCheckedOutById(null);
                newPnDocVersion.setDateCheckedOut(null);
                newPnDocVersion.setDocComment("Copied Document");
                newPnDocVersion.setDocVersionNum(new Integer(1));
                newPnDocVersion.setDocVersionLabel(null);
                newPnDocVersion.setCheckoutDue(null);
                newPnDocVersion.setPnPersonByDocAuthorId(ServiceFactory.getInstance().getPnPersonService().getPerson(creatorId));
                newPnDocVersion.setShortFileName(vVersionRec.getShortFileName());
                newPnDocVersion.setCrc(vTimestamp);
                newPnDocVersion.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                ServiceFactory.getInstance().getPnDocVersionService().save(newPnDocVersion);

                /*
    select *
    into vContentRec
    from pn_doc_content_element
    where doc_content_id = (select doc_content_id
               from pn_doc_version_has_content
              where doc_version_id =
                            vDocRecord.current_version_id
                and doc_id = vDocRecord.doc_id);
                */
                List<PnDocVersionHasContent> all = ServiceFactory.getInstance().getPnDocVersionHasContentService().findByFilter(new IPnDocVersionHasContentFilter() {

                    public boolean isSuitable(PnDocVersionHasContent object) {
                        return ((object.getPnDocVersion().getDocVersionId().equals(vDocRecord.getCurrentVersionId())) &&
                                (object.getDocId().equals(vDocRecord.getDocId())));
                    }
                });
                PnDocVersionHasContent tempPnDocVersionHasContent = all.get(0);
                PnDocContentElement vContentRec = ServiceFactory.getInstance().getPnDocContentElementService().findByPK(new Integer(tempPnDocVersionHasContent.getComp_id().getDocContentId()));
                /*
                -- create the content element(s) for the document version
            insert into pn_doc_content_element
                        (doc_content_id, doc_format_id, record_status, file_size,
                         file_handle, repository_id)
                 values (vNewContentId, vContentRec.doc_format_id,
                         g_active_record_status, vContentRec.file_size,
                         vContentRec.file_handle,
                         DOCUMENT.getNextRepositoryBaseId);
              */
                PnDocContentElement newPnDocContentElement = new PnDocContentElement();
                newPnDocContentElement.setDocContentId(vNewContentId);
                newPnDocContentElement.setDocFormatId(vContentRec.getDocFormatId());
                newPnDocContentElement.setRecordStatus(G_ACTIVE_RECORD_STATUS);
                newPnDocContentElement.setFileSize(vContentRec.getFileSize());
                newPnDocContentElement.setFileHandle(vContentRec.getFileHandle());
                newPnDocContentElement.setPnDocRepositoryBase(ServiceFactory.getInstance().getPnDocRepositoryBaseService().findByPK(getNextRepositoryBaseId()));
                ServiceFactory.getInstance().getPnDocContentElementService().saveObject(newPnDocContentElement);
                PnDocVersionHasContent newPnDocVersionHasContent = new PnDocVersionHasContent();
                PnDocVersionHasContentPK pnDocVersionHasContentPK = new PnDocVersionHasContentPK();
                newPnDocVersionHasContent.setComp_id(pnDocVersionHasContentPK);
                pnDocVersionHasContentPK.setDocContentId(vNewContentId);
                pnDocVersionHasContentPK.setDocVersionId(vNewVersionId);
                newPnDocVersionHasContent.setDocId(vNewDocId);
                ServiceFactory.getInstance().getPnDocVersionHasContentService().saveObvect(newPnDocVersionHasContent);

                /*
            -- link the content to the doc_version
            insert into pn_doc_version_has_content
                        (doc_id, doc_version_id, doc_content_id)
                 values (vNewDocId, vNewVersionId, vNewContentId);
               */

                PnDocContainerHasObject newPnDocContainerHasObject = new PnDocContainerHasObject();
                PnDocContainerHasObjectPK pnDocContainerHasObjectPK = new PnDocContainerHasObjectPK();
                newPnDocContainerHasObject.setComp_id(pnDocContainerHasObjectPK);
                pnDocContainerHasObjectPK.setDocContainerId(toContainerId);
                pnDocContainerHasObjectPK.setObjectId(vNewDocId);
                ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(newPnDocContainerHasObject);
                /*
                discussion.create_discussion_group (
                null,
                get_space_for_id (vDocRecord.doc_id),
                vNewDocId,
                vDocRecord.doc_name,
                creatorId,
                discussionGroupDescription ,
                0, -- do not creaet a welcome message
                NULL,
                v_discussion_group_id,
                v_welcome_message_post_clob,
                v_discussion_grp_charter_clob
            );
            */
                CreateDiscussionGroupClass createDiscussionGroupClass = ServiceFactory.getInstance().getDiscussionService().createDiscussionGroup(null,
                        getSpaceForId(vDocRecord.getDocId()).toString(), vNewDocId.toString(),
                        vDocRecord.getDocName(),
                        creatorId,
                        discussionGroupDescription, new Integer(0),
                        null);

                /*
                -- apply the documents permissions to the discussion group
            security.applyDocumentPermissions (
                v_discussion_group_id,
                vNewDocId,
                'discussion_group',
                get_space_for_id (vDocRecord.doc_id),
                creatorId
            ); */

                ServiceFactory.getInstance().getSecurityService().applyDocumentPermissions(createDiscussionGroupClass.getDiscussionGroupId(),
                        vNewDocId, "discussion_group", getSpaceForId(new Integer(vDocRecord.getDocId())), creatorId);
                result = createDiscussionGroupClass.getDiscussionGroupId();

                /*
                -- insert the doc into the correct container
                insert into pn_doc_container_has_object
                            (doc_container_id, object_id)
                     values (toContainerId, vNewDocId);
                }
                */
            }
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(methodIsFinished);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long timeExecution = endTime - startTime;
            String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
            getLogger().debug(messageTimeExecution);
            getLogger().debug(exceptionInMethod);
            e.printStackTrace();
        }

        return result;
    }

    public Integer getNextRepositoryBaseId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}