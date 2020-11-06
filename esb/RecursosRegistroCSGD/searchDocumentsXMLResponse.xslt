<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema"  xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:syn="http://ws.apache.org/ns/synapse" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:ws="http://www.caib.es/gdib/repository/ws" version="2.0" exclude-result-prefixes="fn xsd soapenv syn ws">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="pageNumber"/>
	<xsl:variable name="numeroTotalResultados" select="//ws:searchNodeResponse/ws:result/ws:numResultados/text()"/>
	<xsl:variable name="eniUriNsLength" select="fn:string-length('{http://www.administracionelectronica.gob.es/model/eni/1.0}')" />
	<xsl:variable name="gdibUriNsLength" select="fn:string-length('{http://www.caib.es/model/gdib/1.0}')" />
	<xsl:template match="/">
		<csgd:searchDocumentsResult>
			<csgd:result>
				<xsl:choose>
					<xsl:when test="$numeroTotalResultados = -1">
						<csgd:code>COD_020</csgd:code>
						<csgd:description>La consulta lucene contiene errores de sintaxis.</csgd:description>
					</xsl:when>
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
				<csgd:resParam>
					<csgd:totalNumberOfResults><xsl:value-of select="$numeroTotalResultados"/></csgd:totalNumberOfResults>
					<xsl:choose>
						<xsl:when test="fn:boolean($pageNumber)">
							<csgd:pageNumber><xsl:value-of select="$pageNumber"/></csgd:pageNumber>
						</xsl:when>
						<xsl:otherwise>
							<csgd:pageNumber>0</csgd:pageNumber>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select="//ws:searchNodeResponse/ws:result/ws:resultados" >
						<csgd:documents>
							<csgd:id>
								<xsl:value-of select="ws:id/text()"/>
							</csgd:id>
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
											<xsl:if test="fn:compare(ws:qname/text(),'eni:firma') != 0 and fn:compare(ws:qname/text(),'gdib:firma_valcert') != 0 and fn:compare(ws:qname/text(),'gdib:zipMigracion') != 0">
												<csgd:metadataCollection>
													<csgd:qname><xsl:value-of select="ws:qname/text()"/></csgd:qname>
													<xsl:apply-templates select="ws:value/text()"/>
												</csgd:metadataCollection>
											</xsl:if>
										</xsl:when>
										<xsl:when test="fn:starts-with(ws:qname/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}')">
											<xsl:if test="fn:compare(ws:qname/text(),'{http://www.administracionelectronica.gob.es/model/eni/1.0}firma') != 0">
												<csgd:metadataCollection>
													<csgd:qname><xsl:value-of select="fn:concat('eni:',fn:substring(ws:qname/text(),$eniUriNsLength+1))"/></csgd:qname>
													<xsl:apply-templates select="ws:value/text()"/>
												</csgd:metadataCollection>
											</xsl:if>
										</xsl:when>
										<xsl:when test="fn:starts-with(ws:qname/text(),'{http://www.caib.es/model/gdib/1.0}')">
											<xsl:if test="fn:compare(ws:qname/text(),'{http://www.caib.es/model/gdib/1.0}firma_valcert') != 0 and fn:compare(ws:qname/text(),'{http://www.caib.es/model/gdib/1.0}zipMigracion') != 0">
												<csgd:metadataCollection>
													<csgd:qname><xsl:value-of select="fn:concat('gdib:',fn:substring(ws:qname/text(),$gdibUriNsLength+1))"/></csgd:qname>
													<xsl:apply-templates select="ws:value/text()"/>
												</csgd:metadataCollection>
											</xsl:if>
										</xsl:when>
									</xsl:choose>
								</xsl:if>
							</xsl:for-each>
						</csgd:documents>
					</xsl:for-each>
				</csgd:resParam>
			</xsl:if>
		</csgd:searchDocumentsResult>
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