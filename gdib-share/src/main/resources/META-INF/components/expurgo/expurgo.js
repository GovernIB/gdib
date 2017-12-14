
var Expurgo = {};

Expurgo.init = function () {
	msg = Alfresco.util.message;

	// creo el boton para mostrar el popup de creacion de series documentales
	Expurgo.add_button();
}

Expurgo.add_button = function (){

	var expurgo_add_btn = new YAHOO.widget.Button({
	    id: "expurgo",
	    type: "button",
	    label: msg("Expurgo.button.label"),
	    container: "expurgar_button"
	});

	// "click" event handler for each Button instance
	function expurgo_add_btn_Click(p_oEvent) {
		//egistroDialog.show();
		Expurgo.create_confirmation_operation_dialog();
	}

	expurgo_add_btn.on("click", expurgo_add_btn_Click);
}

Expurgo.expurgar = function ()  {
	var inputs = document.getElementsByName("inputexpedientes");
	var ids = [];

	for(var i = 0; i < inputs.length; i++){
		var boton = inputs[i];
		if ( boton.checked ){
			ids[ids.length]=boton.value;
		}

	}
	//TODO: Alert por messagebox
	if ( ids.length == 0 ){
		Alfresco.util.PopupManager.displayMessage(
                {
                   text: msg("No hay expedientes seleccionados"),
                   displayTime: 5
                });
		return;
	}
	// enviar datos
	var url, method;
	method = Alfresco.util.Ajax.POST;
	url = Alfresco.constants.PROXY_URI + "gdib/expurgo/expurgar";
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
	                   text: msg("Expurgo.update.data.success"),
	                   displayTime: 5
	                });

			},
			scope : this
		},
		failureCallback : {
			fn : function(res) {
				Alfresco.util.PopupManager.displayMessage(
	                {
	                   text: msg("Expurgo.update.data.error"),
	                   displayTime: 8
	                });
			},
			scope : this
		}
	});


}

Expurgo.create_confirmation_operation_dialog = function (){

	// creo las funciones para el boton de si o no del dialogo
	var handleYes = function() {
	    this.hide();
	    // actualizo la tabla en base de datos
	    //CuadroClasif.updateTable(button, oRecord.getData());
	    Expurgo.expurgar();
	};
	var handleNo = function() {
	    this.hide();
	};
	var confirmationOperationButtons = [
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.yes"), handler: handleYes },
	    { text: msg("cuadro.clasificacion.confirmation.operation.button.text.no"), handler: handleNo, isDefault:true}
	];

	// creo un YAHOO.widget.SimpleDialog
	Expurgo.confirmationOperation = new YAHOO.widget.SimpleDialog("Expurgo_confirmationOperation", {
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
	Expurgo.confirmationOperation.setHeader(msg("Expurgo.confirmation.operation.title"));
	Expurgo.confirmationOperation.setBody(msg("Expurgo.confirmation.operation.body"));
	Expurgo.confirmationOperation.cfg.setProperty("icon", YAHOO.widget.SimpleDialog.ICON_INFO);

	// lo pinto en el DOM
	Expurgo.confirmationOperation.render(document.body);
	Expurgo.confirmationOperation.show();
}

YAHOO.util.Event.onDOMReady(Expurgo.init);