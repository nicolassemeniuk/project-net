<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="news_list">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="tableHeader" align="left">
    <td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    <td class="tableHeader"><a href="javascript:sort(0);" class="tableHeader"><xsl:value-of select="display:get('prm.news.main.priority.link')"/></a></td>
    <td class="tableHeader"><a href="javascript:sort(1);" class="tableHeader"><xsl:value-of select="display:get('prm.news.main.topic.link')"/></a></td>
    <td class="tableHeader" nowrap="true"><a href="javascript:sort(2);" class="tableHeader"><xsl:value-of select="display:get('prm.news.main.postedby.link')"/></a></td>
    <td class="tableHeader" nowrap="true"><a href="javascript:sort(3);" class="tableHeader"><xsl:value-of select="display:get('prm.news.main.postedon.link')"/></a></td>
  </tr>
  <tr class="tableLine">
    <td colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
  </tr>
  <xsl:apply-templates select="news"/>
  <xsl:if test="count(news)=0"><xsl:call-template name="no_news" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="news">	<tr class="tableContent" align="left"> 
		<td class="tableContent">
			<input type="radio" name="selected" value="{news_id}" />
		</td>
		<td class="tableContent"><xsl:value-of select="priority_name"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td class="tableContent">
			<a href="{../jsp_root_url}/news/NewsView.jsp?module=110&amp;id={news_id}"><xsl:value-of select="topic"/></a>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</td>
		<td class="tableContent" nowrap="true"><xsl:value-of select="posted_by_full_name"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
		<td class="tableContent" nowrap="true"><xsl:value-of select="format:formatISODate(posted_datetime)"/></td>
	</tr>
	<tr class="tableLine">
	      <td colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

<xsl:template match="priority_id">
	<xsl:choose>
		<xsl:when test=".!=200">
			<xsl:value-of select="../priority_name" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="no_news">
<tr class="tableContent" align="left"> 
  <td class="tableContent" colspan="4"><xsl:value-of select="display:get('prm.news.main.nonews.message')"/></td>
</tr>
</xsl:template>

</xsl:stylesheet>
