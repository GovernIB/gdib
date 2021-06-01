
function main()
{
	logger.debug("cuadroclasiffunction.get.js :: main");
	var name = args.name;
	var title = args.description;
	if(name || title){
		createFunction(name, title)
	}else{
		getFunctions();
	}
	logger.debug("cuadroclasiffunction.get.js :: main : end");
}

function createFunction(name, title)
{ 
	logger.debug("cuadroclasiffunction.get.js :: createFunction");
	var dm = companyhome.childByNamePath("CAIB/RepositorioDM");
	var rm = companyhome.childByNamePath("Sites/rm/documentLibrary/Cuadro de clasificación");
	if ( rm == null )
		rm = companyhome.childByNamePath("Sitios/rm/documentLibrary/Cuadro de clasificación");
	dmFolder = dm.createFolder(name);
	dmFolder.properties["title"] = title;
	dmFolder.save();

	rmFolder = rm.createFolder(name, "{http://www.alfresco.org/model/recordsmanagement/1.0}recordCategory");
	rmFolder.properties["title"] = title;
	rmFolder.save();

	var functions = new Array();
	var dm_function = new Object();
	dm_function.name = name;
	dm_function.title = title;
	functions.push(dm_function);
	model.functions = functions;
	logger.debug("cuadroclasiffunction.get.js :: createFunction : fin");
}

function getFunctions()
{
	logger.debug("cuadroclasiffunction.get.js :: getFunctions : start");
	var dm = companyhome.childByNamePath("CAIB/RepositorioDM")
	var functions = new Array();
	if(dm!=null){
		var folders = dm.childFileFolders();
		for (var i = 0; i < folders.length; i++) {
			var dm_function = new Object();
			dm_function.name = folders[i].name;
			dm_function.title = folders[i].properties["title"];
			if(!dm_function.title)
				dm_function.title = "";
			functions.push(dm_function);
		}
	}
	model.functions = functions;
	logger.debug("cuadroclasiffunction.get.js :: getFunctions : end");
}

main();