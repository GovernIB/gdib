<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" exclude-result-prefixes="fn xsd soapenv">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:template match="/">
     	<csgd:searchFilterResult>
         	<csgd:numDocuments><xsl:value-of select="fn:count(fn:distinct-values(//csgd:documents/csgd:metadataCollection[csgd:qname = 'eni:id']/csgd:value/text()))"/></csgd:numDocuments>
         	<csgd:document>
         		<csgd:nodeId><xsl:value-of select="/csgd:searchDocumentsResult/csgd:resParam/csgd:documents[1]/csgd:id/text()"/></csgd:nodeId>
         		<csgd:type><xsl:value-of select="/csgd:searchDocumentsResult/csgd:resParam/csgd:documents[1]/csgd:type/text()"/></csgd:type>
         		<csgd:appName><xsl:value-of select="/csgd:searchDocumentsResult/csgd:resParam/csgd:documents[1]/csgd:metadataCollection[csgd:qname = 'eni:app_tramite_doc']/csgd:value/text()"/></csgd:appName>
         		<xsl:if test="fn:boolean(/csgd:searchDocumentsResult/csgd:resParam/csgd:documents[1]/csgd:metadataCollection[csgd:qname = 'gdib:codigo_externo']/csgd:value/text())">
         			<csgd:extId><xsl:value-of select="/csgd:searchDocumentsResult/csgd:resParam/csgd:documents[1]/csgd:metadataCollection[csgd:qname = 'gdib:codigo_externo']/csgd:value/text()"/></csgd:extId>
         		</xsl:if>
         	</csgd:document>
        </csgd:searchFilterResult>
     </xsl:template>
 </xsl:stylesheet>