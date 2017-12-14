function buildTransformMigratedDocRequest(mc) {
	payload = mc.getPayloadJSON();
	transformMigratedDocReq = payload.transformMigratedDocRequest;
    var requestOk = false;
    var requestString = '<wsm:transformNode xmlns:wsm="http://www.caib.es/gdib/migration/ws">';
    
    if(transformMigratedDocReq != null && transformMigratedDocReq.param != null){
    	
    	var paramReq = transformMigratedDocReq.param;
    	var tInfoDocReq = paramReq.transformInfoDoc;
    	
    	if(tInfoDocReq != null){
	    	var fileIdOk = paramReq.fileId != null && paramReq.fileId.length > 0;    	
	    	var migratedDocIdOk = tInfoDocReq.migratedDocId != null && (tInfoDocReq.migratedDocId.applicationId != null && tInfoDocReq.migratedDocId.applicationId.length > 0) && (tInfoDocReq.migratedDocId.externalId != null && tInfoDocReq.migratedDocId.externalId.length > 0);
	    	var metadataCollectionOk = tInfoDocReq.metadataCollection != null && tInfoDocReq.metadataCollection.length > 0;
	    	requestOk =  fileIdOk && migratedDocIdOk && metadataCollectionOk;
	    	
	    	if(requestOk){
	    		requestString = requestString + '<wsm:datanodetransform>';
	    		requestString = requestString + '<wsm:migrationId>';
	    		requestString = requestString + '<wsm:appId>' + tInfoDocReq.migratedDocId.applicationId + '</wsm:appId>';
	    		requestString = requestString + '<wsm:externalId>' + tInfoDocReq.migratedDocId.externalId + '</wsm:externalId>';
	    		requestString = requestString + '</wsm:migrationId>';

	    		for (i = 0; i < tInfoDocReq.metadataCollection.length; i++) {
	    			metadata = tInfoDocReq.metadataCollection[i];
	    			requestString = requestString + '<wsm:metadata>';
	    			requestString = requestString + '<wsm:qname>' + metadata.qname + '</wsm:qname>';
	    			requestString = requestString + '<wsm:value>' + metadata.value + '</wsm:value>';
	    			requestString = requestString + '</wsm:metadata>';
			    }
	    		requestString = requestString + '</wsm:datanodetransform>';
	    		
	    		requestString = requestString + '<wsm:fileNumber>' + paramReq.fileId + '</wsm:fileNumber>';
	    		
	    		requestString = requestString + '</wsm:transformNode>';
	    	} else {
	    		errorMessage = 'Petición mal formada. Los parámetros fileId, transformInfoDoc.migratedDocId y transformInfoDoc.metadataCollection deben ser informados.';
	    	}
    	} else {
    		errorMessage = 'Petición mal formada. El parámetro transformInfoDoc debe ser informado.';
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