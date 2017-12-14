/**
 * Document Details Verify Signature component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentSignature
 */
(function()
{

	var Dom = YAHOO.util.Dom;
	/**
	 * DocumentMigration constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {Alfresco.DocumentMigration} The new component instance
	 * @constructor
	 */
	Alfresco.DocumentSignature = function DocumentSignature_constructor(htmlId)
	{
		Alfresco.DocumentSignature.superclass.constructor.call(this, "Alfresco.DocumentSignature", htmlId, []);
		return this;
	};

	YAHOO.extend(Alfresco.DocumentSignature, Alfresco.component.Base,
	{
		options:
		{
			/**
			 * Referencia al nodo
			 *
			 *	@property nodeRef
			 *	@type string
			 */
			nodeRef: null,
			transform_uid: null

		},

		/**
		 * verify document signature click handler
		 *
		 * @method onAssignWorkflowClick
		 */
		onVerifyClick: function DocumentSignature_onVerifyClick()
		{
			Dom.get('verify-document-form-not-verified').style.display = 'none';

			if(this.options.transform_uid != null)
			{
				var url = '<a href="/share/page/site/dm/document-details?nodeRef=workspace://SpacesStore/'+this.options.transform_uid+'" id="yui-gen108">'+this.options.transform_uid+'</a>';
				Dom.get('transform-uid').innerHTML = url;
				Dom.get('verify-document-form-transform').style.display = 'block';
			}
			else
			{
				Dom.setStyle(['verifyDocumentButton'], 'backgroundImage', "url('/share/res/components/documentlibrary/images/ajax-loader.gif')");
				// Esta llamada esta hecha a un webcript de repo desarrollado a medida, recibe el nodo y devuelve nombre y creador del nodo
				var url = Alfresco.constants.PROXY_URI + "gdib/signature/check?nodeRef=" + this.options.nodeRef;
				Alfresco.util.Ajax.request(
				{
					method: Alfresco.util.Ajax.GET,
					url: url,
					successCallback:
    				{
						fn: function(response){
							if(response.json.validationStatus == "NO DETERMINADO"){
								Dom.get('verify-document-form-not-verified').style.display = 'block';
								Dom.get('h3_operationMessage').innerHTML = response.json.operationMessage

							}else{
								Dom.get('operationMessage').innerHTML = response.json.operationMessage
	    						Dom.get('validationStatus').innerHTML = response.json.validationStatus
	    						Dom.get('detailedValidationStatus').innerHTML = response.json.detailedValidationStatus
	    						Dom.get('validationMessage').innerHTML = response.json.validationMessage
	    						Dom.get('signatureType').innerHTML = response.json.signatureType
	    						Dom.get('signatureForm').innerHTML = response.json.signatureForm

    							Dom.get('verify-document-form-verified').style.display = 'block';

    							Dom.setStyle(['verifyDocumentButton'], 'backgroundImage', "url('/share/res/components/documentlibrary/actions/document-edit-metadata-16.png')");
							}
						},
    					scope: this
    				},
    				failureCallback:
    				{
    					fn : function(response) {
    						Alfresco.util.PopupManager.displayMessage(
    								{
    									text: "No se ha podido realizar la verificacion del documento, fallo en la conexion con Alfresco"
    								});
    						Dom.setStyle(['verifyDocumentButton'], 'backgroundImage', "url('/share/res/components/documentlibrary/actions/document-edit-metadata-16.png')");
    					},
    					scope: this
    				}
				});
			}
		}
	});

})();
