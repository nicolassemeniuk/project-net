<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"
    	extension-element-prefixes="display format" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<!-- Begin content handlers -->

<!--
	DOCUMENT
-->

<xsl:template match="document">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<xsl:for-each select="document_properties">
		<tr>
    		<th align="left" colspan="11"></th>
	    </tr>
	    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.docname.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
			<td align="left" class="tableContent"><xsl:value-of select="name"/></td>

            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.docdescription.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        <td align="left" class="tableContent" colspan="4"><xsl:value-of select="./description"/></td>

	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        <td align="right" class="tableContent">
		        <xsl:if test="name != ''">
				    <a target="view_window" href="{jsp_root_url}/servlet/DownloadDocument?id={./object_id}">
				    <xsl:choose>
						<xsl:when test="app_icon_url != ''">
							<img align="bottom" border="0" src="{jsp_root_url}{./app_icon_url}"/>	
						</xsl:when>
					</xsl:choose>
				    <xsl:value-of select="display:get('prm.workflow.envelope.include.content.download.link')"/>
	                </a>
                </xsl:if>
			</td>
		</tr>
	    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.filename.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        <td align="left" class="tableContent"><xsl:value-of select="./short_file_name"/></td>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.fileformat.label')"/></th>
            <td align="left" ><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left"  class="tableContent"><xsl:value-of select="./file_format"/></td>
            <th align="right"  class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.filesize.label')"/></th>
	        <td align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	        <td align="left" class="tableContent"><xsl:value-of select="./file_size"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="right" class="tableContent">
            	<xsl:if test="name != ''">
	            	<a href="{url}"><xsl:value-of select="display:get('prm.workflow.envelope.include.content.moredetails.link')"/></a>
            	</xsl:if>
            </td>
	    </tr>

	</xsl:for-each>
</table>
</xsl:template>
<xsl:template match="Form">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
    		<th align="left" colspan="11"></th>
	    </tr>
	    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.form.main.name.column')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.form.main.abbrev.column')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.form.main.description.column')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		</tr>
		<tr class="tableLine">
			<td colspan="6" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
	    <tr>
			<td align="left" class="tableContent"><a href="{url}"><xsl:value-of select="name"/></a></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

	        <td align="left" class="tableContent" ><xsl:value-of select="./abbreviation"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <td align="left" class="tableContent" colspan="4"><xsl:value-of select="./description"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
		</tr>
		
	</table>	
</xsl:template>
<xsl:template match="deliverable">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
    		<th align="left" colspan="11"></th>
	    </tr>
	    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.viewphase.deliverables.name.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.status.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.description.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		</tr>
		<tr class="tableLine">
			<td colspan="6" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
	    <tr>
			<td align="left" class="tableContent"><a href="{jsp_root_url}/process/ViewDeliverable.jsp?action=1&amp;module=40&amp;id={deliverable_id}"><xsl:value-of select="deliverable_name"/></a></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

	        <td align="left" class="tableContent" ><xsl:value-of select="./status"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <td align="left" class="tableContent" colspan="4"><xsl:value-of select="./deliverable_desc"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
		</tr>
		
	</table>	
</xsl:template>
<xsl:template match="phase">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
    		<th align="left" colspan="11"></th>
	    </tr>
	    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.name.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.status.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.modifyphase.description.label')"/></th>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		</tr>
		<tr class="tableLine">
			<td colspan="6" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
	    <tr>
	    	<td align="left" class="tableContent"><a href="{phase_url}"><xsl:value-of select="phase_name"/></a></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>

	        <td align="left" class="tableContent" ><xsl:value-of select="./status"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
	        <td align="left" class="tableContent" colspan="4"><xsl:value-of select="./phase_desc"/></td>
	        <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	        
		</tr>
		
	</table>	
</xsl:template>
</xsl:stylesheet>
