function buildCreateClassificationRootRequest(mc) {
	var codigo="COD_099";
	var requestOk = false;
	var requestString;
	payload = mc.getPayloadJSON();
	createRootReq = payload.createClassificationRootRequest;
	if (createRootReq != null && createRootReq.param != null && createRootReq.param.classificationRoot != null ) {
		var rootReq = createRootReq.param.classificationRoot ;
		requestOk = rootReq.type != null;
                if (requestOk) {
                requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
                requestString = requestString + '<ws:node>';
                if(rootReq.id!= null){
                  requestString = requestString + '<ws:id>'+rootReq.id+'</ws:id>';
                }
                if(rootReq.name!= null){
                    requestString = requestString + '<ws:name>'+rootReq.name+'</ws:name>';
                }

                requestString = requestString + '<ws:type>'+rootReq.type+'</ws:type>';

                //Aspectos
                	if(rootReq.aspects!= null){
				for (i = 0; ! i < rootReq.aspects.length; i++) {
					aspect = rootReq.aspects[i];
					requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
				}
		 }


                //Propiedas
								if(rootReq.metadataCollection != null && rootReq.metadataCollection.length > 0){
                	requestString += generatePropertiesReqElement(rootReq.metadataCollection);
								}
			requestString = requestString + '</ws:node>';

			requestString = requestString + '</ws:createNode>';
		} else {
			errorMessage = 'Petición mal formada. No se han mandado alguno de los parametros obligatorios para la creación el cuadro.';
			codigo="COD_010";
		}

	} else {

		errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
	}
	if (requestOk) {
		mc.setProperty('reqService', requestString);
	} else {
		mc.setProperty('reqServiceErrorMessage', errorMessage);
		mc.setProperty('codeErrorMessage', codigo);
	}

	mc.setProperty('reqServiceOk', requestOk);
}
function buildCreateSerieRequest(mc) {
	var codigo="COD_099";
	var requestOk = false;
	var requestString="";
	var errorMessage="error";
        payload = mc.getPayloadJSON();
	createSerieReq = payload.createSerieRequest;
        if (createSerieReq != null && createSerieReq.param != null && createSerieReq.param.serie != null && createSerieReq.param.parentId != null) {
		var serieReq = createSerieReq.param.serie;
		requestOk = serieReq.type != null && serieReq.metadataCollection != null && serieReq.aspects != null && serieReq.binaryContent != null ;
		if (requestOk) {

                  requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    requestString = requestString + '<ws:node>';
    if(serieReq.id!= null){
      requestString = requestString + '<ws:id>'+serieReq.id+'</ws:id>';
    }
    if(serieReq.name!= null){
        requestString = requestString + '<ws:name>'+serieReq.name+'</ws:name>';
    }

    requestString = requestString + '<ws:type>'+serieReq.type+'</ws:type>';

    //Aspectos
    for (i = 0;  i < serieReq.aspects.length; i++) {
      aspect = serieReq.aspects[i];
      requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
    }


    //Propiedas
    requestString += generatePropertiesReqElement(serieReq.metadataCollection);
		var binaryContent= serieReq.binaryContent;
		requestString = requestString + '<ws:content>';
		if(binaryContent.mimetype != null){
			requestString = requestString + '<ws:mimetype>' + binaryContent.mimetype + '</ws:mimetype>';
		}
		//if(binaryContent.content != null){
			requestString = requestString + '<ws:data>' + binaryContent.content + '</ws:data>';
		//}
		if(binaryContent.encoding != null){
			requestString = requestString + '<ws:encoding>' + binaryContent.encoding + '</ws:encoding>';
		}
		requestString = requestString + '</ws:content>';

    requestString = requestString + '</ws:node>';
		requestString = requestString + '<ws:parent>' + createSerieReq.param.parentId + '</ws:parent>';

    requestString = requestString + '</ws:createNode>';
		} else {
			errorMessage = 'Petición mal formada. No se han mandado alguno de los parametros obligatorios para la creación de la Seri.';
			codigo="COD_010";
		}

	} else {

		errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
	}
	//requestOk = false;
	if (requestOk) {
		mc.setProperty('reqService', requestString);
	} else {
		mc.setProperty('reqServiceErrorMessage', errorMessage);
		mc.setProperty('codigoErrorMessage', codigo);
	}

	mc.setProperty('reqServiceOk', requestOk);
}
function buildCreateFunctionRequest(mc) {
	var codigo="COD_099";
	var requestOk = false;
	var requestString;
	payload = mc.getPayloadJSON();
	createFunctionReq = payload.createFunctionRequest;
	if (createFunctionReq != null && createFunctionReq.param.classificationFunction != null && createFunctionReq.param.parentId != null) {
		errorMessage = 'Petición formada6';
		var functionReq = createFunctionReq.param.classificationFunction;
		requestOk = functionReq.type != null && functionReq.metadataCollection != null;
                if (requestOk) {
                requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
                requestString = requestString + '<ws:node>';
                if(functionReq.id!= null){
                  requestString = requestString + '<ws:id>'+functionReq.id+'</ws:id>';
                }
                if(functionReq.name!= null){
                    requestString = requestString + '<ws:name>'+functionReq.name+'</ws:name>';
                }

                requestString = requestString + '<ws:type>'+functionReq.type+'</ws:type>';

                //Aspectos
                	if(functionReq.aspects!= null){
				for (i = 0; ! i < functionReq.aspects.length; i++) {
					aspect = functionReq.aspects[i];
					requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
				}
		 }


                //Propiedas
                requestString += generatePropertiesReqElement(functionReq.metadataCollection);
			requestString = requestString + '</ws:node>';
			requestString = requestString + '<ws:parent>' + createFunctionReq.param.parentId + '</ws:parent>';

			requestString = requestString + '</ws:createNode>';
		} else {
			errorMessage = 'Petición mal formada. No se han mandado alguno de los parametros obligatorios para la creación de la Funcion.';
			codigo="COD_010";
		}

	} else {

		errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados en funcion.';
	}
	if (requestOk) {
		mc.setProperty('reqService', requestString);
	} else {
		mc.setProperty('reqServiceErrorMessage', errorMessage);
		mc.setProperty('codigoErrorMessage', codigo);
	}

	mc.setProperty('reqServiceOk', requestOk);
}
function buildCreateFileRequest(mc) {
	payload = mc.getPayloadJSON();
	createFileReq = payload.createFileRequest;
    var requestOk = false;
    var requestString;

    if(createFileReq != null && createFileReq.param != null  && createFileReq.param.file != null){

    	var fileReq = createFileReq.param.file;
    	requestOk = fileReq.name != null && (fileReq.metadataCollection != null && fileReq.metadataCollection.length > 0) && (fileReq.aspects != null && fileReq.aspects.length >0);

    	if(requestOk){
    		var retrieveNode = mc.getProperty('retrieveNode');
			if(retrieveNode != null && retrieveNode.equals('true')){
				requestString = '<ws:createAndGetNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
			} else {
				requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
			}

			requestString = requestString + '<ws:node>';
    		requestString = requestString + '<ws:name>' + fileReq.name + '</ws:name>';
    		requestString = requestString + '<ws:type>eni:expediente</ws:type>';
    		//Aspectos
       		for (i = 0; i < fileReq.aspects.length; i++) {
    			aspect = fileReq.aspects[i];
    			requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
    		}
       		//Metadatos
       		requestString += generatePropertiesReqElement(fileReq.metadataCollection);

       		requestString = requestString + '</ws:node>';

    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = requestString + '</ws:createAndGetNode>';
    		} else {
    			requestString = requestString + '</ws:createNode>';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros file.name, file.metadataCollection y file.aspects del nuevo expediente deben ser informados.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildCreateChildFileRequest(mc) {
	payload = mc.getPayloadJSON();
	createChildFileReq = payload.createChildFileRequest;
    var requestOk = false;
    var requestString;

    if(createChildFileReq != null && createChildFileReq.param != null  && createChildFileReq.param.file != null){
    	var reqParam = createChildFileReq.param;
    	var childFileReq = createChildFileReq.param.file;

    	requestOk = reqParam.parent != null && childFileReq.name != null && (childFileReq.metadataCollection != null && childFileReq.metadataCollection.length > 0) && (childFileReq.aspects != null && childFileReq.aspects.length >0);

    	if(requestOk){
    		var retrieveNode = mc.getProperty('retrieveNode');
    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = '<ws:createAndGetNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		} else {
    			requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		}
    		requestString = requestString + '<ws:parent>' + reqParam.parent + '</ws:parent>';
    		requestString = requestString + '<ws:node>';
    		requestString = requestString + '<ws:name>' + childFileReq.name + '</ws:name>';
    		requestString = requestString + '<ws:type>eni:expediente</ws:type>';
    		//Aspectos
       		for (i = 0; i < childFileReq.aspects.length; i++) {
        			aspect = childFileReq.aspects[i];
        			requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
   		    }
       		//Metadatos
       		requestString += generatePropertiesReqElement(childFileReq.metadataCollection);

       		requestString = requestString + '</ws:node>';

       		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = requestString + '</ws:createAndGetNode>';
    		} else {
    			requestString = requestString + '</ws:createNode>';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros parent, childFile.name, childFile.metadataCollection y childFile.aspects del nuevo sub expediente deben ser informados.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildCreateFolderRequest(mc) {
	payload = mc.getPayloadJSON();
	createFolderReq = payload.createFolderRequest;
    var requestOk = false;
    var requestString;

    if(createFolderReq != null && createFolderReq.param != null && createFolderReq.param.folder != null){
    	reqParam = createFolderReq.param;
    	folderReq = createFolderReq.param.folder;

    	requestOk = reqParam.parent != null && folderReq.name != null;

    	if(requestOk){
    		var retrieveNode = mc.getProperty('retrieveNode');
    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = '<ws:createAndGetNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		} else {
    			requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		}

    		requestString = requestString + '<ws:parent>' + reqParam.parent + '</ws:parent>';

    		requestString = requestString + '<ws:node>';
    		requestString = requestString + '<ws:name>' + folderReq.name + '</ws:name>';
    		requestString = requestString + '<ws:type>eni:agregacionDoc</ws:type>';
    		requestString = requestString + '</ws:node>';

    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = requestString + '</ws:createAndGetNode>';
    		} else {
    			requestString = requestString + '</ws:createNode>';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros parent y folder.name de la nueva agrupación documental deben ser informados.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildCreateDocRequest(mc) {
	payload = mc.getPayloadJSON();
	createDocReq = payload.createDocumentRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString;

    if(createDocReq != null && createDocReq.param != null && createDocReq.param.document != null){

		var reqParam = createDocReq.param;
		var docReq = createDocReq.param.document;
		var draftAspectFound = false;
		var contentFound = false;
		var signatureFound = false;

    	requestOk = reqParam.parent != null && docReq.name != null && (docReq.binaryContents != null && docReq.binaryContents.length > 0) && (docReq.metadataCollection != null && docReq.metadataCollection.length > 0) && (docReq.aspects != null && docReq.aspects.length > 0);

    	if(requestOk){
    		//Se verifica que se ha informado el contenido y la firma
    		for (i = 0; !(contentFound && signatureFound) && i < docReq.binaryContents.length; i++) {
    			binaryContent = docReq.binaryContents[i];
    			if(binaryContent.binaryType != null && binaryContent.binaryType.equals('CONTENT')){
    				contentFound = binaryContent.content != null && binaryContent.content.length > 0;
    			} else if(binaryContent.binaryType != null && binaryContent.binaryType.equals('SIGNATURE')){
    				signatureFound = binaryContent.content != null && binaryContent.content.length > 0;
    			}
    		}

    		var signatureProperties = getSignatureProperties(docReq.metadataCollection);
    		var signatureType = '';

    		if(signatureProperties != null && signatureProperties.length > 0){
    			signatureType = signatureProperties[0];

    			if(signatureType == 'TF04' && !signatureFound){
    				requestOk = false;
    			} else {
    				requestOk = contentFound;
    			}
    		} else {
    			requestOk = false;
    		}

    		if(requestOk){
    			var retrieveNode = mc.getProperty('retrieveNode');
        		if(retrieveNode != null && retrieveNode.equals('true')){
        			requestString = '<ws:createAndGetNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
        		} else {
        			requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
        		}

    			requestString = requestString + '<ws:parent>' + reqParam.parent + '</ws:parent>';
        		requestString = requestString + '<ws:node>';
				requestString = requestString + '<ws:name>' + docReq.name + '</ws:name>';
	    		requestString = requestString + '<ws:type>eni:documento</ws:type>';

        		for (i = 0; !draftAspectFound && i < docReq.aspects.length; i++) {
        			aspect = docReq.aspects[i];
        			if(aspect.indexOf('-gdib:borrador') < 0 && aspect.indexOf('gdib:borrador') < 0){
        				requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
        			} else {
        				draftAspectFound = true;
        			}
    		    }

	    		if(draftAspectFound){
	    			requestOk = false;
	    			errorMessage = 'Petición mal formada. No está permitido el aspecto gdib:borrador.';
	    		} else {
	    			//Metadatos
	    			requestString += generatePropertiesReqElement(docReq.metadataCollection);

	    			//Contenido y firma
	    			requestString += generateBinaryContentsReqElement(docReq,signatureFound);
	    		}
		    	requestString = requestString + '</ws:node>';

		    	if(retrieveNode != null && retrieveNode.equals('true')){
	    			requestString = requestString + '</ws:createAndGetNode>';
	    		} else {
	    			requestString = requestString + '</ws:createNode>';
	    		}
    		} else {
    			errorMessage = 'Petición mal formada. No se informo el contenido y/o la firma electrónica del nuevo documento, o alguno de los metadatos relacionados con la firma electrónica (eni:tipoFirma o eni:perfil_firma).';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros parent, document.name, document.metadataCollection, document.aspects y document.binaryContents del nuevo documento deben ser informados.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildCreateDraftDocRequest(mc) {
	payload = mc.getPayloadJSON();
	createDraftDocReq = payload.createDraftDocumentRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString;

    if(createDraftDocReq != null && createDraftDocReq.param != null && createDraftDocReq.param.document != null){

		var reqParam = createDraftDocReq.param;
		var draftDocReq = reqParam.document;
		var draftAspectFound = false;

    	requestOk = reqParam.parent != null && draftDocReq.name != null;

    	if(requestOk){
    		var retrieveNode = mc.getProperty('retrieveNode');
    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = '<ws:createAndGetNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		} else {
    			requestString = '<ws:createNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
    		}
    		//Nodo padre
    		requestString = requestString + '<ws:parent>' + reqParam.parent + '</ws:parent>';
    		//Nuevo nodo
    		requestString = requestString + '<ws:node>';
			requestString = requestString + '<ws:name>' + draftDocReq.name + '</ws:name>';
    		requestString = requestString + '<ws:type>eni:documento</ws:type>';

    		//Aspectos
    		requestString = requestString + '<ws:aspects>gdib:borrador</ws:aspects>';
    		if(draftDocReq.aspects != null && draftDocReq.aspects.length >0){
        		for (i = 0; !draftAspectFound && i < draftDocReq.aspects.length; i++) {
        			aspect = draftDocReq.aspects[i];
        			if(aspect.indexOf('-gdib:borrador') < 0 && aspect.indexOf('gdib:borrador') < 0){
        				requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
        			} else {
        				draftAspectFound = true;
        			}
    		    }
    		}

    		if(draftAspectFound){
    			requestOk = false;
    			errorMessage = 'Petición mal formada. No está permitido el aspecto gdib:borrador.';
    		} else {
    			if(draftDocReq.metadataCollection != null && draftDocReq.metadataCollection.length > 0){
        			requestString += generatePropertiesReqElement(draftDocReq.metadataCollection);
        		}
	    		//Contenido y firma
    			requestString += generateBinaryContentsReqElement(draftDocReq,false);
    		}
    		requestString = requestString + '</ws:node>';

    		if(retrieveNode != null && retrieveNode.equals('true')){
    			requestString = requestString + '</ws:createAndGetNode>';
    		} else {
    			requestString = requestString + '</ws:createNode>';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros parent y document.name del nuevo documento deben ser informados.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildSetDocRequest(mc) {
	payload = mc.getPayloadJSON();
	setDocReq = payload.setDocumentRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString = '<ws:node xmlns:ws="http://www.caib.es/gdib/repository/ws">';

    if(setDocReq != null && setDocReq.param != null && setDocReq.param.document != null){

		var reqParam = setDocReq.param;
		var docReq = reqParam.document;
		var draftAspectFound = false;

    	requestOk = docReq.id != null;

    	if(requestOk){
    		requestString = requestString + '<ws:id>' + docReq.id + '</ws:id>';

    		if(docReq.name != null && docReq.name.length > 0){
    			requestString = requestString + '<ws:name>' + docReq.name + '</ws:name>';
    		}

    		if(docReq.aspects != null && docReq.aspects.length > 0){
        		for (i = 0; !draftAspectFound && i < docReq.aspects.length; i++) {
        			aspect = docReq.aspects[i];
        			if(aspect.indexOf('-gdib:borrador') < 0 && aspect.indexOf('gdib:borrador') < 0){
        				requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
        			} else {
        				draftAspectFound = true;
        			}
    		    }
    		}

    		if(draftAspectFound){
    			requestOk = false;
    			errorMessage = 'Petición mal formada. No está permitido el aspecto gdib:borrador.';
    		} else {
    			//Metadatos
    			if(docReq.metadataCollection != null && docReq.metadataCollection.length > 0){
        			requestString += generatePropertiesReqElement(docReq.metadataCollection);
        		}

	    		//Contenido y firma
    			requestString += generateBinaryContentsReqElement(docReq,false);
    		}
    		requestString = requestString + '</ws:node>';
    	} else {
    		errorMessage = 'Petición mal formada. El parámetro document.id del documento debe ser informado.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildCreateTargetDocRequest(mc) {
	var allowedMetadataCol = ["eni:id","eni:app_tramite_doc","eni:csv","eni:tipoFirma","eni:perfil_firma","eni:organo","eni:origen","eni:id_origen","eni:estado_elaboracion","eni:tipo_doc_ENI","eni:fecha_inicio","eni:cod_clasificacion","eni:nombre_formato","eni:extension_formato","eni:resolucin","eni:idioma","eni:tamano_logico","eni:profundidad_color","eni:descripcion","eni:termino_punto_acceso","eni:id_punto_acceso","eni:esquema_punto_acceso","eni:soporte","eni:loc_archivo_central","eni:loc_archivo_general","eni:unidades","eni:subtipo_doc","eni:tipo_asiento_registral","eni:codigo_oficina_registro","eni:fecha_asiento_registral","eni:numero_asiento_registral","gdib:fecha_traslado","gdib:autor_traslado","gdib:destino_traslado","gdib:id_nodo_nueva_loc","gdib:tipo_destino"];
	var dispatchedDoc;
	var dispatchedDocString = mc.getProperty('dispatchedDoc');
	var newDocSeriesParamString = mc.getProperty('newDocSeriesParam');
	var newDocSubSeriesParamString = mc.getProperty('newDocSubSeriesParam');
	var errorMessage = '';
    var createDocReqOk = false;
    var sourceNodeId = mc.getProperty('sourceNodeIdParam');
    var targetDocument = '';

    if(dispatchedDocString != null){
		try{
			dispatchedDoc = eval('('+dispatchedDocString+')');
			createDocReqOk = dispatchedDoc != null && (dispatchedDoc.type != null && dispatchedDoc.type == 'eni:documento');

			if(createDocReqOk){
				createDocReqOk = dispatchedDoc.aspects != null && (dispatchedDoc.aspects.indexOf('gdib:borrador') < 0 && dispatchedDoc.aspects.indexOf('gdib:transformado') < 0);
	    		if(createDocReqOk){
	    			targetDocument = '{';
	    			targetDocument += '"name": "' + dispatchedDoc.name + '",';
	    			targetDocument += '"type": "' + dispatchedDoc.type + '",';
	    			//Metadatos
	    			targetDocument += '"metadataCollection": [';
	    			var arrayMetadataCollectionValue = '';
	    			for (i = 0; i < dispatchedDoc.metadataCollection.length; i++) {
	    				metadata = dispatchedDoc.metadataCollection[i];
	    				if(allowedMetadataCol.indexOf(metadata.qname) >= 0){
	    					//Nombre del metadato
	    					arrayMetadataCollectionValue += '{ "qname" : "' + metadata.qname + '",';
	    					arrayMetadataCollectionValue += '"value" : ';
	    					//Valor del metadato
	    					if(metadata.qname == 'eni:cod_clasificacion'){
	    						//Serie documental
	    						arrayMetadataCollectionValue += '"' + newDocSeriesParamString + '"';
	    					} else if(metadata.qname == 'eni:subtipo_doc' && (newDocSubSeriesParamString != null && newDocSubSeriesParamString.length > 0)){
	    						arrayMetadataCollectionValue += '"' + newDocSubSeriesParamString + '"';
	    					} else {
		    					if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
		    						arrayMetadataCollectionValue += '[';
		    						var arrayMetadataValue = '';
		    						for (j = 0; j < metadata.value.length; j++) {
		    							arrayMetadataValue += '"' + metadata.value[j] + '",';
		    						}
		    						arrayMetadataValue = arrayMetadataValue.substring(0,arrayMetadataValue.lastIndexOf(','));
		    						arrayMetadataCollectionValue += arrayMetadataValue;
		    						arrayMetadataCollectionValue += ']';
		    					} else {
		    						arrayMetadataCollectionValue += '"' + metadata.value + '"';
		    					}
	    					}
	    					arrayMetadataCollectionValue += '},'
	    				}
	    			}
	    			arrayMetadataCollectionValue = arrayMetadataCollectionValue.substring(0,arrayMetadataCollectionValue.lastIndexOf(','));
	    			targetDocument += arrayMetadataCollectionValue;
	    			targetDocument += '],';
	    			//Aspectos
	    			targetDocument += '"aspects": [';
	    			var arrayAspectsValue = '';
	        		for (i = 0; i < dispatchedDoc.aspects.length; i++) {
	        			arrayAspectsValue += '"' + dispatchedDoc.aspects[i] + '",';
	    		    }
	        		arrayAspectsValue = arrayAspectsValue.substring(0,arrayAspectsValue.lastIndexOf(','));
					targetDocument += arrayAspectsValue;
	        		targetDocument += '],';

	        		//Contenidos: contenido del documento y firma
	        		targetDocument += '"binaryContents": [';
	        		var arrayBinaryContentsValue = '';
	        		if(Object.prototype.toString.call(dispatchedDoc.binaryContents) == '[object Array]'){

		        		for (i = 0; i < dispatchedDoc.binaryContents.length; i++) {
		        			binaryContent = dispatchedDoc.binaryContents[i];
		        			arrayBinaryContentsValue += '{';
		        			if(binaryContent.binaryType != null){
		        				arrayBinaryContentsValue += '"binaryType": "' + binaryContent.binaryType + '",';
		        			}
		        			if(binaryContent.mimetype != null){
		        				arrayBinaryContentsValue += '"mimetype": "' + binaryContent.mimetype + '",';
		        			}
		        			arrayBinaryContentsValue += '"content": "' + binaryContent.content + '",';
		        			if(binaryContent.encoding != null){
		        				arrayBinaryContentsValue += '"encoding": "' + binaryContent.encoding + '",';
		        			}
		        			arrayBinaryContentsValue = arrayBinaryContentsValue.substring(0,arrayBinaryContentsValue.lastIndexOf(','));
		        			arrayBinaryContentsValue += '},';
		        		}
		        		arrayBinaryContentsValue = arrayBinaryContentsValue.substring(0,arrayBinaryContentsValue.lastIndexOf(','));

	        		} else {
	        			binaryContent = dispatchedDoc.binaryContents;
	        			arrayBinaryContentsValue += '{';
	        			if(binaryContent.binaryType != null){
	        				arrayBinaryContentsValue += '"binaryType": "' + binaryContent.binaryType + '",';
	        			}
	        			if(binaryContent.mimetype != null){
	        				arrayBinaryContentsValue += '"mimetype": "' + binaryContent.mimetype + '",';
	        			}
	        			arrayBinaryContentsValue += '"content": "' + binaryContent.content + '",';
	        			if(binaryContent.encoding != null){
	        				arrayBinaryContentsValue += '"encoding": "' + binaryContent.encoding + '",';
	        			}
	        			arrayBinaryContentsValue = arrayBinaryContentsValue.substring(0,arrayBinaryContentsValue.lastIndexOf(','));
	        			arrayBinaryContentsValue += '}';
	        		}
	        		targetDocument += arrayBinaryContentsValue;
	        		targetDocument += ']';
	        		targetDocument += '}';
	    		} else {
	    			errorMessage = 'No está permitido el traslado entre expedientes de documentos en estado borrador, o migrados y posteriormente transformados.';
	    		}
			} else {
				errorMessage = 'Documento ' + sourceNodeId + ' a trasladar debe ser de tipo eni:documento.';
			}
		}catch(err){
			errorMessage = 'Documento ' + sourceNodeId + ' a trasladar no encontrado. Excepcion: ' + err.message;
	    }
    }else {
    	errorMessage = 'Documento ' + sourceNodeId + ' a trasladar no encontrado.';
	}

    if(!createDocReqOk){
    	mc.setProperty('errorMessage', errorMessage);
    } else {
    	mc.setProperty('createTargetDocRequest', targetDocument);
    }

    mc.setProperty('createDocReqOk', createDocReqOk);

}

function buildSetDispatchedDocRequest(mc) {
	var dispatchedAspectMetadataCol = ["gdib:id_nodo_nueva_loc","gdib:fecha_traslado","gdib:autor_traslado","gdib:destino_traslado","gdib:tipo_destino"];
	var dispatchedAspectFound = false;
	var dispatchedDocString = mc.getProperty('dispatchedDoc');
	var sourceNodeId = mc.getProperty('sourceNodeIdParam');
	var newDocIdParam = mc.getProperty('newDocIdParam');
	var targetNodeId = mc.getProperty('targetNodeIdParam');
	var targetTypeParam = mc.getProperty('targetTypeParam');
	var appName = mc.getProperty('serviceHeaderAppName');
	var dispatchedDate = new Date();
	var sourceDocument = '';

	if(dispatchedDocString != null){
		try{
			dispatchedDoc = eval('('+dispatchedDocString+')');

			var sourceDocument = '{';
			sourceDocument += '"id": "' + sourceNodeId + '",';

			//Se verifica que existe el documento trasladado
    		for (i = 0; !dispatchedAspectFound && i < dispatchedDoc.aspects.length; i++) {
    			aspect = dispatchedDoc.aspects[i];
    			if(dispatchedDoc.aspects[i] == 'gdib:trasladado'){
    				dispatchedAspectFound = true;
    			}
		    }

    		//Se formatea la fecha a cadena. Formato ISO-8601: YYYY-MM-DDTHH:mm:ss.sssZ
    		if(dispatchedAspectFound){
    			sourceDocument += '"metadataCollection": [';
    			for (i = 0; i < dispatchedDoc.metadataCollection.length; i++) {
    				metadata = dispatchedDoc.metadataCollection[i];
    				var metadataValue = '';
    				if(dispatchedAspectMetadataCol.indexOf(metadata.qname) >= 0){
    					var arrayMetadataValue = '';
    					sourceDocument += '{ "qname" : "' + metadata.qname + '",';
    					sourceDocument += '{ "value" : ';
    					sourceDocument += '{ "value" : [';
    					if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
    						for (j = 0; j < metadata.value.length; j++) {
    							metadataValue += '"' + metadata.value[j] + '",';
    						}
	    				} else {
	    					metadataValue = '"' + metadata.value + '",';
	    				}
    					//Solo es necesario tratar fecha de traslado, por ser una fecha
    					var currentValue = '';
    					if(metadata.qname == 'gdib:id_nodo_nueva_loc'){
    						currentValue = '"' + newDocIdParam + '"';
    					} else if (metadata.qname == 'gdib:fecha_traslado'){
    						currentValue = '"' + dateToISOString(dispatchedDate) + '"';
    					} else if(metadata.qname == 'gdib:autor_traslado'){
    						currentValue = '"' + appName + '"';
    					} else if(metadata.qname == 'gdib:destino_traslado'){
    						currentValue = '"' + targetNodeId + '"';
    					} else if(metadata.qname == 'gdib:tipo_destino'){
    						currentValue = '"' + targetTypeParam + '"';
    					}

    					metadataValue += currentValue;
    					sourceDocument += metadataValue + ']},'
    				}
    			}
    			sourceDocument = sourceDocument.substring(0,sourceDocument.lastIndexOf(','));
    			sourceDocument += ']';
    		} else {
    			sourceDocument += '"metadataCollection": [';
    			sourceDocument += '{ "qname" : "gdib:id_nodo_nueva_loc",';
    			sourceDocument += '"value" : ["' + newDocIdParam + '"]},';
    			sourceDocument += '{ "qname" : "gdib:fecha_traslado",';
    			sourceDocument += '"value" : ["' + dateToISOString(dispatchedDate) + '"]},';
    			sourceDocument += '{ "qname" : "gdib:autor_traslado",';
    			sourceDocument += '"value" : ["' + appName + '"]},';
    			sourceDocument += '{ "qname" : "gdib:destino_traslado",';
    			sourceDocument += '"value" : ["' + targetNodeId + '"]},';
    			sourceDocument += '{ "qname" : "gdib:tipo_destino",';
    			sourceDocument += '"value" : ["' + targetTypeParam + '"]}';
    			sourceDocument += '],';
    			sourceDocument += '"aspects": ["gdib:trasladado"]';
    		}
    		sourceDocument += '}';
		}catch(err){
			errorMessage = 'Documento ' + sourceNodeId + ' trasladado no encontrado, no puede ser modificado. Excepcion: ' + err.message;
	    }
    }else {
    	errorMessage = 'Documento ' + sourceNodeId + ' trasladado no encontrado, no puede ser modificado.';
	}

	mc.setProperty('sourceDocumentToSet', sourceDocument);
}

function buildSetFileRequest(mc) {
	payload = mc.getPayloadJSON();
	setFileReq = payload.setFileRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString = '<ws:node xmlns:ws="http://www.caib.es/gdib/repository/ws">';

    if(setFileReq != null && setFileReq.param != null && setFileReq.param.file != null){

		var reqParam = setFileReq.param;
		var fileReq = reqParam.file;

    	requestOk = fileReq.id != null;

    	if(requestOk){
    		requestString = requestString + '<ws:id>' + fileReq.id + '</ws:id>';

    		if(fileReq.name != null && fileReq.name.length > 0){
    			requestString = requestString + '<ws:name>' + fileReq.name + '</ws:name>';
    		}

    		if(fileReq.aspects != null && fileReq.aspects.length >0){

        		for (i = 0; i < fileReq.aspects.length; i++) {
        			aspect = fileReq.aspects[i];

        			requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
    		    }
    		}

    		if(fileReq.metadataCollection != null && fileReq.metadataCollection.length > 0){
    			requestString += generatePropertiesReqElement(fileReq.metadataCollection);
    		}

    		requestString = requestString + '</ws:node>';
    	} else {
    		errorMessage = 'Petición mal formada. El parámetro file.id del expediente/subexpediente debe ser informado.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildSetFolderRequest(mc) {
	payload = mc.getPayloadJSON();
	setFolderReq = payload.setFolderRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString = '<ws:node xmlns:ws="http://www.caib.es/gdib/repository/ws">';

    if(setFolderReq != null && setFolderReq.param != null && setFolderReq.param.folder != null){

		var reqParam = setFolderReq.param;
		var folderReq = reqParam.folder;

    	requestOk = folderReq.id != null;

    	if(requestOk){
    		requestString = requestString + '<ws:id>' + folderReq.id + '</ws:id>';

    		if(folderReq.name != null && folderReq.name.length > 0){
    			requestString = requestString + '<ws:name>' + folderReq.name + '</ws:name>';
    		}

    		requestString = requestString + '</ws:node>';
    	} else {
    		errorMessage = 'Petición mal formada. El parámetro folder.id de la agrupación documental debe ser informado.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

function buildSetFinalDocRequest(mc) {
	payload = mc.getPayloadJSON();
	setFinalDocReq = payload.setFinalDocumentRequest;
	var errorMessage = '';
    var requestOk = false;
    var requestString = '<ws:node xmlns:ws="http://www.caib.es/gdib/repository/ws">';

    if(setFinalDocReq != null && setFinalDocReq.param != null && setFinalDocReq.param.document != null){

		var reqParam = setFinalDocReq.param;
		var docReq = reqParam.document;
		var draftAspectFound = false;

    	requestOk = docReq.id != null;

    	if(requestOk){
    		requestString = requestString + '<ws:id>' + docReq.id + '</ws:id>';

    		if(docReq.name != null && docReq.name.length > 0){
    			requestString = requestString + '<ws:name>' + docReq.name + '</ws:name>';
    		}

    		//Aspectos
    		requestString = requestString + '<ws:aspects>-gdib:borrador</ws:aspects>';
    		if(docReq.aspects != null && docReq.aspects.length >0){

        		for (i = 0; !draftAspectFound && i < docReq.aspects.length; i++) {
        			aspect = docReq.aspects[i];
        			if(aspect.indexOf('-gdib:borrador') < 0 && aspect.indexOf('gdib:borrador') < 0){
        				requestString = requestString + '<ws:aspects>' + aspect + '</ws:aspects>';
        			} else {
        				draftAspectFound = true;
        			}
    		    }
    		}

    		if(draftAspectFound){
    			requestOk = false;
    			errorMessage = 'Petición mal formada. No está permitido el aspecto gdib:borrador.';
    		} else {
    			//Metadatos
    			if(docReq.metadataCollection != null && docReq.metadataCollection.length > 0){
        			requestString += generatePropertiesReqElement(docReq.metadataCollection);
        		}

	    		//Contenido y firma
    			requestString += generateBinaryContentsReqElement(docReq,false);
    		}
    		requestString = requestString + '</ws:node>';
    	} else {
    		errorMessage = 'Petición mal formada. El parámetro document.id del documento debe ser informado.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición o parámetro de entrada no encontrados.';
    }

    if(requestOk){
    	mc.setProperty('reqService', requestString);
    } else {
    	mc.setProperty('reqServiceErrorMessage', errorMessage);
    }

    mc.setProperty('reqServiceOk', requestOk);
}

//Utilidades de la libreria

function dateToISOString(date){
	var res = '';
	var month = date.getUTCMonth() + 1;
	var monthStr, dateStr, hourStr, minStr,secStr,msecStr;

	//Formato ISO-8601: YYYY-MM-DDTHH:mm:ss.sssZ

	monthStr = (month<10?'0'+month:month);
	dateStr = (date.getUTCDate()<10?'0'+date.getUTCDate():date.getUTCDate());
	hourStr = (date.getUTCHours()<10?'0'+date.getUTCHours():date.getUTCHours());
	minStr = (date.getUTCMinutes()<10?'0'+date.getUTCMinutes():date.getUTCMinutes());
	secStr = (date.getUTCSeconds()<10?'0'+date.getUTCSeconds():date.getUTCSeconds());

	msecStr = date.getUTCMilliseconds();
	if(date.getUTCMilliseconds() < 10){
		msecStr = '00' +  msecStr;
	} else if(date.getUTCMilliseconds() > 10 && date.getUTCMilliseconds() < 100){
		msecStr = '0' +  msecStr;
	}

	res = date.getUTCFullYear() + '-' + monthStr + '-' + dateStr + 'T' + hourStr + ':' + minStr + ':' + secStr + '.' + msecStr + 'Z';

	return res;
}

function generatePropertiesReqElement(metadataCollection){
	var res = '';

	for (i = 0; i < metadataCollection.length; i++) {
		metadata = metadataCollection[i];
		if(metadata.qname != null && metadata.qname.length > 0){
			res = res + '<ws:properties>';
			res = res + '<ws:qname>' + metadata.qname + '</ws:qname>';
			if(Object.prototype.toString.call(metadata.value) == '[object Array]'){
				//Los metadatos que pueden poseer multiples valores son informados como una cadena separada por el caracter ';'
				var arrayMetadataValue = '';
				for (j = 0; j < metadata.value.length; j++) {
					arrayMetadataValue = arrayMetadataValue + metadata.value[j] + ';';
				}

				arrayMetadataValue = arrayMetadataValue.substring(0,arrayMetadataValue.lastIndexOf(';'));
				res = res + '<ws:value>' + arrayMetadataValue + '</ws:value>';
			} else {
				res = res + '<ws:value>' + (metadata.value==null?'':metadata.value) + '</ws:value>';
			}
			res = res + '</ws:properties>';
		}
    }

	return res;
}

function getSignatureProperties(metadataCollection){
	var res = [];

	if(metadataCollection != null){
		for (i = 0; i < metadataCollection.length; i++) {
			metadata = metadataCollection[i];
			if(metadata.qname != null && metadata.qname == 'eni:tipoFirma'){
				res[0] = metadata.value;
			} else if(metadata.qname != null && metadata.qname == 'eni:perfil_firma'){
				res[1] = metadata.value;
			}
	    }
	}

	return res;
}

function generateBinaryContentsReqElement(docReq,signatureRequired){
	var res = '';
	var docContent = '';
	var contentFound = false;
	var signatureFound = false;

	if(docReq.binaryContents != null && docReq.binaryContents.length > 0) {
		//Contenido y firma
		for (i = 0; !(contentFound && signatureFound) && i < docReq.binaryContents.length; i++) {
			binaryContent = docReq.binaryContents[i];
			if(binaryContent.binaryType != null && binaryContent.binaryType.equals('CONTENT')){

				res = res + '<ws:content>';
				if(binaryContent.mimetype != null){
					res = res + '<ws:mimetype>' + binaryContent.mimetype + '</ws:mimetype>';
				}
				if(binaryContent.content != null){
					docContent = binaryContent.content;
					res = res + '<ws:data>' + binaryContent.content + '</ws:data>';
				}
				if(binaryContent.encoding != null){
					res = res + '<ws:encoding>' + binaryContent.encoding + '</ws:encoding>';
				}
				res = res + '</ws:content>';
				contentFound = true;
			} else if(binaryContent.binaryType != null && binaryContent.binaryType.equals('SIGNATURE')){
				if(binaryContent.content != null){
					res = res + '<ws:sign>' + binaryContent.content + '</ws:sign>';
				}
				signatureFound = true;
			}
		}

		/*
		 * 14/11/2016
		 * La firma electrónica solo es requerida de informar si está es explícita
		 * if(!signatureFound && !signatureRequired && docContent != null){
			res = res + '<ws:sign>' + docContent + '</ws:sign>';
		}*/
	}

	return res;
}
