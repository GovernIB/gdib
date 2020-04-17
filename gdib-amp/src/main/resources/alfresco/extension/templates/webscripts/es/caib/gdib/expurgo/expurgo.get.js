function main()
{
	//TODO: No se puede acceder al webscript de alfresco desde aki, esto hay que moverlo a un webscript de alfresco
	//TODO: eni:estado_archivo Pendiente de eliminacion total, Pendiente de eliminacion parcial
	//TODO: Una vez eliminado: ublic static final String ESTADO_ARCHIVO_ELIMINADO = "Eliminado";
	var query = "TYPE:\"rma:recordFolder\" AND (@eni\\:estado_archivo:\"Pendiente de eliminacion total\" OR @eni\\:estado_archivo:\"Pendiente de eliminacion parcial\")";
	//var expedientes = search.luceneSearch("workspace://SpacesStore", "ASPECT:\"eni:marca_expurgo\"");
	var expedientes = search.luceneSearch("workspace://SpacesStore", query );
	model.data = [];
	for (var i = 0 ; i < expedientes.length; i++){
		var exp = expedientes[i].properties;
		var id = expedientes[i].id;
		var name = exp["name"];
		var fecha = exp["eni:fecha_marca_expurgo"];
		var serie = exp["eni:cod_clasificacion"];
		var estado = exp["eni:estado_archivo"];

		if ( serie == null)
			serie = "N/A";
		// expid, expediente, fecha
		if ( fecha == null ){
			model.data[i] = [id,name,serie,estado];
		}else{
			model.data[i] = [id,name,serie,estado, fecha];
		}

	}
}

main();