<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:include href="fillFile.xslt"/>
     <xsl:variable name="resultadoCorrecto" select="fn:boolean(//ws:getNodeResponse/ws:result/ws:id/text())"/>
     <xsl:template match="/">
         <csgd:getFileResult>
            <csgd:result>
                 <xsl:choose>
                     <xsl:when test="$resultadoCorrecto">
                         <csgd:code>COD_000</csgd:code>
                         <csgd:description>Petición realizada correctamente.</csgd:description>
                     </xsl:when>
                     <xsl:otherwise>
                         <csgd:code>COD_001</csgd:code>
                         <csgd:description>No se pudo obtener el expediente.</csgd:description>
                     </xsl:otherwise>
                 </xsl:choose>
            </csgd:result>
            <xsl:if test="$resultadoCorrecto">
				<xsl:apply-templates select="//ws:getNodeResponse/ws:result"/>
             </xsl:if>
         </csgd:getFileResult>
     </xsl:template>
 </xsl:stylesheet>