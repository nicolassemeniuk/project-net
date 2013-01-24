package net.project.schedule.mvc.handler.gantt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.encoders.SunPNGEncoderAdapter;

public class GanttPNGServlet extends GanttServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5259640311318546880L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		super.doGet(request, response);

		SunPNGEncoderAdapter sunPNGEncoderAdapter = new SunPNGEncoderAdapter();
		byte[] encoded = sunPNGEncoderAdapter.encode(_chart
				.createBufferedImage(1200, _pdfHeight));
		ServletOutputStream pdfOutputStream = response.getOutputStream();
		pdfOutputStream.write(encoded);

		response.setContentType("image/png");
	}

}
