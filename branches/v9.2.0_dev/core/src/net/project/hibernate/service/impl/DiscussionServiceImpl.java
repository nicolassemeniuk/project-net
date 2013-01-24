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

import java.sql.Clob;
import java.util.Date;

import net.project.hibernate.model.PnDiscussionGroup;
import net.project.hibernate.model.PnObjectHasDiscussion;
import net.project.hibernate.model.PnObjectHasDiscussionPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPost;
import net.project.hibernate.model.PnPostBodyClob;
import net.project.hibernate.model.PnPostPK;
import net.project.hibernate.model.PnPostReader;
import net.project.hibernate.model.PnPostReaderPK;
import net.project.hibernate.service.IDiscussionService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.utilities.CreateDiscussionGroupClass;
import net.project.hibernate.service.impl.utilities.CreatePostClass;
import net.project.hibernate.service.impl.utilities.DebugMethodUtilites;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="discussionService")
public class DiscussionServiceImpl implements IDiscussionService {
    private Logger logger;

    public Logger getLogger() {
        if (logger == null)
            logger = Logger.getLogger(getClass());
        return logger;
    }
    public CreatePostClass createPost(Integer spaceId, Integer discussionGroupId, Integer parentId, Integer personId, String subject, Integer urgencyId, Integer postBodyId) {
        CreatePostClass postObject = new CreatePostClass();
        String methodName = "createPost";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            Integer oPostId = ServiceFactory.getInstance().getBaseService().createObject("post", personId, "A");
            ServiceFactory.getInstance().getSecurityService().createSecurityPermissions(oPostId, "post", spaceId, personId);
            Date vCurrentDate = new Date();
            /*
                -- Create the object
                oPostId := BASE.CREATE_OBJECT('post', personId, 'A');

                -- Apply default security permissions
                SECURITY.CREATE_SECURITY_PERMISSIONS(oPostId, 'post', spaceId, personId);
                */
            /*
            --Create the post
                    INSERT INTO pn_post
                        (person_id, parent_id, discussion_group_id, post_id, subject, urgency_id, post_body_id, date_posted, record_status)
                    VALUES
                        (personId, parentId, discussionGroupId, oPostId, subject, urgencyId, postBodyId, vCurrentDate, 'A');
                */
            PnPost post = new PnPost();
            PnPostPK pk = new PnPostPK();
            post.setComp_id(pk);
            PnPerson person = ServiceFactory.getInstance().getPnPersonService().getPerson(personId);
            post.setPnPerson(person);
            post.setParentId(parentId);
            PnDiscussionGroup group = ServiceFactory.getInstance().getPnDiscussionGroupService().getDiscussionGroup(discussionGroupId);
            post.setPnDiscussionGroup(group);
            pk.setPostId(oPostId);
            post.setSubject(subject);
            post.setUrgencyId(urgencyId);
            post.setPnPostBodyClob(ServiceFactory.getInstance().getPnPostBodyClobService().findByPK(postBodyId));
            post.setDatePosted(vCurrentDate);
            post.setRecordStatus("");
            ServiceFactory.getInstance().getPnPostService().save(post);
            /*
            -- We say that the originator of the post has also read the post
            INSERT INTO pn_post_reader
                (person_id, discussion_group_id, post_id, date_read)
            VALUES
                (personId, discussionGroupId, oPostId, vCurrentDate);
                */
            PnPostReader reader = new PnPostReader();
            PnPostReaderPK pkPostReader = new PnPostReaderPK();
            reader.setComp_id(pkPostReader);
            pkPostReader.setPersonId(personId);
            pkPostReader.setDiscussionGroupId(discussionGroupId);
            pkPostReader.setPostId(oPostId);
            reader.setDateRead(vCurrentDate);
            ServiceFactory.getInstance().getPnPostReaderService().save(reader);

            postObject.setPostId(oPostId);
            postObject.setPostDate(vCurrentDate);
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
        return postObject;
    }

    public CreateDiscussionGroupClass createDiscussionGroup(Integer discussionGroupId, String spaceId, String objectId, String discussionGroupName, Integer personId, String discussionGroupDescription, Integer createWelcomePost, String welcomeMessageSubject) {
        CreateDiscussionGroupClass result = new CreateDiscussionGroupClass();
        Integer oDiscussionGroupId = null;
        Clob oDiscussionGrpCharterClob = null;
        Clob oWelcomeMessagePostClob = null;
        String methodName = "createDiscussionGroup";
        String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
        String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
        String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
        long startTime = System.currentTimeMillis();
        getLogger().debug(methodIsStart);
        try {
            if (discussionGroupId == null) {
                /*
                  oDiscussionGroupId := BASE.CREATE_OBJECT('discussion_group', personId, 'A');
              -- Apply default security permissions
              SECURITY.CREATE_SECURITY_PERMISSIONS(oDiscussionGroupId, 'discussion_group', spaceId, personId);
                */
                oDiscussionGroupId = ServiceFactory.getInstance().getBaseService().createObject("discussion_group", personId, "A");
                ServiceFactory.getInstance().getSecurityService().createSecurityPermissions(oDiscussionGroupId, "discussion_group", new Integer(spaceId), personId);
                /*
                -- Create the discussion_group
            -- We return the empty_clob into the clob locater output parameter
            INSERT INTO pn_discussion_group
                (discussion_group_name, discussion_group_id, discussion_group_description, discussion_group_charter_clob, record_status)
            VALUES
                (discussionGroupName, oDiscussionGroupId, discussionGroupDescription, empty_clob(), 'A')
            RETURNING discussion_group_charter_clob into oDiscussionGrpCharterClob;
            */
                PnDiscussionGroup dsGroup = new PnDiscussionGroup();
                dsGroup.setDiscussionGroupName(discussionGroupName);
                dsGroup.setDiscussionGroupId(oDiscussionGroupId.intValue());
                dsGroup.setDiscussionGroupDescription(discussionGroupDescription);
                dsGroup.setDiscussionGroupCharterClob(null);
                dsGroup.setRecordStatus("A");
                ServiceFactory.getInstance().getPnDiscussionGroupService().saveObject(dsGroup);
                PnObjectHasDiscussion objectHasDiscussion = new PnObjectHasDiscussion();
                PnObjectHasDiscussionPK pk = new PnObjectHasDiscussionPK();
                objectHasDiscussion.setComp_id(pk);
                pk.setDiscussionGroupId(oDiscussionGroupId.intValue());
                pk.setObjectId(new Integer(objectId).intValue());
                ServiceFactory.getInstance().getPnObjectHasDiscussionService().save(objectHasDiscussion);
                /*
                if (createWelcomePost > 0) THEN

                v_post_body_id := BASE.CREATE_OBJECT('post', personId, 'A');

                insert into pn_post_body_clob
                    (object_id , clob_field)
                values
                    (v_post_body_id, empty_clob())
                returning clob_field into oWelcomeMessagePostClob;

                CREATE_POST(
                    spaceId,
                    oDiscussionGroupId,
                    null,
                    personId,
                    welcomeMessageSubject,
                    100,
                    v_post_body_id,
                    v_post_date,
                    v_post_id);
            END IF;
              */
                if (createWelcomePost.intValue() > 0) {
                    Integer v_post_body_id = ServiceFactory.getInstance().getBaseService().createObject("post", personId, "A");
                    PnPostBodyClob bodyClob = new PnPostBodyClob();
                    bodyClob.setObjectId(v_post_body_id.intValue());
                    bodyClob.setClobField(null);
                    ServiceFactory.getInstance().getPnPostBodyClobService().save(bodyClob);
                    oWelcomeMessagePostClob = bodyClob.getClobField();
                    CreatePostClass createPostObject = createPost(new Integer(spaceId), oDiscussionGroupId, null, personId, welcomeMessageSubject, new Integer(100), v_post_body_id);
                }
            } else {
                /*
                    -- discussion group already exists so just update
                oDiscussionGroupId := discussionGroupId;

                -- Return the CLOB locater into the OUT variable so it can
                -- be updated outside of this procedure
                -- Clears out the charter so that a subsequent write will overwrite
                -- all data
                UPDATE pn_discussion_group
                SET
                   discussion_group_name = discussionGroupName,
                   discussion_group_description = discussionGroupDescription,
                   discussion_group_charter_clob = empty_clob()
                WHERE
                   discussion_group_id = discussionGroupId
                RETURNING discussion_group_charter_clob into oDiscussionGrpCharterClob;
                */
                oDiscussionGroupId = discussionGroupId;
                PnDiscussionGroup group = ServiceFactory.getInstance().getPnDiscussionGroupService().getDiscussionGroup(discussionGroupId);
                if (group != null) {
                    group.setDiscussionGroupName(discussionGroupName);
                    group.setDiscussionGroupDescription(discussionGroupDescription);
                    group.setDiscussionGroupCharterClob(null);
                    ServiceFactory.getInstance().getPnDiscussionGroupService().saveObject(group);
                    oDiscussionGrpCharterClob = group.getDiscussionGroupCharterClob();
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
        result.setDiscussionGroupId(oDiscussionGroupId);
        result.setDiscussionGrpCharterClob(oDiscussionGrpCharterClob);
        result.setWelcomeMessagePostClob(oWelcomeMessagePostClob);
        return result;
    }
}
