<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="envelope_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader" align="left">
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.title.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.description.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.currentstep.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.currentstatus.label')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.lastchanged.label')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes"><xsl:value-of select="display:get('prm.workflow.envelope.abort.label')"/></xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="envelope"/>
	<xsl:if test="count(envelope)=0"><xsl:call-template name="no_envelopes" /></xsl:if>
</table>
</xsl:template>
<xsl:variable name="abortEnvelope"><xsl:value-of select="display:get('prm.workflow.envelope.abort.message')"/></xsl:variable> 
<xsl:template match="envelope">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent">
    <xsl:element name="a">
      <xsl:attribute name="href">
		   <xsl:value-of select="../jsp_root_url" />/workflow/envelope/EnvelopeProperties.jsp?id=<xsl:value-of select="envelope_id"/>
		 </xsl:attribute>
      <xsl:apply-templates select="name"/>
    </xsl:element>
  </td>
  <td class="tableContent"><xsl:apply-templates select="description" /></td>
  <td class="tableContent"><xsl:apply-templates select="current_version/step_name"/></td>
  <td class="tableContent"><xsl:apply-templates select="current_version/status_name" /></td>
  <td class="tableContent">
		<!-- If modified datetime is empty, use created_datetime instead -->
		<xsl:choose>
			<xsl:when test="modified_datetime=''"><xsl:value-of select="format:formatISODateTime(created_datetime)"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="format:formatISODateTime(modified_datetime)"/></xsl:otherwise>
		</xsl:choose>
	</td>
<xsl:choose>
	<xsl:when test="contains(current_version/status_name,'Cancelled')"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:when>
	<xsl:otherwise>  <td class="tableContent"><xsl:text disable-output-escaping="yes"><a href="javascript:abortEnvelope({./envelope_id}, 'envelope');"> <img src="../images/icons/toolbar-gen-cancel_on.gif" title="{$abortEnvelope}" border="0" alt="" /></a></xsl:text></td>
</xsl:otherwise>
</xsl:choose>


</tr>
<tr class="tableLine">
  <td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_envelopes">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="5"><xsl:value-of select="display:get('prm.workflow.envelope.include.envelopelist.noenvelopes.message')"/></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

<xsl:template match="current_status_name"><xsl:apply-templates/></xsl:template>

<xsl:template match="modified_datetime"><xsl:apply-templates/></xsl:template>

<xsl:template match="current_step_name"><xsl:apply-templates/></xsl:template>

<xsl:template match="description"><xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
