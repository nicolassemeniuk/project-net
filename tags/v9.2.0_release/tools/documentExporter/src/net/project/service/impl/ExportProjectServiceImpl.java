package net.project.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import net.project.dao.IExportProjectDAO;
import net.project.dao.impl.ExportProjectDAO;
import net.project.model.Project;
import net.project.service.IExportProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "exportProjectService")
public class ExportProjectServiceImpl implements IExportProjectService {

	@Autowired
	private IExportProjectDAO exportProjectDAO;
	
	/**
	 * @param exportProjectDAO the exportProjectDAO to set
	 */
	public void setExportProjectDAO(ExportProjectDAO exportProjectDAO) {
		this.exportProjectDAO = exportProjectDAO;
	}



	public Map getProjects(Integer userId) {
		return exportProjectDAO.getProjects(userId);
	}
	
	public boolean exportDocuments(ArrayList<Project> projects, String folder){
		return exportProjectDAO.exportDocuments(projects, folder);
	}



	public boolean validateFolder(String folder) {
		try {
			File f = new File(folder);
			if (f.isDirectory()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

}
