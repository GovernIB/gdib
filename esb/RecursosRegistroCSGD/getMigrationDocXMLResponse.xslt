<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ws="http://www.caib.es/gdib/repository/ws" xmlns:wsm="http://www.caib.es/gdib/migration/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws wsm">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:param name="retrieveContent" />
     <xsl:variable name="resultadoCorrecto" select="fn:boolean(//wsm:getMigrationNodeResponse/wsm:result/ws:id/text())"/>
     <xsl:variable name="eniUriNsLength" select="fn:string-length('{http://www.administracionelectronica.gob.es/model/eni/1.0}')" />
     <xsl:variable name="gdibUriNsLength" select="fn:string-length('{http://www.caib.es/model/gdib/1.0}')" />	 
     <xsl:template match="/">
         <csgd:getDocumentResult>
             <csgd:result>
                 <xsl:choose>
                     <xsl:when test="$resultadoCorrecto">
                         <csgd:code>COD_000</csgd:code>
                         <csgd:description>Petición realizada correctamente.</csgd:description>
                     </xsl:when>
                     <xsl:otherwise>
                         <csgd:code>COD_001</csgd:code>
                         <csgd:description>Petición realizada correctamente, pero no se obtuvieron resultados.</csgd:description>
                     </xsl:otherwise>
                 </xsl:choose>
             </csgd:result>
             <xsl:if test="$resultadoCorrecto">
	             <csgd:resParam>
	                 <csgd:id>
            			<xsl:value-of select="//wsm:result/ws:id/text()"/>
            		</csgd:id>
		            <xsl:choose>
						<xsl:when test="fn:starts-with(//wsm:result/ws:type/text(),'eni:') or fn:starts-with(ws:type/text(),'gdib:')">
							<csgd:type><xsl:value-of select="//wsm:result/ws:type/text()"/></csgd:type>
						</xsl:when>
						<xsl:when test="fn:starts-with(//wsm:result/ws:type/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
							<csgd:type><xsl:value-of select="fn:concat('eni:',fn:substring(//wsm:result/ws:type/text(),$eniUriNsLength+1))"/></csgd:type>
						</xsl:when>
						<xsl:when test="fn:starts-with(//wsm:result/ws:type/text(),'{http://www.caib.es/model/gdib/1.0}')">
							<csgd:type><xsl:value-of select="fn:concat('gdib:',fn:substring(//wsm:result/ws:type/text(),$gdibUriNsLength+1))"/></csgd:type>
						</xsl:when>
					</xsl:choose>
		            <csgd:name>
		                 <xsl:value-of select="//wsm:result/ws:name/text()"/>
		            </csgd:name>
		            <xsl:for-each select="//wsm:result/ws:aspects" >
		             	<xsl:choose>
							<xsl:when test="fn:starts-with(text(),'eni:') or fn:starts-with(text(),'gdib:')">
								<csgd:aspects><xsl:value-of select="text()"/></csgd:aspects>
							</xsl:when>
							<xsl:when test="fn:starts-with(text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
								<csgd:aspects><xsl:value-of select="fn:concat('eni:',fn:substring(text(),$eniUriNsLength+1))"/></csgd:aspects>
							</xsl:when>
							<xsl:when test="fn:starts-with(text(),'{http://www.caib.es/model/gdib/1.0}')">
								<csgd:aspects><xsl:value-of select="fn:concat('gdib:',fn:substring(text(),$gdibUriNsLength+1))"/></csgd:aspects>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>
					<xsl:for-each select="//wsm:result/ws:properties" >
						<xsl:if test="fn:boolean(ws:value/text()) and fn:string-length(ws:value/text()) > 0">
							<xsl:choose>
								<xsl:when test="fn:starts-with(ws:qname/text(),'eni:') or fn:starts-with(ws:qname/text(),'gdib:')">
									<csgd:metadataCollection>
			 							<csgd:qname><xsl:value-of select="ws:qname/text()"/></csgd:qname>
			 							<xsl:apply-templates select="ws:value/text()"/>
			 						</csgd:metadataCollection>
								</xsl:when>
								<xsl:when test="fn:starts-with(ws:qname/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
									<csgd:metadataCollection>
			 							<csgd:qname><xsl:value-of select="fn:concat('eni:',fn:substring(ws:qname/text(),$eniUriNsLength+1))"/></csgd:qname>
			 							<xsl:apply-templates select="ws:value/text()"/>
			 						</csgd:metadataCollection>
								</xsl:when>
								<xsl:when test="fn:starts-with(ws:qname/text(),'{http://www.caib.es/model/gdib/1.0}')">
									<csgd:metadataCollection>
			 							<csgd:qname><xsl:value-of select="fn:concat('gdib:',fn:substring(ws:qname/text(),$gdibUriNsLength+1))"/></csgd:qname>
			 							<xsl:apply-templates select="ws:value/text()"/>
			 						</csgd:metadataCollection>
								</xsl:when>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
					<xsl:if test="$retrieveContent">						
						<csgd:binaryContents>
							<csgd:binaryType>CONTENT</csgd:binaryType>
							<xsl:if test="fn:boolean(//wsm:result/ws:content/ws:mimetype/text())">
								<csgd:mimetype><xsl:value-of select="//wsm:result/ws:content/ws:mimetype/text()"/></csgd:mimetype>
							</xsl:if>
			 				<csgd:content><xsl:value-of select="//wsm:result/ws:content/ws:data/text()"/></csgd:content>
			 				<xsl:if test="fn:boolean(//wsm:result/ws:content/ws:encoding/text())">
									<csgd:encoding><xsl:value-of select="//wsm:result/ws:content/ws:encoding/text()"/></csgd:encoding>
							</xsl:if>
						</csgd:binaryContents>					
						<csgd:binaryContents>
							<csgd:binaryType>MIGRATION_SIGNATURE</csgd:binaryType>
							<csgd:content><xsl:value-of select="//wsm:result/ws:sign/text()"/></csgd:content>     					
						</csgd:binaryContents>
						<csgd:binaryContents>
   							<csgd:binaryType>VALCERT_SIGNATURE</csgd:binaryType>
   							<csgd:content><xsl:value-of select="//wsm:result/wsm:valcertSign/text()"/></csgd:content>     					
   						</csgd:binaryContents>

   						<csgd:binaryContents>
   							<csgd:binaryType>MIGRATION_ZIP</csgd:binaryType>
   							<csgd:content><xsl:value-of select="//wsm:result/wsm:zipContent/text()"/></csgd:content>     					
   						</csgd:binaryContents>
					</xsl:if>    				
	             </csgd:resParam>
             </xsl:if>
         </csgd:getDocumentResult>
     </xsl:template>
     <xsl:template match="text()">
     	<xsl:param name="pText" select="."/>
     	<xsl:choose>
     		<xsl:when test="fn:starts-with($pText,'[') and fn:ends-with($pText,']')">
     			<xsl:for-each select="tokenize(fn:substring($pText,2,fn:string-length($pText)-2), ',')">	        		
	        		<csgd:value><xsl:value-of select="."/></csgd:value>
	      		</xsl:for-each>
     		</xsl:when>
     		<xsl:otherwise>
     			<csgd:value><xsl:value-of select="$pText"/></csgd:value>
     		</xsl:otherwise>
     	</xsl:choose>
	</xsl:template>
 </xsl:stylesheet>