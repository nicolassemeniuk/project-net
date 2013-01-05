package net.project.schedule.mvc.handler.gantt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.util.HTMLUtils;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author
 */
public class GanttPDFServlet extends HttpServlet {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1336252291618147968L;

    private int taskCounter;

    private Schedule schedule;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        this.schedule = (Schedule) request.getSession()
                .getAttribute("schedule");
        ServletOutputStream pdfOutputStream = response.getOutputStream();
        File file = new File(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + System.currentTimeMillis() + ".pdf");
        int offset = 0;
        int range = 100;
        Double totalSize = new Double(this.schedule.getTaskList().getList()
                .size());
        Double numberOfPages = Math.ceil(Double.parseDouble(""
                + new Double(totalSize) / 100));
        JFreeChart taskchart[] = new JFreeChart[numberOfPages.intValue()];
        int[] chartHeight = new int[numberOfPages.intValue()];
        for (int index = 0; index < numberOfPages.intValue(); index++) {
            if (range > totalSize) {
                range = totalSize.intValue();
            }
            Iterator iterator = this.schedule.getTaskList().getList().subList(
                    offset, range).iterator();
            int size = this.schedule.getTaskList().getList().subList(offset,
                    range).size();
            IntervalCategoryDataset dataset = this.createDataset(iterator);
            JFreeChart chart = this.createChart(dataset);
            taskchart[index] = chart;
            offset = range;
            range = range + 100;
            int pdfHeight = 110;
            this.taskCounter = size;
            while (this.taskCounter != 0) {
                pdfHeight += 40;
                this.taskCounter--;
                chartHeight[index] = pdfHeight;
            }
        }
        DefaultFontMapper defaultFontMapper = new DefaultFontMapper();
        this.schedule.getTaskList().iterator();
        int pdfHeight = 110;
        this.taskCounter = this.schedule
                .getTaskList()
                .getList()
                .subList(
                        0,
                        this.schedule.getTaskList().getList().size() < 100 ? this.schedule
                                .getTaskList().getList().size()
                                : 100).size();
        while (this.taskCounter != 0) {
            pdfHeight += 40;
            this.taskCounter--;
        }
        BufferedInputStream bufferedInputStream = this.saveChartAsPDF(file,
                taskchart, 1200, pdfHeight, defaultFontMapper, chartHeight);
        int readBytes = 0;
        while ((readBytes = bufferedInputStream.read()) != -1) {
            pdfOutputStream.write(readBytes);
        }
        file.delete();
        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename="
                + HTMLUtils.escapeForValidFileName(this.schedule.getName())
                + "-gantt.pdf");
    }

    /**
     * @param file generated PDF
     * @param chart Gantt JChart object
     * @param width width for PDF
     * @param height height for PDF
     * @param mapper Font style for PDF content.
     * @return Buffered InputStream for generated PDF.
     * @throws IOException
     */
    private BufferedInputStream saveChartAsPDF(File file, JFreeChart[] chart,
            int width, int height, FontMapper mapper, int[] chartHeight)
            throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        this.writeChartAsPDF(out, chart, width, height, mapper, chartHeight);
        out.close();
        return (new BufferedInputStream(new FileInputStream(file.getPath())));
    }

    /**
     * @param out BufferedOutputStream for PDF File
     * @param chart Gantt JChart object
     * @param width width for PDF
     * @param height height for PDF
     * @param mapper Font style for PDF content.
     * @throws IOException
     */
    private void writeChartAsPDF(OutputStream out, JFreeChart[] chart,
            int width, int height, FontMapper mapper, int[] chartHeight)
            throws IOException {
        Rectangle pagesize = new Rectangle(width, height + 50);
        pagesize.setBackgroundColor(Color.WHITE);
        Document document = new Document(pagesize, 0, 0, 0, 0);
        try {

            BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
            BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);
            BaseFont bf_courier = BaseFont.createFont(BaseFont.COURIER,
                    "Cp1252", false);
            BaseFont.createFont(BaseFont.SYMBOL, "Cp1252", false);

            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.addAuthor(PropertyProvider
                    .get("prm.global.application.title"));
            document.addSubject(new Phrase("Gantt Chart",
                    new com.lowagie.text.Font(bf_courier)).toString());
            document.open();

            String URL_ROOT = this.getServletContext().getRealPath("");
            String URL_IMAGES = URL_ROOT + "//images//";

            Image jpg = Image.getInstance(URL_IMAGES + "menu//logo_pnet.png");
            document.add(jpg);

            for (int index = 0; index < chart.length; index++) {
                PdfContentByte cb = writer.getDirectContent();
                document.setPageSize(new Rectangle(width, chartHeight[index]));
                PdfTemplate tp = cb.createTemplate(width, chartHeight[index]);
                Graphics2D g2 = tp.createGraphics(width, chartHeight[index],
                        mapper);
                Rectangle2D r2D = new Rectangle2D.Double(0, 0, width,
                        chartHeight[index]);
                chart[index].draw(g2, r2D);
                if (index != 0) {
                    document.newPage();
                }
                g2.dispose();
                cb.addTemplate(tp, 0, 0);
            }
        } catch (DocumentException de) {
            Logger.getLogger(GanttPDFServlet.class).error(de.getMessage());
        } catch (FileNotFoundException fne) {
            Logger.getLogger(GanttPDFServlet.class).error(fne.getMessage());
        }
        document.close();
    }

    /**
     * @param schedule
     * @return TaskSeries
     */
    private IntervalCategoryDataset createDataset(Iterator schedule) {

        final TaskSeries s1 = new TaskSeries("Scheduled");
        final TaskSeries s2 = new TaskSeries("Actual");

        Iterator iterator = schedule;
        while (iterator.hasNext()) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) iterator.next();
            s1.add(new Task(scheduleEntry.getName(), new SimpleTimePeriod(
                    scheduleEntry.getStartTime(), scheduleEntry.getEndTime())));

            if (scheduleEntry.getActualStartTime() != null
                    && scheduleEntry.getActualEndTime() != null) {

                if (scheduleEntry.getActualStartTime().before(
                        scheduleEntry.getActualEndTime())) {

                    s2.add(new Task(scheduleEntry.getName(),
                            new SimpleTimePeriod(scheduleEntry
                                    .getActualStartTime(), scheduleEntry
                                    .getActualEndTime())));
                }
            }
        }
        this.taskCounter = s1.getItemCount();
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(s1);
        collection.add(s2);
        return collection;
    }

    /**
     * Creates a chart.
     * 
     * @param dataset the dataset.
     * @return The chart.
     */
    private JFreeChart createChart(final IntervalCategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createGanttChart("Gantt Chart",
                "Task", "Date", dataset, true, true, false);

        Font font = new Font("Tahoma", Font.BOLD, 18);
        TextTitle title = new TextTitle("Gantt Chart", font);
        chart.setTitle(title);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.decode("#8088FF"));
        renderer.setSeriesPaint(1, Color.BLACK);
        renderer.setSeriesOutlinePaint(0, Color.blue);
        renderer.setSeriesOutlinePaint(1, Color.black);

        plot.getDomainAxis().setLowerMargin(0.0020);
        plot.getDomainAxis().setUpperMargin(0.0020);
        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.2f);

        return chart;

    }
}
