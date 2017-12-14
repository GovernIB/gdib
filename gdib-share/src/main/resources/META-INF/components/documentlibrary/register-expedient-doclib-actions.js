(function () {
    YAHOO.Bubbling.fire("registerAction",
        {
            actionName: "onActionPreRegisterExpedient",
            fn: function org_alfresco_training_onActionPreRegisterExpedient(expedient) {
                this.modules.actions.genericAction(
                    {

                        success: {
                            callback: {
                                fn: function org_alfresco_training_onActionPreRegisterExpedientSuccess(response) {
                                	response.json.estado
                                	Alfresco.util.PopupManager.displayMessage(
                                		{
            								text: "Se ha cambiado el estado del expediente a " + response.json.estado
            							});
                                },
                                scope: this
                            },
                            event: {
                               name: "metadataRefresh"
                            }
                        },
                        failure: {
                            message: this.msg("gdib.doclib.action.expedient.register.msg.failure",
                                expedient.displayName, Alfresco.constants.USERNAME)
                        },
                        webscript: {
                        	name: "gdib/expedient/register?nodeRef={nodeRef}",
                            stem: Alfresco.constants.PROXY_URI,
                            method: Alfresco.util.Ajax.GET,
                            params: {
                                nodeRef: expedient.nodeRef
                            }
                        },
                        config: {}
                    });
            }
        });
})();