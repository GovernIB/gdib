function main()
{
	var ids = eval('(' +requestbody.getContent()+')').ids;
	var idsArray;

	if(ids != null){
		idsArray = ids.split(",");
	}

	if(idsArray != null){
		for(var i =0; i< idsArray.length ; i++){
			var nodeid = "workspace://SpacesStore/" + idsArray[i];
			expurgo.expurgar(nodeid);
		}
	}

	model.resultado="ACK";
}

main();