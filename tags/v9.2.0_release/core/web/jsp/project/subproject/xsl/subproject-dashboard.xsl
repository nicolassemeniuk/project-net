<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property"/>
<xsl:variable name="radioOptionName" select="$translation[@name='radioOptionName']" />
<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

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
  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
			<xsl:if test="string($radioOptionName)">
                <td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            </xsl:if>
			<td class="tableHeader"><xsl:value-of select="display:get('prm.project.subprojectportfolio.project.label')"/></td>
            <!-- Overall Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.overallstatus.title')}">
                <xsl:value-of select="display:get('prm.project.portfolio.column.overallstatus.label')" />
            </td>
            <!-- Financial Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.financialstatus.label.title')}">
                <xsl:value-of select="display:get('prm.project.portfolio.column.financialstatus.label')" />
            </td>
            <!-- Schedule Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.schedulestatus.label.title')}">
                <xsl:value-of select="display:get('prm.project.portfolio.column.schedulestatus.label')" />
            </td>
            <!-- Resource Status -->
            <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.resourcestatus.label.title')}">
                <xsl:value-of select="display:get('prm.project.portfolio.column.resourcestatus.label')" />
            </td>
			<td colspan="2" class="tableHeader"><xsl:value-of select="display:get('prm.project.subprojectportfolio.percentcomplete.label')"/></td>
		</tr>
		<tr class="tableLine">
			<td colspan="{$numCols}" class="tableLine">
		        <img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="2" border="0" />
			</td>
		</tr>
		<xsl:apply-templates select="ProjectSpace"/>
	</table>
	</xsl:template>
	
	<xsl:template match="ProjectSpace">
		<tr align="left" valign="middle" class="tableContent">
			<xsl:if test="string($radioOptionName)">
    			<td class="tableContent">
                    <input type="radio" name="{$radioOptionName}" value="{project_id}" />
                </td>
            </xsl:if>
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href"><xsl:value-of select="$JSPRootURL" />/project/Dashboard?id=<xsl:value-of select="project_id"/></xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            <xsl:apply-templates select="OverallStatus" />
            <xsl:apply-templates select="FinancialStatus" />
            <xsl:apply-templates select="ScheduleStatus" />
            <xsl:apply-templates select="ResourceStatus" />
			<td class="tableContent" NOWRAP="1" width="110">
        		<table border="1" width="100" height="8" cellspacing="0" cellpadding="0">
        		<tr>
			        <td bgcolor="#FFFFFF" title="{format:formatPercent(percent_complete, 0, 2)}">
				        <xsl:variable name="percentageWidth">
					        <xsl:choose>
                    	        <!-- A zero width box will render 1 pixel wide on IE and 8 pixels wide on
                                    Netscape.  Let's make all of them display one pixel. -->
                                <xsl:when test="percent_complete = ''">1</xsl:when>
                                <xsl:when test="percent_complete = 0">1</xsl:when>
                                <xsl:otherwise><xsl:value-of select="percent_complete"/></xsl:otherwise>
					        </xsl:choose>
				        </xsl:variable>
				        <img src="{$JSPRootURL}/images/lgreen.gif" width="{$percentageWidth}" height="8" />
			        </td>
		        </tr>
		        </table>
			</td>
			<td class="tableContent" align="left" ><xsl:value-of select="format:formatPercent(percent_complete, 0, 0)"/></td>
         </tr>
        <tr class="tableLine">
	        <td colspan="{$numCols}">
		        <img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" />
	        </td>
        </tr>
	</xsl:template>

<xsl:template match="OverallStatus|FinancialStatus|ScheduleStatus|ResourceStatus">
    <td class="tableContent" align="center">
        <xsl:choose>
            <xsl:when test="not(string(ImageURL))">
                <img src="{$JSPRootURL}/images/trans.gif" width="12" alt="" title="" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="improvementCodeName" select="display:get(ImprovementCode/NameToken)" />
                <xsl:variable name="colorCodeName" select="display:get(ColorCode/NameToken)" />
                <img src="{$JSPRootURL}{ImageURL}" title="{display:get(ImprovementCode/ImageTitleToken, $improvementCodeName, $colorCodeName)}" />
            </xsl:otherwise>
        </xsl:choose>
    </td>
</xsl:template>
	
</xsl:stylesheet>
