package net.project.util;

import java.math.BigDecimal;
import java.sql.ResultSet;

import net.project.database.DBBean;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnAssignmentPK;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;
import net.project.hibernate.model.PnNews;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWikiPage;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

public class PerformanceQueries {

	public static PnBusinessSpace getBusinessSpaceById(int id) throws PersistenceException {
		PnBusinessSpace pnBusinessSpace = null;
		DBBean db = new DBBean();

		try {
			String sql = "SELECT BUSINESS_SPACE_ID, BUSINESS_ID, SPACE_TYPE, COMPLETE_PORTFOLIO_ID, RECORD_STATUS, INCLUDES_EVERYONE"
					+ " FROM PN_BUSINESS_SPACE bs WhERE bs.BUSINESS_SPACE_ID = " + id;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnBusinessSpace = new PnBusinessSpace();

				pnBusinessSpace.setBusinessSpaceId(rs.getInt("BUSINESS_SPACE_ID"));
				pnBusinessSpace.setPnBusiness(getPnBusiness(rs.getInt("BUSINESS_ID")));

				pnBusinessSpace.setSpaceType(rs.getInt("SPACE_TYPE"));
				pnBusinessSpace.setCompletePortfolioId(rs.getInt("COMPLETE_PORTFOLIO_ID"));

				pnBusinessSpace.setRecordStatus(rs.getString("RECORD_STATUS"));
				pnBusinessSpace.setIncludesEveryone(rs.getInt("INCLUDES_EVERYONE"));
			}

		} catch (Exception e) {
			System.out.println("Exception in getBusinessSpaceById : " + e);
			e.printStackTrace();

		} finally {
			db.release();
		}

		return pnBusinessSpace;
	}

	public static PnBusiness getPnBusiness(int id) {
		PnBusiness pnBusiness = null;
		DBBean db = new DBBean();

		try {
			String sql = "SELECT BUSINESS_ID, ADDRESS_ID, BUSINESS_NAME, BUSINESS_DESC, BUSINESS_TYPE, "
					+ "LOGO_IMAGE_ID, IS_LOCAL, REMOTE_HOST_ID, REMOTE_BUSINESS_ID, RECORD_STATUS, IS_MASTER, "
					+ "BUSINESS_CATEGORY_ID, BRAND_ID, BILLING_ACCOUNT_ID FROM PN_BUSINESS WHERE BUSINESS_ID = " + id;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnBusiness = new PnBusiness();

				pnBusiness.setBusinessId(rs.getInt("BUSINESS_ID"));
				pnBusiness.setPnAddress(null);
				pnBusiness.setBusinessName(rs.getString("BUSINESS_NAME"));
				pnBusiness.setBusinessDesc(rs.getString("BUSINESS_DESC"));

				pnBusiness.setBusinessType(rs.getString("BUSINESS_TYPE"));
				pnBusiness.setLogoImageId(rs.getInt("LOGO_IMAGE_ID"));
				pnBusiness.setIsLocal(rs.getInt("IS_LOCAL"));
				pnBusiness.setRemoteHostId(rs.getInt("REMOTE_HOST_ID"));
				pnBusiness.setRemoteBusinessId(rs.getInt("REMOTE_BUSINESS_ID"));

				pnBusiness.setRecordStatus(rs.getString("RECORD_STATUS"));
				pnBusiness.setIsMaster(rs.getInt("IS_MASTER"));
				pnBusiness.setBusinessCategoryId(rs.getInt("BUSINESS_CATEGORY_ID"));
				pnBusiness.setBrandId(rs.getInt("BRAND_ID"));

				pnBusiness.setBillingAccountId(rs.getInt("BILLING_ACCOUNT_ID"));
			}
		} catch (Exception e) {
			System.out.println("Exception in getBusinessSpaceById : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnBusiness;
	}

	public static PnWikiPage getWikiPage(int id) {
		PnWikiPage pnWikiPage = null;
		DBBean db = new DBBean();

		try {
			String sql = "SELECT WIKI_PAGE_ID, PAGE_NAME, CONTENT, EDIT_DATE, EDITED_BY, "
					+ "OWNER_OBJECT_ID, RECORD_STATUS, COMMENT_TEXT, PARENT_PAGE_ID, CREATED_BY, "
					+ "CREATED_DATE, ACCESS_LEVEL FROM PN_WIKI_PAGE WHERE WIKI_PAGE_ID=" + id;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnWikiPage = new PnWikiPage();
				pnWikiPage.setWikiPageId(rs.getInt("WIKI_PAGE_ID"));
				pnWikiPage.setPageName(rs.getString("PAGE_NAME"));
				pnWikiPage.setContent(rs.getString("CONTENT"));
				pnWikiPage.setEditDate(rs.getDate("EDIT_DATE"));
				//Edit By...
				Person editedBy = new Person();
				editedBy.setID(rs.getString("EDITED_BY"));
				editedBy.load();
				pnWikiPage.setRecordStatus(rs.getString("RECORD_STATUS"));
				pnWikiPage.setCommentText(rs.getString("COMMENT_TEXT"));

				//created by...
				Person createdBy = new Person();
				createdBy.setID(rs.getString("CREATED_BY"));
				createdBy.load();

				pnWikiPage.setCreatedDate(rs.getDate("CREATED_DATE"));
				pnWikiPage.setAccessLevel(rs.getInt("ACCESS_LEVEL"));
			}
		} catch (Exception e) {
			System.out.println("Exception in Wiki : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnWikiPage;
	}

	public static PnWeblog getPnWeblogById(int wikiPageId) {
		PnWeblog pnWeblog = null;
		DBBean db = new DBBean();

		try {
			String sql = "SELECT WEBLOG_ID, NAME, DESCRIPTION, PERSON_ID, ALLOW_COMMENTS, EMAIL_COMMENTS, EMAIL_FROM_ADDRESS, EMAIL_ADDRESS, "
					+ "LOCALE, TIMEZONE, IS_ENABLED, IS_ACTIVE, CREATED_DATE, DEFAULT_ALLOW_COMMENTS, DEFAULT_COMMENT_DAYS, SPACE_ID "
					+ "FROM PN_WEBLOG WHERE WEBLOG_ID=" + wikiPageId;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnWeblog = new PnWeblog();
				pnWeblog.setWeblogId(rs.getInt("WEBLOG_ID"));
				pnWeblog.setName(rs.getString("NAME"));
				pnWeblog.setDescription(rs.getString("DESCRIPTION"));
				pnWeblog.setPnPerson(null);

				pnWeblog.setAllowComments(rs.getInt("ALLOW_COMMENTS"));
				pnWeblog.setEmailComments(rs.getInt("EMAIL_COMMENTS"));
				pnWeblog.setEmailFromAddress(rs.getString("EMAIL_FROM_ADDRESS"));
				pnWeblog.setEmailAddress(rs.getString("EMAIL_ADDRESS"));

				pnWeblog.setLocale(rs.getString("LOCALE"));
				pnWeblog.setTimezone(rs.getString("TIMEZONE"));
				pnWeblog.setIsEnabled(rs.getInt("IS_ENABLED"));
				pnWeblog.setIsActive(rs.getInt("IS_ACTIVE"));

				pnWeblog.setCreatedDate(rs.getDate("CREATED_DATE"));
				pnWeblog.setDefaultAllowComments(rs.getInt("DEFAULT_ALLOW_COMMENTS"));
				pnWeblog.setDefaultCommentDays(rs.getInt("DEFAULT_COMMENT_DAYS"));
				pnWeblog.setSpaceId(rs.getInt("SPACE_ID"));
			}
		} catch (Exception e) {
			System.out.println("Exception in getPnWeblogById : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnWeblog;
	}

	public static PnAssignment getPnAssignmentById(PnAssignmentPK assignmentPK) {
		PnAssignment pnAssignment = null;
		DBBean db = new DBBean();

		try {
			String sql = "Select SPACE_ID, PERSON_ID, OBJECT_ID, STATUS_ID, "
					+ "PERCENT_ALLOCATED, ROLE, IS_PRIMARY_OWNER, RECORD_STATUS,"
					+ " START_DATE, END_DATE, WORK, WORK_UNITS, WORK_COMPLETE, "
					+ "WORK_COMPLETE_UNITS, DATE_CREATED, MODIFIED_DATE, MODIFIED_BY, "
					+ "ACTUAL_START, ACTUAL_FINISH,"
					+ " ESTIMATED_FINISH, ASSIGNOR_ID FROM PN_ASSIGNMENT Where SPACE_ID='" + assignmentPK.getPersonId()
					+ "' AND " + "PERSON_ID='" + assignmentPK.getPersonId() + "' AND OBJECT_ID='"
					+ assignmentPK.getObjectId() + "'";

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnAssignment = new PnAssignment();
				PnAssignmentPK assignmentPK2 = new PnAssignmentPK();
				assignmentPK2.setSpaceId(rs.getInt("SPACE_ID"));
				assignmentPK2.setPersonId(rs.getInt(rs.getInt("PERSON_ID")));
				assignmentPK2.setObjectId(rs.getInt("OBJECT_ID"));
				pnAssignment.setComp_id(assignmentPK2);
				pnAssignment.setStatusId(rs.getInt("STATUS_ID"));

				pnAssignment.setPercentAllocated(rs.getBigDecimal("PERCENT_ALLOCATED"));
				pnAssignment.setRole(rs.getString("ROLE"));
				pnAssignment.setIsPrimaryOwner(rs.getInt("IS_PRIMARY_OWNER"));
				pnAssignment.setRecordStatus(rs.getString("RECORD_STATUS"));

				pnAssignment.setStartDate(rs.getDate("START_DATE"));
				pnAssignment.setEndDate(rs.getDate("END_DATE"));
				pnAssignment.setWork(rs.getBigDecimal("WORK"));
				pnAssignment.setWorkUnits(rs.getInt("WORK_UNITS"));
				pnAssignment.setWorkComplete(rs.getBigDecimal("WORK_COMPLETE"));

				pnAssignment.setWorkCompleteUnits(rs.getInt("WORK_COMPLETE_UNITS"));
				pnAssignment.setDateCreated(rs.getDate("DATE_CREATED"));
				pnAssignment.setModifiedDate(rs.getDate("MODIFIED_DATE"));
				pnAssignment.setModifiedBy(rs.getInt("MODIFIED_BY"));
				pnAssignment.setActualStart(rs.getDate("ACTUAL_START"));
				pnAssignment.setActualFinish(rs.getDate("ACTUAL_FINISH"));
				pnAssignment.setEstimatedFinish(rs.getDate("ESTIMATED_FINISH"));
			}
		} catch (Exception e) {
			System.out.println("Exception in PnAssignment : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnAssignment;
	}

	public static PnWeblogEntry getPnWeblogEntry(int id) {
		PnWeblogEntry pnWeblogEntry = null;
		DBBean db = new DBBean();

		try {
			String sql = "Select WEBLOG_ENTRY_ID, PERSON_ID, ANCHOR, TITLE, TEXT, PUB_TIME, UPDATE_TIME, "
					+ "WEBLOG_ID, PUBLISH_ENTRY, LINK, ALLOW_COMMENTS, COMMENT_DAYS, RIGHT_TO_LEFT, LOCALE, "
					+ "STATUS, SUMMARY, CONTENT_TYPE, CONTENT_SRC, IS_IMPORTANT "
					+ "FROM PN_WEBLOG_ENTRY WHERE WEBLOG_ENTRY_ID=" + id;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnWeblogEntry = new PnWeblogEntry();
				pnWeblogEntry.setWeblogEntryId(rs.getInt("WEBLOG_ENTRY_ID"));

				//Get Person Object
				Person person = new Person();
				person.setID(rs.getString("PERSON_ID"));

				pnWeblogEntry.setAnchor(rs.getString("ANCHOR"));
				pnWeblogEntry.setTitle(rs.getString("TITLE"));
				pnWeblogEntry.setText(rs.getString("TEXT"));
				pnWeblogEntry.setPubTime(rs.getDate("PUB_TIME"));
				pnWeblogEntry.setUpdateTime(rs.getDate("UPDATE_TIME"));
				pnWeblogEntry.setWeblogEntryId(rs.getInt("WEBLOG_ID"));
				pnWeblogEntry.setPublishEntry(rs.getInt("PUBLISH_ENTRY"));
				pnWeblogEntry.setLink(rs.getString("LINK"));
				pnWeblogEntry.setAllowComments(rs.getInt("ALLOW_COMMENTS"));
				pnWeblogEntry.setCommentDays(rs.getInt("COMMENT_DAYS"));
				pnWeblogEntry.setRightToLeft(rs.getInt("RIGHT_TO_LEFT"));
				pnWeblogEntry.setLocale(rs.getString("LOCALE"));
				pnWeblogEntry.setStatus(rs.getString("STATUS"));
				pnWeblogEntry.setSummary(rs.getString("SUMMARY"));
				pnWeblogEntry.setContentType(rs.getString("CONTENT_TYPE"));
				pnWeblogEntry.setContentSrc(rs.getString("ALLOW_COMMENTS"));
				pnWeblogEntry.setIsImportant(rs.getInt("IS_IMPORTANT"));
			}
		} catch (Exception e) {
			System.out.println("Exception in PnWeblogEntry : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnWeblogEntry;
	}

	/**
	 * Get pnTask by task id.
	 * @param taskId
	 * @return
	 */
	public static PnTask getTask(int taskId) {
		PnTask pnTask = null;
		DBBean db = new DBBean();
		try {
			String sql = "Select TASK_ID, TASK_NAME, TASK_DESC, TASK_TYPE, "
					+ "DURATION, WORK, WORK_UNITS, WORK_COMPLETE, DATE_START, "
					+ "WORK_COMPLETE_UNITS, DATE_FINISH, ACTUAL_START, ACTUAL_FINISH, "
					+ "PRIORITY, PERCENT_COMPLETE, DATE_CREATED, DATE_MODIFIED, MODIFIED_BY, "
					+ "DURATION_UNITS, PARENT_TASK_ID, RECORD_STATUS, CRITICAL_PATH, SEQ, "
					+ "IGNORE_TIMES_FOR_DATES, IS_MILESTONE, EARLY_START, EARLY_FINISH, LATE_START, "
					+ "LATE_FINISH, WORK_PERCENT_COMPLETE, CALCULATION_TYPE_ID, UNALLOCATED_WORK_COMPLETE, "
					+ "UNALLOCATED_WORK_COMPLETE_UNIT, CONSTRAINT_TYPE, CONSTRAINT_DATE, DEADLINE, "
					+ "WORK_MS, WORK_COMPLETE_MS, UNASSIGNED_WORK, UNASSIGNED_WORK_UNITS, WBS, WBS_LEVEL "
					+ "FROM PN_TASK WHERE TASK_ID=" + taskId;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnTask = new PnTask();
				pnTask.setTaskId(rs.getInt("TASK_ID"));
				pnTask.setTaskName(rs.getString("TASK_NAME"));
				pnTask.setTaskDesc(rs.getString("TASK_DESC"));
				pnTask.setTaskType(rs.getString("TASK_TYPE"));

				pnTask.setDuration(rs.getBigDecimal("DURATION"));
				pnTask.setWork(rs.getBigDecimal("WORK"));
				pnTask.setWorkUnits(rs.getInt("WORK_UNITS"));
				pnTask.setWorkComplete(rs.getBigDecimal("WORK_COMPLETE"));
				pnTask.setDateStart(rs.getDate("DATE_START"));

				pnTask.setWorkCompleteUnits(rs.getInt("WORK_COMPLETE_UNITS"));
				pnTask.setDateFinish(rs.getDate("DATE_FINISH"));
				pnTask.setActualStart(rs.getDate("ACTUAL_START"));
				pnTask.setActualFinish(rs.getDate("ACTUAL_FINISH"));

				pnTask.setPriority(rs.getInt("PRIORITY"));
				pnTask.setPercentComplete(rs.getBigDecimal("PERCENT_COMPLETE"));
				pnTask.setDateCreated(rs.getDate("DATE_CREATED"));
				pnTask.setDateModified(rs.getDate("DATE_MODIFIED"));
				pnTask.setModifiedBy(rs.getInt("MODIFIED_BY"));

				pnTask.setDurationUnits(rs.getInt("DURATION_UNITS"));
				pnTask.setParentTaskId(rs.getInt("PARENT_TASK_ID"));
				pnTask.setRecordStatus(rs.getString("RECORD_STATUS"));
				pnTask.setCriticalPath(rs.getInt("CRITICAL_PATH"));
				pnTask.setSeq(rs.getInt("SEQ"));

				pnTask.setIgnoreTimesForDates(rs.getInt("IGNORE_TIMES_FOR_DATES"));
				pnTask.setIsMilestone(rs.getInt("IS_MILESTONE"));
				pnTask.setEarlyStart(rs.getDate("EARLY_START"));
				pnTask.setEarlyFinish(rs.getDate("EARLY_FINISH"));
				pnTask.setLateStart(rs.getDate("LATE_START"));

				pnTask.setLateFinish(rs.getDate("LATE_FINISH"));
				pnTask.setWorkPercentComplete(rs.getBigDecimal("WORK_PERCENT_COMPLETE"));
				pnTask.setCalculationTypeId(rs.getInt("CALCULATION_TYPE_ID"));

				pnTask.setUnallocatedWorkComplete(rs.getBigDecimal("UNALLOCATED_WORK_COMPLETE"));
				pnTask.setUnallocatedWorkCompleteUnit(rs.getInt("UNALLOCATED_WORK_COMPLETE_UNIT"));
				pnTask.setConstraintType(rs.getString("CONSTRAINT_TYPE"));
				pnTask.setConstraintDate(rs.getDate("CONSTRAINT_DATE"));
				pnTask.setDeadline(rs.getDate("DEADLINE"));

				pnTask.setWorkMs(rs.getBigDecimal("WORK_MS"));
				pnTask.setWorkCompleteMs(rs.getBigDecimal("WORK_COMPLETE_MS"));
				pnTask.setUnassignedWork(rs.getBigDecimal("UNASSIGNED_WORK"));
				pnTask.setUnassignedWorkUnits(rs.getInt("UNASSIGNED_WORK_UNITS"));
				pnTask.setWbs(rs.getString("WBS"));
				pnTask.setWbsLevel(rs.getString("WBS_LEVEL"));
			}
		} catch (Exception e) {
			System.out.println("Exception in getTask : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnTask;
	}

	public static PnNews getPnNewsById(BigDecimal newsId) {
		PnNews pnNews = null;
		DBBean db = new DBBean();

		try {
			String sql = "Select NEWS_ID, TOPIC, PRIORITY_ID, POSTED_BY_ID, POSTED_DATETIME, "
					+ "CREATED_BY_ID, CREATED_DATETIME, MODIFIED_BY_ID, MODIFIED_DATETIME, "
					+ "CRC, RECORD_STATUS, NOTIFICATION_ID, MESSAGE_CLOB " + "FROM PN_NEWS WHERE NEWS_ID=" + newsId;

			db.executeQuery(sql);
			ResultSet rs = db.result;

			while (rs.next()) {
				pnNews = new PnNews();

				pnNews.setNewsId(rs.getInt("NEWS_ID"));
				pnNews.setTopic(rs.getString("TOPIC"));
				pnNews.setPriorityId(rs.getInt("PRIORITY_ID"));
				pnNews.setPostedById(rs.getInt("POSTED_BY_ID"));
				pnNews.setPostedDatetime(rs.getDate("POSTED_DATETIME"));

				pnNews.setCreatedById(rs.getInt("CREATED_BY_ID"));
				pnNews.setCreatedDatetime(rs.getDate("CREATED_DATETIME"));
				pnNews.setModifiedById(rs.getInt("MODIFIED_BY_ID"));
				pnNews.setModifiedDatetime(rs.getDate("MODIFIED_DATETIME"));

				pnNews.setCrc(rs.getDate("CRC"));
				pnNews.setRecordStatus(rs.getString("RECORD_STATUS"));
				pnNews.setNotificationId(rs.getInt("NOTIFICATION_ID"));
				pnNews.setMessageClob(rs.getClob("MESSAGE_CLOB"));
			}
		} catch (Exception e) {
			System.out.println("Exception in getPnNewsById : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnNews;
	}

	public static PnObject getObjectByObjectId(Integer objectId) {
		PnObject pnObject = null;
		DBBean db = new DBBean();
		try {
			String sql = "SELECT OBJECT_ID, DATE_CREATED, CREATED_BY, RECORD_STATUS FROM PN_OBJECT WHERE OBJECT_ID ="
					+ objectId;

			db.executeQuery(sql);
			ResultSet rs = db.result;
			while (rs.next()) {
				pnObject = new PnObject();
				pnObject.setObjectId(rs.getInt("OBJECT_ID"));
				pnObject.setDateCreated(rs.getDate("DATE_CREATED"));
				pnObject.setRecordStatus(rs.getString("RECORD_STATUS"));
			}
		} catch (Exception e) {
			System.out.println("Exception in getObjectByObjectId : " + e);
			e.printStackTrace();
		} finally {
			db.release();
		}
		return pnObject;
	}

}
