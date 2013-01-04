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
package net.project.wiki;

import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.ImageFormat;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.tags.ATag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.wiki.model.PnBlikiTemplates;

import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;

public class ExtWikiModel extends WikiModel {
		
	//modified for displaying non existing wiki page links.
	private boolean isPreview;
	private String objectId;
	private String rootWikiPageId; 
	private boolean isRootPage;
	// TODO: make images list a Map to hold image simbolic name and link as key value pairs in order to display nameges
	/** List holding images on current wiki page */
	private List<String> images = new ArrayList<String>();
	/** Map holding namespaces (as keys) and pages of namespaces (as list values), that current wiki page contains */
	private TreeMap<String, List<String>> namespacesMap = new TreeMap<String, List<String>>();
	
	public ExtWikiModel(String imageBaseURL, String linkBaseURL) {
		super(AddonConfiguration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
//		super(imageBaseURL, linkBaseURL);
	}

	public ExtWikiModel(String imageBaseURL, String linkBaseURL, boolean isPreview) {
		super(AddonConfiguration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
//		super(imageBaseURL, linkBaseURL);
		this.isPreview = isPreview;
		this.objectId = SessionManager.getUser().getCurrentSpace().getID();	//wi
	}
	
	public ExtWikiModel(String imageBaseURL, String linkBaseURL, String rootWikiPageId, boolean isPreview) {	//wi
		super(AddonConfiguration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
//		super(imageBaseURL, linkBaseURL);
		this.rootWikiPageId = rootWikiPageId;
		//this.objectId = objectId;
		this.objectId = ServiceFactory.getInstance().getPnWikiPageService()
							.getWikiPage(Integer.valueOf(rootWikiPageId))
							.getOwnerObjectId().getObjectId().toString();
		this.isPreview = isPreview;
	}

	public ExtWikiModel(String imageBaseURL, String linkBaseURL, String rootWikiPageId, boolean isPreview, boolean isRootPage) {
		super(AddonConfiguration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
//		super(imageBaseURL, linkBaseURL);
		//this.objectId = objectId;
		this.rootWikiPageId = rootWikiPageId;
//		this.objectId = ServiceFactory.getInstance().getPnWikiPageService()
//							.getWikiPage(Integer.valueOf(rootWikiPageId))
//							.getOwnerObjectId().getObjectId().toString();
		this.objectId = ServiceFactory.getInstance().getPnWikiPageService()
					.getWikiPage(Integer.valueOf(rootWikiPageId)) != null  
					?	ServiceFactory.getInstance().getPnWikiPageService()
							.getWikiPage(Integer.valueOf(rootWikiPageId))
							.getOwnerObjectId().getObjectId().toString()
					:	rootWikiPageId;
	
		this.isPreview = isPreview;
		this.isRootPage = isRootPage;
//		images = new ArrayList<String>();
	}

	/**
	 * Templates
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getRawWikiContent(String namespace, String templateName, Map templateParameters) {
		String result = super.getRawWikiContent(namespace, templateName, templateParameters);
		if (result != null) {
			return result;
		}
//		System.out.println("templateParameters:\n" + templateParameters + "objectId: " + objectId);
		if (namespace.equals("Template")) {
			String name = Encoder.encodeTitleToUrl(templateName, true);						// important: the Template name starts with an uppercase character!
			return new PnBlikiTemplates().parseTemplate(name, templateParameters, Integer.valueOf(objectId));
		}
		return null;
	}
	
	@Override
	public boolean isSemanticWebActive() {
		return true;
	}
	
	public String parseCategories(String urlRoot) {
		return "";
	}
	
	public void appendExternalLink(String link, String linkName, boolean withoutSquareBrackets) {
		// is it an image?
		link = Utils.escapeXml(link, true, false, false);
		int indx = link.lastIndexOf(".");
		if (indx > 0 && indx < (link.length() - 3)) {
			String ext = link.substring(indx + 1);
			if (ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")
					|| ext.equalsIgnoreCase("bmp")) {
				appendExternalImageLink(link, linkName);
				return;
			}
		}
		TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", link, true);
		aTagNode.addAttribute("class", "popup", true);	//"externallink"
		aTagNode.addAttribute("title", link, false);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addAttribute("target", "_blank", false);
		aTagNode.addChild(new ContentToken(linkName));
	}

	/**
	 * Added for displaying <b>Attachments</b> div on wiki page.
	 * @param wikiPageId
	 * @return HTML representation of images attached to this wiki page
	 * @author Uros Lates 
	 */
	public String parseAttachedImages(String wikiPageName, String objectId) {
		StringBuffer result = new StringBuffer("");
		boolean isImgDetailsPg = wikiPageName.startsWith("Image:") ? true : false;
		
		IPnWikiAttachmentService wikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
		IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
		List<PnWikiAttachment> list = null;

		PnWikiPage wikiPage = null; 
		if( isRootPage ) {
			wikiPage = wikiPageService.getRootPageForObject( Integer.valueOf(objectId) );
		} else {
			wikiPage = wikiPageService.getWikiPageWithName(wikiPageName, Integer.valueOf(objectId) );
		}

		if( wikiPage != null ) {
			list = wikiAttachmentService.getAllAttachmentsFromWikiPage(wikiPage.getWikiPageId());
		}
	    
		if (list != null && !list.isEmpty()) {
	    	result.append("\n<div id=\"attachedImages\" class=\"wContent\">" +
	    			"<h3 title=\"Attachments\" ><img src=\"" + SessionManager.getJSPRootURL() + "/images/attachment.gif\"/>Attachments:</h3><ul>");
	    	for (PnWikiAttachment str : list) {
            	try {
            		result.append("<span class\"imagesLinks\" dir=\"ltr\"><li>\"");
    				if( wikiPageName.startsWith("Image:") ) {
    					result.append("<b>");
    				} else {
    					result.append("<a title=\"");
    					
    					if(WikiManager.isFileTypeOfImage(str.getAttachmentName().replaceAll(" ", "_"))){
    						result.append("Image:");
    					} else {
    						result.append("File:");
    					}
    					result.append(str.getAttachmentName().replaceAll(" ", "_") +
        				"\" href=" +SessionManager.getJSPRootURL() +
						"/wiki/" + wikiPageName + "/");
    					
    					if(WikiManager.isFileTypeOfImage(str.getAttachmentName().replaceAll(" ", "_"))){
    						result.append("Image:");
    					} else {
    						result.append("File:");
    					}
						
    					result.append(URLEncoder.encode(str.getAttachmentName().replaceAll(" ","_") , SessionManager.getCharacterEncoding()) +																				
						"?module=" + Module.PROJECT_SPACE +">"); //+ "&id=" + objectId +
    				}
    				result.append(str.getAttachmentName().replaceAll(" ", "_"));
					if( wikiPageName.startsWith("Image:") ) {
						result.append("</b>");
					} else {
						result.append("</a>");
					}
					result.append("\", added by <b>" + str.getAttachedBy().getDisplayName() + "</b> on <i>" + SessionManager.getUser().getDateFormatter().formatDate(str.getAttachedOnDate(), "EEE, MMM dd, yyyy hh:mm:ss") + ".</i>");
					result.append("&nbsp;<b>Description:</b>&nbsp;");
					if ( str.getDescription() != null && !str.getDescription().equals("") ) {
						result.append(str.getDescription() + ".");
					} else {
						result.append("Not commented.");
					}
							
					if( !isImgDetailsPg ) {
						if( wikiPage.getParentPageName() != null )	//added not to show 'Detach Image' link in Attachment list on root page
							result.append("	<span class=\"detachImage\"> <b>[<a href=\"javascript:deleteAttachment('detach', '" + wikiPageName + "', '" + str.getAttachmentName() + "');\">Detach Image</a>]</b> </span>");
					} /*else {
						result.append("	<b>[<a href=\"javascript:deleteAttachment('delete', '" + wikiPageName + "', '" + str.getAttachmentName() + "');\">Delete Image</a>]</b> ");
					}*/
					result.append("</li></span>");

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	    	}
	    	result.append("</ul></p></div>");
	    }
		return result.toString();
	}
	
	/**
	 * Added for displaying <b>Attachments</b> div on wiki page.
	 * @param wikiPageId
	 * @return HTML representation of images attached to this wiki page
	 */
	public String parseAttachedImages(PnWikiPage wikiPage) {
		StringBuffer result = new StringBuffer("");
//		boolean isImgDetailsPg = wikiPage.getPageName().startsWith("Image:") ? true : false;
		
		IPnWikiAttachmentService wikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
		List<PnWikiAttachment> list = null;

		if( wikiPage != null ) {
			list = wikiAttachmentService.getAllAttachmentsFromWikiPage(wikiPage.getWikiPageId());
		}
	    
		if (list != null && !list.isEmpty()) {
	    	result.append("\n<div id=\"attachedImages\" class=\"wContent\">" +
	    			"<h3 title=\"Attachments\" ><img src=\"" + SessionManager.getJSPRootURL() + "/images/attachment.gif\"/>Attachments:</h3><ul>");
	    	for (PnWikiAttachment str : list) {
            	try {
            		String wikiPageName = null;
            		boolean isImageFile = WikiManager.isFileTypeOfImage(str.getAttachmentName().replaceAll(" ", "_"));
            		result.append("<span class\"imagesLinks\" dir=\"ltr\"><li>\"");
    				if( wikiPage.getPageName().startsWith("Image:") ) {
    					result.append("<b>");
    				} else {
    					result.append("<a title=\"");
    					
    					if(isImageFile){
    						result.append("Image:");
    					} else {
    						result.append("File:");
    					}
    					
    					if (wikiPage.getParentPageName() == null) {
							wikiPageName = wikiPage.getPageName().replaceAll(" ", "_");
						} else {
							wikiPageName = wikiPage.getParentPageName().getPageName().replaceAll(" ", "_");
						}
    					
    					if(isImageFile) {
	    					result.append(str.getAttachmentName().replaceAll(" ", "_") +
					        				"\" href=" + SessionManager.getJSPRootURL() +
											"/wiki/" + URLEncoder.encode(wikiPageName , SessionManager.getCharacterEncoding()) + "/");	    								
	    					result.append("Image:"+ URLEncoder.encode(str.getAttachmentName().replaceAll(" ","_") , SessionManager.getCharacterEncoding()) +																				
							"?module=" + Module.PROJECT_SPACE +">");
    					} else {
    						result.append(str.getAttachmentName().replaceAll(" ", "_") +
					        				"\" href=" + SessionManager.getJSPRootURL() +
											"/servlet/Download?id="+ str.getFileId() +"&module="+ Module.PROJECT_SPACE +">");
    					}
    				}
    				result.append(str.getAttachmentName().replaceAll(" ", "_"));
					if( wikiPage.getPageName().startsWith("Image:") ) {
						result.append("</b>");
					} else {
						result.append("</a>");
					}
					result.append("\", added by <b>" + str.getAttachedBy().getDisplayName() + "</b> on <i>" + SessionManager.getUser().getDateFormatter().formatDate(str.getAttachedOnDate(), "EEE, MMM dd, yyyy hh:mm:ss") + ".</i>");
					result.append("&nbsp;<b>Description:</b>&nbsp;");
					if ( str.getDescription() != null && !str.getDescription().equals("") ) {
						result.append(str.getDescription() + ".");
					} else {
						result.append("Not commented.");
					}
					result.append("	<b>[<a href=\"javascript:deleteAttachment(" + wikiPage.getWikiPageId() + ","+ wikiPage.getOwnerObjectId().getObjectId() +",'" + str.getAttachmentName() + "');\">"
							+ PropertyProvider.get("prm.wiki.deleteimage.link") + "</a>]</b> ");
					result.append("</li></span>");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	    	}
	    	result.append("</ul></p></div>");
	    }
		return result.toString();
	}
	
	/**
	 * This method parses all internal wiki links on one wiki page
	 */
	@Override
	public void appendInternalLink(String link, String hashSection, String linkText, String cssClass, boolean parseRecursive) {
//		System.out.println("appendInternalLink(String "+link+", String "+hashSection+", String "+linkText+", String "+cssClass+", boolean "+parseRecursive+")");
		link = link.replaceAll(" ", "_");
		// get list of all wiki pages in current object space
		IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
		ATag aTagNode = new ATag();
		append(aTagNode);
		aTagNode.addAttribute("id", "w", true);
		String href = "";
		link = WikiURLManager.converToWikiPageName(link);
		if( !isPreview ) {
			String parentName = fExternalWikiBaseURL.substring(fExternalWikiBaseURL.indexOf("wiki/") + 5);
			parentName = parentName.substring(0, parentName.indexOf("/"));
			href = parentName.equals(link) ? SessionManager.getJSPRootURL() + "/wiki/"+ parentName : SessionManager.getJSPRootURL() + "/wiki/"+ parentName + "/" + link;
		} else {
			href = "#";
		}
		if (hashSection != null) {
			href = href + '#' + hashSection;
		}
		aTagNode.addAttribute("href", href, true);
		PnWikiPage wikiPage = wikiPageService.getWikiPageWithName(link, Integer.valueOf(objectId));
		if( wikiPage == null) {
			aTagNode.addAttribute("style", "color: gray;", false);
			aTagNode.addAttribute("class", "linkToNonExistingPage", true);
		}
		
		aTagNode.addAttribute("wikilink", link, true);
		ContentToken text = new ContentToken(linkText);
		aTagNode.addChild(text);
	}
	
	/**
	 * Method for determining what will be presented: image or error. 
	 */
	@Override 
	public void appendInternalImageLink(String hrefImageLink, String srcImageLink, ImageFormat imageFormat) {
//		System.out.println("appendInternalImageLink(String "+hrefImageLink+", String "+srcImageLink+", ImageFormat "+imageFormat+")");
		images.add("Image:"+imageFormat.getFilename());
		try {
			srcImageLink = java.net.URLDecoder.decode(srcImageLink, SessionManager.getCharacterEncoding());
			hrefImageLink = java.net.URLDecoder.decode(hrefImageLink, SessionManager.getCharacterEncoding());
		} catch (UnsupportedEncodingException pnetEx) {
			pnetEx.printStackTrace();
		}
		
		String imageName = srcImageLink.substring(srcImageLink.indexOf("=")+1, srcImageLink.indexOf("&"));
		IPnWikiAttachmentService wikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
		IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
		
//		PnWikiPage pnWikiPage = wikiPageService.getWikiPage(Integer.valueOf(objectId) );
		PnWikiPage pnWikiPage = wikiPageService.getWikiPage(Integer.valueOf(rootWikiPageId) );

		boolean imgExists = false;
		if( pnWikiPage != null ) {
			Integer wikiPageId = pnWikiPage.getParentPageName() != null ? pnWikiPage.getParentPageName().getWikiPageId() : pnWikiPage.getWikiPageId();
			PnWikiAttachment imgDetailAttachment = wikiAttachmentService.getFileIdWithWikiPageAndFileName(wikiPageId, imageName);
			if( imgDetailAttachment != null && "A".equals(imgDetailAttachment.getRecordStatus())) {
				imgExists = true;
			}
		}
		
		if ( imgExists ) {
			int pxSize = imageFormat.getSize();
			String caption = imageFormat.getCaption();
			TagNode divTagNode = new TagNode("div");
			divTagNode.addAttribute("id", imageName, false);
			divTagNode.addAttribute("href", hrefImageLink, false);
			divTagNode.addAttribute("src", srcImageLink, false);
			divTagNode.addObjectAttribute("wikiobject", imageFormat);
			if (pxSize != -1) {
				divTagNode.addAttribute("style", "width:" + pxSize + "px", false);
			}
			pushNode(divTagNode);
	
			if (caption != null && caption.length() > 0) {
				TagNode captionTagNode = new TagNode("div");
				String clazzValue = "caption";
				String type = imageFormat.getType();
				if (type != null) {
					clazzValue = type + clazzValue;
				}
				captionTagNode.addAttribute("class", clazzValue, false);
				pushNode(captionTagNode);
				WikipediaParser.parseRecursive(caption, this);
				popNode();
			}
	
			popNode();
		} else {
			TagNode divTagNode = new TagNode("div");
			append(divTagNode);
			divTagNode.addAttribute("id", "noImageError", false);
			divTagNode.addAttribute("title", imageName, false);
			divTagNode.addAttribute("style", "color: red; background: rgb(224, 224, 224); border: 1px solid black; padding: 5px; margin: 20px;", false);
			divTagNode.addChild(new ContentToken(PropertyProvider.get("prm.wiki.imagenotexists.error.message") + imageName ));
		}
	}
	
	/**
	 * Modified method to check if current wiki page contains some of custom wiki prefixes
	 * and if it does make that link opens in new tab (make it external).
	 */
	@Override
	public void appendInterWikiLink(String wikiPrefix, String wikiSuffix, String link) {
//		System.out.println("appendInterWikiLink(String "+wikiPrefix+", String "+wikiSuffix+", String "+link+")");
		ATag aTagNode = new ATag();
		append(aTagNode);
		aTagNode.addAttribute("id", "w", true);
		String href = "";
		
		if( !isPreview ) {
				String[] configuredPrefixes = AddonConfiguration.getPNET_INTERWIKI_STRINGS();
				for (int i = 0; i < configuredPrefixes.length; i += 2) {
					if( wikiPrefix.equals(configuredPrefixes[i]) ) {		// if prefix is equal to some prefix configured with pnet
						href = configuredPrefixes[i+1] + wikiSuffix;			// set href to value specified by system property
						aTagNode.addAttribute("rel", "nofollow", true);
						aTagNode.addAttribute("target", "_blank", true);				// open specified location in new tab
						aTagNode.addAttribute("class", "popup", true);				// set style of this link to external link 
					}
				}
		} else {
			href = "#";
		}

		aTagNode.addAttribute("href", href, false);
		aTagNode.addAttribute("title", wikiSuffix, true);		// was link instead of wikiSuffix
		
		ContentToken text = new ContentToken(link);
		aTagNode.addChild(text);
	}
	
	@Override
	public boolean appendRedirectLink(String redirectLink) {
		redirectLink = redirectLink.replaceAll(" ", "_");

		/* setup redirect link */
		IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
		PnWikiPage wikiPage = null; 
		wikiPage = wikiPageService.getWikiPageWithName(redirectLink, Integer.valueOf(objectId) );
		
		String link = wikiPage.getParentPageName() == null ? 
						SessionManager.getJSPRootURL() + "/wiki/"+redirectLink : 
						SessionManager.getJSPRootURL() + "/wiki/"+ wikiPage.getParentPageName().getPageName() + "/" + redirectLink;
						
		link += "?wikiRedirect=true";
		/* setup redirect link */
		
		return super.appendRedirectLink(redirectLink);
	}

	public List<String> getImages() {
		return images;
	}
	
	/** Rendering images referenced on current wiki page */
	public String referencedImagesBlock() {
		// TODO: add div element as container (with css)
		StringBuffer imagesBlock = new StringBuffer("Images referenced: ");
		if(this.getImages() != null && !this.getImages().isEmpty()) {
			PnWikiPage rootWikiPage = ServiceFactory.getInstance().getPnWikiPageService()
											.getWikiPage(Integer.valueOf(rootWikiPageId) );
			for(String image : images) {
				String imageName = image.substring(image.lastIndexOf("/Image:")+1);
				imagesBlock.append(">> <a href=\"" + SessionManager.getJSPRootURL() + "/wiki/" +
					rootWikiPage.getPageName() + "/" + imageName + "\">" + imageName + "</a>" + " << ");
			}
		} else {
				imagesBlock.append("No images referenced on this page");
		}
		return imagesBlock.toString();
	}
}
