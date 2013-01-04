/*

 * Copyright 2000-2009 Project.net Inc.

 *

 * This file is part of Project.net.

 * Project.net is free software: you can redistribute it and/or modify it under the terms of

 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.

 *

 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;

 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 * See the GNU General Public License for more details.

 *

 * You should have received a copy of the GNU General Public License along with Project.net.

 * If not, see http://www.gnu.org/licenses/gpl-3.0.html

 */

package net.project.portfolio.view;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.project.base.finder.FinderIngredients;
import net.project.base.property.PropertyProvider;
import net.project.project.ProjectPortfolioRow;
import net.project.report.ReportComponents;
import net.project.security.SessionManager;
import net.project.util.ProjectNode;
import net.project.util.StringUtils;
import net.project.xml.XMLFormatException;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.markup.MarkupTags;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class MetaColumnView {
	
	private static PersonalPortfolioView view; 
	private static String oldSortColumn;
	private static String oldSortOrder;
	
	public static String getHtml(PersonalPortfolioViewResults viewResults, PersonalPortfolioView personalPortfolioView) {
		try {
			MyXMLFormatter formatter = new MyXMLFormatter();
			StringReader reader = new StringReader(getXML(viewResults));
			StringWriter writer = new StringWriter();
			formatter.transform(XslCreator.getXsl(viewResults,
					personalPortfolioView), reader, writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}

	public static ByteArrayOutputStream getPDF(List<ProjectNode> projectList,List<MetaColumn> projectColumnList, String imagePath) {
		try {
			return generatePDFDocument(projectList, projectColumnList, imagePath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getXML(PersonalPortfolioViewResults viewResults) {

		StringBuffer xml = new StringBuffer();
		final String PROJECTXML_ELEMENT = "ProjectXML";
		final String PROPERTIES_ELEMENT = "Properties";
		final String SCHEMA_ELEMENT = "Schema";
		final String TRANSLATION_ELEMENT = "Translation";
		final String CONTENT_ELEMENT = "Content";
		// Combine content and properties xml
		xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
		xml.append("<" + PROJECTXML_ELEMENT + ">");
		xml.append("<" + PROPERTIES_ELEMENT + ">");
		xml.append("<" + SCHEMA_ELEMENT + " />");
		xml.append("<" + TRANSLATION_ELEMENT + ">");
		xml.append("</" + TRANSLATION_ELEMENT + ">");
		xml.append("</" + PROPERTIES_ELEMENT + ">");
		xml.append("<" + CONTENT_ELEMENT + ">");
		try {

			xml.append(viewResults.getXMLBody());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		xml.append("</" + CONTENT_ELEMENT + ">");
		xml.append("</" + PROJECTXML_ELEMENT + ">");
		return xml.toString();

	}

	@SuppressWarnings("unchecked")
	private static ByteArrayOutputStream generatePDFDocument(List<ProjectNode> projectList,List<MetaColumn> projectColumnList, String imagePath) {
		// Create a new document with default page size and landscape orientation
		Document document = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
			document.addAuthor(PropertyProvider.get("prm.global.application.title"));
			document.open();
			String URL_ROOT = imagePath;
			String URL_IMAGES = URL_ROOT + "//images//";
			Image jpg = Image.getInstance(URL_IMAGES + "menu//logo_pnetPDF.png");
			jpg.scaleToFit(100, 50);
			document.add(jpg);

			// Add a detailed header section
			if (projectColumnList != null) {
				document.add(createDetailedHeaderSection(projectColumnList));
			}

			// Creates a table
			if (projectList != null) {
				PdfPTable detailedTable;
				for (ProjectNode project : projectList) {
					detailedTable = createDetailedTable(projectColumnList);
					Image image = null;
					String value = project.getProject().getProjectName();
					int colspan = 1;
					detailedTable.addCell(createBodyCell(value, colspan));
					for (ProjectPortfolioRow projectData : project.getSequensedProject()) {
						if (StringUtils.equalsIgnoreCase(projectData.getType(), "percent_complete")) {
							value = projectData.getActualValue().indexOf('.') != -1 ? projectData
									.getActualValue().substring(0, projectData.getActualValue().indexOf('.'))
									: projectData.getActualValue();
							detailedTable.addCell(createBodyCell(value, colspan));
						} else if (StringUtils.startsWithIgnoreCase(projectData.getType(), "BudgetedTotalCost/Money")
								|| StringUtils.startsWithIgnoreCase(projectData.getType(), "CurrentEstimatedTotalCost/Money")
								|| StringUtils.startsWithIgnoreCase(projectData.getType(), "ActualCostToDate/Money")
								|| StringUtils.startsWithIgnoreCase(projectData.getType(), "EstimatedROI/Money")) {
							if (projectData.getDisplayValue() != null) {
								value = projectData.getDisplayValue().indexOf('.') != -1 ? projectData
										.getDisplayValue().substring(0,	projectData.getDisplayValue().indexOf('.'))
										: projectData.getDisplayValue();
								detailedTable.addCell(createBodyCell(value, colspan));
							} else {
								detailedTable.addCell(createBodyCell("", colspan));
							}
						} else if (StringUtils.equalsIgnoreCase(projectData.getType(), "OverallStatus")
								|| StringUtils.equalsIgnoreCase(projectData.getType(), "FinancialStatus")
								|| StringUtils.equalsIgnoreCase(projectData.getType(), "ScheduleStatus")
								|| StringUtils.equalsIgnoreCase(projectData.getType(), "ResourceStatus")) {
							if (StringUtils.isNotEmpty(projectData.getImageUrl())) {
								image = Image.getInstance(URL_ROOT + projectData.getImageUrl());
								image.scalePercent(60f);
								detailedTable.addCell(createImageBodyCell(image));
							} else {
								detailedTable.addCell(createBodyCell("", colspan));
							}
						} else {
							value = projectData.getDisplayValue();
							detailedTable.addCell(createBodyCell(value, colspan));
						}
					}

					document.add(detailedTable);
				}
			}
			document.close();
			return out;
		} catch (DocumentException e) {
			Logger.getLogger(MetaColumnView.class).error(e.getMessage());
		} catch (MalformedURLException e) {
			Logger.getLogger(MetaColumnView.class).error(e.getMessage());
		} catch (IOException e) {
			Logger.getLogger(MetaColumnView.class).error(e.getMessage());
		}
		return out;
	}

	/**
	 * Creates the defaults views of the Meta Data of the Default Views
	 * 
	 * @return List<MetaColumn>
	 */

	private static List<MetaColumn> createDefaultMetaData() {

		List<MetaColumn> columnsDefault = new ArrayList<MetaColumn>();

		MetaColumn newColumn = new MetaColumn();

		newColumn.setOrderId("A");

		newColumn.setPropertyName("name");

		newColumn.setDescription("Name");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("B");

		newColumn.setPropertyName("ParentBusinessName");

		newColumn.setDescription("Business");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("C");

		newColumn.setPropertyName("StartDate");

		newColumn.setDescription("Start Date");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("D");

		newColumn.setPropertyName("EndDate");

		newColumn.setDescription("End Date");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("E");

		newColumn.setPropertyName("status_code");

		newColumn.setDescription("Status");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("F");

		newColumn.setPropertyName("OverallStatus");

		newColumn.setDescription("O");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("G");

		newColumn.setPropertyName("FinancialStatus");

		newColumn.setDescription("F");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("H");

		newColumn.setPropertyName("ScheduleStatus");

		newColumn.setDescription("S");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("I");

		newColumn.setPropertyName("ResourceStatus");

		newColumn.setDescription("R");

		columnsDefault.add(newColumn);

		newColumn = new MetaColumn();

		newColumn.setOrderId("J");

		newColumn.setDescription("Completion Percentage");

		newColumn.setPropertyName("percent_complete");

		columnsDefault.add(newColumn);

		Collections.sort(columnsDefault, new Comparator() {

			public int compare(Object o1, Object o2) {

				MetaColumn p1 = (MetaColumn) o1;

				MetaColumn p2 = (MetaColumn) o2;

				return p1.getOrderId().compareToIgnoreCase(p2.getOrderId());

			}

		});

		return columnsDefault;

	}

	private static PdfPTable createDetailedHeaderSection(
			List<MetaColumn> metaColumns)
			throws DocumentException, IOException {

		float[] columnWidths =  getColumnsWidths(metaColumns);
		PdfPTable detailedTable = new PdfPTable(columnWidths);
		detailedTable.setSpacingAfter(5);
		detailedTable.setSpacingBefore(5);
		detailedTable.setWidthPercentage(100);
		for (MetaColumn metaColumn : metaColumns) {
			PdfPCell headerCell = createHeaderCell(metaColumn.getDescription());
			detailedTable.addCell(headerCell);
		}
		return detailedTable;
	}
	
	private static PdfPTable createDetailedTable(List<MetaColumn> metaColumns)
		throws BadElementException {
		float[] columnWidths =  getColumnsWidths(metaColumns);
		PdfPTable detailedTable = new PdfPTable(columnWidths);
		detailedTable.setSpacingAfter(2);
		detailedTable.setSpacingBefore(2);
		detailedTable.setWidthPercentage(100);
		return detailedTable;
	}
	
	// This method will set small column widths for known narrow columns
	// and give the rest of the columns the same size according to
	// the remaining space
	private static float[] getColumnsWidths(List<MetaColumn> metaColumns)
	{
		float totalWidth = PageSize.A4.rotate().width();
		float narrowColumnWidth = 20f;
		int narrowColumns = 0;
		int columnCount = 0;
		
		// get the number of narrow columns
		for (MetaColumn metaColumn : metaColumns) {
			if (metaColumn != null)
			{
				String propertyName = metaColumn.getPropertyName();
				if((propertyName != null)
						&& (StringUtils.equalsIgnoreCase(propertyName,"OverallStatus")
	    				|| StringUtils.equalsIgnoreCase(propertyName,"FinancialStatus") 
	    				|| StringUtils.equalsIgnoreCase(propertyName,"ScheduleStatus") 
	    				|| StringUtils.equalsIgnoreCase(propertyName,"ResourceStatus"))){
					narrowColumns++;
				}
				columnCount++;
			}
		}
		
		float remainingColumnsWidth = (totalWidth - (narrowColumns * narrowColumnWidth)) / (columnCount - narrowColumns);
		float[] widths = new float[columnCount];
		
		int i = 0;
		for (MetaColumn metaColumn : metaColumns) {
			String propertyName = metaColumn.getPropertyName();
			if(StringUtils.equalsIgnoreCase(propertyName,"OverallStatus")
    				|| StringUtils.equalsIgnoreCase(propertyName,"FinancialStatus") 
    				|| StringUtils.equalsIgnoreCase(propertyName,"ScheduleStatus") 
    				|| StringUtils.equalsIgnoreCase(propertyName,"ResourceStatus")){
				widths[i] = narrowColumnWidth;
			}
			else
			{
				widths[i] = remainingColumnsWidth;
			}
			i++;
		}
		return widths;
	}
	
	private static PdfPCell createHeaderCell(String cellText)
			throws BadElementException {
		PdfPCell detailedCell = new PdfPCell(new Phrase(cellText == null ? ""
				: cellText, FontFactory.getFont(FontFactory.HELVETICA, 11)));
		detailedCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedcell");
		detailedCell.setBorder(Rectangle.NO_BORDER);
		detailedCell.setBorderWidthBottom(1);
		detailedCell.setBorderColor(Color.BLACK);
		detailedCell.setBorderWidth(0.1f);
		detailedCell.setPaddingBottom(5);
		detailedCell.setPaddingTop(5);
		detailedCell.setNoWrap(false);
		detailedCell.setMinimumHeight(0);
		return detailedCell;
	}
	
	private static PdfPCell createBodyCell(String cellText, int colspan)
		throws BadElementException {
		PdfPCell detailedCell = new PdfPCell(new Phrase(cellText == null ? ""
				: cellText, FontFactory.getFont(FontFactory.COURIER, 10)));
		detailedCell.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS,
				"detailedcell");
		detailedCell.setBorder(Rectangle.NO_BORDER);
		detailedCell.setNoWrap(false);
		detailedCell.setMinimumHeight(0);
		detailedCell.setColspan(colspan);
		return detailedCell;
	}

	private static PdfPCell createImageBodyCell(Image image)
			throws BadElementException {
		PdfPCell detailedCell = new PdfPCell(image);
		detailedCell.setBorder(Rectangle.NO_BORDER);
		detailedCell.setMinimumHeight(0);
		detailedCell.setPaddingTop(5);
		detailedCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		return detailedCell;
	}

	public static int getTableColumnCount(List<MetaColumn> metaColumns) {
		return metaColumns.size();
	}

	private static class MyXMLFormatter extends XMLFormatter {
		@Override
		protected void transform(InputStream input, Reader source, Writer result)
		throws XMLFormatException {
			super.transform(input, source, result);
		}
	}

	private static class XslCreator {

		private static String header = "<?xml version='1.0'?>\n"
				+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
				+ "\txmlns:display=\"xalan://net.project.base.property.PropertyProvider\"\n"
				+ "\txmlns:format=\"xalan://net.project.util.XSLFormat\"\n"
				+ "    extension-element-prefixes=\"display format\" >\n"
				+ "\n" + "<xsl:output method=\"html\" indent=\"yes\" />\n"
				+ "\n" + "<xsl:variable name=\"numCols\" select=\"number(";

		private static String header2 = ")\" />\n"
				+ "\n"
				+ "<xsl:template match=\"/\">\n"
				+ "\t<xsl:apply-templates select=\"ProjectXML/Content/PersonalPortfolioViewResults\" />\t\n"
				+ "</xsl:template>\n"
				+ "\n"
				+ "<xsl:template match=\"PersonalPortfolioViewResults\">\n"
				+ "\n"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" name=\"tableWithEvenRows\"  width=\"100%\">\n"
				+ "\t<tr class=\"tableHeader\" align=\"left\" valign=\"top\">\n"
				+ "\t\t<td class=\"tableHeader\" width=\"1%\"><xsl:text disable-output-escaping=\"yes\">&amp;nbsp;</xsl:text></td>\n";

		private static String middle = "\t</tr>\n"

				+ "\t<tr class=\"tableLine\">\n"

				+ "\t\t<td colspan=\"{$numCols}\" class=\"tableLine\">\n"

				+ "\t\t\t<img src=\""

				+ SessionManager.getJSPRootURL()

				+ "/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" />\n"

				+ "\t\t</td>\n"

				+ "\t</tr>\n"

				+ "\t<xsl:apply-templates select=\"PortfolioEntries/ProjectPortfolioEntry\" />\n"

				+ "</table>\n"

				+ "</xsl:template>\n"

				+ "\n"

				+ "<xsl:template match=\"ProjectPortfolioEntry\">\n"

				+ "\n"

				+ "<tr align=\"left\" valign=\"middle\" class=\"tableContent\">\n"

				+ "\t<!-- Radio Option -->\n"

				+ "\t<td class=\"tableContent\">\n"

				+ "\t\t<input type=\"radio\" name=\"selected\" value=\"{project_id}\" />\n"

				+ "\t</td>\n";

		private static String footer = "</tr>\n" 
				+ "</xsl:template>\n"

				+ "<xsl:template match=\"OverallStatus|FinancialStatus|ScheduleStatus|ResourceStatus\">\n"

				+ "    <td class=\"tableContent\" align=\"center\">\n"

				+ "        <xsl:choose>\n"

				+ "            <xsl:when test=\"not(string(ImageURL))\">\n"

				+ "                <img src=\""

				+ SessionManager.getJSPRootURL()

				+ "/images/trans.gif\" width=\"12\" alt=\"\" title=\"\" />\n"

				+ "            </xsl:when>\n"

				+ "            <xsl:otherwise>\n"

				+ "                <xsl:variable name=\"improvementCodeName\" select=\"display:get(ImprovementCode/NameToken)\" />\n"

				+ "                <xsl:variable name=\"colorCodeName\" select=\"display:get(ColorCode/NameToken)\" />\n"

				+ "                <img src=\""

				+ SessionManager.getJSPRootURL()

				+ "{ImageURL}\" title=\"{display:get(ImprovementCode/ImageTitleToken, $improvementCodeName, $colorCodeName)}\" />\n"

				+ "            </xsl:otherwise>\n"

				+ "        </xsl:choose>\n"

				+ "    </td>\n"

				+ "</xsl:template>\n"

				+ "\n"

				+ "</xsl:stylesheet>";

		public static InputStream getXsl(

		PersonalPortfolioViewResults viewResults,

		PersonalPortfolioView personalPortfolioView) {

			return new ByteArrayInputStream(generateXslString(viewResults,

			personalPortfolioView).getBytes());

		}

		private static String getHeaderCell(MetaColumn metaColumn) {

			String additionalAttributes = "";

			if (metaColumn.getColumnWidth() > 0) {

				additionalAttributes += " width=\""

				+ metaColumn.getColumnWidth() + "%\"";

			}

			if (!metaColumn.getDescription().equals(

			metaColumn.getShortDescription())) {

				additionalAttributes += " title=\""

				+ metaColumn.getDescription() + "\"";

			}

			if ("percent_complete".equals(metaColumn.getPropertyName())) {

				additionalAttributes += " colspan=\"2\"";

			}

			if (!"name".equals(metaColumn.getPropertyName())) {

				additionalAttributes += " align=\"center\" ";

			}

			String sortLinkStart = "";

			String sortLinkEnd = "";

			if (metaColumn.isMetaProperty()) {

				sortLinkStart = "<a class=\"tableHeader\" href=\"javascript:doProjectSort('meta"

						+ metaColumn.getPropertyName() + "', 'string', '" 
						+ SessionManager.getJSPRootURL() + "', '" + view.getID() + "', '" 
						+ oldSortColumn+ "', '" + oldSortOrder +	"')\">";

				sortLinkEnd = "</a>";

			} else if ("description".equals(metaColumn.getPropertyName())

			|| "ParentBusinessName"

			.equals(metaColumn.getPropertyName())

			|| "sponsor".equals(metaColumn.getPropertyName())

			|| "StartDate".equals(metaColumn.getPropertyName())

			|| "EndDate".equals(metaColumn.getPropertyName())

			|| "name".equals(metaColumn.getPropertyName())

			|| "PriorityCode".equals(metaColumn.getPropertyName())

			|| "RiskRatingCode".equals(metaColumn.getPropertyName())) {

				sortLinkStart = "<a class=\"tableHeader\" href=\"javascript:doProjectSort('"

					+ metaColumn.getPropertyName() + "', 'string', '" 
					+ SessionManager.getJSPRootURL() + "', '" + view.getID() + "', '" 
					+ oldSortColumn+ "', '" + oldSortOrder +	"')\">";

				sortLinkEnd = "</a>";

			}

			return "<td class=\"tableHeader\" " + additionalAttributes + ">"

			+ sortLinkStart + metaColumn.getShortDescription()

			+ sortLinkEnd + "</td>\n";

		}

		private static String getContentCell(MetaColumn metaColumn) {

			String additionalAttributes = "";

			if (metaColumn.isNoWrap()) {

				additionalAttributes += " nowrap=\"yes\"";

			}

			if ("name".equals(metaColumn.getPropertyName())) {

				return "\t<td align=\"left\">\n"

						+ "    \t<a href=\"../project/Dashboard?id={project_id}\"><xsl:value-of select=\"name\"/></a>\n"

						+ "\t</td>\n";

			} else if ("OverallStatus".equals(metaColumn.getPropertyName())

			|| "FinancialStatus".equals(metaColumn.getPropertyName())

			|| "ScheduleStatus".equals(metaColumn.getPropertyName())

			|| "ResourceStatus".equals(metaColumn.getPropertyName())) {

				return "<xsl:apply-templates select=\""

				+ metaColumn.getPropertyName() + "\" />";

			} else if ("percent_complete".equals(metaColumn.getPropertyName())) {

				return "<td class=\"tableContent\" width=\"110\" align=\"left\">\n"

				+   "	<div class=\"progress-bar-container\">\n"
				+	"		<div class=\"progress-line\" style=\"width:{format:formatPercent(percent_complete, 0, 2)}\"></div>\n"
				+	"   </div>\n"	
				
				+ "\t</td>\n"

				+ "\t<td class=\"tableContent\" align=\"left\" ><xsl:value-of select=\"format:formatPercent(percent_complete, 0, 0)\"/></td>";

			} else {

				return "<td class=\"tableContent\" align=\"left\" "

				+ additionalAttributes + "><xsl:value-of select=\""

				+ getXslValueOfSelect(metaColumn) + "\" /></td>";

			}

		}

		private static String getXslValueOfSelect(MetaColumn metaColumn) {

			if (metaColumn.isMetaProperty()) {

				return "Meta/" + metaColumn.getPropertyName();

			} else if ("StartDate".equals(metaColumn.getPropertyName())

			|| "EndDate".equals(metaColumn.getPropertyName())) {

				return "format:formatISODate(" + metaColumn.getPropertyName()

				+ ")";

			} else if ("PriorityCode".equals(metaColumn.getPropertyName())) {

				return "display:get(" + metaColumn.getPropertyName()

				+ "/PriorityCode/NameToken)";

			} else if ("RiskRatingCode".equals(metaColumn.getPropertyName())) {

				return "display:get(" + metaColumn.getPropertyName()

				+ "/RiskCode/NameToken)";

			} else {

				return metaColumn.getPropertyName();

			}

		}

		private static String generateXslString(

		PersonalPortfolioViewResults viewResults,

		PersonalPortfolioView personalPortfolioView) {

			FinderIngredients finderIngredients = viewResults.getIngredients();

			if (finderIngredients instanceof PersonalPortfolioFinderIngredients) {

				PersonalPortfolioFinderIngredients ppfi = (PersonalPortfolioFinderIngredients) finderIngredients;

				MetaColumnList metaColumnList = ppfi.getMetaColumnList();

				List<MetaColumn> metaColumns = metaColumnList

				.getSortedIncludedColumns();

				// we should use the old scheme on the views created on versions

				// prior to 8.3

				// if the view contains only 'name' column then it is probably

				// created on pre 8.3 version as

				// there is no need to create a view with just 'name' column

				boolean isCreatedOnPre83 = false;

				if (metaColumns.size() == 0) {

					isCreatedOnPre83 = true;

				}

				if (metaColumns.size() == 1) {

					if ("name".equals(metaColumns.get(0).getPropertyName())) {

						isCreatedOnPre83 = true;

						try {

							if (personalPortfolioView.getCreatedDate().after(

							new Date(1194983462078L))) {

								isCreatedOnPre83 = false;

							}

						} catch (Exception ignored) {

						}

					}

				}

				if (!isCreatedOnPre83) {

					StringBuffer xsl = new StringBuffer();

					xsl.append(header);

					xsl.append(getTableColumnCount(metaColumns));

					xsl.append(header2);

					for (MetaColumn metaColumn : metaColumns) {

						xsl.append(getHeaderCell(metaColumn));

					}

					xsl.append(middle);

					for (MetaColumn metaColumn : metaColumns) {

						xsl.append(getContentCell(metaColumn));

					}

					xsl.append(footer);

					return xsl.toString();

				}

			}

			return "<?xml version='1.0'?>\n"

					+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"

					+ "\txmlns:display=\"xalan://net.project.base.property.PropertyProvider\"\n"

					+ "\txmlns:format=\"xalan://net.project.util.XSLFormat\"\n"

					+ "    extension-element-prefixes=\"display format\" >\n"

					+ "\n"

					+ "<xsl:output method=\"html\" indent=\"yes\" />\n"

					+ "\n"

					+ "<xsl:variable name=\"numCols\" select=\"number(12)\" />\n"

					+ "\n"

					+ "<xsl:template match=\"/\">\n"

					+ "\t<xsl:apply-templates select=\"ProjectXML/Content/PersonalPortfolioViewResults\" />\t\n"

					+ "</xsl:template>\n"

					+ "\n"

					+ "<xsl:template match=\"PersonalPortfolioViewResults\">\n"

					+ "\n"

					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" name=\"tableWithEvenRows\" width=\"100%\">\n"

					+ "\t<tr class=\"tableHeader\" align=\"left\" valign=\"top\">\n"

					+ "\t\t<td class=\"tableHeader\" width=\"1%\"><xsl:text disable-output-escaping=\"yes\">&amp;nbsp;</xsl:text></td>\n"

					+ "\t\t<td class=\"tableHeader\"><xsl:value-of select=\"display:get('prm.project.portfolio.column.projectname.label')\" /></td>\n"

					+ "\t\t<td class=\"tableHeader\"><xsl:value-of select=\"display:get('prm.project.portfolio.column.businessname.label')\" /></td>\n"

					+ "        <td class=\"tableHeader\"><xsl:value-of select=\"display:get('prm.project.portfolio.column.startdate.label')\" /></td>\n"

					+ "        <td class=\"tableHeader\"><xsl:value-of select=\"display:get('prm.project.portfolio.column.enddate.label')\" /></td>\n"

					+ "\t\t<td class=\"tableHeader\"><xsl:value-of select=\"display:get('prm.project.portfolio.column.status.label')\" /></td>\n"

					+ "        <!-- Overall Status -->\n"

					+ "        <td class=\"tableHeader\" align=\"center\" title=\"{display:get('prm.project.portfolio.column.overallstatus.title')}\">\n"

					+ "            <xsl:value-of select=\"display:get('prm.project.portfolio.column.overallstatus.label')\" />\n"

					+ "        </td>\n"

					+ "        <!-- Financial Status -->\n"

					+ "        <td class=\"tableHeader\" align=\"center\" title=\"{display:get('prm.project.portfolio.column.financialstatus.label.title')}\">\n"

					+ "            <xsl:value-of select=\"display:get('prm.project.portfolio.column.financialstatus.label')\" />\n"

					+ "        </td>\n"

					+ "        <!-- Schedule Status -->\n"

					+ "        <td class=\"tableHeader\" align=\"center\" title=\"{display:get('prm.project.portfolio.column.schedulestatus.label.title')}\">\n"

					+ "            <xsl:value-of select=\"display:get('prm.project.portfolio.column.schedulestatus.label')\" />\n"

					+ "        </td>\n"

					+ "        <!-- Resource Status -->\n"

					+ "        <td class=\"tableHeader\" align=\"center\" title=\"{display:get('prm.project.portfolio.column.resourcestatus.label.title')}\">\n"

					+ "            <xsl:value-of select=\"display:get('prm.project.portfolio.column.resourcestatus.label')\" />\n"

					+ "        </td>\n"

					+ "\t\t<td colspan=\"2\" class=\"tableHeader\">\n"

					+ "            <xsl:value-of select=\"display:get('prm.project.portfolio.column.percentcomplete.label.title')\" />\n"

					+ "\t\t</td>\n" + "\t</tr>\n"

					+ "\t<tr class=\"tableLine\">\n"

					+ "\t\t<td colspan=\"{$numCols}\" class=\"tableLine\">\n"

					+ "\t\t\t<img src=\""

					+ SessionManager.getJSPRootURL()

					+ "/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" />\n"

					+ "\t\t</td>\n"

					+ "\t</tr>\n"

					+ "\t<xsl:apply-templates select=\"PortfolioEntries/ProjectPortfolioEntry\" />\n"

					+ "</table>\n"

					+ "</xsl:template>\n"

					+ "\n"

					+ "<xsl:template match=\"ProjectPortfolioEntry\">\n"

					+ "\n"

					+ "<tr align=\"left\" valign=\"middle\" class=\"tableContent\">\n"

					+ "\t<!-- Radio Option -->\n"

					+ "\t<td class=\"tableContent\">\n"

					+ "\t\t<input type=\"radio\" name=\"selected\" value=\"{project_id}\" />\n"

					+ "\t</td>\n"

					+ "\t<td align=\"left\">\n"

					+ "    \t<a href=\"../project/Dashboard?id={project_id}\"><xsl:value-of select=\"name\"/></a>\n"

					+ "\t</td>\n"

					+ "\t<td class=\"tableContent\" align=\"left\"><xsl:text disable-output-escaping=\"yes\">&amp;nbsp;</xsl:text><xsl:value-of select=\"ParentBusinessName\"/></td>\n"

					+ "    <td class=\"tableContent\" align=\"left\"><xsl:value-of select=\"format:formatISODate(StartDate)\" /></td>\n"

					+ "    <td class=\"tableContent\" align=\"left\"><xsl:value-of select=\"format:formatISODate(EndDate)\" /></td>\n"

					+ "    <td class=\"tableContent\" align=\"left\"><xsl:value-of select=\"status_code\" /></td>\n"

					+ "    <xsl:apply-templates select=\"OverallStatus\" />\n"

					+ "    <xsl:apply-templates select=\"FinancialStatus\" />\n"

					+ "    <xsl:apply-templates select=\"ScheduleStatus\" />\n"

					+ "    <xsl:apply-templates select=\"ResourceStatus\" />\n"

					+ "    <!-- Percentage complete horizontal bar -->\n"

					+ "\t<td class=\"tableContent\" width=\"110\" align=\"left\">\n"

					+ "\t\t<table border=\"1\" width=\"100\" height=\"8\" cellspacing=\"0\" cellpadding=\"0\">\n"

					+ "\t\t<tr>\n"

					+ "\t\t\t<td bgcolor=\"#FFFFFF\" title=\"{format:formatPercent(percent_complete, 0, 2)}\">\n"

					+ "\t\t\t\t<xsl:variable name=\"percentageWidth\">\n"

					+ "\t\t\t\t\t<xsl:choose>\n"

					+ "                    \t<!-- A zero width box will render 1 pixel wide on IE and 8 pixels wide on\n"

					+ "                             Netscape.  Let's make all of them display one pixel. -->\n"

					+ "                        <xsl:when test=\"percent_complete = 0\">1</xsl:when>\n"

					+ "                        <xsl:when test=\"percent_complete = ''\">1</xsl:when>\n"

					+ "                        <xsl:otherwise><xsl:value-of select=\"format:formatPercent(percent_complete, 0, 0)\"/></xsl:otherwise>\n"

					+ "\t\t\t\t\t</xsl:choose>\n"

					+ "\t\t\t\t</xsl:variable>\n"

					+ "\t\t\t\t<img src=\""

					+ SessionManager.getJSPRootURL()

					+ "/images/lgreen.gif\" width=\"{$percentageWidth}\" height=\"8\" />\n"

					+ "\t\t\t</td>\n"

					+ "\t\t</tr>\n"

					+ "\t\t</table>\n"

					+ "\t</td>\n"

					+ "\t<td class=\"tableContent\" align=\"left\" ><xsl:value-of select=\"format:formatPercent(percent_complete, 0, 0)\"/></td>\n"

					+ "</tr>\n"

					+ "<tr class=\"tableLine\">\n"

					+ "\t<td colspan=\"{$numCols}\">\n"

					+ "\t\t<img src=\""

					+ SessionManager.getJSPRootURL()

					+ "/images/spacers/trans.gif\" width=\"1\" height=\"1\" border=\"0\" />\n"

					+ "\t</td>\n"

					+ "</tr>\n"

					+ "</xsl:template>\n"

					+ "\n"

					+ "<xsl:template match=\"OverallStatus|FinancialStatus|ScheduleStatus|ResourceStatus\">\n"

					+ "    <td class=\"tableContent\" align=\"center\">\n"

					+ "        <xsl:choose>\n"

					+ "            <xsl:when test=\"not(string(ImageURL))\">\n"

					+ "                <img src=\""

					+ SessionManager.getJSPRootURL()

					+ "/images/trans.gif\" width=\"12\" alt=\"\" title=\"\" />\n"

					+ "            </xsl:when>\n"

					+ "            <xsl:otherwise>\n"

					+ "                <xsl:variable name=\"improvementCodeName\" select=\"display:get(ImprovementCode/NameToken)\" />\n"

					+ "                <xsl:variable name=\"colorCodeName\" select=\"display:get(ColorCode/NameToken)\" />\n"

					+ "                <img src=\""

					+ SessionManager.getJSPRootURL()

					+ "{ImageURL}\" title=\"{display:get(ImprovementCode/ImageTitleToken, $improvementCodeName, $colorCodeName)}\" />\n"

					+ "            </xsl:otherwise>\n"

					+ "        </xsl:choose>\n"

					+ "    </td>\n"

					+ "</xsl:template>\n" + "\n" + "</xsl:stylesheet>";

		}

	}

	public static String getHtml(PersonalPortfolioViewResults viewResults,
								PersonalPortfolioView personalPortfolioView, 
								String oldSortCol, String oldSortOrd) {
		view = personalPortfolioView;
		oldSortColumn = oldSortCol;
		oldSortOrder = oldSortOrd;
		return getHtml(viewResults, personalPortfolioView);
	}


}