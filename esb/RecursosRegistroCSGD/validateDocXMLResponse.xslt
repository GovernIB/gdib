<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:param name="afirmaServiceRespOk"/>	 
     <xsl:param name="dssResultAfirmaResponse"/>
     <xsl:param name="documentResp"/>
	 <xsl:param name="signsDetInfoAfirmaResponse"/>
     <xsl:template match="/">
		<csgd:validateDocResult>           
		<xsl:choose>
			<xsl:when test="fn:boolean($afirmaServiceRespOk)">
				<xsl:value-of select="$dssResultAfirmaResponse" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>
				<csgd:result>
            		<csgd:resultMajor>urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError</csgd:resultMajor>
            		<csgd:resultMessage>No fue posible determinar la validez de la firma electrónica del documento. Ocurrío un problema al parsear la respuesta retornada por la plataforma @firma.</csgd:resultMessage>
           		</csgd:result>
           	</xsl:otherwise>
		</xsl:choose>
        <csgd:resParam>
	             <xsl:value-of select="$documentResp" disable-output-escaping="yes"/>
	             <xsl:if test="fn:boolean($afirmaServiceRespOk)">
		             <xsl:value-of select="$signsDetInfoAfirmaResponse" disable-output-escaping="yes"/>
		         </xsl:if>
	        </csgd:resParam>
        </csgd:validateDocResult>
     </xsl:template>
 </xsl:stylesheet>