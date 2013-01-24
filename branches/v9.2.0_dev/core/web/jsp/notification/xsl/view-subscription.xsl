<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

	<xsl:template match="subscription">
  	
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
			<tr>
				<td class="editTableContent statusPadding" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.status.label')" /></td>
				<td colspan="3" align="left" class="tableContent statusPadding"><xsl:value-of select="./status" /></td>
			</tr>

			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>
			
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.createdate.label')" /></td>
				<td colspan="3" align="left" class="tableContent">
				<span class="tableContent">
					<xsl:value-of select="created_date" />
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> 
					<xsl:value-of select="display:get('prm.notification.viewsubscription.details.by.label')" />
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					<xsl:value-of select="./created_by_name" />
					<xsl:variable name="modifiedDate" select="modified_date" />
					<xsl:variable name="createdDate" select="created_date" />
			 		<xsl:if test="string-length($modifiedDate) > 0">
					<xsl:value-of select="display:get('prm.notification.editsubscription.modified.label')" />
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					<xsl:value-of select="modified_date" />
					</xsl:if>
				</span>
				
				<xsl:element name="input">
					<xsl:attribute name="type">hidden</xsl:attribute>
					<xsl:attribute name="id">createdById</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./created_by_id"/></xsl:attribute>
				</xsl:element>
				
				</td>
			</tr>

			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.name.label')" /></td>
				<td colspan="3" align="left" class="notifyFieldRequired"><xsl:value-of select="./name" /></td>
			</tr>
				
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.viewsubscription.details.description.label')" /></td>
				<td colspan="3"  align="left" class="tableContent"><xsl:value-of select="./description" /></td>
			</tr>
			
			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>
			
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.itemsnotified.label')" /></td>
				<td colspan="3" align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>
		
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.itemtype.label')" /></td>
				<td colspan="3" align="left" class="tableContent"><xsl:value-of select="display:get(./subscription_type)"/>
				</td>
			</tr>

			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.itemname.label')" /></td>
				<td colspan="3" align="left" class="notifyFieldRequired">
						<xsl:variable name="itemName" select="./item_name" />
  						 <xsl:if test="string-length($itemName) > 0">
							<xsl:value-of select="display:get(./item_name)" />
  					 	</xsl:if>
				
	 					<xsl:if test="string-length($itemName) = 0">
							<xsl:value-of select="display:get(./subscription_type)" />
  					 	</xsl:if>
				</td>
			</tr>
			
			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>

			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.actionnotified.label')"/></td>
				<td colspan="3" align="left" class="tableContent">					
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>
			</tr>				
			<xsl:for-each select="./notification_types/notification_type">
			<tr>
				<td class="editTableContent" width="18%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td colspan="3" align="left" class="tableContent">					
						<xsl:value-of select="display:get(.)"/>
						<xsl:choose>
						<xsl:when test="not(position() = last())"><xsl:text disable-output-escaping="yes">,</xsl:text></xsl:when>
						</xsl:choose>				
				</td>
			</tr>	
			</xsl:for-each>		
			
			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>
			
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.viewsubscription.details.deliverto.label')"/></td>
				<td colspan="3" align="left" class="notifyFieldRequired"><xsl:value-of select="./Participant" />
				</td>					
			</tr>	
				
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.viewsubscription.details.deliverymethod.label')" /></td>
				<td colspan="3" align="left" class="tableContent"><xsl:value-of select="./delivery_method" /></td>
			</tr>
			
			<tr>
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.viewsubscription.details.deliveryschedule.label')"/></td>
				<td colspan="3"  align="left" class="tableContent"><xsl:value-of select="./subscription_schedule/delivery_interval_name" /></td>
			</tr>	

			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>

		    <tr> 
				<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.viewsubscription.details.externalemail.label')"/></td>
					<xsl:variable name="external" >
						<xsl:for-each select="./subscriber_group_list/subscriber">
						<xsl:sort select="./address_collection/address/address_details"/>	
						<xsl:variable name="str" select="./group_name" />
							 <xsl:if test="$str='External'">
								<xsl:value-of select="./address_collection/address/address_details" />
									<xsl:choose>
										<xsl:when test="not(position() = last())"><xsl:text disable-output-escaping="yes">, </xsl:text></xsl:when>
									</xsl:choose>				
						 	</xsl:if>
						</xsl:for-each>
					</xsl:variable>
				<td colspan="3" align="left" class="tableContent"><xsl:value-of select="$external" /></td>
			</tr>
			
			<xsl:variable name="customMessage" select="./custom_message" />
			 <xsl:if test="string-length($customMessage) > 0">
				<tr>
					<td class="editTableContent" width="18%"><xsl:value-of select="display:get('prm.notification.editsubscription.message.label')"/></td>
					<td colspan="3" align="left" class="tableContent"><xsl:value-of select="./custom_message"/></td>
				</tr>
		 	 </xsl:if>
			
			<tr class="tableHeader" align="left" valign="top">
				<td class="tableHeader" width="1%" colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</tr>
	</table>
	</xsl:template>	
</xsl:stylesheet>
