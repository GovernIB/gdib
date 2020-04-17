function main()
{			
	var ids = eval('(' +requestbody.getContent()+')')["ids"].split(",");
	for(var i =0; i< ids.length ; i++){
		var nodeid = "workspace://SpacesStore/" + ids[i];
		var node = search.findNode(nodeid);
		node.properties["eni:estado_archivo"] = "Ingresado";
		node.save();
	}
		
	model.resultado="ACK";
}

main();