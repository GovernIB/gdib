
var SubtypeDoc = {};

SubtypeDoc.init = function () {
	msg = Alfresco.util.message;

	// creo el boton para mostrar el popup de creacion de series documentales
	SubtypeDoc.add_button();

	// muestro la tabla con las series documentales
	SubtypeDoc.showDataTable();
}

// Incluir una fila a la tabla
SubtypeDoc.add_button = function (){

	var subtypeDoc_add_btn = new YAHOO.widget.Button({
	    id: "subtypedoc_add",
	    type: "button",
	    label: msg("cuadro.clasificacion.subtypedoc.button.label"),
	    container: "subtypedoc_addRow"
	});

	// "click" event handler for each Button instance
	function subtypeDoc_add_btn_Click(p_oEvent) {
		subtypeDocDataTable.addRow(
        	    {code_clasificacion:"", description:""} );
	}

	subtypeDoc_add_btn.on("click", subtypeDoc_add_btn_Click);
}

SubtypeDoc.showDataTable = function (){
	var url = Alfresco.constants.PROXY_URI + "gdib/cuadro/subtypedoc";

	// realizo una llamada ajax a un webscript del repo que me devuelve el cuadro documental
	Alfresco.util.Ajax.request({
		url : url,
		method : Alfresco.util.Ajax.GET,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				SubtypeDoc.renderDataTable(res.json);
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

SubtypeDoc.check_primray_keys = function (recordCount){
	var code_clasificacion = oRecord.getData().code_clasificacion;
	var code_subtype = oRecord.getData().code_subtype;
	var recordSet = cuadroClasifDataTable.getRecordSet();
	var exits = false;
	for (var i = 0; i < recordSet.getLength(); i++) {
		if(i != recordCount
				&& code_clasificacion == recordSet.getRecord(i).getData().code_subtype)
		{
			return true;
		}

	}
	return false;
}

SubtypeDoc.updateTable = function (operation, rowData){
	var url, method;
	if(operation == "save"){
		method = Alfresco.util.Ajax.POST;
		url = Alfresco.constants.PROXY_URI + "gdib/cuadro/subtypedoc";
	}else{
		method = Alfresco.util.Ajax.DELETE;
		var clasi = rowData["code_clasificacion"];
		url = Alfresco.constants.PROXY_URI + "gdib/cuadro/subtypedoc/"+clasi;
	}
	Alfresco.util.Ajax.request({
		url : url,
		method : method,
		dataObj: rowData,
		requestContentType: Alfresco.util.Ajax.JSON,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("cuadro.clasificacion.update.data.success"),
	                   displayTime: 5
	                });
				// actualizo la tabla visualmente eliminado la fila si el button no es save
			    if(button != "save")
			    {
			    	subtypeDocDataTable.deleteRow(oRecord);
			    }
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

SubtypeDoc.dataTable_create_confirmation_operation_dialog = function (){

	// creo las funciones para el boton de si o no del dialogo
	var handleYes = function() {
	    this.hide();
	    SubtypeDoc.updateTable(button, oRecord.getData());
	};
	var handleNo = function() {
	    this.hide();
	};
	var confirmationOperationButtons = [
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.yes"), handler: handleYes },
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.no"), handler: handleNo, isDefault:true}
	];

	// creo un YAHOO.widget.SimpleDialog
	SubtypeDoc.confirmationOperation = new YAHOO.widget.SimpleDialog("SubtypeDoc_confirmationOperation", {
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
	SubtypeDoc.confirmationOperation.setHeader(msg("cuadro.clasificacion.confirmation.operation.title"));
	SubtypeDoc.confirmationOperation.setBody(msg("cuadro.clasificacion.confirmation.operation.body"));
	SubtypeDoc.confirmationOperation.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_INFO);

	// lo pinto en el DOM
	SubtypeDoc.confirmationOperation.render(document.body);
}

SubtypeDoc.dataTable_button_click_event = function (){
	// evento de captura de pulsacion de un boton
	subtypeDocDataTable.subscribe("buttonClickEvent", function(event){
		button = null;
		oRecord = null;
		var header = event.target.parentElement.parentElement.headers;
		// salvo el boton que se esta pulsando. "save" or "delete"
		button = header.substring(header.lastIndexOf("-")+1, header.length).trim();
		// salvo el contenido de la fila
		oRecord = this.getRecord(event.target);

		var duplicatedKeys = SubtypeDoc.check_primray_keys(cuadroClasifDataTable.getRecordIndex(oRecord));

		if(button == "save" && duplicatedKeys)
		{
			Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("cuadro.clasificacion.primary.keys.duplicated"),
	                   displayTime: 5
	                });
			return;
		}
		SubtypeDoc.confirmationOperation.show();
	});
}

SubtypeDoc.renderDataTable = function (data){
	// defino como van a ser los datos que estan en la columna de la tabla, la key coincide con la key del json
	// donde vienen los datos
	var subtypeDocColumnDefs = [
	    {key:"code_clasificacion", label:msg("cuadro.clasificacion.metadata.code_clasificacion"), sortable: true, editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"description", label:msg("cuadro.clasificacion.metadata.description"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"save", label:"", formatter:CuadroClasif.formatButtonSaveLabel},
	    {key:"delete", label:"", formatter:CuadroClasif.formatButtonDeleteLabel}
	];

	// obtengo el datasource, parseando el array que viene, en el elemento "root" del json
	var subtypeDocDataSource = new YAHOO.util.DataSource(data.root);
	subtypeDocDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	// los campos del esquema coinciden con la key dentro del json
	subtypeDocDataSource.responseSchema = {
	        fields: ["code_clasificacion","description"]
	};

	// muestro la tabla, pasando el "id" del elemento html donde pintar la tabla
	subtypeDocDataTable = new YAHOO.widget.DataTable("subtypedoc_dataTable", subtypeDocColumnDefs, subtypeDocDataSource, {});

	// permite sobresaltar una celda cuando paso el boton por encima
	subtypeDocDataTable.subscribe("cellMouseoverEvent", CuadroClasif.dataTable_highlightEditableCell);
	subtypeDocDataTable.subscribe("cellMouseoutEvent", subtypeDocDataTable.onEventUnhighlightCell);

	// evento para que al hacer click en una celda muestre un editor
	subtypeDocDataTable.subscribe("cellClickEvent", subtypeDocDataTable.onEventShowCellEditor);

	SubtypeDoc.dataTable_create_confirmation_operation_dialog();

	SubtypeDoc.dataTable_button_click_event();
}

YAHOO.util.Event.onDOMReady(SubtypeDoc.init);