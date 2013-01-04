<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"
		extension-element-prefixes="display format">

<xsl:output method="html" cdata-section-elements="message"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="news_list">
<table border="0" cellspacing="0" cellpadding="0" width="100%" name="tableWithEvenRows">
  <xsl:apply-templates select="news"/>
  <xsl:if test="count(news)=0"><xsl:call-template name="no_news" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="news">	
	<tr class="tableContent" align="left"> 
		<td class="tableContent" align="left">
			<a href="{../jsp_root_url}/news/NewsView.jsp?module=110&amp;id={news_id}"><xsl:value-of select="topic"/></a>
		</td>
		<td class="tableContent" align="right">
			<xsl:variable name="dateTime" select="format:formatISODate(posted_datetime)"/>
			<xsl:value-of select="display:get($dateTime) "/>
		</td>
	</tr>
	<tr>
		<td class="tableContent" colspan="2">
			<xsl:value-of select="display:get(string(posted_by_full_name)) "/>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="tableContent">
			<i>
                <!-- Wraps message truncating at appropariate length and number of paragraphs (linefeeds)
                     format text as hyperlink -->
                <xsl:value-of select="format:formatTextHyperlink(message, 50, 10)" disable-output-escaping="yes"/>
            </i>
            <!-- Include "read more" link if the text was truncated -->
            <xsl:choose>
                <xsl:when test="format:isTruncationRequired(message, 50, 10)">
                    ...
                    <a href="{../jsp_root_url}/news/NewsView.jsp?module=110&amp;id={news_id}">
                        <xsl:value-of select="display:get('prm.news.newschannel.readmore.label') "/>
                    </a>
                </xsl:when>
            </xsl:choose>
		</td>
	</tr>
	<tr class="tableLine">
	      <td colspan="2" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

<xsl:template match="priority_id">
	<xsl:choose>
		<xsl:when test=".!=200">
			<xsl:value-of select="../priority_name" />:
		</xsl:when>
		<xsl:otherwise>
			<!--<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>-->
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="no_news">
<tr class="tableContent" align="left"> 
  <td class="tableContent" colspan="5"><xsl:value-of select="display:get('prm.news.newschannel.nonews.message')"/></td>
</tr>
</xsl:template>

</xsl:stylesheet>