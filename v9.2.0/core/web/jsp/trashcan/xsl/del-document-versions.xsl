<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

  <xsl:template match="/">

       <xsl:for-each select="version_collection/version">

               <tr> 
                  <td class="tableContent"><xsl:value-of select="format:formatNumber(version_num)" /></td>
		  <td class="tableContent">
  		   <a target="view_window" href="{./jsp_root_url}/servlet/DownloadVersion?id={./parent_object_id}&amp;versionid={./version_id}&amp;status=D"><xsl:value-of select="./short_file_name"/></a>
		   </td> 
                  <td class="tableContent"><xsl:value-of select="format:formatISODateTime(last_modified)"/></td>
                   <td class="tableContent"><xsl:value-of select="./modified_by" /></td>
                  <td class="tableContent"><xsl:value-of select="./file_format" /></td>
                  <td align="right" class="tableContent"><xsl:value-of select="./file_size" /></td>
                </tr>

     </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>

