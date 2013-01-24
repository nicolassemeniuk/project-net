package net.project.wiki.diff;

import java.awt.Color;

public class LineCellRenderer  {

	public static Color MOVED_COLOR = new Color(134, 156, 234);
	
	public static Color INSERTED_COLOR = new Color(156, 250, 158);
	
	public static Color DELETED_COLOR = new Color(249, 113, 113);
	
	public static Color MODIFIED_COLOR = new Color(252, 177, 109);
	
	public static Color CONFLICT_COLOR = DELETED_COLOR;
	
	public static Color WARNING_COLOR = MODIFIED_COLOR;
	
	private boolean showNoMatchAsAdded;
	
	/**
	 * @return the showNoMatchAsAdded
	 */
	public boolean getIsShowNoMatchAsAdded() {
		return showNoMatchAsAdded;
	}

	
}
