package net.project.model;

public class Project {
	
	public Project(){
	}
	
	private Integer projectId;
	private Integer parentSpaceId;
	private Integer childSpaceId;
	private String projectName;
	private Integer level;
	private String path;
	private String folderName;
	private boolean selected;
	
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * @return the projectId
	 */
	public Integer getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the parentSpaceId
	 */
	public Integer getParentSpaceId() {
		return parentSpaceId;
	}
	/**
	 * @param parentSpaceId the parentSpaceId to set
	 */
	public void setParentSpaceId(Integer parentSpaceId) {
		this.parentSpaceId = parentSpaceId;
	}
	/**
	 * @return the childSpaceId
	 */
	public Integer getChildSpaceId() {
		return childSpaceId;
	}
	/**
	 * @param childSpaceId the childSpaceId to set
	 */
	public void setChildSpaceId(Integer childSpaceId) {
		this.childSpaceId = childSpaceId;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("projectId:"+this.projectId+"\n");
		sb.append("childSpaceId:"+this.childSpaceId+"\n");
		sb.append("projectName:"+this.projectName+"\n");
		sb.append("selected:"+this.selected+"\n");
		return sb.toString();
	}
	
	
	
}
