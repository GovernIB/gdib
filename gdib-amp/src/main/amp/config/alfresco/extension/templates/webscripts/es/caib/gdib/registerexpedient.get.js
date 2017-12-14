function main()
{
	// obtengo los parametros de la url
	var nodeRef = args["nodeRef"];

	// obtengo el nodo
	var node = search.findNode(nodeRef);

	// cambio la propiedad del estado del expediente
	node.properties["eni:estado_archivo"]="Ingresado";

	// salvo el nodo
	node.save();

	// guardoe el valor dentro del modelo
	model.estado = node.properties["eni:estado_archivo"];
}

main();