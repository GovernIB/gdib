<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:enids="http://administracionelectronica.gob.es/ENI/XSD/v1.0/firma" xmlns:enidocmeta="http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e/metadatos" xmlns:enifile="http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e/contenido" xmlns:enidoc="http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e" xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" exclude-result-prefixes="fn xsd soapenv csgd">
     <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
     <xsl:param name="docContent"/>
     <xsl:variable name="transformedDoc" select="fn:boolean(/csgd:document/csgd:aspects[text() = 'gdib:transformado'])"/>
     <xsl:variable name="signatureType" select="fn:upper-case(/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:tipoFirma']/csgd:value/text())"/>
     <xsl:template match="/">
         <enidoc:documento>
             <enifile:contenido>
             	<xsl:attribute name="Id">CONTENT_ID_1</xsl:attribute>
             	<xsl:choose>
                     <xsl:when test="$transformedDoc">
                     	<enifile:ValorBinario><xsl:value-of select="$docContent"/></enifile:ValorBinario>
                     </xsl:when>
                     <xsl:otherwise>
                     	<xsl:choose>
                     		<xsl:when test="fn:upper-case(/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:nombre_formato']/csgd:value/text()) = 'XML'">
                     			<xsl:choose>
	                     			<xsl:when test="fn:upper-case(//csgd:binaryContents[csgd:binaryType = 'CONTENT']/csgd:encoding/text()) = 'UTF-8'">
	                     				<enifile:DatosXML><xsl:value-of select="$docContent"/></enifile:DatosXML>
	                     			</xsl:when>
	                     			<xsl:otherwise>
	                     				<enifile:DatosXML><![CDATA[$docContent]]></enifile:DatosXML>
	                     			</xsl:otherwise>
	                     		</xsl:choose>
	                     	</xsl:when>
	                     	<xsl:otherwise>
	                     		<enifile:ValorBinario><xsl:value-of select="$docContent"/></enifile:ValorBinario>
	                     	</xsl:otherwise>
                     	</xsl:choose>                     	
                     </xsl:otherwise>
             	</xsl:choose>
             	<enifile:NombreFormato><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:nombre_formato']/csgd:value/text()"/></enifile:NombreFormato>
             </enifile:contenido>

             <enidocmeta:metadatos>
             	<xsl:attribute name="Id">METADATA_1</xsl:attribute>
             	<enidocmeta:VersionNTI>http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e</enidocmeta:VersionNTI>
             	<enidocmeta:Identificador><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:id']/csgd:value/text()"/></enidocmeta:Identificador>
             	<xsl:for-each select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:organo']/csgd:value">
             		<enidocmeta:Organo><xsl:value-of select="text()"/></enidocmeta:Organo>
             	</xsl:for-each>
             	<enidocmeta:FechaCaptura><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:fecha_inicio']/csgd:value/text()"/></enidocmeta:FechaCaptura>             	
             	<enidocmeta:OrigenCiudadanoAdministracion><xsl:value-of select="fn:boolean(/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:origen']/csgd:value/text() = '1')"/></enidocmeta:OrigenCiudadanoAdministracion>
             	<enidocmeta:EstadoElaboracion>
             		<enidocmeta:ValorEstadoElaboracion><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:estado_elaboracion']/csgd:value/text()"/></enidocmeta:ValorEstadoElaboracion>
             		<xsl:if test="fn:boolean(/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:id_origen']/csgd:value/text())">
             			<enidocmeta:IdentificadorDocumentoOrigen><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:id_origen']/csgd:value/text()"/></enidocmeta:IdentificadorDocumentoOrigen>
             		</xsl:if>
             	</enidocmeta:EstadoElaboracion>
             	<enidocmeta:TipoDocumental><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:tipo_doc_ENI']/csgd:value/text()"/></enidocmeta:TipoDocumental>
             </enidocmeta:metadatos>
             
             <enids:firmas>
             	<enids:firma>
             		<xsl:attribute name="Id">SIGNATURE_ID_1</xsl:attribute>
             		<xsl:if test="$signatureType = 'TF04'">
                    	<xsl:attribute name="ref">#CONTENT_ID_1</xsl:attribute>
         			</xsl:if>
             		<enids:TipoFirma><xsl:value-of select="$signatureType"/></enids:TipoFirma>
             		<enids:ContenidoFirma>
             			<xsl:choose>
             				<xsl:when test="$signatureType = 'TF02'">
             					<enids:FirmaConCertificado>
                    				<enids:ReferenciaFirma>#CONTENT_ID_1</enids:ReferenciaFirma>
                    			</enids:FirmaConCertificado>
         					</xsl:when>
         					<xsl:when test="$signatureType = 'TF03'">
                    			<enids:FirmaConCertificado>
                    				<enids:ReferenciaFirma>#CONTENT_ID_1</enids:ReferenciaFirma>
                    			</enids:FirmaConCertificado>
         					</xsl:when>
                    		<xsl:when test="$signatureType = 'TF04'">                    			
                    			<enids:FirmaConCertificado>
                    				<enids:FirmaBase64><xsl:value-of select="/csgd:document/csgd:binaryContents[csgd:binaryType = 'SIGNATURE']/csgd:content/text()"/></enids:FirmaBase64>
                    			</enids:FirmaConCertificado>
         					</xsl:when>
         					<xsl:when test="$signatureType = 'TF05'">		
                    			<enids:FirmaConCertificado>
                    				<enids:ReferenciaFirma>#CONTENT_ID_1</enids:ReferenciaFirma>
                    			</enids:FirmaConCertificado>
         					</xsl:when>
         					<xsl:when test="$signatureType = 'TF06'">		
                    			<enids:FirmaConCertificado>
                    				<enids:ReferenciaFirma>#CONTENT_ID_1</enids:ReferenciaFirma>
                    			</enids:FirmaConCertificado>
         					</xsl:when>
         					<xsl:otherwise>
         						<!-- Tipo de firma: TF01 -->
         						<enids:CSV>
                    				<enids:ValorCSV><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:csv']/csgd:value/text()"/></enids:ValorCSV>
                    				<enids:RegulacionGeneracionCSV><xsl:value-of select="/csgd:document/csgd:metadataCollection[csgd:qname = 'eni:def_csv']/csgd:value/text()"/></enids:RegulacionGeneracionCSV>
                    			</enids:CSV>
         					</xsl:otherwise>
         				</xsl:choose>		    		
             		</enids:ContenidoFirma>             		
             	</enids:firma>
             </enids:firmas>
             
         </enidoc:documento>
     </xsl:template>
 </xsl:stylesheet>