
var SerieDocumental = {};

SerieDocumental.init = function () {
	msg = Alfresco.util.message;

	// creo el dialogo para el formulario de creacion de series documentales
	SerieDocumental.dialog();

	// creo el boton para mostrar el popup de creacion de series documentales
	SerieDocumental.add_button();

	// muestro la tabla con las series documentales
	SerieDocumental.showDataTable();
}

SerieDocumental.dialog = function (){
	// Defino los eventos para el envio del formulario o cancelacion
	var handleSubmit = function() {
		this.submit();
	};
	var handleCancel = function() {
		this.cancel();
	};

	// Eliminar la clase de contenido progresivamente mejorada, justo antes del módulo Creación
    YAHOO.util.Dom.removeClass("serieDocumental_dialog", "yui-pe-content");

	// Instancio el dialogo
	serieDocumentalDialog = new YAHOO.widget.Dialog("serieDocumental_dialog",
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
		serieDocumentalDataTable.addRow(
        	    {code_clasificacion:response.code_clasificacion, description:response.description}
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
	serieDocumentalDialog.callback = {
			success: handleSuccess,
			failure: handleFailure };

	// Dibujo el dialogo
	serieDocumentalDialog.render();
}

// boton para mostrar el popup de crear una serie
SerieDocumental.add_button = function (){

	var serieDocumental_add_btn = new YAHOO.widget.Button({
	    id: "serieDocumental_add",
	    type: "button",
	    label: msg("cuadro.clasificacion.seriedocumental.button.label"),
	    container: "serieDocumental_button"
	});

	// "click" event handler for each Button instance
	function serieDocumental_add_btn_Click(p_oEvent) {
		serieDocumentalDialog.show();
	}

	serieDocumental_add_btn.on("click", serieDocumental_add_btn_Click);
//	YAHOO.util.Event.addListener("serieDocumental_add-button", "click", serieDocumentalDialog.show, serieDocumentalDialog, true);
}


SerieDocumental.showDataTable = function (){
	var url = Alfresco.constants.PROXY_URI + "gdib/cuadro/seriedocumental";

	// realizo una llamada ajax a un webscript del repo que me devuelve el cuadro documental
	Alfresco.util.Ajax.request({
		url : url,
		method : Alfresco.util.Ajax.GET,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				SerieDocumental.renderDataTable(res.json);
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

SerieDocumental.renderDataTable = function (data){
	// defino como van a ser los datos que estan en la columna de la tabla, la key coincide con la key del json
	// donde vienen los datos
	var serieDocumentalColumnDefs = [
	    {key:"code_clasificacion", label:msg("cuadro.clasificacion.metadata.code_clasificacion"), sortable: true},
	    {key:"description", label:msg("cuadro.clasificacion.metadata.description")},
	    {key:"delete", label: "", formatter:CuadroClasif.formatButtonDeleteLabel}
	];

	// obtengo el datasource, parseando el array que viene, en el elemento "root" del json
	var serieDocumentalDataSource = new YAHOO.util.DataSource(data.root);
	serieDocumentalDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	// los campos del esquema coinciden con la key dentro del json
	serieDocumentalDataSource.responseSchema = {
	        fields: ["code_clasificacion","description", "delete"]
	};

	// muestro la tabla, pasando el "id" del elemento html donde pintar la tabla
	serieDocumentalDataTable = new YAHOO.widget.DataTable("serieDocumental_dataTable", serieDocumentalColumnDefs, serieDocumentalDataSource, {});

	//Incluir un boton para añadir una fila a la tabla
	SerieDocumental.dataTable_create_confirmation_operation_dialog();

	SerieDocumental.dataTable_button_click_event();
}

SerieDocumental.dataTable_create_confirmation_operation_dialog = function (){

	// creo las funciones para el boton de si o no del dialogo
	var handleYes = function() {
	    this.hide();
	    // actualizo la tabla en base de datos
	    SerieDocumental.removeRowTable(oRecord.getData());

	};
	var handleNo = function() {
	    this.hide();
	};
	var confirmationOperationButtons = [
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.yes"), handler: handleYes },
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.no"), handler: handleNo, isDefault:true}
	];

	// creo un YAHOO.widget.SimpleDialog
	SerieDocumental.confirmationOperation = new YAHOO.widget.SimpleDialog("SerieDocumental_confirmationOperation", {
	    width: "30em",
	    effect:{
	        effect: YAHOO.widget.ContainerEffect.FADE,
	        duration: 0.25
	    },
	    fixedcenter: true,
	    modal: true,
	    visible: false,
	    draggable: false,
	    constraintoviewport: true,
	    buttons: confirmationOperationButtons
	});

	// relleno titulo, cuerpo e imagen del dialogo
	SerieDocumental.confirmationOperation.setHeader(msg("cuadro.clasificacion.confirmation.operation.title"));
	SerieDocumental.confirmationOperation.setBody(msg("cuadro.clasificacion.confirmation.operation.body"));
	SerieDocumental.confirmationOperation.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_INFO);

	// lo pinto en el DOM
	SerieDocumental.confirmationOperation.render(document.body);
}

SerieDocumental.dataTable_button_click_event = function (){
	// evento de captura de pulsacion de un boton
	serieDocumentalDataTable.subscribe("buttonClickEvent", function(event){
		// salvo el contenido de la fila
		oRecord = this.getRecord(event.target);

		// muestro la ventana de confirmacion de operacion
		SerieDocumental.confirmationOperation.show();
	});
}

SerieDocumental.removeRowTable = function (rowData){
	var url, method;
	method = Alfresco.util.Ajax.DELETE;
	var clasi = rowData["code_clasificacion"];
	url = Alfresco.constants.PROXY_URI + "gdib/cuadro/seriedocumental/"+clasi;

	Alfresco.util.Ajax.request({
		url : url,
		method : method,
		requestContentType: Alfresco.util.Ajax.JSON,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("cuadro.clasificacion.update.data.success"),
	                   displayTime: 5
	                });
				serieDocumentalDataTable.deleteRow(oRecord);
			},
			scope : this
		},
		failureCallback : {
			fn : function(res) {
				var result = eval('(' + res.serverResponse.responseText + ')');
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("cuadro.clasificacion.update.data.error", null, [result.message]),
	                   displayTime: 8
	                });
			},
			scope : this
		}
	});
}

YAHOO.util.Event.onDOMReady(SerieDocumental.init);