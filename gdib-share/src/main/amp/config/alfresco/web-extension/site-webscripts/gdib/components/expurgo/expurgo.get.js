
function main()
{			
	// obtener las funciones del DM y RM para el select
	var response = remote.call("/gdib/expurgo?a"+Math.random().toString(36).substr(2, 7));
	var result = eval('(' + response + ')');

	model.data = result.data;
	
}

main();