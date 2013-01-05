<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"		
    	extension-element-prefixes="display format" >
<xsl:output method="html"/>

  <xsl:template match="container">
    <table border="0" width="100%" cellpadding="0" cellspacing="0">

        <xsl:apply-templates select="container_contents/container_entry_columns" /> 	
        
        <xsl:for-each select="//container/container_contents/entry[@type='doc_container']">
            <xsl:call-template name="container" />
         </xsl:for-each>

        <xsl:call-template name="contents" />
        
    </table>
  </xsl:template>

  <xsl:template match="container_contents/container_entry_columns">  

	 <tr align="left"> 

	    <td class="tableHeader" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={name}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.documentname.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={format}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.documentformat.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={version_num}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.version.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={is_cko}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.checkedout.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={status}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.status.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={author}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.author.link')"/></a></div>
        </td>
   
	    <td align="left" class="tableHeader" NOWRAP="1"> 
		    <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={date_modified}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.lastmodified.link')"/></a></div>
        </td>
  
	    <td align="right" class="tableHeader"> 
		    <div align="right" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={file_size}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.size.link')"/></a></div>
        </td>

	    <td colspan="2" align="right" class="tableHeader"></td>
	 </tr>
	 
	<tr class="tableLine">

		<td colspan="11" class="tableLine">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">2</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>

   </xsl:template> 

   <xsl:template name="container">

    <tr align="center" class="tableContent"> 

	    <td align="left" class="tableContent" width="1%"> 
            <xsl:element name="input">
                <xsl:attribute name="type">radio</xsl:attribute>
                <xsl:attribute name="name">selected</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
            </xsl:element>

            <xsl:element name="input">
                <xsl:attribute name="type">hidden</xsl:attribute>
                <xsl:attribute name="name">item<xsl:value-of select="./object_id"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
            </xsl:element>
	    </td>

        <td align="left" class="tableContent">
            <xsl:if test="position() > 1">
                <a href="{/container/jsp_root_url}/document/TraverseFolderProcessing.jsp?id={./object_id}&amp;module=10"><img border="0" src="../images/folder.gif"/></a>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:if>
        </td>

        <td align="left" class="tableContent">
            <xsl:choose>
                <xsl:when test="position() > 1">
                    <xsl:element name="a">
                        <xsl:attribute name="href"><xsl:value-of select = "/container/jsp_root_url" />/document/TraverseFolderProcessing.jsp?id=<xsl:value-of select="./object_id"/>&amp;module=10</xsl:attribute>
                        <xsl:attribute name="title"><xsl:value-of select="description" /></xsl:attribute>
                        <xsl:choose>
                        <xsl:when test="string-length(./name) > 40">
                        	<xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(name,1,40))"/>
                        </xsl:when>
            			<xsl:otherwise>
	                        <xsl:value-of select="./name"/>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                	<xsl:choose>
                    <xsl:when test="string-length(./name) > 40">
                        <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(name,1,40))"/>
                    </xsl:when>
            		<xsl:otherwise>
	                     <xsl:value-of select="./name"/>
                    </xsl:otherwise>
                    </xsl:choose>	
                </xsl:otherwise>
            </xsl:choose>
        </td>

        <td align="center" class="tableContent">
            <xsl:choose>
                <xsl:when test="position() > 1">
                    <div align="left"><xsl:value-of select="display:get('prm.document.main.folder.label')"/></div>
                </xsl:when>
                <xsl:otherwise>
                    <div align="left"><xsl:value-of select="display:get('prm.document.main.currentfolder.label')"/></div>
                </xsl:otherwise>
            </xsl:choose>
        </td>

	    <td colspan="7" align="center" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	
	<tr class="tableLine">
	    <td colspan="11" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>

   </xsl:template>


   <xsl:template name="document">

	<xsl:variable name="is_cko" select="./is_cko"/>
	<xsl:variable name="user_id" select="./user_id"/>
	<xsl:variable name="cko_by" select="./cko_by"/>
	<xsl:variable name="cko_by_id" select="./cko_by_id"/>
    <xsl:variable name="cko_by_alt" select="display:get('prm.document.main.xsl.checkedoutby')"/>
    <xsl:variable name="cko_by_me_alt" select="display:get('prm.document.main.xsl.checkedoutbyme')"/>
    <xsl:variable name="discussions_alt" select="display:get('prm.document.main.xsl.discussions')"/>
    <xsl:variable name="links_alt" select="display:get('prm.document.main.xsl.links')"/>
    <xsl:variable name="workflows_alt" select="display:get('prm.document.main.xsl.workflows')"/>
    <tr align="center"> 	

	    <td align="left" class="tableContent"> 
            <xsl:element name="input">
                <xsl:attribute name="type">radio</xsl:attribute>
                <xsl:attribute name="name">selected</xsl:attribute>
                <xsl:attribute name="id"><xsl:value-of select="./name"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
            </xsl:element>
            
            <xsl:element name="input">
                <xsl:attribute name="type">hidden</xsl:attribute>
                <xsl:attribute name="name">item<xsl:value-of select="./object_id"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
            </xsl:element>
	    </td>

	    <td align="left" class="tableContent">
		    <a target="view_window" href="{/container/jsp_root_url}/servlet/DownloadDocument?id={./object_id}"><img border="0" src="..{./app_icon_url}"/></a>
	    </td>

	    <td align="left" class="tableContent">
            <xsl:choose>
        	<xsl:when test="string-length(./name) > 40">
                    <a target="view_window" href="{/container/jsp_root_url}/servlet/DownloadDocument?id={./object_id}" title="{description}"><xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(name,1,40))"/></a>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                    <a target="view_window" href="{/container/jsp_root_url}/servlet/DownloadDocument?id={./object_id}" title="{description}"><xsl:value-of select="./name"/></a>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:otherwise>
            </xsl:choose>    
	    </td>

	    <td align="left" class="tableContent"><xsl:value-of select="./format"/></td>
	    <td align="left" class="tableContent"><xsl:value-of select="format:formatNumber(version)"/> </td>

	    <td align="left" class="tableContent">
		      
            <xsl:choose>
                
                <xsl:when test='$is_cko=0'></xsl:when>

                <xsl:when test='$user_id=$cko_by_id'>
                       <img border="0" id="documentCheckIn_{./object_id}" src="../images/check_green.gif" alt="{$cko_by_me_alt}" title="{$cko_by_me_alt}"/>
                </xsl:when>
                
                <xsl:when test="$user_id!=$cko_by_id">	
                    <img border="0" src="../images/check_red.gif" alt="{$cko_by_alt} {$cko_by}" title="{$cko_by_alt} {$cko_by}"/>
                </xsl:when>
                
                <xsl:otherwise></xsl:otherwise>
              </xsl:choose> 			

	    </td> 

	    <td align="left" class="tableContent"><xsl:value-of select="./status"/> </td>
	    <td align="left" class="tableContent"><xsl:value-of select="./author"/> </td>
	    <td align="left" class="tableContent"><xsl:value-of select="format:formatISODateTime(last_modified)"/> </td>
 	    <td align="right" class="tableContent"><xsl:value-of select="./file_size"/> </td>
        <td align="right" class="tableContent">
            <xsl:choose>
                <xsl:when test="./has_links = 1">
                    <a href="#" onClick="selectRadio(theForm.selected,'{./object_id}');link();"><img src="../images/document/link-button.gif" height="18" width="19" border="0" alt="{$links_alt}" title="{$links_alt}"/></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="./has_discussions = 1">
                    <a href="#" onClick="selectRadio(theForm.selected,'{./object_id}');discussTab();"><img src="../images/document/discussion-button.gif" height="18" width="19" border="0" alt="{$discussions_alt}" title="{$discussions_alt}"/></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>                                        
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="./has_workflows = 1">
                    <img src="../images/document/workflow-button.gif" height="18" width="19" border="0" alt="{$workflows_alt}" title="{$workflows_alt}"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </tr>
	<tr class="tableLine">
	    <td colspan="11" class="tableLine">
	    	<img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
    	</td>
	</tr>
   
   </xsl:template>



   <xsl:template name="contents">
    <xsl:for-each select="//container/container_contents/entry">

	<xsl:choose>

		<xsl:when test="@type = 'document'">
			<xsl:call-template name="document" />
		</xsl:when>

		<xsl:when test="@type = 'bookmark'">
			<xsl:call-template name="bookmark" />
		</xsl:when>
	</xsl:choose>


    </xsl:for-each>
   </xsl:template>

   <xsl:template name="bookmark">

    <xsl:variable name="discussions_alt" select="display:get('prm.document.main.xsl.discussions')"/>
    <xsl:variable name="links_alt" select="display:get('prm.document.main.xsl.links')"/>
    <xsl:variable name="workflows_alt" select="display:get('prm.document.main.xsl.workflows')"/>
    <tr align="center"> 	
        <td align="left" class="tableContent"> 
        
            <input type="radio" name="selected" value="{./object_id}" />
            <input type="hidden" name="item{./object_id}" value="{./object_type}" />
            
        </td>

        <td align="left" class="tableContent">
            <a target="view_window" href="{./url}">
                <img border="0" src="../images/appicons/url.gif"/>
            </a>
        </td>

        <td align="left" class="tableContent">
            <xsl:choose>
        	<xsl:when test="string-length(./name) > 40">
                    <a target="view_window" href="{./url}" title="{description}"><xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(name,1,40))"/></a>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                    <a target="view_window" href="{./url}" title="{description}"><xsl:value-of select="./name"/></a>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:otherwise>
            </xsl:choose>    
        </td>

        <td align="left" class="tableContent">
            <xsl:value-of select="./format"/>
        </td>
        
        <td align="left" class="tableContent">
            <xsl:value-of select="format:formatNumber(version)"/>
        </td>

        <td align="left" class="tableContent">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </td> 

        <td align="left" class="tableContent"><xsl:value-of select="./status"/> </td>
        <td align="left" class="tableContent"><xsl:value-of select="./author"/> </td>
        <td align="left" class="tableContent"><xsl:value-of select="format:formatISODateTime(last_modified)"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="./file_size"/> </td>

        <td align="right" class="tableContent">
                <xsl:choose>
                    <xsl:when test="./has_links = 1">
                        <img src="../images/document/link-button.gif" height="18" width="19" border="0" alt="{$links_alt}" title="{$links_alt}"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>                    
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="./has_discussions = 1">
                        <img src="../images/document/discussion-button.gif" height="18" width="19" border="0" alt="{$discussions_alt}" title="{$discussions_alt}"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>                                        
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="./has_workflows = 1">
                        <img src="../images/document/workflow-button.gif" height="18" width="19" border="0" alt="{$workflows_alt}" title="{$workflows_alt}"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
        </td>
    </tr>
	<tr class="tableLine">
    	<td colspan="11" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
   
   </xsl:template>
</xsl:stylesheet>

