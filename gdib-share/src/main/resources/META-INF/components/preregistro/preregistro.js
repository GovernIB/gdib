
var Preregistro = {};

Preregistro.init = function () {
	msg = Alfresco.util.message;

	// creo el boton para mostrar el popup de creacion de series documentales
	Preregistro.add_button();
}

Preregistro.add_button = function (){

	var registro_add_btn = new YAHOO.widget.Button({
	    id: "registrar",
	    type: "button",
	    label: msg("preregistro.button.label"),
	    container: "registrar_button"
	});

	// "click" event handler for each Button instance
	function registro_add_btn_Click(p_oEvent) {
		//egistroDialog.show();
		Preregistro.create_confirmation_operation_dialog();
	}

	registro_add_btn.on("click", registro_add_btn_Click);
}

Preregistro.registrar = function ()  {
	var inputs = document.getElementsByName("inputexpedientes");
	var ids = [];
	
	for(var i = 0; i < inputs.length; i++){
		var boton = inputs[i];
		if ( boton.checked ){
			ids[ids.length]=boton.value;
		}
			
	}
	
	if ( ids.length == 0 ){
		Alfresco.util.PopupManager.displayMessage(
                {
                   text: msg("No hay documentos seleccionados"),
                   displayTime: 5
                });
		return;
	}
	// enviar datos
	var url, method;
	method = Alfresco.util.Ajax.POST;
	url = Alfresco.constants.PROXY_URI + "gdib/prereg/registrar";
	var data = new FormData();
	
	
	Alfresco.util.Ajax.request({
		url : url,
		method : method,
		dataObj:{"ids":ids.toString()},
		requestContentType: Alfresco.util.Ajax.JSON,
		successCallback : {
			fn : function(res) {
				// le paso el json, es una array de json con elemento raiz root
				
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("preregistro.update.data.success"),
	                   displayTime: 5
	                });
				
			},
			scope : this
		},
		failureCallback : {
			fn : function(res) {
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("preregistro.update.data.error"),
	                   displayTime: 8
	                });
			},
			scope : this
		}
	});
	
	
}

Preregistro.create_confirmation_operation_dialog = function (){

	// creo las funciones para el boton de si o no del dialogo
	var handleYes = function() {
	    this.hide();
	    // actualizo la tabla en base de datos
	    //CuadroClasif.updateTable(button, oRecord.getData());
	    Preregistro.registrar();
	};
	var handleNo = function() {
	    this.hide();
	};
	var confirmationOperationButtons = [
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.yes"), handler: handleYes },
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.no"), handler: handleNo, isDefault:true}
	];

	// creo un YAHOO.widget.SimpleDialog
	Preregistro.confirmationOperation = new YAHOO.widget.SimpleDialog("CuadroClasif_confirmationOperation", {
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
	Preregistro.confirmationOperation.setHeader(msg("preregistro.confirmation.operation.title"));
	Preregistro.confirmationOperation.setBody(msg("preregistro.confirmation.operation.body"));
	Preregistro.confirmationOperation.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_INFO);

	// lo pinto en el DOM
	Preregistro.confirmationOperation.render(document.body);
	Preregistro.confirmationOperation.show();
}

YAHOO.util.Event.onDOMReady(Preregistro.init);