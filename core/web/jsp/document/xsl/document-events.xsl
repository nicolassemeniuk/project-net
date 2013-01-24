<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"		
    	extension-element-prefixes="display format" >
<xsl:output method="html"/>

  <xsl:template match="/">

       <xsl:for-each select="event_collection/event">

                <tr> 
                  <td width="22%" class="tableContent"><xsl:value-of select="./name" /></td>
                  <td width="39%" class="tableContent"><xsl:value-of select="./notes" /></td>
                  <td width="16%" class="tableContent"><xsl:value-of select="./event_by" /></td>
                  <td width="23%" class="tableContent"><xsl:value-of select="format:formatISODateTime(dateTime)"/></td>
                </tr>

     </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>

