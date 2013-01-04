/**
 * 
 */
package net.project.project;

/**
 * Class provides row of sequenced project data 
 *
 */
public class ProjectPortfolioRow {

	private String displayValue;
	private String type;
	private String actualValue;
	private String imageUrl;
	/**
	 * @return the actualValue
	 */
	public String getActualValue() {
		return actualValue;
	}
	/**
	 * @param actualValue the actualValue to set
	 */
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	/**
	 * @return the displayValue
	 */
	public String getDisplayValue() {
		return displayValue;
	}
	/**
	 * @param displayValue the displayValue to set
	 */
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
