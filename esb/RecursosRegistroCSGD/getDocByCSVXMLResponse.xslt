<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" exclude-result-prefixes="fn xsd soapenv">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="retrieveContent" />
	<xsl:param name="returnedProperites" select="('eni:app_tramite_doc','eni:csv','eni:tipoFirma','eni:perfil_firma','eni:id','eni:organo','eni:v_nti','eni:origen','eni:id_origen','eni:estado_elaboracion','eni:nombre_formato','eni:extensión_formato','eni:def_csv','eni:mimeType','eni:fileSize')"/>
	<xsl:template match="/">
		<csgd:getDocumentResult>
			<csgd:result>
	        	<csgd:code><xsl:value-of select="/csgd:getDocumentResult/csgd:result/csgd:code/text()"/></csgd:code>
                <csgd:description><xsl:value-of select="/csgd:getDocumentResult/csgd:result/csgd:description/text()"/></csgd:description>
            </csgd:result>
            <csgd:resParam>
	            <csgd:name><xsl:value-of select="/csgd:getDocumentResult/csgd:resParam/csgd:name/text()"/></csgd:name>
	            <csgd:type><xsl:value-of select="/csgd:getDocumentResult/csgd:resParam/csgd:type/text()"/></csgd:type>
    
				<xsl:for-each select="//csgd:metadataCollection" >
					<xsl:if test="fn:exists(fn:index-of($returnedProperites,csgd:qname/text()))">
						<csgd:metadataCollection>
 							<csgd:qname><xsl:value-of select="csgd:qname/text()"/></csgd:qname>
 							<xsl:for-each select="./csgd:value" >
 								<csgd:value><xsl:value-of select="text()"/></csgd:value>
 							</xsl:for-each> 							
 						</csgd:metadataCollection>
					</xsl:if>
				</xsl:for-each>
				<xsl:if test="$retrieveContent = 'true'">
					<xsl:for-each select="//csgd:binaryContents" >
						<xsl:if test="./csgd:binaryType/text() = 'CONTENT' or ./csgd:binaryType/text() = 'SIGNATURE'">
							<csgd:binaryContents>
								<csgd:binaryType><xsl:value-of select="./csgd:binaryType/text()" /></csgd:binaryType>
								<xsl:if test="fn:boolean(./csgd:mimetype/text())">
									<csgd:mimetype><xsl:value-of select="./csgd:mimetype/text()"/></csgd:mimetype>
								</xsl:if>
		 						<csgd:content><xsl:value-of select="./csgd:content/text()"/></csgd:content>
		 						<xsl:if test="fn:boolean(./csgd:encoding/text())">
									<csgd:encoding><xsl:value-of select="./csgd:encoding/text()"/></csgd:encoding>
								</xsl:if>
							</csgd:binaryContents>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
        	</csgd:resParam>
		</csgd:getDocumentResult>
	</xsl:template>
 </xsl:stylesheet>