<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat">


<xsl:output method="html"/>


<xsl:variable name="defaultBrand" select="2000" />
<xsl:variable name="defaultLanguage">en</xsl:variable>
<xsl:variable name="activeLanguage" select="/brand_glossary/brand/active_language" />
<xsl:variable name="defaultElements" select="//token_list/token[@contextID=$defaultBrand][@language=$defaultLanguage]" />

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>
<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="brand_glossary">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody>

		<xsl:choose>

			<xsl:when test="(brand/brand_id = $defaultBrand) and ($activeLanguage = $defaultLanguage)">
                <xsl:call-template name="systemContextDisplay" />
            </xsl:when>

			<xsl:otherwise>
				<xsl:call-template name="normalContextDisplay" />
			</xsl:otherwise>

		</xsl:choose>

	</tbody>
</table>

</xsl:template>

<!--end brand_glossary-->

<xsl:template match="getTokenValues" name="getTokenValues"><xsl:param name="token" />
<xsl:param name="type" />

<xsl:variable name="value" select="/brand_glossary/token_list/token[@name=$token][@contextID=/brand_glossary/brand/brand_id]
	[@language=/brand_glossary/brand/active_language]" />

		<xsl:choose>

			<xsl:when test="@type = 'boolean'">
				<xsl:choose>
					<xsl:when test="$value = '1'">
						True <input type="radio" name="token::{@name}" value="1" checked="true" /> False <input type="radio" name="token::{@name}" value="0" /> Default <input type="radio" name="token::{@name}" value="" />
					</xsl:when>
					<xsl:when test="$value = '0'">
						True <input type="radio" name="token::{@name}" value="1" /> False <input type="radio" name="token::{@name}" value="0" checked="true"/> Default <input type="radio" name="token::{@name}" value="" />
					</xsl:when>
					<xsl:otherwise>
						True <input type="radio" name="token::{@name}" value="1" /> False <input type="radio" name="token::{@name}" value="0" /> Default <input type="radio" name="token::{@name}" value="" checked="true"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="@type = 'largetext'">
				<a href="javascript:resizeTextArea('token::{@name}', 2);" tabindex="-1">up</a>
				<a href="javascript:resizeTextArea('token::{@name}', -2);" tabindex="-1">down</a>
				<a href="javascript:popupTextArea('token::{@name}','Description');" tabindex="-1">popEdit</a><br/>
				<textarea rows="4" cols="80" name="token::{@name}"><xsl:value-of select="$value" /></textarea>
			</xsl:when>
			<xsl:otherwise>
				<input type="text" size="40" maxlength="250" value="{$value}" name="token::{@name}" />
			</xsl:otherwise>
		</xsl:choose>

<input type="hidden" value="{@type}" name="{@name}::type" />
<input type="hidden" value="{@isSystemProperty}" name="{@name}::systemProperty" />
<input type="hidden" value="{@isTranslatableProperty}" name="{@name}::isTranslatableProperty" />
</xsl:template>

<xsl:template match="systemContextDisplay" name="systemContextDisplay"><tr class="tableHeader">
	<td align="left" class="tableHeader">Token</td>
	<td align="left" class="tableHeader">Project.net Default (en)</td>
	<td align="left" class="tableHeader">Type</td>
	<td align="left" class="tableHeader">isSystemProperty</td>
    <td align="left" class="tableHeader">isTranslatable</td>
</tr>

		<tr class="tableLine">
			<td colspan="5" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>

<xsl:for-each select="$defaultElements">

		<tr class="tableContent" align="left" valign="middle">
			<td class="tableContent">
				<xsl:value-of select="@name" />
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>

			<td class="tableContent">

				<xsl:choose>

					<xsl:when test="@type = 'boolean'">
					<xsl:variable name="value" select="." />

						<xsl:choose>
			 				<xsl:when test="$value = 1">
									True <input type="radio" name="token::{@name}" value="1" checked="true" /> False <input type="radio" name="token::{@name}" value="0" />
							</xsl:when>

							<xsl:otherwise>
								True <input type="radio" name="token::{@name}" value="1" /> False <input type="radio" name="token::{@name}" value="0" checked="true" />
							</xsl:otherwise>
						</xsl:choose>

					</xsl:when>
					<xsl:when test="@type = 'largetext'">
						<a href="javascript:resizeTextArea('token::{@name}', 2);" tabindex="-1">up</a>
						<a href="javascript:resizeTextArea('token::{@name}', -2);" tabindex="-1">down</a>
						<a href="javascript:popupTextArea('token::{@name}','Description');" tabindex="-1">popEdit</a><br/>
						<textarea rows="4" cols="60" name="token::{@name}"><xsl:value-of select="." /></textarea>
					</xsl:when>
					<xsl:otherwise>
						<input type="text" size="40" maxlength="250" value="{.}" name="token::{@name}" />
					</xsl:otherwise>

				</xsl:choose>
			</td>

			<td class="tableContent">
				<input type="text" value="{@type}" name="{@name}::type" />
			</td>

			<td class="tableContent">
				<xsl:call-template name="displayIsSystemPropertyRadio">
					<xsl:with-param name="tokenValue" select="@isSystemProperty" />
				</xsl:call-template>
			</td>
            <td class="tableContent">
				<xsl:call-template name="displayIsTranslatablePropertyRadio">
					<xsl:with-param name="tokenValue" select="@isTranslatableProperty" />
				</xsl:call-template>
			</td>
         </tr>
	  <tr class="tableLine">
		<td colspan="5">
			<xsl:element name="img">
				<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>

	</xsl:for-each></xsl:template>

<xsl:template match="normalContextDisplay" name="normalContextDisplay"><tr class="tableHeader">
	<td align="left" class="tableHeader">Token</td>
	<td align="left" class="tableHeader">Project.net Default</td>
    <td align="left" class="tableHeader">Is Translatable</td>
	<td colspan="2" align="left" class="tableHeader">
		<xsl:value-of select="/brand_glossary/brand/name" /> (<xsl:value-of select="/brand_glossary/brand/active_language" />)
	</td>
</tr>


		<tr class="tableLine">
			<td colspan="5" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>

		<xsl:for-each select="$defaultElements">

		<xsl:if test="@isSystemProperty = 'false' or /brand_glossary/brand/brand_id = $defaultBrand">

			<tr class="tableContent" align="left" valign="middle">
				<td class="tableContent">
					<xsl:value-of select="@name" />
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>
				<td class="tableContent">
					<xsl:call-template name="formatValue">

						<xsl:with-param name="tokenValue">
							<xsl:call-template name="getSystemValue">
								<xsl:with-param name="tokenName" select="@name" />
							</xsl:call-template>
						</xsl:with-param>

						<xsl:with-param name="tokenType" select="@type" />

					</xsl:call-template>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>

				</td>
                <td class="tableContent">
                    <xsl:choose>
                        <xsl:when test="@isTranslatableProperty='true'">
                            True
                        </xsl:when>
                        <xsl:otherwise>
                            False
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
				<td class="tableContent">
					<xsl:call-template name="getTokenValues">
						<xsl:with-param name="token" select="@name" />
						<xsl:with-param name="type" select="@type" />
					</xsl:call-template>
				</td>
	         </tr>
		  <tr class="tableLine">
			<td colspan="5">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">1</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
	</xsl:if>

	</xsl:for-each></xsl:template>

<xsl:template match="formatValue" name="formatValue"><xsl:param name = "tokenValue" />
<xsl:param name = "tokenType" />


<xsl:choose>

	<xsl:when test="$tokenType = 'boolean'">

		<xsl:choose>
			<xsl:when test="$tokenValue = '1'">
				True
			</xsl:when>
			<xsl:when test="$tokenValue = '0'">
				False
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$tokenValue" />
			</xsl:otherwise>
		</xsl:choose>

	</xsl:when>
	<xsl:when test="$tokenType = 'largetext'">
		<textarea rows="4" cols="40" name="{@name}" readonly="true"><xsl:value-of select="." /></textarea>
	</xsl:when>

	<xsl:otherwise>
		<xsl:value-of select="$tokenValue" />
	</xsl:otherwise>

</xsl:choose>
	</xsl:template>

<xsl:template match="displayIsSystemPropertyRadio" name="displayIsSystemPropertyRadio"><xsl:param name="tokenValue" />

		<xsl:choose>

			<xsl:when test="$tokenValue = 'true'">
				True <input type="radio" name="{@name}::systemProperty" value="1" checked="true" /> False <input type="radio" name="{@name}::systemProperty" value="0" />
			</xsl:when>
			<xsl:otherwise>
				True <input type="radio" name="{@name}::systemProperty" value="1" /> False <input type="radio" name="{@name}::systemProperty" value="0" checked="true"/>
			</xsl:otherwise>

		</xsl:choose>
</xsl:template>

<xsl:template match="displayIsTranslatablePropertyRadio" name="displayIsTranslatablePropertyRadio"><xsl:param name="tokenValue" />

		<xsl:choose>

			<xsl:when test="$tokenValue = 'true'">
				True <input type="radio" name="{@name}::translatableProperty" value="1" checked="true" /> False <input type="radio" name="{@name}::translatableProperty" value="0" />
			</xsl:when>
			<xsl:otherwise>
				True <input type="radio" name="{@name}::translatableProperty" value="1" /> False <input type="radio" name="{@name}::translatableProperty" value="0" checked="true"/>
			</xsl:otherwise>

		</xsl:choose>
</xsl:template>

<xsl:template match="getSystemValue" name="getSystemValue"><xsl:param name = "tokenName" />
<xsl:variable name="myToken" select="//brand_glossary/token_list/token[@contextID=$defaultBrand][@name=$tokenName]" />
<xsl:choose>

	<xsl:when test="$myToken[@language=$activeLanguage]">
		<xsl:value-of select="$myToken[@language=$activeLanguage]" />
	</xsl:when>

	<xsl:otherwise>
		<xsl:value-of select="$myToken[@language=$defaultLanguage]" />
	</xsl:otherwise>

</xsl:choose></xsl:template>

</xsl:stylesheet>
