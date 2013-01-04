package net.project.service;

import java.util.ArrayList;
import java.util.Map;

import net.project.model.Project;

public interface IExportProjectService {

	public Map getProjects(Integer userId);
	
	public boolean exportDocuments(ArrayList<Project> projects, String folder);
	
	public boolean validateFolder(String folder);
	
}
