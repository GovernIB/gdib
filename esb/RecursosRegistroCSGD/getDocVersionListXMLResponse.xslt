<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:variable name="numeroTotalResultados" select="fn:count(//ws:getNodeVersionListResponse/ws:result)"/>	 
     <xsl:template match="/">
         <csgd:getDocVersionListResult>
             <csgd:result>
                 <xsl:choose>
                     <xsl:when test="$numeroTotalResultados > 0">
                         <csgd:code>COD_000</csgd:code>
                         <csgd:description>Petición realizada correctamente.</csgd:description>
                     </xsl:when>
                     <xsl:otherwise>
                         <csgd:code>COD_001</csgd:code>
                         <csgd:description>Petición realizada correctamente, pero no se obtuvieron resultados.</csgd:description>
                     </xsl:otherwise>
                 </xsl:choose>
             </csgd:result>
             <xsl:if test="$numeroTotalResultados > 0">	             
             	<xsl:for-each select="//ws:getNodeVersionListResponse/ws:result" >
             		<csgd:resParam>
		                <csgd:id>
		                     <xsl:value-of select="ws:id/text()"/>
		                </csgd:id>
		                <csgd:date>
		                     <xsl:value-of select="ws:date/text()"/>
		                </csgd:date>			                
	    			</csgd:resParam>    				
   				</xsl:for-each>
             </xsl:if>
         </csgd:getDocVersionListResult>
     </xsl:template>
 </xsl:stylesheet>