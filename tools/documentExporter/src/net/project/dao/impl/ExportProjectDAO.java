package net.project.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.project.dao.IExportProjectDAO;
import net.project.model.PnUser;
import net.project.model.Project;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ExportProjectDAO extends AbstractHibernateDAO<PnUser, Integer> implements IExportProjectDAO{

	public ExportProjectDAO() {
		super(PnUser.class);
	}

	
	private static Logger log = Logger.getLogger(ExportProjectDAO.class);
	
	@SuppressWarnings("unchecked")
	public Map getProjects(Integer userId) {
		Map result = new HashMap();
		try {
			Integer userPrincipal = userId + 1;
			 String sql = "select m.*, pss.project_id " +
				" from   " +
				"  (select distinct  " +
				"         shs.parent_space_id as parent_space_id,  " +
				"         shs.child_space_id as child_space_id, " +
				"         SYS_CONNECT_BY_PATH(shs.child_space_id, '/') as path, " +
				"		  SYS_CONNECT_BY_PATH(REGEXP_REPLACE(ps.project_name, '/', ' '), '/') as folder, " +
				"         level, " +
				"         ps.project_name as project_name, " +
				"         ps.crc as crc " +
				"  from  " +
				"         pn_space_has_space shs, " +
				"         pn_project_space ps " +      
				"  where  " +
				"         shs.child_space_id = ps.project_id and " +
				"         shs.child_space_id in (SELECT  ss.child_space_id " +
				"FROM   pn_space_has_space ss,   pn_business_space bs,   pn_business b,   pn_project_view p, " +   
				"pn_space_has_plan shp " + 
				"WHERE    " +
				"     ss.child_space_id(+) = p.project_id   and  " +
				"     bs.business_space_id(+) = ss.parent_space_id   and " + 
				"     shp.space_id = p.project_id   and  " +
				"     b.business_id(+) = bs.business_id   and  " +
				"     ss.relationship_child_to_parent(+) = 'owned_by'  and  " +
				"     p.project_id in (select pv.project_id from pn_portfolio_view pv where pv.portfolio_id = ?)) and " +
				"         ps.record_status = 'A' " +
				"  start with shs.parent_space_id in (select ppp.person_id as par from pn_person ppp where ppp.record_status='A' " +
				" union select bb.business_id as par from pn_business bb where bb.record_status='A') " +
				"  connect by NOCYCLE shs.parent_space_id = prior shs.child_space_id " +
				"  order by folder) m, " +
				"  (select project_id, '/'||project_id||'%' as project_start, is_subproject from pn_project_space where is_subproject=0 and record_status='A') pss " +
				" where " +
				" m.path like project_start ";
			 
			 Connection connection = getHibernateTemplate().getSessionFactory().getCurrentSession().connection();
			 PreparedStatement ps = connection.prepareStatement(sql);
			 ps.setInt(1, userPrincipal);
			 ResultSet rs = ps.executeQuery();
			
			 ArrayList<Project> results = new ArrayList<Project>();
			 while (rs.next()) {
				 Project p = new Project();
				 p.setProjectId(rs.getInt("project_id"));
				 p.setChildSpaceId(rs.getInt("child_space_id"));
				 p.setParentSpaceId(rs.getInt("parent_space_id"));
				 p.setLevel(rs.getInt("level"));
				 String projectName = rs.getString("project_name");
				 String folderName = rs.getString("folder");
				 projectName = projectName.replaceAll("&", "&amp;");
				 projectName = projectName.replaceAll("\"", "&quot;");
				 folderName = folderName.replaceAll("\"", "");
				 folderName = folderName.replaceAll("[\\\\\\*\\(\\)\\[\\]\\{\\}\\?\\|\\,\\<\\>\\%\\&]", "");
				 p.setFolderName(folderName);
				 p.setProjectName(projectName);
				 p.setPath(rs.getString("path"));
				 results.add(p);
			 }
			
			 String generatedXml = generateXML(results, 0);
			 
			 result.put("results", results);
			 result.put("generatedXml", generatedXml);
			 log.info("generatedXml:" + generatedXml);
			 ps.close();
			 rs.close();
			 connection.close();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	private StringBuffer xml;
	private int previousLevel = 1;

	private String generateXML(ArrayList<Project> list, int position) {
		xml = new StringBuffer();
		try {
			xml.append("<projects Name=\"Projects\" ProjectId=\"root\">\n");
			int currentLevel = 0;
			for (int i = position; i < list.size(); i++) {
				Project p = list.get(i);
				if (p.getChildSpaceId() == p.getProjectId()) {
					// previousLevel = p.getLevel();
					xml.append("<Project Name=\"").append(p.getProjectName()).append("\" ");
					xml.append("Level=\"").append(p.getLevel()).append("\" ");
					xml.append("Folder=\"").append(p.getFolderName()).append("\" ");
					xml.append("selected=\"false\" ");
					xml.append("ProjectId=\"").append(p.getChildSpaceId()).append("\">\n");
				} else {
					if (previousLevel >= p.getLevel() && i != 0) {
						for (int k = 0; k < previousLevel - p.getLevel() + 1; k++) {
							xml.append("\t</Project>\n");
						}
					} else {
						// if(previousLevel <= p.getLevel()){
						// xml.append("\t</Project>\n");
						// }
					}
					
					xml.append("<Project Name=\"").append(p.getProjectName()).append("\" ");
					xml.append("Level=\"").append(p.getLevel()).append("\" ");
					xml.append("Folder=\"").append(p.getFolderName()).append("\" ");
					xml.append("selected=\"false\" ");
					xml.append("ProjectId=\"").append(p.getChildSpaceId()).append("\">\n");

				}
				if (i == list.size() - 1) {
					for (int k = 0; k < p.getLevel() - previousLevel - 1; k++) {
						xml.append("\t</Project>\n");
					}
				}
				previousLevel = p.getLevel();
				currentLevel = p.getLevel();
			}
			for (int k = 0; k < currentLevel; k++) {
				xml.append("\t</Project>\n");
			}
			//xml.append("\t</Project>\n");
			xml.append("</projects>");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return xml.toString();
	}
	
	public boolean exportDocuments(ArrayList<Project> projects, String folder){
		try {
			for (Project p : projects){
				if(p.isSelected()){
					//System.out.println("\n\n\n p:"+p.getProjectId()+"\n\n\n");
					Connection connection = getHibernateTemplate().getSessionFactory().getCurrentSession().connection();
					String docSpaceSql = " select doc_space_id from pn_space_has_doc_space where space_id = ? ";
					PreparedStatement docSpacePs = connection.prepareStatement(docSpaceSql);
					System.out.println("projekat:"+p.toString());
					int projectId = p.getChildSpaceId();
					docSpacePs.setInt(1, projectId);
					ResultSet docSpaceRs = docSpacePs.executeQuery();
					Integer docSpaceId = 0;
					if (docSpaceRs != null) {
						docSpaceRs.next();
						docSpaceId = docSpaceRs.getInt("doc_space_id");
					}
					docSpaceRs.close();
					docSpacePs.close();
		
					String docContainerSql = "select doc_container_id  from pn_doc_space_has_container  where doc_space_id = ? and is_root = 1";
					PreparedStatement docContainerPs = connection.prepareStatement(docContainerSql);
					docContainerPs.setInt(1, docSpaceId);
					ResultSet docContainerRs = docContainerPs.executeQuery();
					Integer docContainerId = 0;
					if (docContainerRs != null) {
						docContainerRs.next();
						docContainerId = docContainerRs.getInt("doc_container_id");
					}
					docContainerRs.close();
					docContainerPs.close();
		
					exportUsers(connection, projectId, folder, p.getProjectName(), p.getFolderName());
					
					copyFiles(connection, docSpaceId, projectId, docContainerId, folder, p.getProjectName(),p.getFolderName());
					
					connection.close();
				}
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private void exportUsers(Connection connection, Integer projectId, String rootFolder, String projectName, String projectFolder){
		try {
			String sql = " select    p.person_id,   p.display_name,   p.first_name,   p.last_name,   pp.middle_name,   p.email, " +   
							          "pp.timezone_code,   p.user_status,   u.last_login,   u.username " +
							"from   " +
							          "pn_space_has_person shp, " + 
							          "pn_person p,   " +
							          "pn_person_profile pp, " +  
							          "pn_user u   " +
							"where    " +
							          "shp.space_id = ?  and " + 
							          "p.person_id = shp.person_id   and " + 
							          "p.record_status = 'A'   and  " +
							          "u.user_id(+) = p.person_id   and " + 
							          "p.person_id = pp.person_id (+)   " +
							"order by    " +
							          "lower(p.first_name) asc, " +   
							          "lower(p.last_name) asc   ";
			
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1,projectId);
			ResultSet rs = ps.executeQuery();
			
			short rownum = (short) 0;
			// create a new workbook
			HSSFWorkbook wb = new HSSFWorkbook();
			// create a new sheet
			HSSFSheet s = wb.createSheet("Project members");
			// declare a row object reference
			HSSFRow r = null;
			// declare a cell object reference
			
			HSSFCell c = null;
			int i = 0;
			
			r = s.createRow(rownum);
			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Display name"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("First Name"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Last Name"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Username"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("User Status"));
			
			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Last Login"));
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			rownum++;
			
			while(rs.next()){
				i = 0;
				r = s.createRow(rownum);
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getString("display_name")));

				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getString("first_name")));

				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getString("last_name")));
				
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getString("username")));
				
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getString("email")));
				
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rs.getDate("last_login") != null ? dateFormatter.format(rs.getDate("last_login")) : ""));
		
				rownum++;
			}
			
			String rolesQuery = "select   " +
								"  g.group_id, " + 
								"  u.username as username, " +
								"  pp.last_name||' '||pp.first_name as full_name, " +
								"  pp.email as email, " +
								"  p.property_value as role_name, " +
								"  ghp.person_id " +
								"from  " +
								"  pn_group_view g, " + 
								"  pn_space_has_group shg, " +
								"  pn_property p, " +
								"  pn_user u, " +
								"  pn_group_has_person ghp, " +
								"  pn_person pp " +
								"where  " +
								"  g.group_id = shg.group_id and " + 
								"  g.record_status = 'A' and  " +
								"  shg.space_id = ? and  " +
								"  shg.is_owner = 1  and   " +
								"  substr(g.group_name, 2) = p.property and " +
								"  ghp.group_id = g.group_id and " +
								"  pp.person_id = ghp.person_id and  " +
								"  pp.person_id = u.user_id and " +
								"  p.property_value <> 'Principal' " +
								"order by " +
								"  3, 5";
			
			PreparedStatement rolesQueryPs = connection.prepareStatement(rolesQuery);
			rolesQueryPs.setInt(1,projectId);
			ResultSet rolesQueryRs = rolesQueryPs.executeQuery();
			
			rownum = (short) 0;
			// create a new sheet
			HSSFSheet ss = wb.createSheet("Project memeber permissions");
			// declare a row object reference
			r = null;
			// declare a cell object reference
			
			c = null;
			i = 0;
			
			r = ss.createRow(rownum);
			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Username"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Full Name"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Email"));

			c = r.createCell((short) i++);
			c.setCellValue(new HSSFRichTextString("Role granted"));
			
			rownum++;
			
			while(rolesQueryRs.next()){
				i = 0;
				r = ss.createRow(rownum);
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rolesQueryRs.getString("username")));

				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rolesQueryRs.getString("full_name")));

				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rolesQueryRs.getString("email")));
				
				c = r.createCell((short) i++);
				c.setCellValue(new HSSFRichTextString(rolesQueryRs.getString("role_name")));

				rownum++;
			}
			  
			rolesQueryRs.close();
			rolesQueryPs.close();
			
			String fileName = projectName + "_Users.xls";
			
			File f = new File(rootFolder +  projectFolder);
			if(!f.isDirectory()){
				boolean success = f.mkdirs();
				System.out.println("success: "+success + " folder:"+(rootFolder +  projectFolder));
			}
			FileOutputStream out = null;
			if (f.isDirectory()){
				out = new FileOutputStream(new File(f.getAbsoluteFile() + File.separator + fileName));
			}
			wb.write(out);
			out.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void copyFiles(Connection connection, Integer docSpaceId, Integer projectId, Integer docContainerId, String rootFolder, String projectName, String projectFolder) {
		try {
			String fileNamesForContainerSql = "select object_id, object_type, name, format, version, author, date_modified ,  "
					+ "file_size, doc_container_id, short_file_name from pn_doc_container_list_view  where doc_container_id = ? and is_hidden = 0 " + "  order by UPPER(name) asc";
			PreparedStatement fileNamesForContainerPs = connection.prepareStatement(fileNamesForContainerSql);
			fileNamesForContainerPs.setInt(1, docContainerId);
			ResultSet fileNamesForContainerRs = fileNamesForContainerPs.executeQuery();
			while (fileNamesForContainerRs != null && fileNamesForContainerRs.next()) {
				String objectType = fileNamesForContainerRs.getString("object_type");
				if ("doc_container".equals(objectType)) {
					Integer docContainer = fileNamesForContainerRs.getInt("object_id");
					copyFiles(connection, docSpaceId, projectId, docContainer, rootFolder, projectName, projectFolder);
				} else if ("document".equals(objectType)) {
					Integer documentId = fileNamesForContainerRs.getInt("object_id");
					String documentDetailsSql = "select document_id, document_name, doc_version_num, repository_path, file_handle "
							+ "	from  pn_doc_version_view  where document_id = ? and record_status = 'A' order by doc_version_num desc";
					PreparedStatement documentDetailsPs = connection.prepareStatement(documentDetailsSql);
					documentDetailsPs.setInt(1, documentId);
					ResultSet documentDetailsRs = documentDetailsPs.executeQuery();
					boolean first = true;
					if (documentDetailsRs != null) {
						while (documentDetailsRs.next()) {
							String sourcePath = documentDetailsRs.getString("repository_path") + File.separator + projectId + File.separator + docSpaceId + File.separator
									+ documentDetailsRs.getInt("document_id") + File.separator + documentDetailsRs.getString("file_handle");
							File src = new File(sourcePath);
							String file = documentDetailsRs.getString("document_name");
							Integer versionNumber = documentDetailsRs.getInt("doc_version_num");
							if (!first) {
								int dotPos = file.lastIndexOf(".");
								String extension = file.substring(dotPos);
								String fileName = file.substring(0, dotPos);
								file = fileName + "_Rev"+versionNumber+extension;
							}else{
								first = false;
							}
							// create project folder
							String destProjectFolderName = rootFolder + projectFolder;
							File destProjectFolder = new File(destProjectFolderName);
							if(!destProjectFolder.isDirectory()){
								destProjectFolder.mkdirs();
							}
							String destionationPath = destProjectFolderName + File.separator + file;
							File dest = new File(destionationPath);
						
							log.info("coping file from: " + src);
							log.info("coping file to: " + dest);
							copy(src, dest);
						}
					}
					documentDetailsRs.close();
					documentDetailsPs.close();
				}
			}
			fileNamesForContainerRs.close();
			fileNamesForContainerPs.close();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	private void copy(File src, File dst) throws IOException {
		try {
			InputStream 	 in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
