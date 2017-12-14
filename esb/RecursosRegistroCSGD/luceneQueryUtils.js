function checkDocLuceneQuery(mc) {
	var luceneQueryOk = false;
    var luceneQueryString = mc.getProperty('luceneQuery');
    if(luceneQueryString != null){
    	luceneQueryOk = (luceneQueryString.indexOf('TYPE:\"eni:documento\"') >= 0 || luceneQueryString.indexOf('TYPE:\"gdib:documentoMigrado\"') >= 0) && luceneQueryString.indexOf('TYPE:\"eni:expediente\"') < 0;
	    mc.setProperty('luceneQueryOk', luceneQueryOk);
	}
}

function checkFileLuceneQuery(mc) {
	var luceneQueryOk = false;
    var luceneQueryString = mc.getProperty('luceneQuery');
    if(luceneQueryString != null){
    	luceneQueryOk = (luceneQueryString.indexOf('TYPE:\"eni:expediente\"') >= 0 || luceneQueryString.indexOf('TYPE:\"rma:recordFolder\"') >= 0) && (luceneQueryString.indexOf('TYPE:\"eni:documento\"') < 0 && luceneQueryString.indexOf('TYPE:\"gdib:documentoMigrado\"') < 0);
	    mc.setProperty('luceneQueryOk', luceneQueryOk);
	}
}

function buildDocByCSVLuceneQuery(mc){
	var luceneQueryString = '+(TYPE:\"gdib:documentoMigrado\" OR TYPE:\"eni:documento\") +ASPECT:\"eni:firmadoBase\"' + ' +@eni\\:csv:\"'+ mc.getProperty('nodeId') + '\"';
    mc.setProperty("luceneQuery", luceneQueryString);
}

function buildFileLuceneQuery(mc) {
	payload = mc.getPayloadJSON();
	searchFilesRequest = payload.searchFilesRequest;
	var datePeriodString;
	var errorMessage = '';
    var initialized = false;
    var luceneQueryString = '+(TYPE:\"eni:expediente\" OR TYPE:\"rma:recordFolder\")';
    
    if(searchFilesRequest != null && searchFilesRequest.param != null){
    	
    	paramReq = searchFilesRequest.param;
    	if(paramReq.requiredFilters != null || paramReq.optionalFilters != null){
	    	if(paramReq.requiredFilters != null){
	    		requiredFilters = paramReq.requiredFilters;
	    		//Nombre documento, tipo contiene
	    		if(requiredFilters.name != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@cm\\:name:\"*'+ requiredFilters.name + '*\"';
	    		}
	    		//Rango fechas para fecha apertura expediente
	    		if(requiredFilters.custDate != null){
	    			datePeriod = requiredFilters.custDate;
	    			datePeriodString = dateRange('@eni\\:fecha_inicio', datePeriod.initialDate, datePeriod.finalDate);
	    			luceneQueryString = luceneQueryString + ' +' + datePeriodString;
	    			initialized = true;
	    		}
	    		//Rango fechas para fecha cierre expediente	    		
	    		if(requiredFilters.closingDate != null){
	    			datePeriod = requiredFilters.closingDate;
	    			datePeriodString = dateRange('@eni\\:fecha_fin_exp', datePeriod.initialDate, datePeriod.finalDate);
	    			luceneQueryString = luceneQueryString + ' +' + datePeriodString;
	    			initialized = true;
	    		}
	    		//Autor
	    		if(requiredFilters.author != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@cm\\:creator:\"'+ requiredFilters.appName + '\"';
	    		}
	    		//Nombre aplicación
	    		if(requiredFilters.appName != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:app_tramite_exp:\"'+ requiredFilters.appName + '\"';
	    		}
	    		//Identificador ENI
	    		if(requiredFilters.eniId != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:id:\"'+ requiredFilters.eniId + '\"';
	    		}
	    		//Interesados
	    		if(requiredFilters.applicants != null && requiredFilters.applicants.length > 0){
	    			//Array de string
	    			initialized = true;    			
	    			for (i = 0; i < requiredFilters.applicants.length; ++i) {
	    				applicant = requiredFilters.applicants[i];
	    				luceneQueryString = luceneQueryString + ' +@eni\\:interesados_exp:\"'+ applicant + '\"';
	    		    }
	    		}
	    		//Clasificación documental
	    		if(requiredFilters.docSeries != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:cod_clasificacion:\"'+ requiredFilters.docSeries + '\"';
	    		}
	    	}
	    	if(paramReq.optionalFilters != null){
	    		optionalFilters = paramReq.optionalFilters;
	
	    		//Nombre expediente, tipo contiene
	    		if(optionalFilters.name != null && optionalFilters.name.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.name;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @cm\\:name:\"*'+ filterValue + '*\"';
	    			}
	    		}
	    		
	    		//Rango fechas para fecha apertura expediente
	    		if(optionalFilters.custDate != null && optionalFilters.custDate.length > 0){
	    			//Array de periodos de tiempo
	    			initialized = true;
	    			paramValue = optionalFilters.custDate;
	    			for (i = 0; i < paramValue.length; i++) {
	    				datePeriod = paramValue[i];
	    				
	    				datePeriodString = dateRange('@eni\\:fecha_inicio', datePeriod.initialDate, datePeriod.finalDate);
		    			luceneQueryString = luceneQueryString + ' OR ' + datePeriodString;
		    			initialized = true;
	    			}
	    		}
	    		//Rango fechas para fecha de cierre de expediente
	    		if(optionalFilters.closingDate != null && optionalFilters.closingDate.length > 0){
	    			//Array de periodos de tiempo
	    			initialized = true;
	    			paramValue = optionalFilters.closingDate;
	    			for (i = 0; i < paramValue.length; i++) {
	    				datePeriod = paramValue[i];
	    				
	    				datePeriodString = dateRange('@eni\\:fecha_fin_exp', datePeriod.initialDate, datePeriod.finalDate);
		    			luceneQueryString = luceneQueryString + ' OR ' + datePeriodString;
		    			initialized = true;
	    			}
	    		}
	    		//Autor
	    		if(optionalFilters.author != null && optionalFilters.author.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.author;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @cm\\:creator:\"'+ filterValue + '*\"';
	    			}
	    		}	    		
	    		//Nombre aplicación
	    		if(optionalFilters.appName != null && optionalFilters.appName.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.appName;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:app_tramite_doc:\"'+ filterValue + '*\"';
	    			}
	    		}	    		
	    		//Identificador ENI
	    		if(optionalFilters.eniId != null && optionalFilters.eniId.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.eniId;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:id:\"'+ filterValue + '*\"';
	    			}
	    		}
	    		//Interesados
	    		if(optionalFilters.applicants != null && optionalFilters.applicants.length > 0){
	    			initialized = true;
	    			for (i = 0; i < optionalFilters.applicants.length; ++i) {
	    				applicant = optionalFilters.applicants[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:interesados_exp:\"'+ applicant + '\"'; 
	    		    }
	    		}
	    		//Clasificación documental
	    		if(optionalFilters.docSeries != null && optionalFilters.docSeries.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.docSeries;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:cod_clasificacion:\"'+ filterValue + '\"';
	    			}
	    		}
	    	}
    	} else {
    		errorMessage = 'Petición mal formada. No han sido informados filtros de búsqueda.';
    	}
    } else {
    	errorMessage = 'Petición mal formada. Petición no encontrada.';
    }
    
    if(initialized){
    	mc.setProperty('luceneQuery', luceneQueryString);    	
    }
    mc.setProperty('reqServiceErrorMessage', errorMessage);
    mc.setProperty('reqServiceOk', initialized);
}

function buildDocLuceneQuery(mc) {
	payload = mc.getPayloadJSON();
    searchDocsRequest = payload.searchDocsRequest;
    var datePeriodString;
    var errorMessage = '';
    var datePeriod;
    var initialized = false;
    var luceneQueryString = '+(TYPE:\"gdib:documentoMigrado\" OR TYPE:\"eni:documento\")';
    
    if(searchDocsRequest != null && searchDocsRequest.param != null){
    	paramReq = searchDocsRequest.param;
    	
    	if(paramReq.requiredFilters != null || paramReq.optionalFilters != null){
	    	if(paramReq.requiredFilters != null){
	    		requiredFilters = paramReq.requiredFilters;
	    		//Nombre documento, tipo contiene
	    		if(requiredFilters.name != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@cm\\:name:\"*'+ requiredFilters.name + '*\"';
	    		}

	    		//Rango fechas para fecha custodia
	    		if(requiredFilters.custDate != null){
	    			datePeriod = requiredFilters.custDate;
	    			datePeriodString = dateRange('@eni\\:fecha_inicio', datePeriod.initialDate, datePeriod.finalDate);
	    			luceneQueryString = luceneQueryString + ' +' + datePeriodString;
	    			initialized = true;
	    		}
	    		
	    		//Rango fechas para fecha ultima modificación
	    		if(requiredFilters.lastModDate != null){
	    			datePeriod = requiredFilters.lastModDate;
	    			
	    			datePeriodString = dateRange('@cm\\:modified', datePeriod.initialDate, datePeriod.finalDate);
	    			luceneQueryString = luceneQueryString + ' +' + datePeriodString;
	    			initialized = true;
	    		}
	    		
	    		//Autor
	    		if(requiredFilters.author != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@cm\\:creator:\"'+ requiredFilters.appName + '\"';
	    		}
	    		//Nombre aplicación
	    		if(requiredFilters.appName != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:app_tramite_doc:\"'+ requiredFilters.appName + '\"';
	    		}
	    		//Identificador ENI
	    		if(requiredFilters.eniId != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:id:\"'+ requiredFilters.eniId + '\"';
	    		}
	    		//Contenido (tipo coentiene)
	    		if(requiredFilters.content != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +TEXT:\"*'+ requiredFilters.content + '*\"';
	    		}
	    		//Mimetype
	    		if(requiredFilters.mimetype != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@cm\\:content.mimetype:\"'+ requiredFilters.mimetype + '\"';
	    		}
	    		//Clasificación documental
	    		if(requiredFilters.docSeries != null){
	    			initialized = true;
	    			luceneQueryString = luceneQueryString + ' +@eni\\:cod_clasificacion:\"'+ requiredFilters.docSeries + '\"';
	    		}
	    	}
	    	if(paramReq.optionalFilters != null){
	    		optionalFilters = paramReq.optionalFilters;
	
	    		//Nombre documento, tipo contiene
	    		if(optionalFilters.name != null && optionalFilters.name.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.name;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @cm\\:name:\"*'+ filterValue + '*\"';
	    			}
	    		}
	    		//Rango fechas para fecha custodia
	    		if(optionalFilters.custDate != null && optionalFilters.custDate.length > 0){
	    			//Array de periodos de tiempo
	    			initialized = true;
	    			paramValue = optionalFilters.custDate;
	    			for (i = 0; i < paramValue.length; i++) {
	    				datePeriod = paramValue[i];
	    				
	    				datePeriodString = dateRange('@eni\\:fecha_inicio', datePeriod.initialDate, datePeriod.finalDate);
		    			luceneQueryString = luceneQueryString + ' OR ' + datePeriodString;
		    			initialized = true;
	    			}
	    		}
	    		//Rango fechas para fecha ultima modificación
	    		if(optionalFilters.lastModDate != null && optionalFilters.lastModDate.length > 0){
	    			//Array de periodos de tiempo
	    			initialized = true;
	    			paramValue = optionalFilters.lastModDate;
	    			for (i = 0; i < paramValue.length; i++) {
	    				datePeriod = paramValue[i];
	    				
	    				datePeriodString = dateRange('@cm\\:modified', datePeriod.initialDate, datePeriod.finalDate);
		    			luceneQueryString = luceneQueryString + ' OR ' + datePeriodString;
		    			initialized = true;
	    			}
	    		}

	    		//Autor
	    		if(optionalFilters.author != null && optionalFilters.author.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.author;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @cm\\:creator:\"'+ filterValue + '\"';
	    			}
	    		}
	    		
	    		//Nombre aplicación
	    		if(optionalFilters.appName != null && optionalFilters.appName.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.appName;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:app_tramite_doc:\"'+ filterValue + '\"';
	    			}
	    		}
	    		
	    		//Identificador ENI
	    		if(optionalFilters.eniId != null && optionalFilters.eniId.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.eniId;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:id:\"'+ filterValue + '\"';
	    			}
	    		}
	    		
	    		//Contenido (tipo contiene)
	    		if(optionalFilters.content != null && optionalFilters.content.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.content;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR TEXT:\"*'+ filterValue + '*\"';
	    			}
	    		}
	    		
	    		//Mimetype
	    		if(optionalFilters.mimetype != null && optionalFilters.mimetype.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.mimetype;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @cm\\:content.mimetype:\"'+ filterValue + '\"';
	    			}
	    		}
	    		
	    		//Clasificación documental
	    		if(optionalFilters.docSeries != null && optionalFilters.docSeries.length > 0){
	    			//Array de String
	    			initialized = true;
	    			paramValue = optionalFilters.docSeries;
	    			for (i = 0; i < paramValue.length; i++) {
	    				filterValue = paramValue[i];
	    				luceneQueryString = luceneQueryString + ' OR @eni\\:cod_clasificacion:\"'+ filterValue + '\"';
	    			}
	    		}
	    	}
	    } else {
			errorMessage = 'Petición mal formada. No han sido informados filtros de búsqueda.';
		}
	} else {
		errorMessage = 'Petición mal formada. Petición no encontrada.';
	}
    
    if(initialized){
    	mc.setProperty('luceneQuery', luceneQueryString);    	
    }
    mc.setProperty('reqServiceErrorMessage', errorMessage);
    mc.setProperty('reqServiceOk', initialized);
}

function dateRange(prop, date1, date2){
	if (date1 == null || (date1 != null && date1 == '')){
		date1 = '\"1000\\-01\\-01\"';
	}
	
	if (date2 == null || (date2 != null && date2 == '')){
		date2 = 'NOW';
	}

	return prop+':[' + date1 + ' TO ' + date2 + ']';
}

