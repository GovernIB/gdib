main();

function main(){
	if(document.hasAspect("eni:interoperable")){
		logger.log("Cambiamos la propiedad idEni al doc: " +  document.nodeRef);
		cambiarIdENI();
	}
}

function cambiarIdENI(){
	var id = "ES" + "_" + calcularOrgano() + "_" + calcularAnnoActual() + "_" + calcularRandom();
	logger.log("NUEVO ID: " + id);

	document.properties["eni:id"] = id;
	document.save();
}

function calcularRandom(){
	var res;
	res = calculateAlphanumeric(30);
	return res;
}

function calculateAlphanumeric(lenght)
{
    var text = "";
//    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    var possible = "abcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < lenght; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

// calculo el año
function calcularAnnoActual(){
	var hoy = new Date();
	var anno = hoy.getFullYear();
	return anno;
}

// recoge el organo del documento migrado, si no lo tiene devuelve el valor por defecto
function calcularOrgano(){
	var organo = document.properties["eni:organo"];
	var res = null;

	if(organo.length == 0){
		res = "A04003003"; // valor por defecto, == ORGANO_DEFAULT_VALUE = "A04003003" del proyecto de migracion
	}else if(organo instanceof Array){
		res = organo[0];
	}

	return res;
}