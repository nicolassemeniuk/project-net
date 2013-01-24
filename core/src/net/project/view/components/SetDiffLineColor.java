/**
 * 
 */
package net.project.view.components;

import net.project.wiki.diff.FileLine;

import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class SetDiffLineColor {
	
	@Parameter(required = true)
	@Property
	private FileLine fileLine;
	
	@Parameter
	private boolean isLeftFile;
	
	@Property
	private String  classBackColor;
	
	@Property
	private String LineNum;
	
	@Property
	private String color;
	
	@BeginRender
	void renderDifferenceLine() {
		if (fileLine.getStatus() == FileLine.NO_MATCH) {
			if (isLeftFile) {
				color = "#9CFA9E";
			} else {
				color = "#F97171";
			}
		} else if (fileLine.getStatus() == FileLine.MOVED) {
			color = "#869CEA";
		} else if (fileLine.getStatus() == FileLine.MODIFIED) {
			//color = "#CC0000";
			color = "#A0EE91";
		} else if (fileLine.getStatus() == FileLine.DELETED_ON_OTHER_SIDE || fileLine.getStatus() == FileLine.INSERTED_ON_OTHER_SIDE) {
			color = "#999999";
		} else {
			color = "#ffffff";
		}
		
		if(fileLine.getIndex() == -1){
			LineNum = "- ";
		} else {
			LineNum = ""+(fileLine.getIndex()+ 1) +" ";
		}
	}

}
