function main(){

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
		
	       model.data[i] = [id,name,serie];
              expedientes[i].properties["eni:estado_archivo"] = "Ingresado";
              expedientes[i].save();
		  
	}	

}
main();