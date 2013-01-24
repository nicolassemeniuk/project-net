package net.project.test.acceptance.wiki;

/**
 * Basic test class for wiki features:
 * <ul>
 * 	<li>create new wiki page</li>
 * 	<li>edit existing wiki page</li>
 *  <li>editing preview</li>
 *  <li>upload page</li>
 * </ul><br>
 * Change properties <b>wikiText</b> and <b>wikiHTML</b> to values that will
 * represent wiki page content.<br>
 * @author Uros
 *
 */
public class BasicWikiFeaturesTest extends WikiBase {

	//////////////////////////////////////////////////////////
	//wiki text markup
	private String wikiText = "==Header Create Test==";
	private String wikiEditText = "==Header Edit Test==";
	//generated HTML content 
	private String wikiHTML = "<h2>Header Create Test</h2>";
	private String wikiEditHTML = "<h2>Header Edit Test</h2>";
	//////////////////////////////////////////////////////////
	

	/** <b>Test Case: </b> Create new wiki page in Projec.net.<br>
	 * 
	 * This test creates new <b>wiki page</b> for project.<br><br>
	 */
	public void testCreateNewWikiPage() throws Exception {
		editWikiPageContent(wikiPageName, wikiText, wikiHTML);
	}

	/**
	 * <b>Test Case: </b> Edit existing wiki page in Projec.net.<br>
	 * This test is used to demonstrate proper editing of <b>wiki page</b> for one project.<br><br>
	 */
	public void testEditExistingWikiPage() {
		createNewWikiPage(wikiText, wikiHTML);
		clickLink("editPage");
		editWikiPageContent(wikiPageName, wikiEditText, wikiEditHTML);
	}
	
	/**
	 * <b>Test Case:</b> Upload page in Projec.net.<br>
	 * This test is used to demonstrate proper preview of wiki upload attachment page.<br><br>
	 */
	public void testUploadPage() {
		createNewWikiPage(wikiText, wikiHTML);
		//go to upload page
		_framework.assertLinkPresent("uploadLink");
		_framework.clickLink("uploadLink");
		assertUploadAttachmentPage(wikiPageName);
		//click cancel link
		_framework.clickLink("cancelLink");
		assertViewingWikiPage(wikiPageName);
	}

	/**
	 * <b>Test Case:</b> Preview wiki page content while editing/creating wiki page in Projec.net.<br>
	 * This test is used to demonstrate proper functioning of <b>preview</b> functionality in wiki editing page.<br><br>
	 */
	@SuppressWarnings("static-access")
	public void testPreviewWikiPageContent() {
		//fill in textarea with wikiText then click on Preview link
		_framework.setTextField("wikiContentText", wikiText);
		_framework.clickLink("previewLink");
		assertEditingWikiPage(wikiPageName);
		try {
			_framework.wait(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		_framework.assertTrue( getPageSource().contains(wikiHTML));
	}
	
}
