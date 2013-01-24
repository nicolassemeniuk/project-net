/**
 * 
 */
package net.project.hibernate.constants;

/**
 * @author
 *
 */
public class WikiConstants {
	
	public static final String WIKI_EDIT_ACTION = "edit";
	
    public static final String WIKI_VIEW_ACTION = "view";
    
    public static final String WIKI_PREVIEW_ACTION = "preview";
    
    public static final String WIKI_DELETE_ACTION = "delete";
    
    public static final String WIKI_INDEX_ACTION = "index";
    
    public static final String WIKI_RECENT_CHANGES_ACTION = "recent_changes";
    
    public static final String WIKI_HISTORY_ACTION = "history";
    
    public static final Object WIKI_VERSION_ACTION = "version";
    
    public static final String WIKI_SHOW_IMAGES_ACTION = "show_images";
    
    // accessible to project members only
	public static final Integer PROJECT_LEVEL_ACCESS = 0;
	
	// accessible to system members only
	public static final Integer SYSTEM_LEVEL_ACCESS = 1;
	
	// accessible to unauthorised members
	public static final Integer PUBLIC_LEVEL_ACCESS = 2;


}
