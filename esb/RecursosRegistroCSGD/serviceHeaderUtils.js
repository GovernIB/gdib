
function parseServiceHeader(mc) {
	var serviceHeader;
	var serviceHeaderString = mc.getProperty('serviceHeader');
	var errorMessage = '';
    var serviceHeaderOk = false;
    var gdibHeaderString = '';
    var ticketInternalUser = '';
    
	if(serviceHeaderString != null){
		try{
			serviceHeader = eval('('+serviceHeaderString+')');
		
			if(serviceHeader != null && serviceHeader.securityInfo != null){
				secInfoReq = serviceHeader.securityInfo;
				
				serviceHeaderOk = secInfoReq.user != null && ((secInfoReq.password != null && secInfoReq.password.length > 0) || (secInfoReq.ticket != null && secInfoReq.ticket.length > 0)) ;
				
				if(serviceHeaderOk){
					gdibHeaderString = '<ws:gdibHeader xmlns:ws="http://www.caib.es/gdib/repository/ws">';
					var userName = '';
					var password = '';
					var secToken = '';

					if(secInfoReq.password != null && secInfoReq.password.length > 0){
						userName = secInfoReq.user;
						password = secInfoReq.password;
						secToken = 'password';
					} else {
						userName = ticketInternalUser;
						password = secInfoReq.ticket;
						secToken = 'ticket';
					}

					mc.setProperty('username', userName);
					mc.setProperty('password', password);
					mc.setProperty('secToken', secToken);
					
					auditInfoReq = serviceHeader.auditInfo;
					if(auditInfoReq != null){
						gdibHeaderString += '<ws:gdibAudit>';
						//Solicitante cuidadano
						if(auditInfoReq.applicant != null){
							gdibHeaderString += '<ws:applicant>';
							if(auditInfoReq.applicant.document != null){    					
								mc.setProperty('serviceHeaderApplicantDoc', auditInfoReq.applicant.document);
								gdibHeaderString += '<ws:document>'+auditInfoReq.applicant.document+'</ws:document>';
							} 
							if(auditInfoReq.applicant.name != null){    					
								mc.setProperty('serviceHeaderApplicantName', auditInfoReq.applicant.name);
								gdibHeaderString += '<ws:name>'+auditInfoReq.applicant.name+'</ws:name>';
							}
							gdibHeaderString += '</ws:applicant>';
						}
						//Nombre aplicación
						var appName = '';
						if(auditInfoReq.application != null && auditInfoReq.application.length > 0){
							appName = auditInfoReq.application;
						} else {
							appName = secInfoReq.user;
						}
						gdibHeaderString += '<ws:application>'+appName+'</ws:application>';
						mc.setProperty('serviceHeaderAppName', appName);
						
						//Solicitante funcionario
						if(auditInfoReq.publicServant != null){
							gdibHeaderString += '<ws:publicServant>';
							if(auditInfoReq.publicServant.identificationData != null){
								if(auditInfoReq.publicServant.identificationData.document != null){    					
									mc.setProperty('serviceHeaderPublicServantDoc', auditInfoReq.publicServant.identificationData.document);
									gdibHeaderString += '<ws:document>'+auditInfoReq.publicServant.identificationData.document+'</ws:document>';
								} 
								if(auditInfoReq.publicServant.identificationData.name != null){    					
									mc.setProperty('serviceHeaderPublicServantName', auditInfoReq.publicServant.identificationData.name);
									gdibHeaderString += '<ws:name>'+auditInfoReq.publicServant.identificationData.name+'</ws:name>';
								}
							}
							if(auditInfoReq.publicServant.organization != null){    					
								mc.setProperty('serviceHeaderOrgAuthor', auditInfoReq.publicServant.organization);
								gdibHeaderString += '<ws:organization>'+auditInfoReq.publicServant.organization+'</ws:organization>';
							}
							gdibHeaderString += '</ws:publicServant>';
						}
						//Operación del ESB invocada
						if(auditInfoReq.esbOperation != null && auditInfoReq.esbOperation.length > 0){
							//Si es informada la operación ESB en la cabecera de la petición, la establecemos en el contexto
							//En caso contrario, se utiliza la establecida en el servicio
							mc.setProperty('csgdOperation', auditInfoReq.esbOperation);
						}
						gdibHeaderString += '<ws:esbOperation>'+mc.getProperty('csgdOperation')+'</ws:esbOperation>';
						//Información sobre el expediente y procedimiento
						if(auditInfoReq.file != null){
							var fileUidString = '';
							var fileIdFound = false;
							var fileProcIdFound = false;
							var fileProcNameFound = false;
							if(auditInfoReq.file.id != null){
								mc.setProperty('serviceHeaderFileId', auditInfoReq.file.id);
								
								fileIdFound = true;
							}
							if(auditInfoReq.file.proceedings != null){
								if(auditInfoReq.file.proceedings.id != null && auditInfoReq.file.proceedings.id.length > 0){
									mc.setProperty('serviceHeaderFileProcId', auditInfoReq.file.proceedings.id);								
									fileProcIdFound = true;
								} 
								if(auditInfoReq.file.proceedings.name != null && auditInfoReq.file.proceedings.name.length > 0){
									mc.setProperty('serviceHeaderFileProcName', auditInfoReq.file.proceedings.name);
									fileProcNameFound = true;
								}
							}
							
							if(fileIdFound){
								fileUidString = auditInfoReq.file.id;
							}
							
							if(fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += ' (Proc. ';
							} else if(!fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += 'Proc. ';
							}
							
							if(fileProcIdFound){
								fileUidString += auditInfoReq.file.proceedings.id;
							}
							if(fileProcIdFound && fileProcNameFound){
								fileUidString += ' - ';
							}
							if(fileProcNameFound){
								fileUidString += auditInfoReq.file.proceedings.name;
							}
							
							if(fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += ')';
							}
							if(fileUidString.length > 0){
								gdibHeaderString += '<ws:fileUid>' + fileUidString + '</ws:fileUid>';
							}
						}
	
						gdibHeaderString += '</ws:gdibAudit>';
					}
	
					//Se añaden las restricciones
					//Se mantienen las retricciones informadas en la petición.
					var typeRestrictionString = '';
					restrInfoReq = serviceHeader.restrictions;
					
					if(restrInfoReq != null && restrInfoReq.types != null){
						mc.setProperty('typeRestrictions',restrInfoReq.types);
					}
					
					typeRestrictionString = mc.getProperty('typeRestrictions');
					
					if(typeRestrictionString != null){
						gdibHeaderString += '<ws:gdibRestriction>';
						var typeRestrictions = typeRestrictionString.split(',');
													
						for (i = 0; i < typeRestrictions.length; i++) {
							gdibHeaderString += '<ws:types>'+typeRestrictions[i]+'</ws:types>';
						}
						
						gdibHeaderString += '</ws:gdibRestriction>';
					}
					//Se añade la sección de seguridad del servicio
					gdibHeaderString += '<ws:gdibSecurity>';
					gdibHeaderString += '<ws:user>'+userName+'</ws:user>';
					gdibHeaderString += '<ws:password>'+password+'</ws:password>';
					gdibHeaderString += '</ws:gdibSecurity>';
					//Se finaliza la cabecera de seguridad
					gdibHeaderString += '</ws:gdibHeader>';
					mc.setProperty('serviceHeaderString', gdibHeaderString);
				} else {
					errorMessage = 'Usuario y/o password/ticket no informados en sección de seguridad de la cabecera de la petición del servicio.';
				}		 
			} else {
				errorMessage = 'Sección de seguridad de la cabecera de la petición del servicio no informada.';
			}
		}catch(err){
			errorMessage = 'Petición mal formada. Cabecera de la petición del servicio no informada. Excepcion: ' + err.message;
	    }
	} else {
		errorMessage = 'Petición mal formada. Cabecera de la petición del servicio no informada.';
	}
	
    if(!serviceHeaderOk){
    	mc.setProperty('errorMessage', errorMessage);    	
    } 

    mc.setProperty('serviceHeaderOk', serviceHeaderOk);
}

function parseGetEcmAutTicketServiceHeader(mc) {
	var serviceHeader;
	var serviceHeaderString = mc.getProperty('serviceHeader');
	var errorMessage = '';
    var serviceHeaderOk = false;
    var gdibHeaderString = '';
    
	if(serviceHeaderString != null){
		try{
			serviceHeader = eval('('+serviceHeaderString+')');
		
			if(serviceHeader != null && serviceHeader.securityInfo != null){
				secInfoReq = serviceHeader.securityInfo;
				
				serviceHeaderOk = secInfoReq.user != null && (secInfoReq.password != null && secInfoReq.password.length > 0) ;
				
				if(serviceHeaderOk){
					gdibHeaderString = '<ws:gdibHeader xmlns:ws="http://www.caib.es/gdib/repository/ws">';
					
					mc.setProperty('username', secInfoReq.user);
					mc.setProperty('password', secInfoReq.password);
					mc.setProperty('secToken', 'password');
					
					auditInfoReq = serviceHeader.auditInfo;
					if(auditInfoReq != null){
						gdibHeaderString += '<ws:gdibAudit>';
						//Solicitante cuidadano
						if(auditInfoReq.applicant != null){
							gdibHeaderString += '<ws:applicant>';
							if(auditInfoReq.applicant.document != null){    					
								mc.setProperty('serviceHeaderApplicantDoc', auditInfoReq.applicant.document);
								gdibHeaderString += '<ws:document>'+auditInfoReq.applicant.document+'</ws:document>';
							} 
							if(auditInfoReq.applicant.name != null){    					
								mc.setProperty('serviceHeaderApplicantName', auditInfoReq.applicant.name);
								gdibHeaderString += '<ws:name>'+auditInfoReq.applicant.name+'</ws:name>';
							}
							gdibHeaderString += '</ws:applicant>';
						}
						//Nombre aplicación
						var appName = '';
						if(auditInfoReq.application != null && auditInfoReq.application.length > 0){
							appName = auditInfoReq.application;
						} else {
							appName = secInfoReq.user;
						}
						gdibHeaderString += '<ws:application>'+appName+'</ws:application>';
						mc.setProperty('serviceHeaderAppName', appName);
						
						//Solicitante funcionario
						if(auditInfoReq.publicServant != null){
							gdibHeaderString += '<ws:publicServant>';
							if(auditInfoReq.publicServant.identificationData != null){
								if(auditInfoReq.publicServant.identificationData.document != null){    					
									mc.setProperty('serviceHeaderPublicServantDoc', auditInfoReq.publicServant.identificationData.document);
									gdibHeaderString += '<ws:document>'+auditInfoReq.publicServant.identificationData.document+'</ws:document>';
								} 
								if(auditInfoReq.publicServant.identificationData.name != null){    					
									mc.setProperty('serviceHeaderPublicServantName', auditInfoReq.publicServant.identificationData.name);
									gdibHeaderString += '<ws:name>'+auditInfoReq.publicServant.identificationData.name+'</ws:name>';
								}
							}
							if(auditInfoReq.publicServant.organization != null){    					
								mc.setProperty('serviceHeaderOrgAuthor', auditInfoReq.publicServant.organization);
								gdibHeaderString += '<ws:organization>'+auditInfoReq.publicServant.organization+'</ws:organization>';
							}
							gdibHeaderString += '</ws:publicServant>';
						}
						//Operación del ESB invocada
						if(auditInfoReq.esbOperation != null && auditInfoReq.esbOperation.length > 0){
							//Si es informada la operación ESB en la cabecera de la petición, la establecemos en el contexto
							//En caso contrario, se utiliza la establecida en el servicio
							mc.setProperty('csgdOperation', auditInfoReq.esbOperation);
						}
						gdibHeaderString += '<ws:esbOperation>'+mc.getProperty('csgdOperation')+'</ws:esbOperation>';
						//Información sobre el expediente y procedimiento
						if(auditInfoReq.file != null){
							var fileUidString = '';
							var fileIdFound = false;
							var fileProcIdFound = false;
							var fileProcNameFound = false;
							if(auditInfoReq.file.id != null){
								mc.setProperty('serviceHeaderFileId', auditInfoReq.file.id);
								
								fileIdFound = true;
							}
							if(auditInfoReq.file.proceedings != null){
								if(auditInfoReq.file.proceedings.id != null && auditInfoReq.file.proceedings.id.length > 0){
									mc.setProperty('serviceHeaderFileProcId', auditInfoReq.file.proceedings.id);								
									fileProcIdFound = true;
								} 
								if(auditInfoReq.file.proceedings.name != null && auditInfoReq.file.proceedings.name.length > 0){
									mc.setProperty('serviceHeaderFileProcName', auditInfoReq.file.proceedings.name);
									fileProcNameFound = true;
								}
							}
							
							if(fileIdFound){
								fileUidString = auditInfoReq.file.id;
							}
							
							if(fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += ' (Proc. ';
							} else if(!fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += 'Proc. ';
							}
							
							if(fileProcIdFound){
								fileUidString += auditInfoReq.file.proceedings.id;
							}
							if(fileProcIdFound && fileProcNameFound){
								fileUidString += ' - ';
							}
							if(fileProcNameFound){
								fileUidString += auditInfoReq.file.proceedings.name;
							}
							
							if(fileIdFound && (fileProcIdFound || fileProcNameFound)){
								fileUidString += ')';
							}
							if(fileUidString.length > 0){
								gdibHeaderString += '<ws:fileUid>' + fileUidString + '</ws:fileUid>';
							}
						}
	
						gdibHeaderString += '</ws:gdibAudit>';
					}
	
					//Se añaden las restricciones
					//Se mantienen las retricciones informadas en la petición.
					var typeRestrictionString = '';
					restrInfoReq = serviceHeader.restrictions;
					
					if(restrInfoReq != null && (restrInfoReq.types != null && restrInfoReq.types.length > 0)){						
						mc.setProperty('typeRestrictions',typesString);
					}
					typeRestrictionString = mc.getProperty('typeRestrictions');
					if(typeRestrictionString != null && typeRestrictionString.length > 0){
						gdibHeaderString += '<ws:gdibRestriction>';
						var typeRestrictions = typeRestrictionString.split(','); 
						for (i = 0; i < typeRestrictions.length; i++) {
							gdibHeaderString += '<ws:types>'+typeRestrictions[i]+'</ws:types>';
						}
						gdibHeaderString += '</ws:gdibRestriction>';
					}
					
					
					//Se añade la sección de seguridad del servicio
					gdibHeaderString += '<ws:gdibSecurity>';
					gdibHeaderString += '<ws:user>'+secInfoReq.user+'</ws:user>';
					gdibHeaderString += '<ws:password>'+secInfoReq.password+'</ws:password>';
					gdibHeaderString += '</ws:gdibSecurity>';
					//Se finaliza la cabecera de seguridad
					gdibHeaderString += '</ws:gdibHeader>';
					mc.setProperty('serviceHeaderString', gdibHeaderString);
				} else {
					errorMessage = 'Usuario y/o password/ticket no informados en sección de seguridad de la cabecera de la petición del servicio.';
				}		 
			} else {
				errorMessage = 'Sección de seguridad de la cabecera de la petición del servicio no informada.';
			}
		}catch(err){
			errorMessage = 'Cabecera de la petición del servicio no informada. Excepcion: ' + err.message;
	    }
	} else {
		errorMessage = 'Cabecera de la petición del servicio no informada, no tengo ni idea que pasa.';
	}
	
    if(!serviceHeaderOk){
    	mc.setProperty('errorMessage', errorMessage);    	
    } 

    mc.setProperty('serviceHeaderOk', serviceHeaderOk);
}
