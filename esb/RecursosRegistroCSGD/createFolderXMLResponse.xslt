<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:include href="fillFolder.xslt"/>
     <xsl:variable name="resultadoCorrecto" select="fn:boolean(fn:boolean(//ws:createNodeResponse/ws:result/text()) or fn:boolean(//ws:createAndGetNodeResponse/ws:result/ws:id/text()))"/>
     <xsl:variable name="retrieveNode" select="fn:boolean(//ws:createAndGetNodeResponse/ws:result/ws:id/text())"/>	 
     <xsl:template match="/">
         <csgd:createFolderResult>
             <csgd:result>
                 <xsl:choose>
                     <xsl:when test="$resultadoCorrecto">
                         <csgd:code>COD_000</csgd:code>
                         <csgd:description>Petición realizada correctamente.</csgd:description>
                     </xsl:when>
                     <xsl:otherwise>
                         <csgd:code>COD_001</csgd:code>
                         <csgd:description>No se pudo crear la agregación documental.</csgd:description>
                     </xsl:otherwise>
                 </xsl:choose>
             </csgd:result>
             <xsl:if test="$resultadoCorrecto">
	             <xsl:choose>
                 	<xsl:when test="$retrieveNode">
                    	<xsl:apply-templates select="//ws:createAndGetNodeResponse/ws:result"/>
                    </xsl:when>
                    <xsl:otherwise>
                    	<csgd:resParam>
               				<csgd:id><xsl:value-of select="//ws:createNodeResponse/ws:result/text()"/></csgd:id>
               			</csgd:resParam>
                    </xsl:otherwise>
				</xsl:choose>	             
             </xsl:if>
         </csgd:createFolderResult>
     </xsl:template>
 </xsl:stylesheet>