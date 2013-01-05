package net.project.schedule.mvc.handler.gantt;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;

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

/**
 * @author
 * 
 */
public class GanttServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8911938178924389993L;
	protected int taskCounter;
	protected JFreeChart _chart;
	protected int _pdfHeight;
	protected Schedule _schedule;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		_schedule = (Schedule) request.getSession().getAttribute("schedule");
		IntervalCategoryDataset dataset = createDataset(_schedule);
		_chart = createChart(dataset);
		_pdfHeight = 110;
		while (taskCounter != 0) {
			_pdfHeight += 40;
			taskCounter--;
		}
	}

	/**
	 * @param schedule
	 * @return TaskSeries
	 */
	protected IntervalCategoryDataset createDataset(Schedule schedule) {

		final TaskSeries s1 = new TaskSeries("Scheduled");
		final TaskSeries s2 = new TaskSeries("Actual");

		Iterator<ScheduleEntry> iterator = schedule.getTaskList().iterator();
		while (iterator.hasNext()) {
			ScheduleEntry scheduleEntry = iterator.next();
			s1.add(new Task(scheduleEntry.getName(), new SimpleTimePeriod(
					scheduleEntry.getStartTime(), scheduleEntry.getEndTime())));

			if (scheduleEntry.getActualStartTime() != null
					&& scheduleEntry.getActualEndTime() != null) {

				s2.add(new Task(scheduleEntry.getName(), new SimpleTimePeriod(
						scheduleEntry.getActualStartTime(), scheduleEntry
								.getActualEndTime())));
			}
		}
		taskCounter = s1.getItemCount();
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		collection.add(s2);
		return collection;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	protected JFreeChart createChart(final IntervalCategoryDataset dataset) {
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
