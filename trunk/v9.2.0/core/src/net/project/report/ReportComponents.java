package net.project.report;

import java.awt.Color;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.markup.MarkupTags;

/**
 * Class that provides a place to centralize reporting component creation. This
 * allows reports to spend less time creating a common look and feel and more
 * time making a report.
 * 
 * @author Carlos Montemuio
 * @author Matthew Flower
 * @since Version 7.4
 */

public class ReportComponents {

	/**
	 * Create a table which will contain cells which describe which parameters
	 * were passed to the report generator.
	 * 
	 * @param cols
	 *            a <code>int</code> value which indicates the table width in
	 *            columns.
	 * @return a <code>Table</code> object which is preinitialized with the
	 *         reporting look and feel.
	 * @throws BadElementException
	 *             if the user specifies a table with of less than 1 column.
	 */

	public static Table createParameterTable(int cols)
			throws BadElementException {
		Table parameterTable = new Table(cols);
		parameterTable.setBorder(Rectangle.BOX);
		parameterTable.setWidth(100f);
		parameterTable.setBorderWidth(1.0f);
		parameterTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"parameterTable");
		parameterTable.setSpaceInsideCell(3f);
		parameterTable.setAlignment(Table.ALIGN_LEFT);
		parameterTable.setDefaultVerticalAlignment(Table.ALIGN_MIDDLE);
		return parameterTable;
	}

	/**
	 * Create a table cell that contains the title of the "parameter" table.
	 * This is probably a cell that contains the title "Report Parameters".
	 * 
	 * @param headerText
	 *            a <code>String</code> value that contains the text that will
	 *            appear in the header cell.
	 * @return a <code>Cell</code> parameter which is preinitialized with the
	 *         look and feel of a parameter header cell and which contains the
	 *         text passed in through the <code>headerText</code> parameter.
	 * @throws BadElementException
	 */

	public static Cell createParameterHeaderCell(String headerText)
			throws BadElementException {
		Cell headerCell = new Cell(new Phrase(headerText, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 10)));
		headerCell.setBorder(Rectangle.BOX);
		headerCell.setBackgroundColor(Color.LIGHT_GRAY);
		headerCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedheader");
		return headerCell;
	}

	/**
	 * Create a standard cell in the "Report Parameter" table.
	 * 
	 * @param cellText
	 *            a <code>String</code> value that contains the text that will
	 *            appear in the cell.
	 * @return a <code>Cell</code> value that which is properly formatted to be
	 *         a report parameter cell and which contains the text passed in the
	 *         <code>cellText</code> parameter.
	 * @throws BadElementException
	 *             if a programmer has changed this code in a way that breaks
	 *             the creation of a cell.
	 */

	public static Cell createParameterCell(String cellText)
			throws BadElementException {
		Cell parameterCell = new Cell(new Phrase(cellText, FontFactory.getFont(
				FontFactory.HELVETICA, 10)));
		parameterCell.setBorder(Rectangle.NO_BORDER);
		parameterCell.setBorderWidth(0.0f);
		parameterCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"parameterCell");
		return parameterCell;
	}

	/**
	 * Create a table which will contain fields that summarizes roll-up
	 * information pertaining to the detailed section of your report.
	 * 
	 * @param cols
	 *            a <code>int</code> value which indicates the width of the
	 *            table in columns.
	 * @return a <code>Table</code> which is properly formatted to display
	 *         summary information.
	 * @throws BadElementException
	 *             if a table is created with <1 columns.
	 */

	public static Table createSummaryTable(int cols) throws BadElementException {
		Table summaryTable = new Table(cols);
		summaryTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"summaryTable");
		summaryTable.setDefaultCellBorder(Rectangle.NO_BORDER);
		summaryTable.setBorder(Rectangle.NO_BORDER);
		summaryTable.setBorderWidth(0.0f);
		summaryTable.setWidth(100);
		return summaryTable;
	}

	/**
	 * Create a table which will contain fields that summarizeds roll-up
	 * information pertaining to the detailed section of your report.
	 * 
	 * @param cols
	 *            an <code>int</code> value which indicates the width of the
	 *            table in columns.
	 * @param rows
	 *            an <code>int</code> value which indicates the number of rows
	 *            in a table.
	 * @return a <code>Table</code> which is properly formatted to display
	 *         summary information.
	 * @throws BadElementException
	 *             if a table is created with <1 columns.
	 */

	public static Table createSummaryTable(int cols, int rows)
			throws BadElementException {
		Table summaryTable = new Table(cols, rows);
		summaryTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"summaryTable");
		summaryTable.setDefaultCellBorder(Rectangle.NO_BORDER);
		summaryTable.setBorder(Rectangle.NO_BORDER);
		summaryTable.setBorderWidth(0.0f);
		summaryTable.setWidth(100);
		summaryTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"summaryTable");
		return summaryTable;
	}

	/**
	 * Create a cell which is appropriate for a summary table.
	 * 
	 * @param cellText
	 *            a <code>String</code> value which contains the text that will
	 *            appear in the cell.
	 * @return a <code>Cell</code> value that is properly formatted for
	 *         insertion into a summary table.
	 * @throws BadElementException
	 *             shouldn't occur unless the someone has changed the method
	 *             internally.
	 */

	public static Cell createSummaryCell(String cellText)
			throws BadElementException {
		Cell summaryCell = new Cell(new Phrase(cellText, FontFactory.getFont(
				FontFactory.HELVETICA, 12)));
		summaryCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"summaryCell");
		return summaryCell;
	}

	/**
	 * Create a table which will contain all the detailed information from the
	 * report. This is generally multiple rows of data.
	 * 
	 * @param cols
	 *            an <code>int</code> value which indicates the width of the
	 *            table in columns.
	 * @return a <code>Table</code> value which is properly formatted to display
	 *         detailed information.
	 * @throws BadElementException
	 *             if a table is created with < 1 columns.
	 */

	public static Table createDetailedTable(int cols)

	throws BadElementException {
		Table detailedTable = new Table(cols);
		detailedTable.setSpacing(2);
		detailedTable.setDefaultCellBorder(Rectangle.NO_BORDER);
		detailedTable.setBorder(Rectangle.NO_BORDER);
		detailedTable.setBorderWidth(0.0f);
		detailedTable.setBorderColor(Color.WHITE);
		detailedTable.setWidth(100);
		detailedTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedTable");
		return detailedTable;
	}

	/**
	 * Create a table which will contain all the detailed information from the
	 * report. This is generally multiple rows of data. This table will have
	 * borders.
	 * 
	 * @param cols
	 *            an <code>int</code> value which indicates the width of the
	 *            table in columns.
	 * @return a <code>Table</code> value which is properly formatted to display
	 *         detailed information.
	 * @throws BadElementException
	 *             if a table is created with < 1 columns.
	 */

	public static Table createDetailedTableWithBorder(int cols)
			throws BadElementException {
		Table detailedTable = new Table(cols);
		detailedTable.setSpacing(2);
		detailedTable.setBorderWidth(1.0f);
		detailedTable.setBorderColor(Color.BLACK);
		detailedTable.setWidth(100);
		detailedTable.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedTable");
		return detailedTable;
	}

	/**
	 * Create a cell that is properly formatted to the be a cell in the first
	 * row of a detailed header table. This field is generally a column header
	 * for a grid of detailed information.
	 * 
	 * @param headerText
	 *            a <code>String</code> value that contains the text that will
	 *            be displayed in the <code>Cell</code>.
	 * @return a <code>Cell</code> value which is properly formatted to be the
	 *         first column in a detailed information table.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedHeaderCell(String headerText)
			throws BadElementException {
		Cell headerCell = new Cell(new Phrase(headerText, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 10)));
		headerCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedheader");
		return headerCell;
	}

	/**
	 * Create a cell that is properly formatted to the be a cell in the first
	 * row of a detailed header table. This field is generally a column header
	 * for a grid of detailed information.
	 * 
	 * @param headerText
	 *            a <code>String</code> value that contains the text that will
	 *            be displayed in the <code>Cell</code>.
	 * @param colspan
	 *            a <code>int</code> value that contains the colspan that will
	 *            be have in the <code>Cell</code>.
	 * @param font
	 *            a <code>int</code> value that contains the font that will be
	 *            have in the <code>Cell</code>.
	 * @param fontSize
	 *            a <code>int</code> value that contains the font size that will
	 *            be have in the <code>Cell</code>.
	 * @return a <code>Cell</code> value which is properly formatted to be the
	 *         first column in a detailed information table.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedHeaderCell(String headerText, int colspan,
			String font, int fontSize) throws BadElementException {
		Cell headerCell = new Cell(new Phrase(headerText, FontFactory.getFont(
				font, fontSize)));
		headerCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedheader");
		return headerCell;
	}

	/**
	 * Create a cell that is properly formatted to the be a cell in the first
	 * row of a detailed header table. This field is generally a column header
	 * for a grid of detailed information.
	 * 
	 * @param headerText
	 *            a <code>String</code> value that contains the text that will
	 *            be displayed in the <code>Cell</code>.
	 * @param width
	 *            a <code>String</code> value that contains the width that will
	 *            be displayed in the <code>Cell</code>.
	 * @param colspan
	 *            a <code>int</code> value that contains the colspan that will
	 *            be have in the <code>Cell</code>.
	 * @return a <code>Cell</code> value which is properly formatted to be the
	 *         first column in a detailed information table.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedHeaderCell(String headerText,
			String width, int colspan) throws BadElementException {
		Cell headerCell = new Cell(new Phrase(headerText, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 10)));
		headerCell.setWidth(width);
		if (colspan > 0) {
			headerCell.setColspan(2);
		}
		headerCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedheader");
		return headerCell;
	}

	/**
	 * Create a cell that is properly formatted to the be a cell in the first
	 * row of a detailed header table. This field is generally a column header
	 * for a grid of detailed information.
	 * 
	 * @param headerText
	 *            a <code>String</code> value that contains the text that will
	 *            be displayed in the <code>Cell</code>.
	 * @param width
	 *            a <code>String</code> value that contains the width that will
	 *            be displayed in the <code>Cell</code>.
	 * @param colspan
	 *            a <code>int</code> value that contains the colspan that will
	 *            be have in the <code>Cell</code>.
	 * @param colspan
	 *            a <code>int</code> value that contains the Font that will be
	 *            have in the <code>Cell</code>.
	 * @param colspan
	 *            a <code>int</code> value that contains the Font size that will
	 *            be have in the <code>Cell</code>.
	 * @return a <code>Cell</code> value which is properly formatted to be the
	 *         first column in a detailed information table.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedHeaderCell(String headerText,
			String width, int colspan, String font, int fontSize)
			throws BadElementException {
		Cell headerCell = new Cell(new Phrase(headerText, FontFactory.getFont(
				font, fontSize)));
		headerCell.setWidth(width);
		if (colspan > 0) {
			headerCell.setColspan(2);
		}
		headerCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedheader");
		return headerCell;
	}

	/**
	 * Created a cell that is properly formatted to be a detailed information
	 * cell. This type of cell generally contains one field of detailed
	 * information, such as a one attribute of a task in a late task report.
	 * 
	 * @param cellText
	 *            a <code>String</code> value which contains the text that will
	 *            appear in the report.
	 * @return a <code>Cell</code> value which is properly formatted to be used
	 *         as a detailed cell.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedCell(String cellText)
			throws BadElementException {
		Cell detailedCell = new Cell(new Phrase(cellText == null ? ""
				: cellText, FontFactory.getFont(FontFactory.HELVETICA, 10)));
		detailedCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedcell");
		return detailedCell;
	}

	/**
	 * Created a cell that is properly formatted to be a detailed information
	 * cell. This type of cell generally contains one field of detailed
	 * information, such as a one attribute of a task in a late task report.
	 * 
	 * @param cellText
	 *            a <code>String</code> value which contains the text that will
	 *            appear in the report.
	 * @param colspan
	 *            a <code>int</code> value which contains the colspan that will
	 *            appear in the report.
	 * @return a <code>Cell</code> value which is properly formatted to be used
	 *         as a detailed cell.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedCell(String cellText, int colspan)
			throws BadElementException {
		Cell detailedCell = new Cell(new Phrase(cellText == null ? ""
				: cellText, FontFactory.getFont(FontFactory.HELVETICA, 10)));
		detailedCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedcell");
		if (colspan > 0) {
			detailedCell.setColspan(colspan);
		}
		return detailedCell;
	}

	/**
	 * Created a cell that is properly formatted to be a detailed information
	 * cell. This type of cell generally contains one field of detailed
	 * information, such as a one attribute of a task in a late task report.
	 * 
	 * @param image
	 *            a <code>Image</code> value which contains the image that will
	 *            appear in the report.
	 * @return a <code>Cell</code> value which is properly formatted to be used
	 *         as a detailed cell.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedCell(Image image)
			throws BadElementException {
		Cell detailedCell = new Cell(image);
		return detailedCell;
	}

	/**
	 * Created a cell that is properly formatted to be a detailed information
	 * cell. This type of cell generally contains one field of detailed
	 * information, such as a one attribute of a task in a late task report.
	 * 
	 * @param cellText
	 *            a <code>String</code> value which contains the text that will
	 *            appear in the report.
	 * @param colspan
	 *            a <code>int</code> value which contains the colspan that will
	 *            appear in the report.
	 * @param font
	 *            a <code>int</code> value which contains the colspan that will
	 *            appear in the report.
	 * @param fontSize
	 *            a <code>int</code> value which contains the font that will
	 *            appear in the report.
	 * @return a <code>Cell</code> value which is properly formatted to be used
	 *         as a detailed cell.
	 * @throws BadElementException
	 *             shouldn't be thrown under normal conditions. This maybe be
	 *             possible if the method signature is altered.
	 */

	public static Cell createDetailedCell(String cellText, int colspan,
			String font, int fontSize) throws BadElementException {
		Cell detailedCell = new Cell(new Phrase(cellText == null ? ""
				: cellText, FontFactory.getFont(font, fontSize)));
		detailedCell.setWidth("150");
		return detailedCell;

	}

	/**
	 * Create a paragraph element to be used at the top of a report.
	 * 
	 * @param titleText
	 *            a <code>String</code> which is the text to be displayed as the
	 *            title.
	 * @return a <code>Paragraph</code> object formatted to be the title of the
	 *         report.
	 */

	public static Paragraph createReportTitle(String titleText) {
		return new Paragraph(titleText, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 20));
	}

	/**
	 * Create a workspace element to be used a the top of the report.
	 * 
	 * @param name
	 *            a <code>String</code> which contains the name of the
	 *            workspace.
	 * @return a <code>Paragraph</code> object which is properly formatted, and
	 *         which contains the name of the workspace.
	 */

	public static Paragraph createWorkspaceHeader(String name) {
		return new Paragraph(name, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 16));
	}

	/**
	 * Create a paragraph element suited to be a header of the report. Headers
	 * are the elements such as "created by" on "created on" that appear
	 * underneath the title.
	 * 
	 * @param reportHeader
	 *            a <code>String</coe> value that will be the text of

* the paragraph.
	 * @return a <code>Paragraph</code> containing the the text passed as the
	 *         reportHeader parameter.
	 */

	public static Paragraph createReportHeader(String reportHeader) {
		return new Paragraph(reportHeader, FontFactory.getFont(
				FontFactory.HELVETICA, 12));
	}

	/**
	 * Create a cell to be shown when a grouping iterator indicates there is a
	 * change in group.
	 * 
	 * @param groupName
	 *            a <code>String</code> value which will be displayed in the
	 *            cell as a group name.
	 * @param colspan
	 *            an <code>int</code> value indicating how many cells wide this
	 *            cell should be.
	 * @return a <code>BadElementException</code> when creation of the Cell
	 *         fails.
	 * @throws BadElementException
	 */

	public static Cell createGroupChangeCell(String groupName, int colspan)
			throws BadElementException {
		Cell groupingCell = new Cell(new Phrase(groupName, FontFactory.getFont(
				FontFactory.HELVETICA_BOLD, 10)));
		groupingCell.setBackgroundColor(Color.LIGHT_GRAY);
		groupingCell.setColspan(colspan);
		groupingCell.setBorder(Rectangle.BOX);
		return groupingCell;
	}
}