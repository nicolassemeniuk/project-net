<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
 <xsl:template match="BusinessPortfolio">
<tr align="left" valign="middle" class="tableContent">
<td content="tableContent"><xsl:text>	</xsl:text>Business Name</td>
<td class="tableContent">
<select name="selected" SIZE="1">
	<xsl:apply-templates select="BusinessSpace"/>
			<xsl:if test="count(BusinessSpace)=0">
            	<xsl:call-template name="NoBusinesses"/>
        	</xsl:if>
</select>			
	</td>
		</tr>		
</xsl:template>
  <xsl:template match="BusinessPortfolio/BusinessSpace">
		 <option value="{businessID}"> <xsl:value-of select="./name"/>
		 </option>
  </xsl:template>
 <xsl:template name="NoBusinesses">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> No Such Business found</td>
        </tr>
    </xsl:template>
</xsl:stylesheet>

