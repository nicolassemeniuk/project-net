package net.project.portfolio.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.util.ProjectNode;

import org.apache.log4j.Logger;

public class MetaColumnPdfView extends HttpServlet {

    private static final long serialVersionUID = 5785145128529630450L;

    @SuppressWarnings("static-access")
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
    		List<ProjectNode> projectList = (List<ProjectNode>) request.getSession().getAttribute("projectList");
    		List<MetaColumn> projectColumnList =  (List<MetaColumn>) request.getSession().getAttribute("projectColumnList");

            // Get the data to display
            ByteArrayOutputStream doc;
            doc = MetaColumnView.getPDF(projectList, projectColumnList,this.getServletContext().getRealPath(""));
            response.setContentType("application/pdf");
            response.setContentLength(doc.size());
            response.addHeader("Content-Disposition", "attachment; filename="
                    + String.valueOf(System.currentTimeMillis()) + ".pdf");

            ServletOutputStream outStream = response.getOutputStream();
            doc.writeTo(outStream);
            outStream.flush();

        } catch (Exception e) {
            Logger.getLogger(MetaColumnPdfView.class).error(e.getMessage());
        }
    }

}