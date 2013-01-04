<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property"/>
<xsl:variable name="radioOptionName" select="$translation[@name='radioOptionName']" />

<xsl:variable name="numCols">
    <xsl:choose>
        <xsl:when test="string($radioOptionName)">11</xsl:when>
        <xsl:otherwise>10</xsl:otherwise>
    </xsl:choose>
</xsl:variable>

<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML" />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content/ProjectPortfolio" />
</xsl:template>

  <xsl:template match="ProjectPortfolio">
  	<table cellpadding="0" cellspacing="0" border="0" width="100%" name="tableWithEvenRows">
		<tr class="tableHeader" align="left" valign="top">
			<xsl:if test="string($radioOptionName)">
                <td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            </xsl:if>
			<td class="tableHeader"><xsl:value-of select="display:get('prm.business.project.portfolio.project.column')"/></td>
            <td class="tableHeader" colspan="2"><xsl:value-of select="display:get('prm.business.project.portfolio.completionpercentage.column')"/></td>
			<!-- Overall Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.overallstatus.title')}" width="18px">
                <xsl:value-of select="display:get('prm.project.portfolio.column.overallstatus.label')" />
            </td>
            <!-- Financial Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.financialstatus.label.title')}" width="18px">
                <xsl:value-of select="display:get('prm.project.portfolio.column.financialstatus.label')" />
            </td>
            <!-- Schedule Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.schedulestatus.label.title')}" width="18px">
                <xsl:value-of select="display:get('prm.project.portfolio.column.schedulestatus.label')" />
            </td>
            <!-- Resource Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.resourcestatus.label.title')}" width="18px">
                <xsl:value-of select="display:get('prm.project.portfolio.column.resourcestatus.label')" />
            </td>
		</tr>
    	<xsl:apply-templates select="ProjectPortfolioEntry"/>
	</table>
	</xsl:template>

	<xsl:template match="ProjectPortfolioEntry">
		<tr align="left" valign="middle" class="tableContent">
			<xsl:if test="string($radioOptionName)">
    			<td class="tableContent">
                    <input type="radio" name="{$radioOptionName}" value="{project_id}" />
                </td>
            </xsl:if>
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="class">project</xsl:attribute>
					<xsl:attribute name="href">../project/Dashboard?id=<xsl:value-of select="./project_id"/></xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            <td class="tableContent" width="30%" nowrap="nowrap">
            <xsl:variable name="percentageWidth">
		       		<xsl:choose>
                    	<xsl:when test="percent_complete = ''">1</xsl:when>
                    	<xsl:when test="percent_complete = 0">1</xsl:when>
                    	<xsl:otherwise><xsl:value-of select="format:formatPercent(percent_complete,0,0)"/></xsl:otherwise>
			        </xsl:choose>
		        </xsl:variable>
		        <xsl:variable name="percentComplete" select="format:formatPercent(percent_complete,0,2)" />
		        <div class="progress-bar-container" title="{$percentComplete}">
		    		<div class="progress-line" style="width:{$percentageWidth};" title="{$percentComplete}"></div>
		    		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		    	</div>		    	
		    </td>
		    <td width="8%"> <xsl:value-of select="format:formatPercent(percent_complete,0,2)" /> </td>
			<xsl:apply-templates select="OverallStatus" />
            <xsl:apply-templates select="FinancialStatus" />
            <xsl:apply-templates select="ScheduleStatus" />
            <xsl:apply-templates select="ResourceStatus" />
         </tr>
	</xsl:template>

<xsl:template match="OverallStatus|FinancialStatus|ScheduleStatus|ResourceStatus">
    <td class="tableContent" align="center">
        <xsl:choose>
            <xsl:when test="not(string(ImageURL))">
                <img src="../images/trans.gif" width="12" alt="" title="" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="improvementCodeName" select="display:get(ImprovementCode/NameToken)" />
                <xsl:variable name="colorCodeName" select="display:get(ColorCode/NameToken)" />
                <img src="..{ImageURL}" title="{display:get(ImprovementCode/ImageTitleToken, $improvementCodeName, $colorCodeName)}" />
            </xsl:otherwise>
        </xsl:choose>
    </td>
</xsl:template>

</xsl:stylesheet>
