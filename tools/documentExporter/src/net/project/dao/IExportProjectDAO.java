package net.project.dao;

import java.util.ArrayList;
import java.util.Map;

import net.project.model.Project;

public interface IExportProjectDAO {

	public Map getProjects(Integer userId);

	public boolean exportDocuments(ArrayList<Project> projects, String folder);
	
}
