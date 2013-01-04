package net.project.test.acceptance.wiki;

import net.project.test.acceptance.engine.PnetTestEngine;

public class WikiBase extends PnetTestEngine{

	private String projectName;
	private String businessName;
	protected String wikiPageName;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setupWiki();
	}
	
	@Override
	public void tearDown() throws Exception {
		_framework.deleteBusiness(businessName);
		super.tearDown();
	}
	
	public void setupWiki(){
		businessName = _framework.createNewBusiness("WikiTest");
		projectName = _framework.createNewProject("WikiTest", businessName);
		wikiPageName = projectName.replaceAll(" ", "_");
		System.out.println("GENERATED Wiki Page: " + wikiPageName);
		_framework.goToPersonal();
		_framework.clickLinkWithExactText(projectName);
		_framework.clickLinkWithExactText("Wiki");	
		assertEditingWikiPage(wikiPageName);
	}
	
	public void goToProjectWiki(){
		_framework.goToPersonal();
		_framework.clickLinkWithExactText(projectName);
		_framework.clickLinkWithExactText("Wiki");
	}

	public void assertEditingWikiPage(String wikiPageName) {
		_framework.assertTextPresent("Editing Page - " + wikiPageName);
		assertLinkPresent("saveLink");
		assertLinkPresent("previewLink");
		assertTextPresent("Cancel");
		assertElementPresentByXPath("//textarea[@name=\"wikiContentText\"]");
	}
	
	@SuppressWarnings("static-access")
	public void editWikiPageContent(String wikiPageName, String wikiText, String wikiHTML) {
		assertEditingWikiPage(wikiPageName);
		
		//fill in textarea with wikiText then click on Save Page
		_framework.setTextField("wikiContentText", wikiText);
		_framework.clickLink("saveLink");
		//checking HTML result
		_framework.assertTrue( getPageSource().contains(wikiHTML));
		_framework.assertLinkPresent("editPage");
	}
	
	public void assertUploadAttachmentPage(String wikiPgName) {
		_framework.assertTextPresent("Upload Image - " + wikiPgName);
		_framework.assertLinkPresent("cancelLink");
		_framework.assertLinkPresent("submitUploadLink");
	}
	
	public void assertViewingWikiPage(String wikiPageName) {
		_framework.assertTextPresent("Viewing Page - " + wikiPageName);
		_framework.assertLinkPresent("editPage");
		_framework.assertLinkPresent("uploadLink");
	}
	
	/**
	 * method for creating new wiki page
	 */
	public void createNewWikiPage(String wikiText,String wikiHTML) {
		//goToProjectWiki();
		editWikiPageContent(wikiPageName, wikiText, wikiHTML);
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	public void setProjectName(String strVal){
		projectName = strVal;
	}
	
	/**
	 * Returns XPath for type of HTML element you give it with given id attribut value.<br>
	 * 
	 * @param typeOfElement	the type of HTML element (span, a, tr...)
	 * @param idAttributValue value for id attribut of that element
	 * @return String
	 */
	public String getElementsXPathWithIdValue(String typeOfElement, String idAttributValue) {
		String xPath = "//" + typeOfElement + "[@id=\"" + idAttributValue + "\"]";
		return xPath;
	}
		
	/**
	 * Returns XPath for type of HTML element you give him with given name attribut value.<br>
	 * 
	 * @param typeOfElement	the type of HTML element (span, a, tr...)
	 * @param nameAttributValue value for name attribut of that element
	 * @return String
	 */
	public String getElementsXPathWithNameValue(String typeOfElement, String nameAttributValue) {
		String xPath = "//" + typeOfElement + "[@name=\"" + nameAttributValue + "\"]";
		return xPath;
	}	
}
