function buildAuthorizeDocsRequest(mc) {
	payload = mc.getPayloadJSON();
	grantPermDocsReq = payload.grantPermissionsOnDocsRequest;
    var requestOk = false;
    var requestString = '';
    
    if(grantPermDocsReq != null && grantPermDocsReq.param != null){
    	
    	var paramReq = grantPermDocsReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0) && paramReq.permission != null;
    	
    	if(requestOk){
    		if(paramReq.permission == 'read' || paramReq.permission == 'write'){
    			requestString = buildAuthorizeNodeRequest(paramReq);
    		} else {
    			requestOk = false;
    			errorMessage = 'Petición mal formada. Los valores posibles para el parámetro permission son read o write.';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds, authorities y permission de la petición deben ser informados.';
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

function buildCancelPermissionsOnDocsRequest(mc) {
	payload = mc.getPayloadJSON();
	cancelPermDocsReq = payload.cancelPermissionsOnDocsRequest;
    var requestOk = false;
    var requestString = '';
    
    if(cancelPermDocsReq != null && cancelPermDocsReq.param != null){
    	
    	var paramReq = cancelPermDocsReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0);
    	
    	if(requestOk){
    		requestString = buildRemoveAuthorityRequest(paramReq);
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds y authorities de la petición deben ser informados.';
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

function buildAuthorizeFilesRequest(mc) {
	payload = mc.getPayloadJSON();
	grantPermFilesReq = payload.grantPermissionsOnFilesRequest;
    var requestOk = false;
    var requestString = '';
    
    if(grantPermFilesReq != null && grantPermFilesReq.param != null){
    	
    	var paramReq = grantPermFilesReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0) && paramReq.permission != null;
    	
    	if(requestOk){
    		if(paramReq.permission == 'read' || paramReq.permission == 'write'){
    			requestString = buildAuthorizeNodeRequest(paramReq);
    		} else {
    			requestOk = false;
    			errorMessage = 'Petición mal formada. Los valores posibles para el parámetro permission son read o write.';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds, authorities y permission de la petición deben ser informados.';
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

function buildCancelPermissionsOnFilesRequest(mc) {
	payload = mc.getPayloadJSON();
	cancelPermFilesReq = payload.cancelPermissionsOnFilesRequest;
    var requestOk = false;
    var requestString = '';
    
    if(cancelPermFilesReq != null && cancelPermFilesReq.param != null){
    	
    	var paramReq = cancelPermFilesReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0);
    	
    	if(requestOk){
    		requestString = buildRemoveAuthorityRequest(paramReq);
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds y authorities de la petición deben ser informados.';
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

function buildAuthorizeFoldersRequest(mc) {
	payload = mc.getPayloadJSON();
	grantPermFoldersReq = payload.grantPermissionsOnFoldersRequest;
    var requestOk = false;
    var requestString = '';
    
    if(grantPermFoldersReq != null && grantPermFoldersReq.param != null){
    	
    	var paramReq = grantPermFoldersReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0) && paramReq.permission != null;
    	
    	if(requestOk){
    		if(paramReq.permission == 'read' || paramReq.permission == 'write'){
    			requestString = buildAuthorizeNodeRequest(paramReq);
    		} else {
    			requestOk = false;
    			errorMessage = 'Petición mal formada. Los valores posibles para el parámetro permission son read o write.';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds, authorities y permission de la petición deben ser informados.';
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

function buildAuthorizeSeriesRequest(mc) {
	payload = mc.getPayloadJSON();
	grantPermSeriesReq = payload.grantPermissionsOnSeriesRequest;
    var requestOk = false;
    var requestString = '';
    
    if(grantPermSeriesReq != null && grantPermSeriesReq.param != null){
    	
    	var paramReq = grantPermSeriesReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0) && paramReq.permission != null;
    	
    	if(requestOk){
    		if(paramReq.permission == 'read' || paramReq.permission == 'write' || paramReq.permission == 'contributor'){
    			requestString = buildAuthorizeNodeRequest(paramReq);
    		} else {
    			requestOk = false;
    			errorMessage = 'Petición mal formada. Los valores posibles para el parámetro permission son read, write o contributor.';
    		}
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds, authorities y permission de la petición deben ser informados.';
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

function buildCancelPermissionsOnFoldersRequest(mc) {
	payload = mc.getPayloadJSON();
	cancelPermFoldersReq = payload.cancelPermissionsOnFoldersRequest;
    var requestOk = false;
    var requestString = '';
    
    if(cancelPermFoldersReq != null && cancelPermFoldersReq.param != null){
    	
    	var paramReq = cancelPermFoldersReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0);
    	
    	if(requestOk){
    		requestString = buildRemoveAuthorityRequest(paramReq);
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds y authorities de la petición deben ser informados.';
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

function buildCancelPermissionsOnSeriesRequest(mc) {
	payload = mc.getPayloadJSON();
	cancelPermSeriesReq = payload.cancelPermissionsOnSeriesRequest;
    var requestOk = false;
    var requestString = '';
    
    if(cancelPermSeriesReq != null && cancelPermSeriesReq.param != null){
    	
    	var paramReq = cancelPermSeriesReq.param;
    	requestOk = (paramReq.nodeIds != null && paramReq.nodeIds.length > 0) && (paramReq.authorities != null && paramReq.authorities.length > 0);
    	
    	if(requestOk){
    		requestString = buildRemoveAuthorityRequest(paramReq);
    	} else {
    		errorMessage = 'Petición mal formada. Los parámetros nodeIds y authorities de la petición deben ser informados.';
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


function buildAuthorizeNodeRequest(paramReq){
	var res = '<ws:authorizeNode xmlns:ws="http://www.caib.es/gdib/repository/ws">';
	
    for (i = 0; i < paramReq.nodeIds.length; i++) {
		nodeId = paramReq.nodeIds[i];
		res = res + '<ws:nodeIds>' + nodeId + '</ws:nodeIds>';
	}

	for (i = 0; i < paramReq.authorities.length; i++) {
		authority = paramReq.authorities[i];
		res = res + '<ws:authorities>' + authority + '</ws:authorities>';
	}
	
	res = res + '<ws:permission>' + paramReq.permission + '</ws:permission>';
	
	res = res + '</ws:authorizeNode>';
	
	return res;
}

function buildRemoveAuthorityRequest(paramReq){
	var res = '<ws:removeAuthority xmlns:ws="http://www.caib.es/gdib/repository/ws">';
	
	for (i = 0; i < paramReq.nodeIds.length; i++) {
		nodeId = paramReq.nodeIds[i];
		res = res + '<ws:nodeIds>' + nodeId + '</ws:nodeIds>';
	}

	for (i = 0; i < paramReq.authorities.length; i++) {
		authority = paramReq.authorities[i];
		res = res + '<ws:authorities>' + authority + '</ws:authorities>';
	}
	
	res = res + '</ws:removeAuthority>';
	
	return res;
}