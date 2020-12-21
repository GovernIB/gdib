<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:template match="ws:result" name="fillSerie">
     	<xsl:variable name="eniUriNsLength" select="fn:string-length('{http://www.administracionelectronica.gob.es/model/eni/1.0}')" />
     	<xsl:variable name="gdibUriNsLength" select="fn:string-length('{http://www.caib.es/model/gdib/1.0}')" />
     	<xsl:variable name="cmUriNsLength" select="fn:string-length('{http://www.alfresco.org/model/content/1.0}')" />

         <csgd:resParam>
           <csgd:id>
           <xsl:choose>
         <xsl:when test="ws:id != null">
             <xsl:value-of select="ws:id/text()"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:value-of select="ws:id"/>
         </xsl:otherwise>
       </xsl:choose>
        </csgd:id>

            <xsl:choose>
				<xsl:when test="fn:starts-with(ws:type/text(),'eni:') or fn:starts-with(ws:type/text(),'gdib:')">
					<csgd:type><xsl:value-of select="ws:type/text()"/></csgd:type>
				</xsl:when>
				<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
					<csgd:type><xsl:value-of select="fn:concat('eni:',fn:substring(ws:type/text(),$eniUriNsLength+1))"/></csgd:type>
				</xsl:when>
				<xsl:when test="fn:starts-with(ws:type/text(),'{http://www.caib.es/model/gdib/1.0}')">
					<csgd:type><xsl:value-of select="fn:concat('eni:',fn:substring(ws:type/text(),$gdibUriNsLength+1))"/></csgd:type>
				</xsl:when>
				<xsl:when test="fn:ends-with(ws:type/text(),'recordFolder')">
					<csgd:type>eni:expediente</csgd:type>
				</xsl:when>
			</xsl:choose>
             <csgd:name>
                 <xsl:value-of select="ws:name/text()"/>
             </csgd:name>
             <xsl:for-each select="ws:aspects" >
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
			<xsl:for-each select="ws:properties" >
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
      <xsl:if test="fn:boolean(ws:content/ws:data/text())">
        <csgd:binaryContents>
          <csgd:binaryType>CONTENT</csgd:binaryType>
          <xsl:if test="fn:boolean(ws:content/ws:mimetype/text())">
            <csgd:mimetype><xsl:value-of select="ws:content/ws:mimetype/text()"/></csgd:mimetype>
          </xsl:if>
        <csgd:content><xsl:value-of select="ws:content/ws:data/text()"/></csgd:content>
        <xsl:if test="fn:boolean(ws:content/ws:encoding/text())">
            <csgd:encoding><xsl:value-of select="ws:content/ws:encoding/text()"/></csgd:encoding>
          </xsl:if>
        </csgd:binaryContents>
      </xsl:if>
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
