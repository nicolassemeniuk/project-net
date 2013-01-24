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
package net.project.hibernate.service;

import java.util.Date;

public interface IDocumentService {


    public Integer createDocumentSpace(Integer spaceId, Integer personId);

    public Integer createBookmark(String name, String description, String url, Integer containerId, Integer statusId, Integer ownerId, String comments, Integer createdById);

    public Integer getSpaceForContainerId(final Integer containerId);

    public Integer getContainerForObjectId(final Integer objectId);

    public Integer getContainerForDocId(Integer docId);

    public Integer getDocSpaceForId(Integer id);

    public Integer getSpaceForId(Integer id);

    public int getCount(Integer spaceId);

    public void addObjectToContainer(String inObjectId, String inContainerId);

    public void modifyBookmark(Integer id,
                                String name,
                                String description,
                                String url,
                                Integer containerId,
                                Integer statusId,
                                Integer ownerId,
                                String comments,
                                Integer createdById);

    public boolean fVerifyCrc(Date origCrc, Date currentCrc);

    public Integer hasLinks(final Integer id);

    public void removeObjectFromContainer(final String inObjectId, final String inContainerId);

    public boolean fVerifyUniqueName(final String newName, final Integer containerId, final String objectType);

    public Integer isParentContainer(final String parentContainerId, final String childContainerId);

    public void moveObject(final String inObjectId, final String inContainerId);

    public Integer modifyContainer(String parentId, String myContainerId, String folderName, String description, String whoami, String isHidden, Date origCrc);

    public void logEvent(String docId, String whoami, String action, String actionName, String notes);

    public Integer copyContainer(final Integer fromContainerId, final Integer newParentContainerId, final Integer creatorId);

    public void modifyProperties(String tmpDoc, String whoami, Date origCrc);

    public void removeContainer(String containerId, String recordStatus, String whoami);

    public void removeDoc(String docId, Date lastModified, String recordStatus, String whoami);

    public Integer createContainer(String inContainerId, String whoami, String folderName, String description, String spaceId, String isHidden);

    public void checkOut(String docId, String whoami, Date ckoDue, String notes, Date origCrc);

    public void undoCheckOut(String docId, String whoami, Date origCrc);

    public Integer copyBookmark(Integer fromBookmarkId, Integer toContainerId, Integer creatorId);

    public void checkIn(String tmpDoc, String whoami, String spaceId, Date origCrc);

    public int getCountOpen(final Integer inSpaceId);

    public int getCountClosed(final Integer inSpaceId);

    public int getCountClosedLastWeek(final Integer inSpaceId);

    public void createDoc(String tmpDoc, String containerId, String whoami, String spaceId, Integer ignoreNameConstraint, String discussionGroupDescription);

    public Integer copyDocument(
            Integer fromDocumentId,
            Integer toContainerId,
            Integer creatorId,
            String discussionGroupDescription
    );

    public Integer getNextRepositoryBaseId();

    public static final String G_DOCUMENT_OBJECT_TYPE = "document";
    public static final String G_DOCUMENT_VERSION_OBJECT_TYPE = "document_version";
    public static final String G_CONTAINER_OBJECT_TYPE = "doc_container";
    public static final String G_BOOKMARK_OBJECT_TYPE = "bookmark";
    public static final String G_ACTIVE_RECORD_STATUS = "A";
}
