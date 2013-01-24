<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<!--
	 
	     $RCSfile$
	    $Revision: 17022 $
	        $Date: 2008-03-09 15:58:28 -0200 (dom, 09 mar 2008) $
	      $Author: avinash $
	 
	  Workflow Designer translates a workflow_menu structure and
	  includes radio group for selecting a workflow.
	-->

<xsl:template match="*|/">
		<xsl:apply-templates/>
	</xsl:template>

<xsl:template match="text()|@*">
		<xsl:value-of select="."/>
	</xsl:template>

<xsl:template match="configurationPortfolio">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<tr class="tableHeader" align="left">
				<th class="tableHeader">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</th>
				<th class="tableHeader">Name</th>
				<th class="tableHeader">Description</th>
			</tr>
			<tr class="tableLine">
				<td colspan="3" class="tableLine">
					<img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/>
				</td>
			</tr>
			<!-- Insert line items or a message if there are none -->
			<xsl:apply-templates select="configurationSpace"/>
			<xsl:if test="count(configurationSpace)=0">
				<xsl:call-template name="no_configurations"/>
			</xsl:if>
		</table>
	</xsl:template>

<xsl:template match="configurationSpace">
		<tr class="tableContent" align="left">
			<td class="tableContent">
				<xsl:variable name="mypos" select="position()"/>
				<xsl:apply-templates select="id">
					<xsl:with-param name="pos" select="$mypos"/>
				</xsl:apply-templates>
			</td>
			<td class="tableContent">
				<a href="javascript:view(&apos;{id}&apos;);">
					<xsl:value-of select="name"/>
				</a>
			</td>
			<td class="tableContent">
				<xsl:value-of select="description"/>
			</td>
		</tr>
		<tr class="tableLine">
			<td colspan="3" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
			</td>
		</tr>
	</xsl:template>

<xsl:template match="description">
		<xsl:apply-templates/>
	</xsl:template>

<xsl:template match="name">
		<xsl:apply-templates/>
	</xsl:template>

<xsl:template match="id">
		<xsl:param name="pos"/>
		<xsl:choose>
			<xsl:when test="$pos=1">
				<input name="configuration_id" type="radio" value="{.}" checked=""/>
			</xsl:when>
			<xsl:otherwise>
				<input name="configuration_id" type="radio" value="{.}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<xsl:template name="no_configurations">
		<tr class="tableContent" align="left">
			<td class="tableContent" colspan="3">No configurations have been defined.
				<br/>
				<a href="javascript:create()">Create</a> first configuration.</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
