
var Functions = {};

Functions.init = function () {
	msg = Alfresco.util.message;

	// creo el dialogo para el formulario de creacion de series documentales
	Functions.dialog();

	// creo el boton para mostrar el popup de creacion de series documentales
	Functions.add_button();

	// muestro la tabla con las series documentales
	Functions.showDataTable();
}

Functions.dialog = function (){
	// Defino los eventos para el envio del formulario o cancelacion
	var handleSubmit = function() {
		this.submit();
	};
	var handleCancel = function() {
		this.cancel();
	};

	// Eliminar la clase de contenido progresivamente mejorada, justo antes del módulo Creación
    YAHOO.util.Dom.removeClass("functions_dialog", "yui-pe-content");

	// Instancio el dialogo
    functionsDialog = new YAHOO.widget.Dialog("functions_dialog",
				{
				  fixedcenter : true,
				  visible : false,
				  constraintoviewport : true,
				  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
							  { text:"Cancel", handler:handleCancel } ]
				 } );

	// Defino los eventos finales del envio del formulario del dialogo
	var handleSuccess = function(res) {
		var response = eval('(' + res.responseText + ')');
		var functionName = response.functions[0].name;
		var functionTitle = response.functions[0].title;
		// incluyo la funcion al select de crear serieDocumental
		var select = YAHOO.util.Selector.query('select[id*=serieDocumental]')[0];
		var option = new Option('option');
		option.value = functionName;
		option.text = functionName + " " + functionTitle;
		select.add(option);
		// incluyo la funcion a la tabla de funciones
		functionsDataTable.addRow(
        	    {
        	    	name:functionName, title:functionTitle}
        	    );
	};

	var handleFailure = function(res) {
		var result = eval('(' + res.responseText + ')');
		Alfresco.util.PopupManager.displayMessage(
            {
               text: msg("cuadro.clasificacion.update.data.error", null, [result.message]),
               displayTime: 8
            });
	};

	// asigno los eventos finales al dialogo
	functionsDialog.callback = {
			success: handleSuccess,
			failure: handleFailure };

	// Dibujo el dialogo
	functionsDialog.render();
}

Functions.add_button = function (){

	var functions_add_btn = new YAHOO.widget.Button({
	    id: "functions_add",
	    type: "button",
	    label: msg("cuadro.clasificacion.functions.button.label"),
	    container: "functions_button"
	});

	// "click" event handler for each Button instance
	function functions_add_btn_Click(p_oEvent) {
		functionsDialog.show();
	}

	functions_add_btn.on("click", functions_add_btn_Click);
}


Functions.showDataTable = function (){
	var url = Alfresco.constants.PROXY_URI + "gdib/cuadro/functions";

	// realizo una llamada ajax a un webscript del repo que me devuelve el cuadro documental
	Alfresco.util.Ajax.request({
		url : url,
		method : Alfresco.util.Ajax.GET,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				Functions.renderDataTable(res.json);
			},
			scope : this
		},
		failureCallback : {
			fn : function(res) {
				Alfresco.util.PopupManager.displayMessage(
		                {
		                   text: msg("cuadro.clasificacion.get.data.error", null, res),
		                   displayTime: 5
		                });
			},
			scope : this
		}
	});
}

Functions.renderDataTable = function (data){
	// defino como van a ser los datos que estan en la columna de la tabla, la key coincide con la key del json
	// donde vienen los datos
	var functionsColumnDefs = [
	    {key:"name", label:msg("cuadro.clasificacion.metadata.name"), sortable: true},
	    {key:"title", label:msg("cuadro.clasificacion.metadata.title")}
	];

	// obtengo el datasource, parseando el array que viene, en el elemento "root" del json
	var functionsDataSource = new YAHOO.util.DataSource(data.functions);
	functionsDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	// los campos del esquema coinciden con la key dentro del json
	functionsDataSource.responseSchema = {
	        fields: ["name","title"]
	};

	// muestro la tabla, pasando el "id" del elemento html donde pintar la tabla
	functionsDataTable = new YAHOO.widget.DataTable("functions_dataTable", functionsColumnDefs, functionsDataSource, {});
}

YAHOO.util.Event.onDOMReady(Functions.init);