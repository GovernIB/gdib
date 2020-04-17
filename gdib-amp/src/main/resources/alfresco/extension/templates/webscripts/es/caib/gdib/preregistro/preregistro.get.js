function main()
{			
	//TODO: No se puede acceder al webscript de alfresco desde aki, esto hay que moverlo a un webscript de alfresco
	var expedientes = search.luceneSearch("workspace://SpacesStore", "TYPE:\"rma:recordFolder\" AND @eni\\:estado_archivo:Preingreso");
	model.data = [];
	for (var i = 0 ; i < expedientes.length; i++){
		var exp = expedientes[i].properties;
		var id = expedientes[i].id;
		var name = exp["name"];
		var fecha = exp["eni:fecha_fin_exp"];
		
		
		var serie = exp["eni:cod_clasificacion"];
		if ( serie == null)
			serie = "N/A";
		// expid, expediente, fecha
		if ( fecha == null ){
			model.data[i] = [id,name,serie];
		}else{
			model.data[i] = [id,name,serie,fecha];	
		}
		  
	}	
}

main();