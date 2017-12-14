<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:template match="ws:result" name="fillFolder">
     	<xsl:variable name="eniUriNsLength" select="fn:string-length('{http://www.administracionelectronica.gob.es/model/eni/1.0}')" />
     	<xsl:variable name="gdibUriNsLength" select="fn:string-length('{http://www.caib.es/model/gdib/1.0}')" />
     	<xsl:variable name="cmUriNsLength" select="fn:string-length('{http://www.alfresco.org/model/content/1.0}')" />
        <csgd:resParam>
            <csgd:id>
            	<xsl:value-of select="ws:id/text()"/>
            </csgd:id>
            <xsl:choose>
				<xsl:when test="fn:starts-with(ws:type/text(),'eni:')">
					<csgd:type><xsl:value-of select="ws:type/text()"/></csgd:type>
				</xsl:when>
				<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
					<csgd:type><xsl:value-of select="fn:concat('eni:',fn:substring(ws:type/text(),$eniUriNsLength+1))"/></csgd:type>
				</xsl:when>
			</xsl:choose>
             <csgd:name>
                 <xsl:value-of select="ws:name/text()"/>
             </csgd:name>
			<xsl:for-each select="ws:childs" >
             	<csgd:childObjects>
             		<xsl:if test="fn:boolean(ws:id/text())">
             			<csgd:id><xsl:value-of select="ws:id/text()"/></csgd:id>
             		</xsl:if>
             		<xsl:if test="fn:boolean(ws:name/text())">
						<csgd:name><xsl:value-of select="ws:name/text()"/></csgd:name>
					</xsl:if>
					<xsl:if test="fn:boolean(ws:type/text())">
						<xsl:choose>
							<xsl:when test="fn:starts-with(ws:type/text(),'eni:') or fn:starts-with(ws:type/text(),'gdib:')">
								<csgd:type><xsl:value-of select="ws:type/text()"/></csgd:type>
							</xsl:when>
							<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
								<csgd:type><xsl:value-of select="fn:concat('eni:',fn:substring(ws:type/text(),$eniUriNsLength+1))"/></csgd:type>
							</xsl:when>
							<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.caib.es/model/gdib/1.0}')">
								<csgd:type><xsl:value-of select="fn:concat('gdib:',fn:substring(ws:type/text(),$gdibUriNsLength+1))"/></csgd:type>
							</xsl:when>
							<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.alfresco.org/model/content/1.0}')">
								<csgd:type><xsl:value-of select="fn:concat('cm:',fn:substring(ws:type/text(),$cmUriNsLength+1))"/></csgd:type>
							</xsl:when>
						</xsl:choose>
					</xsl:if>					 	
				</csgd:childObjects>
			</xsl:for-each>
		</csgd:resParam>
	</xsl:template>
 </xsl:stylesheet>