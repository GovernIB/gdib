
function main()
{
	// obtener las funciones del DM y RM para el select
	var response = remote.call("/gdib/cuadro/functions");
	var result = eval('(' + response + ')');

	model.functions = result.functions;
}

main();