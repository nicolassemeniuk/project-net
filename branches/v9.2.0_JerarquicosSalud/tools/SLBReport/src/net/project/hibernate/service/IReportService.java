package net.project.hibernate.service;

import java.io.OutputStream;

public interface IReportService {
  
	public OutputStream createUserActivityReport();
	
	public OutputStream createProjectActivityReport();
	
}
