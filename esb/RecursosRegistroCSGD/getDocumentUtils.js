function getTypeDocId(mc) {
	var documentId;
	var documentIdString = mc.getProperty('documentId');
	var errorMessage = '';
    var documentIdOk = false;
    
	if(documentIdString != null){
		try{
			documentId = eval('('+documentIdString+')');
			if(documentId != null){				
				documentIdOk = (documentId.nodeId != null && documentId.nodeId.length > 0) || 
				(documentId.csv != null && documentId.csv.length > 0) || 
				(documentId.migratedDocId != null && (documentId.migratedDocId.externalId != null && documentId.migratedDocId.externalId.length > 0) && (documentId.migratedDocId.applicationId != null && documentId.migratedDocId.applicationId.length > 0));
				if(documentIdOk){
					if(documentId.nodeId != null && documentId.nodeId.length > 0){
						mc.setProperty('typeId', 'node');
						mc.setProperty('nodeId', documentId.nodeId);
					} else {
						if(documentId.csv != null && documentId.csv.length > 0){
							mc.setProperty('typeId', 'csv');
							mc.setProperty('nodeId', documentId.csv);
						} else {
							mc.setProperty('typeId', 'migratedNode');
							mc.setProperty('nodeId', documentId.migratedDocId.externalId);
							mc.setProperty('appName', documentId.migratedDocId.applicationId);
						}
					}
				} else {
					errorMessage = 'Petición mal formada. No fue informado el identificador o localizador del documento a recuperar (documentId.nodeId,documentId.csv o documentId.migratedDocId).';
				}
			} else {
				errorMessage = 'Petición mal formada. No fue informado el identificador o localizador del documento a recuperar.';
			}
		}catch(err){
			errorMessage = 'Petición mal formada. No fue informado el identificador o localizador del documento a recuperar. Excepcion: ' + err.message;
	    }
	} else {
		errorMessage = 'Petición mal formada. No fue informado el identificador o localizador del documento a recuperar.';
	}
	
    if(!documentIdOk){
    	mc.setProperty('errorMessage', errorMessage);    	
    } 

    mc.setProperty('documentIdOk', documentIdOk);
}

function extractContentAndSignatureGetDocResp(mc) {
	var errorMessage = '';
    var responseOk = false;
    var isEniDocType = false;
    var isMigrDocType = false;
    
    var getDocResString = mc.getProperty('getDocRes');
    if(getDocResString != null){
	    var getDocRes = eval('('+getDocResString+')');
	    
	    if(getDocRes != null && getDocRes.resParam != null && getDocRes.resParam.id != null && getDocRes.resParam.type != null){
	    	var docType = getDocRes.resParam.type;
	    	var typeDocFound = true;
	    	var transformedAspectFound = false;
	    	if(docType != 'eni:documento' && docType != '{http://www.administracionelectronica.gob.es/model/eni/1.0}documento' && docType != 'gdib:documentoMigrado' && docType != '{http://www.caib.es/model/gdib/1.0}documentoMigrado'){
	    		errorMessage = 'Tipo de entidad documental no admitido para la validación de documentos ('+typeDoc+').';
    			typeDocFound = false;
	    	} 

	    	if(typeDocFound){
	    		var contentRequired = false;
	    		isEniDocType = docType == 'eni:documento' || docType == '{http://www.administracionelectronica.gob.es/model/eni/1.0}documento';
	    		isMigrDocType = docType == 'gdib:documentoMigrado' || docType == '{http://www.caib.es/model/gdib/1.0}documentoMigrado';
	    		var signatureType = '';
	    		mc.setProperty('docType', docType);
	    		if(isEniDocType){
	    			if(getDocRes.resParam.metadataCollection != null){
		    			var signatureTypeFound = false;
		    			if(Object.prototype.toString.call(getDocRes.resParam.metadataCollection) == '[object Array]'){
			    			for (i = 0; !signatureTypeFound && i < getDocRes.resParam.metadataCollection.length; i++) {
			        			metadata = getDocRes.resParam.metadataCollection[i];
			        			if(metadata.qname == 'eni:tipoFirma' || metadata.qname == '{http://www.administracionelectronica.gob.es/model/eni/1.0}tipoFirma'){
			        				signatureType = metadata.value;	        				
			        				signatureTypeFound = true;
			        			}
			    		    }
		    			} else {
		    				metadata = getDocRes.resParam.metadataCollection;
		        			if(metadata.qname == 'eni:tipoFirma' || metadata.qname == '{http://www.administracionelectronica.gob.es/model/eni/1.0}tipoFirma'){
		        				signatureType = metadata.value;	        				
		        				signatureTypeFound = true;
		        			}
		    			}
		    			
		    			if(signatureType == 'TF04'){
		    				contentRequired = true;
		    			}
		    			mc.setProperty('signatureType', signatureType);
	    			}
	    			
	    			if(getDocRes.resParam.aspects != null){
		    			for (i = 0; !transformedAspectFound && i < getDocRes.resParam.aspects.length; i++) {
		        			aspect = getDocRes.resParam.aspects[i];
		        			if(aspect == 'gdib:transformado' || aspect == '{http://www.caib.es/model/gdib/1.0}transformado'){
		        				mc.setProperty('transformedAspect', aspect);
		        				transformedAspectFound = true;
		        			}
		    		    }
	    			}	    			
	    		}

		    	if(getDocRes.resParam.binaryContents != null){
		    		var contentFound = false;
		    		var docSignatureContentFound = false;
		    		var signatureFound = false;
		    		var valCertSignatureFound = false;
		    		
		    		if(Object.prototype.toString.call(getDocRes.resParam.binaryContents) == '[object Array]'){
		    			mc.setProperty('traza', 'Firma explicita');
				    	for (i = 0; i < getDocRes.resParam.binaryContents.length; i++) {
				    		binaryContent = getDocRes.resParam.binaryContents[i];
				    		if(binaryContent.binaryType == 'CONTENT' && contentRequired){
			    				//Firma explícita
			    				mc.setProperty('docContent', binaryContent.content);
			    				contentFound = true;
			    			}

				    		if(isEniDocType){
				    			if(binaryContent.binaryType == 'SIGNATURE' && contentRequired){
					    			mc.setProperty('docSignature', binaryContent.content);
					    			signatureFound = true;
					    		}
				    			if(transformedAspectFound){
				    				if(binaryContent.binaryType == 'MIGRATION_ZIP'){
						    			mc.setProperty('docSignatureContent', binaryContent.content);
						    			docSignatureContentFound = true;
						    		}  else if(binaryContent.binaryType == 'VALCERT_SIGNATURE'){
						    			mc.setProperty('valCertDocSignature', binaryContent.content);
						    			valCertSignatureFound = true;
						    		}
				    			}
					    	} else if(isMigrDocType){
					    		if(binaryContent.binaryType == 'MIGRATION_ZIP'){
					    			mc.setProperty('docSignatureContent', binaryContent.content);
					    			docSignatureContentFound = true;
					    		} else if(binaryContent.binaryType == 'MIGRATION_SIGNATURE'){
					    			mc.setProperty('docSignature', binaryContent.content);
					    			signatureFound = true;
					    		}  else if(binaryContent.binaryType == 'VALCERT_SIGNATURE'){
					    			mc.setProperty('valCertDocSignature', binaryContent.content);
					    			valCertSignatureFound = true;
					    		}
					    	}
					    }
		    		} else {
		    			mc.setProperty('traza', 'Firma implicita');
		    			//Cuando se retorna un solo binario, no se retorna como array, sino como objeto simple
		    			//Esto solo sucede con documentos tipo eni:documento, que poseen contenido y, opcionalmente, firma
		    			//Por tanto, solo es posible en este caso firmas implicitas
		    			binaryContent = getDocRes.resParam.binaryContents;
		    			if(binaryContent.binaryType == 'CONTENT' && !contentRequired){
		    				mc.setProperty('traza', 'Firma implicita - Cumple condicion');

		    				//Firma implícita
		    				mc.setProperty('docSignature', binaryContent.content);
			    			signatureFound = true;
			    		} else {
			    			mc.setProperty('traza', 'Firma implicita - No Cumple condicion' + binaryContent.binaryType);
			    		}
		    		}

			    	mc.setProperty('docType', docType);
			    	mc.setProperty('signatureFound', signatureFound);
			    	mc.setProperty('contentRequired', contentRequired);
			    	mc.setProperty('contentFound', contentFound);
			    	mc.setProperty('docSignatureContentFound', docSignatureContentFound);
			    	mc.setProperty('valCertSignatureFound', valCertSignatureFound);

			    	responseOk = signatureFound;
			    	if(!responseOk){
			    		errorMessage = 'El documento a verificar no posee firma electrónica, o esta no puede ser recuperada.';
			    	} else {
				    	if(isEniDocType){
				    		var contentOk = true;
				    		if(contentRequired){
				    			if(!contentFound){
				    				contentOk = false;
				    			}
				    		}
				    		mc.setProperty('contentOk', contentOk);
				    		responseOk = contentOk;
				    		if(!responseOk){
				    			errorMessage = 'El documento a verificar no posee contenido, o no puede ser recuperado, y es requerido para el proceso de validación.';
				    		}
				    	} else if(isMigrDocType){
				    		responseOk = docSignatureContentFound && valCertSignatureFound;
				    		if(!responseOk){
				    			errorMessage = 'No se obtuvo el contenido y la firma electrónica del documento migrado requeridos para la verificación del mismo.';
				    		}
				    	}
			    	}
		    	} else {
			    	errorMessage = 'No se obtuvo el contenido y la firma electrónica del documento. El documento no posee binarios.';
			    }
	    	}
	    } else {
	    	errorMessage = 'No se obtuvo documento del Archivo Digital.';
	    }
    } else {
    	errorMessage = 'No se obtuvo documento del Archivo Digital.';
    }

    if(!responseOk){
    	mc.setProperty('respServiceErrorMessage', errorMessage);
    }

    mc.setProperty('respServiceOk', responseOk);
}

function extractDocumentInfo(mc) {
	var errorMessage = '';
	var documentResp = '';
    var responseOk = false;

    var getDocResString = mc.getProperty('getDocRes');
    if(getDocResString != null){
	    var getDocRes = eval('('+getDocResString+')');

	    if(getDocRes != null && getDocRes.resParam != null && getDocRes.resParam.id != null && getDocRes.resParam.type != null){
	    	var documentResp = '<csgd:document xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0">';

	    	documentResp = documentResp + '<csgd:id>' + getDocRes.resParam.id + '</csgd:id>';
	    	documentResp = documentResp + '<csgd:name>' + getDocRes.resParam.name + '</csgd:name>';
    		documentResp = documentResp + '<csgd:type>' + getDocRes.resParam.type + '</csgd:type>';

    		//Aspectos
    		if(getDocRes.resParam.aspects != null){
    			if(Object.prototype.toString.call(getDocRes.resParam.aspects) == '[object Array]'){
		       		for (i = 0; i < getDocRes.resParam.aspects.length; i++) {
		        			aspect = getDocRes.resParam.aspects[i];
		        			documentResp = documentResp + '<csgd:aspects>' + aspect + '</csgd:aspects>';
		    		}
    			} else {
    				documentResp = documentResp + '<csgd:aspects>' + getDocRes.resParam.aspects + '</csgd:aspects>';
    			}
    		}

    		//Metadatos
    		if(getDocRes.resParam.metadataCollection != null){
    			if(Object.prototype.toString.call(getDocRes.resParam.metadataCollection) == '[object Array]'){
		    		for (i = 0; i < getDocRes.resParam.metadataCollection.length; i++) {
		    			metadata = getDocRes.resParam.metadataCollection[i];

		    			documentResp = documentResp + '<csgd:metadataCollection>';
		    			documentResp = documentResp + '<csgd:qname>' + metadata.qname + '</csgd:qname>';
		    			if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
		    				for (j = 0; j < metadata.value.length; j++) {
		    					documentResp = documentResp + '<csgd:value>' + metadata.value[j] + '</csgd:value>';
		    				}
		    			} else {
		    				documentResp = documentResp + '<csgd:value>' + metadata.value + '</csgd:value>';
		    			}
		    			documentResp = documentResp + '</csgd:metadataCollection>';
				    }
    			} else {
    				metadata = getDocRes.resParam.metadataCollection;

	    			documentResp = documentResp + '<csgd:metadataCollection>';
	    			documentResp = documentResp + '<csgd:qname>' + metadata.qname + '</csgd:qname>';
	    			if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
	    				for (j = 0; j < metadata.value.length; j++) {
	    					documentResp = documentResp + '<csgd:value>' + metadata.value[j] + '</csgd:value>';
	    				}
	    			} else {
	    				documentResp = documentResp + '<csgd:value>' + metadata.value + '</csgd:value>';
	    			}
	    			documentResp = documentResp + '</csgd:metadataCollection>';
    			}
    		}

	    	documentResp += '</csgd:document>';
	    	mc.setProperty('documentResp', documentResp);
	    }
    }
}

function extractDocumentInfoAndContents(mc) {
	var errorMessage = '';
	var documentResp = '';
    var responseOk = false;

    var getDocResString = mc.getProperty('getDocRes');
    if(getDocResString != null){
	    var getDocRes = eval('('+getDocResString+')');

	    if(getDocRes != null && getDocRes.resParam != null && getDocRes.resParam.id != null && getDocRes.resParam.type != null){
	    	var documentResp = '<csgd:document xmlns:csgd="urn:es.caib.archivodigital.esb.services:1.0.0">';

	    	documentResp = documentResp + '<csgd:id>' + getDocRes.resParam.id + '</csgd:id>';
	    	documentResp = documentResp + '<csgd:name>' + getDocRes.resParam.name + '</csgd:name>';
    		documentResp = documentResp + '<csgd:type>' + getDocRes.resParam.type + '</csgd:type>';

    		//Aspectos
    		if(getDocRes.resParam.aspects != null){
    			if(Object.prototype.toString.call(getDocRes.resParam.aspects) == '[object Array]'){
		       		for (i = 0; i < getDocRes.resParam.aspects.length; i++) {
	        			aspect = getDocRes.resParam.aspects[i];
	        			documentResp = documentResp + '<csgd:aspects>' + aspect + '</csgd:aspects>';
		    		}
    			} else {
    				documentResp = documentResp + '<csgd:aspects>' + getDocRes.resParam.aspects + '</csgd:aspects>';
    			}
    		}

    		//Metadatos
    		if(getDocRes.resParam.metadataCollection != null){
    			if(Object.prototype.toString.call(getDocRes.resParam.metadataCollection) == '[object Array]'){
		    		for (i = 0; i < getDocRes.resParam.metadataCollection.length; i++) {
		    			metadata = getDocRes.resParam.metadataCollection[i];

		    			documentResp = documentResp + '<csgd:metadataCollection>';
		    			documentResp = documentResp + '<csgd:qname>' + metadata.qname + '</csgd:qname>';
		    			if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
		    				for (j = 0; j < metadata.value.length; j++) {
		    					documentResp = documentResp + '<csgd:value>' + metadata.value[j] + '</csgd:value>';
		    				}
		    			} else {
		    				documentResp = documentResp + '<csgd:value>' + metadata.value + '</csgd:value>';
		    			}
		    			documentResp = documentResp + '</csgd:metadataCollection>';
				    }
    			} else {
    				metadata = getDocRes.resParam.metadataCollection;

	    			documentResp = documentResp + '<csgd:metadataCollection>';
	    			documentResp = documentResp + '<csgd:qname>' + metadata.qname + '</csgd:qname>';
	    			if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
	    				for (j = 0; j < metadata.value.length; j++) {
	    					documentResp = documentResp + '<csgd:value>' + metadata.value[j] + '</csgd:value>';
	    				}
	    			} else {
	    				documentResp = documentResp + '<csgd:value>' + metadata.value + '</csgd:value>';
	    			}
	    			documentResp = documentResp + '</csgd:metadataCollection>';
    			}
    		}

    		//Contenidos
    		if(getDocRes.resParam.binaryContents != null){
    			if(Object.prototype.toString.call(getDocRes.resParam.binaryContents) == '[object Array]'){
	    			for (i = 0; i < getDocRes.resParam.binaryContents.length; i++) {
	    				binaryContent = getDocRes.resParam.binaryContents[i];
	        			documentResp = documentResp + '<csgd:binaryContents>';
	        			documentResp = documentResp + '<csgd:binaryType>' + binaryContent.binaryType + '</csgd:binaryType>';
	        			if(binaryContent.mimetype != null){
	        				documentResp = documentResp + '<csgd:mimetype>' + binaryContent.mimetype + '</csgd:mimetype>';
	        			}
	        			documentResp = documentResp + '<csgd:content>' + binaryContent.content + '</csgd:content>';
	        			if(binaryContent.byteSize != null){
	        			    documentResp = documentResp + '<csgd:byteSize>' + binaryContent.byteSize + '</csgd:byteSize>';
	        			}
	        			if(binaryContent.encoding != null){
	        				documentResp = documentResp + '<csgd:encoding>' + binaryContent.encoding + '</csgd:encoding>';
	        			}
	        			documentResp = documentResp + '</csgd:binaryContents>';
	    			}
    			} else {
    				binaryContent = getDocRes.resParam.binaryContents;
        			documentResp = documentResp + '<csgd:binaryContents>';
        			documentResp = documentResp + '<csgd:binaryType>' + binaryContent.binaryType + '</csgd:binaryType>';
        			if(binaryContent.mimetype != null){
        				documentResp = documentResp + '<csgd:mimetype>' + binaryContent.mimetype + '</csgd:mimetype>';
        			}
        			documentResp = documentResp + '<csgd:content>' + binaryContent.content + '</csgd:content>';
        			if(binaryContent.encoding != null){
        				documentResp = documentResp + '<csgd:encoding>' + binaryContent.encoding + '</csgd:encoding>';
        			}
        			if(binaryContent.byteSize != null){
                        documentResp = documentResp + '<csgd:byteSize>' + binaryContent.byteSize + '</csgd:byteSize>';
                    }
        			documentResp = documentResp + '</csgd:binaryContents>';
    			}
    		}
	    	documentResp += '</csgd:document>';
	    	mc.setProperty('documentResp', documentResp);
	    }
    }
}