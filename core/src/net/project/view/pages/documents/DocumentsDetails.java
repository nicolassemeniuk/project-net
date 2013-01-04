package net.project.view.pages.documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.document.Container;
import net.project.document.ContainerEntry;
import net.project.document.DocumentManagerBean;
import net.project.document.DocumentsDataFactory;
import net.project.document.IContainerObject;
import net.project.hibernate.model.PnDocContainerListView;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.IPnDocContainerListViewService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.link.ILinkableObject;
import net.project.link.LinkManagerBean;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStoreDataFactory;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.ServletSecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.Node;
import net.project.util.NodeFactory;
import net.project.util.Version;
import net.project.view.pages.blog.BlogEntries;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

public class DocumentsDetails {

	@Property
	private String jSPRootURL;

	@Property
	@Persist
	private String versionNumber;

	@Property
	@Persist
	private Integer moduleId;

	@Property
	@Persist
	private String spaceType;

	@Property
	@Persist
	private String spaceName;

	@Inject
	private RequestGlobals requestGlobals;

	private static Logger log = Logger.getLogger(DocumentsDetails.class);

	@Property
	private User user;

	@Property
	private String userId;

	@Property
	private String currentSpaceId;

	private HttpServletRequest request;

	private Container container;

	@Property
	private ArrayList<ContainerEntry> documentContainents;

	@Property
	private List<PnDocContainerListView> documentList;

	private DateFormat userDateFormatter;

	@Property
	private IPnDocContainerListViewService containerListViewService;

	@Property
	private IBlogViewProvider blogViewProvider;

	@InjectPage
	private BlogEntries blogEntries;

	@Property
	private DocumentManagerBean documentManagerBean;

	@Property
	private SecurityProvider securityProvider;

	@Property
	private Integer actionId;

	@Property
	private Integer contextId;

	@Property
	private Integer viewId;

	@Property
	private Integer linkActionId;

	@Property
	private Integer linkModule;

	@Property
	private String businessOptionsString;

	@Property
	private AssignmentStoreDataFactory assignmentStoreDataFactory;

	@Property
	private String projectOptionsString;
	
	private DocumentsDataFactory documentsDataFactory; 

	private enum DocumentsAction {
		GETDOCUMENTSTREEDATA, LOADBLOGENTRIES, REMOVEDOCUMENT
	}

	/**
	 * Initialize properties of class
	 */
	void initialize() {
		documentManagerBean = (DocumentManagerBean) requestGlobals.getHTTPServletRequest().getSession().getAttribute("docManager");
		securityProvider = (SecurityProvider) requestGlobals.getHTTPServletRequest().getSession().getAttribute("securityProvider");
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		moduleId = Module.DOCUMENT;
		spaceType = SessionManager.getUser().getCurrentSpace().getType();
		userId = SessionManager.getUser().getID();
		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
		user = SessionManager.getUser();
		request = requestGlobals.getHTTPServletRequest();
		container = new Container();
		userDateFormatter = user.getDateFormatter();
		containerListViewService = ServiceFactory.getInstance().getDocContainerListViewService();
		documentList = new ArrayList<PnDocContainerListView>();
		spaceName = user.getCurrentSpace().getSpaceType().getName();
		documentManagerBean.getNavigator().put("TopContainer", "/documents/Details?module=" + Module.DOCUMENT);
		request.getSession().setAttribute("refererLink", jSPRootURL + "/documents/Details?module=" + Module.DOCUMENT);
		request.setAttribute("id", documentManagerBean.getCurrentContainerID());
		actionId = Action.DELETE;
		contextId = ILinkableObject.GENERAL;
		viewId = LinkManagerBean.VIEW_ALL;
		linkActionId = Action.MODIFY;
		linkModule = Module.DOCUMENT;
		documentManagerBean.setUser(user);
		assignmentStoreDataFactory = new AssignmentStoreDataFactory();
		documentsDataFactory = new DocumentsDataFactory();
		blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
	}

	@SetupRender
	public void setValues() {
		createUserBusinessOptionsString();
		createUserProjectOptionsString();
	}

	Object onActivate() {
		initialize();
		return null;
	}

	Object onActivate(String action) {
		initialize();
		if (net.project.util.StringUtils.isNotEmpty(action)) {
			if (action.equalsIgnoreCase(DocumentsAction.GETDOCUMENTSTREEDATA.toString())) {
				return getDocumentsTreeData(request);
			} else if (action.equalsIgnoreCase(DocumentsAction.LOADBLOGENTRIES.toString())) {
				return loadBlogEntries();
			} else if (action.equalsIgnoreCase(DocumentsAction.REMOVEDOCUMENT.toString())) {
				removeDocuments(request);
			}
		}
		return null;
	}

	/**
	 * @param request
	 * @return TextStreamResponse
	 */
	public TextStreamResponse getDocumentsTreeData(HttpServletRequest request) {

		//Get all document records for space
		String currentId =  request.getParameter("currentId");
		if(currentId != null){
			currentSpaceId = currentId;
		}else{
			currentSpaceId = request.getParameter("currentSpaceId");
		}
		documentList = containerListViewService.getAllContainersDocument(currentSpaceId);

		return new TextStreamResponse("text/json", "{\"success\":true,\"total\":" + 0 + ",\"data\":[" + 
					getJsonData() + "]}");
	}

	/**
	 * Method to create json string 
	 * @return string
	 */
	public String getJsonData() {

		StringBuilder jsonData = new StringBuilder();
		final List<String> columns = new ArrayList<String>();
		columns.add("objectId");
		columns.add("containerId");
		columns.add("name");
		columns.add("format");
		columns.add("version");
		columns.add("is_checked_out");
		columns.add("status");
		columns.add("author");
		columns.add("date_modified");
		columns.add("file_size");
		columns.add("objectType");

		final NodeFactory factory = new NodeFactory(columns);
		Node node = null;

		// Append all nodes in map 
		for (PnDocContainerListView containerListView : documentList) {
			node = getNewNode(factory, containerListView);
		}

		// Append a child node in to parent
		List<Node> checkParent = factory.getNodes();
		for (Node childNode : checkParent) {

			Node addedNode = getNodeForId(checkParent, Integer.parseInt(childNode.getMap().get("containerId")
					.toString()));

			if (addedNode != null) {
				addedNode.add(childNode);
			}
		}

		// Creante json data string
		List<Node> nodes = factory.getNodes();
		if (CollectionUtils.isNotEmpty(nodes)) {
			int order = 1;
			for (Node checkDataNode : nodes) {
				/* 
				 * Check whether this node has a parent. In such a case
				 * it will be added to the json represention by its parent.
				 */
				if (null == MapUtils.getString(checkDataNode.getMap(), "_parent")) {
					checkDataNode.order(order);
					order = checkDataNode.getRight() + 1;
					jsonData.append(checkDataNode.toJSON());
					jsonData.append(',');
				}
			}
			jsonData.deleteCharAt(jsonData.length() - 1);
		}

		return jsonData.toString();
	}

	/**
	 * Method to create node
	 * 
	 * @param factory
	 * @param containerEntry
	 * @return parentNode
	 */
	public Node getNewNode(NodeFactory factory, PnDocContainerListView containerEntry) {

		Node nodeForContainerEntry = factory.nextNode();

		nodeForContainerEntry.set("objectId", containerEntry.getObjectId());
		nodeForContainerEntry.set("containerId", containerEntry.getDocContainerId());
		nodeForContainerEntry.set("name", containerEntry.getName().equals("_system.directory.folder") ? "Top Folder"
				: containerEntry.getName());
		nodeForContainerEntry.set("format", PropertyProvider.get(containerEntry.getFormat()));
		nodeForContainerEntry.set("version", containerEntry.getVersion());
		nodeForContainerEntry.set("is_checked_out", containerEntry.getIsCheckedOut());
		nodeForContainerEntry.set("status", PropertyProvider.get(containerEntry.getStatus()));
		nodeForContainerEntry.set("author", containerEntry.getAuthor());
		nodeForContainerEntry.set("date_modified", userDateFormatter.formatDate(containerEntry.getDateModified()));
		nodeForContainerEntry.set("file_size", containerEntry.getFileSize());
		nodeForContainerEntry.set("appIcon", containerEntry.getAppIconUrl() != null ? containerEntry.getAppIconUrl()
				: "/images/folder.gif");
		nodeForContainerEntry.set("objectType", containerEntry.getObjectType());

		return nodeForContainerEntry;
	}

	/**
	 * Check if node have a parent node
	 * 
	 * @param nodes
	 * @param id
	 * @return targetParent
	 */
	private Node getNodeForId(List<Node> nodes, Integer id) {
		for (Node targetParent : nodes) {
			if (id == Integer.parseInt(targetParent.getMap().get("objectId").toString())) {
				return targetParent;
			}
		}
		return null;
	}


	public void removeDocuments(HttpServletRequest request) {
		String objectId = request.getParameter("objectId");
		int module = securityProvider.getCheckedModuleID();
		int action = securityProvider.getCheckedActionID();
		String id = securityProvider.getCheckedObjectID();
		String status = "D"; //regular delete

		try {
			ServletSecurityProvider.setAndCheckValues(request);
		} catch (IOException e) {
			log.error("IO Exception" + e.getMessage());
		} catch (ServletException se) {
			log.error("ServletException" + se.getMessage());
		}

		// security checks are now done within docManager		
		if (actionId == 512) {//hard delete
			status = "H";
		} else if (actionId == 1024) {
			status = "A";
		}

		documentManagerBean.setListDeleted(); //look for deleted objects

		IContainerObject containerObject = documentManagerBean.getCurrentObject();
		if (containerObject != null) {
			containerObject.setUser(documentManagerBean.getUser());
		}

		try {
			documentManagerBean.removeObject(containerObject, status);
		} catch (PnetException e) {
			log.error(e.getMessage());
		}

		if (action == 1024 || action == 512) {
			documentManagerBean.unSetListDeleted(); // unset the flag
			request.setAttribute("action", Integer.toString(Action.LIST_DELETED));
			request.setAttribute("module", Integer.toString(Module.TRASHCAN));
		} else {
			request.setAttribute("action", Integer.toString(Action.VIEW));
			request.setAttribute("module", Integer.toString(Module.DOCUMENT));
		}
	}

	/**
	 * This method generates business option string for drop down list. 
	 */
	private void createUserBusinessOptionsString() {
		List<BusinessSpace> businesses = null;
		BusinessSpaceFinder businessFinder = new BusinessSpaceFinder();
		try {
			businesses = businessFinder.findByUser(net.project.security.SessionManager.getUser(), "A");
		} catch (PersistenceException e) {
			log
					.error("Error occured while generating list values for Businesses model: MyAssignments.createUserBusinessOptionsString()");
		}
		businessOptionsString = assignmentStoreDataFactory.generateUserBusinessOptionsString(businesses);
	}

	/**
	 *This method generates project option string for drop down list. 
	 */
	private void createUserProjectOptionsString() {
		IPnProjectSpaceService pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		List<PnProjectSpace> projects = pnProjectSpaceService.getProjectsByUserId(Integer.parseInt(SessionManager
				.getUser().getID()));
		projectOptionsString = assignmentStoreDataFactory.generateUserProjectOptionsString(projects);
	}
	
	/** 
	 * Method to load blog entries
	 *  
	 * @param docContainerId
	 * @param objectId
	 * @return blogEntries
	 */
	public Object loadBlogEntries() {
		String jSPRootURL = SessionManager.getJSPRootURL();
		String objectId = request.getParameter("objectId");
		String documentObjectType = request.getParameter("documentObjectType");
		String currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();

		documentManagerBean.setCurrentObjectID(objectId);
		List<PnWeblogEntry> entries = null;
		IBlogProvider blogProvider = ServiceFactory.getInstance().getBlogProvider();
		blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
		Integer urlModuleId = moduleId;

		try {
			if (StringUtils.isEmpty("" + objectId) || (StringUtils.isNotEmpty("" + objectId) && objectId.equals(0))) {
				blogEntries.setMessage("<br/>Select any object from left pane to see corresponding blog entries.");
				blogEntries.setUserWeblogEntries(null);
				blogEntries.setTotalWorkDone("");
				blogEntries.setIsMoreEntriesToSee(false);
				blogEntries.setMorePostUrl("");
			} else if (documentObjectType.equals(ObjectType.CONTAINER)) {
				return new TextStreamResponse("text", "<br /><div style=\"padding-left: 15px;\">"
						+ "<label style=\"text-align: center; color: #848484; \">"
						+ "Blog is not supported for this object!</label>");
			} else if (objectId != null) {
				entries = blogProvider.getWeblogEntriesByObjectId(objectId);
			}
			if (entries == null || entries.size() <= 0) {
				blogEntries.setMessage("Blog entries not found.");
				blogEntries.setUserWeblogEntries(null);
			} else {
				if (entries.size() > 10) {
					entries = entries.subList(0, 10);
					urlModuleId = Module.PROJECT_SPACE;
					blogEntries.setMessage(entries.size() + " blog entries shown");
					blogEntries.setIsMoreEntriesToSee(true);
					blogEntries.setMorePostUrl(jSPRootURL + "/blog/view/" + currentSpaceId + "/" + userId + "/"
							+ spaceType + "/" + moduleId + "?module=" + moduleId);
				} else {
					blogEntries.setIsMoreEntriesToSee(false);
					blogEntries.setMorePostUrl("");
					blogEntries.setMessage(entries.size() == 1 ? "1 blog entry found" : entries.size()
							+ " blog entries found");
				}

				blogEntries.setJspRootURL(jSPRootURL);
				blogEntries.setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(entries, jSPRootURL,
						SpaceTypes.PERSONAL_SPACE, DateFormat.getInstance()));
				blogEntries.setLinkToPersonSpace(false);
				blogEntries.setShowEditLink(false);
				blogEntries.setShowExpandCollapseImage(false);
				blogEntries.setShowPersonImage(false);
				blogEntries.setBlogCommentDivClass("");
				blogEntries.setBlogPostDivClass("post-body1");
				blogEntries.setShowCommentLink(true);
			}
			blogEntries.setTotalWorkDone("");
			blogEntries.setIsAssignmentPage(true);
			return blogEntries;
		} catch (Exception e) {
			log.error("Error occured while loading blog entries." + e.getMessage());
		}
		return null;
	}
}