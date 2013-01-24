package net.project.report;

public class ReportController  {

//	public ModelAndView initReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//		Map<String, String> model = new HashMap<String, String>();
//		return new ModelAndView("/tiles_report", "model", model);
//	}
//
//	public ModelAndView printReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//		Map<String, String> model = new HashMap<String, String>();
//		try {
//			System.out.println("\n\n\n 3.nesto se izvrsava\n\n\n");
//			request.setAttribute("test", "neki test atribut !");
//			OutputStream output = response.getOutputStream();
//			IReportService service = ServiceFactory.getInstance().getReportService();
//			response.setContentType("application/excel");
//			response.setHeader("Content-Disposition", "download; filename=Statistic.xls");
//			ByteArrayOutputStream out = (ByteArrayOutputStream) service.getStatisticReport();
//
//			output.write(out.toByteArray());
//			output.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return new ModelAndView("/tiles_statistic", "model", model);
//	}
//	
//	public ModelAndView printReport2(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//		Map<String, String> model = new HashMap<String, String>();
//		try {
//			System.out.println("\n\n\n 3.nesto se izvrsava\n\n\n");
//			request.setAttribute("test", "neki test atribut !");
//			OutputStream output = response.getOutputStream();
//			IReportService service = ServiceFactory.getInstance().getReportService();
//			response.setContentType("application/pdf");
//			response.setHeader("Content-Disposition", "download; filename=Statistic.xls");
//			ByteArrayOutputStream out = (ByteArrayOutputStream) service.getStatisticReport();
//
//			output.write(out.toByteArray());
//			output.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return new ModelAndView("/tiles_statistic", "model", model);
//	}	
//
//	public ModelAndView statisticReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//		Map<String, List> model = new HashMap<String, List>();
//		try {
//			List<Statistic> statistic = ServiceFactory.getInstance().getReportService().getHtmlStatisticReport();
//			model.put("statistic", statistic);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//		return new ModelAndView("/tiles_statistic", "model", model);
//	}

}
