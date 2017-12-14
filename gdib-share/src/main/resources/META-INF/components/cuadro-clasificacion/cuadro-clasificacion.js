var cuadroClasifDataTable,
	button,
	oRecord;

var CuadroClasif = {};

CuadroClasif.init = function () {
	msg = Alfresco.util.message;
	CuadroClasif.showDataTable();
}

CuadroClasif.showDataTable = function () {
	var url = Alfresco.constants.PROXY_URI + "gdib/cuadro.json";

	// realizo una llamada ajax a un webscript del repo que me devuelve el cuadro documental
	Alfresco.util.Ajax.request({
		url : url,
		method : Alfresco.util.Ajax.GET,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				CuadroClasif.renderDataTable(res.json);
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

CuadroClasif.updateTable = function (operation, rowData){
	var url, method;
	if(operation == "save"){
		method = Alfresco.util.Ajax.POST;
		url = Alfresco.constants.PROXY_URI + "gdib/cuadro";
	}else{
		method = Alfresco.util.Ajax.DELETE;
		var clasi = rowData["code_clasificacion"];
		var subtype = rowData["code_subtype"];
		url = Alfresco.constants.PROXY_URI + "gdib/cuadro/"+clasi+"/"+subtype;
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
			    	cuadroClasifDataTable.deleteRow(oRecord);
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

// muestro un boton con la etiqueta SAVE
CuadroClasif.formatButtonSaveLabel = function (el, oRecord, oColumn, oData, oDataTable) {
    var sValue = msg("cuadro.clasificacion.column.button.save");
        el.innerHTML = "<button type=\"button\" class=\"yui-dt-button\">" + sValue + "</button>";
}

//muestro un boton con la etiqueta DELETE
CuadroClasif.formatButtonDeleteLabel = function (el, oRecord, oColumn, oData, oDataTable) {
    var sValue = msg("cuadro.clasificacion.column.button.delete");
    	el.innerHTML = "<button type=\"button\" class=\"yui-dt-button\">" + sValue + "</button>";
}

// permite sobresaltar una celda cuando paso el boton por encima
CuadroClasif.dataTable_highlightEditableCell = function (oArgs) {
    var elCell = oArgs.target;
    if(YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable")) {
        this.highlightCell(elCell);
    }
}

//Incluir una fila a la tabla
CuadroClasif.dataTable_add_row = function (){
	var addRowFunction = function() {
    	cuadroClasifDataTable.addRow(
        	    {code_clasificacion:"", lopd:"Basico", confidentiality:"Bajo", accesstype:"Libre", causelimitationcode:"", normative:"",
        	    	reutilizationcond:"", valuetype:"Administrativo", term:"", secundaryvalue:"", dictumtype:"PD", dictatedaction:"",
        	    	termdictatedaction:"", designationclass:"", classificationtype:"",
        	    	saveButton:"", deleteButton:""} );
	};

	var btn = new YAHOO.widget.Button({
	    id: "addRow",
	    type: "button",
	    label: msg("cuadro.clasificacion.addRow.button.label"),
	    container: "cuadoclasif_addRow"
	});
	btn.on("click", addRowFunction);
}

/**
 * Creo un dialogo de confirmacion de operacion. Usado en los botones de salvar y eliminar fila
 */
CuadroClasif.dataTable_create_confirmation_operation_dialog = function (){

	// creo las funciones para el boton de si o no del dialogo
	var handleYes = function() {
	    this.hide();
	    // actualizo la tabla en base de datos
	    CuadroClasif.updateTable(button, oRecord.getData());
	};
	var handleNo = function() {
	    this.hide();
	};
	var confirmationOperationButtons = [
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.yes"), handler: handleYes },
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.no"), handler: handleNo, isDefault:true}
	];

	// creo un YAHOO.widget.SimpleDialog
	CuadroClasif.confirmationOperation = new YAHOO.widget.SimpleDialog("CuadroClasif_confirmationOperation", {
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
	CuadroClasif.confirmationOperation.setHeader(msg("cuadro.clasificacion.confirmation.operation.title"));
	CuadroClasif.confirmationOperation.setBody(msg("cuadro.clasificacion.confirmation.operation.body"));
	CuadroClasif.confirmationOperation.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_INFO);

	// lo pinto en el DOM
	CuadroClasif.confirmationOperation.render(document.body);
}

CuadroClasif.check_primray_keys = function (recordCount){
	var code_clasificacion = oRecord.getData().code_clasificacion;
	var code_subtype = oRecord.getData().code_subtype;
	var recordSet = cuadroClasifDataTable.getRecordSet();
	var exits = false;
	for (var i = 0; i < recordSet.getLength(); i++) {
		if(i != recordCount
				&& code_clasificacion == recordSet.getRecord(i).getData().code_clasificacion
				&& code_subtype == recordSet.getRecord(i).getData().code_subtype)
		{
			return true;
		}

	}
	return false;
}

CuadroClasif.dataTable_button_click_event = function (){
	// evento de captura de pulsacion de un boton
	cuadroClasifDataTable.subscribe("buttonClickEvent", function(event){
		button = null;
		oRecord = null;
		var header = event.target.parentElement.parentElement.headers;
		// salvo el boton que se esta pulsando. "save" or "delete"
		button = header.substring(header.lastIndexOf("-")+1, header.length).trim();
		// salvo el contenido de la fila
		oRecord = this.getRecord(event.target);

		var duplicatedKeys = CuadroClasif.check_primray_keys(cuadroClasifDataTable.getRecordIndex(oRecord));

		if(button == "save" && duplicatedKeys)
		{
			Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("cuadro.clasificacion.primary.keys.duplicated"),
	                   displayTime: 5
	                });
			return;
		}
//		if(button == "delete" && duplicatedKeys)
//		{
//			cuadroClasifDataTable.deleteRow(oRecord.getCount());
//			return;
//		}
		// muestro el dialogo de confirmacion de operacion
		CuadroClasif.confirmationOperation.show();
	});
}

CuadroClasif.renderDataTable = function(data) {

	// defino como van a ser los datos que estan en la columna de la tabla, la key coincide con la key del json
	// donde vienen los datos
	var cuadroClasifColumnDefs = [
	    {key:"save", label: "", formatter:CuadroClasif.formatButtonSaveLabel},
	    {key:"delete", label: "", formatter:CuadroClasif.formatButtonDeleteLabel},
	    {key:"code_clasificacion", label:msg("cuadro.clasificacion.metadata.code_clasificacion"), sortable: true, editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"lopd", label:msg("cuadro.clasificacion.metadata.lopd"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["Basico", "Medio", "Alto"], disableBtns:true})},
	    {key:"confidentiality", label:msg("cuadro.clasificacion.metadata.confidentiality"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["Bajo", "Medio", "Alto"], disableBtns:true})},
	    {key:"accesstype", label:msg("cuadro.clasificacion.metadata.accesstype"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["Libre", "Limitado"], disableBtns:true})},
	    {key:"causelimitationcode", label:msg("cuadro.clasificacion.metadata.causelimitationcode"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"], disableBtns:true})},
	    {key:"normative", label:msg("cuadro.clasificacion.metadata.normative"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"reutilizationcond", label:msg("cuadro.clasificacion.metadata.reutilizationcond"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"valuetype", label:msg("cuadro.clasificacion.metadata.valuetype"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["Administrativo", "Fiscal", "Juridico", "Otros"], disableBtns:true})},
	    {key:"term", label:msg("cuadro.clasificacion.metadata.term"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"secundaryvalue", label:msg("cuadro.clasificacion.metadata.secundaryvalue"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["Si", "No", "Sin cobertura de calificacion"], disableBtns:true})},
	    {key:"dictumtype", label:msg("cuadro.clasificacion.metadata.dictumtype"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["CP", "EP", "ET", "PD"], disableBtns:true})},
	    {key:"dictatedaction", label:msg("cuadro.clasificacion.metadata.dictatedaction"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"termdictatedaction", label:msg("cuadro.clasificacion.metadata.termdictatedaction"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"designationclass", label:msg("cuadro.clasificacion.metadata.designationclass"), editor: new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
	    {key:"classificationtype", label:msg("cuadro.clasificacion.metadata.classificationtype"), editor: new YAHOO.widget.DropdownCellEditor({ dropdownOptions: ["SIA", "Funcional"], disableBtns:true})}
	];

	// obtengo el datasource, parseando el array que viene, en el elemento "root" del json
	var cuadroClasifDataSource = new YAHOO.util.DataSource(data.root);
	cuadroClasifDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	// los campos del esquema coinciden con la key dentro del json
	cuadroClasifDataSource.responseSchema = {
	        fields: ["save", "delete", "code_clasificacion",
	                 "lopd","confidentiality","accesstype","causelimitationcode","normative","reutilizationcond","valuetype","term",
	                 "secundaryvalue","dictumtype","dictatedaction","termdictatedaction", "designationclass",
	                 "classificationtype"
	                 ]
	};

	// muestro la tabla, pasando el "id" del elemento html donde pintar la tabla
	cuadroClasifDataTable = new YAHOO.widget.DataTable("cuadoclasif_dataTable", cuadroClasifColumnDefs, cuadroClasifDataSource, {});

	// permite sobresaltar una celda cuando paso el boton por encima
	cuadroClasifDataTable.subscribe("cellMouseoverEvent", CuadroClasif.dataTable_highlightEditableCell);
	cuadroClasifDataTable.subscribe("cellMouseoutEvent", cuadroClasifDataTable.onEventUnhighlightCell);

	// evento para que al hacer click en una celda muestre un editor
	cuadroClasifDataTable.subscribe("cellClickEvent", cuadroClasifDataTable.onEventShowCellEditor);

	//Incluir un boton para añadir una fila a la tabla
	CuadroClasif.dataTable_add_row();

	CuadroClasif.dataTable_create_confirmation_operation_dialog();

	CuadroClasif.dataTable_button_click_event();

}

YAHOO.util.Event.onDOMReady(CuadroClasif.init);