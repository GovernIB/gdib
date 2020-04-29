<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function checkTransform(){
	var jsonNode = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
	if ( jsonNode != null && jsonNode.item != null && jsonNode.item.node != null ){
		var transform_uuid = jsonNode.item.node.properties["gdib:transform_uuid"];
		
		if(transform_uuid == undefined)
		{
			model.transform_uuid = null;
		}
		else
		{
			model.transform_uuid = transform_uuid;
		}
	}else{
		model.transform_uuid = null;
	}	
}

function main()
{
	AlfrescoUtil.param('nodeRef', null);
	AlfrescoUtil.param('site', null);

	checkTransform();

	// Widget instantiation metadata...
	var documentSignature = {
		id : "DocumentSignature",
		name : "Alfresco.DocumentSignature",
		options : {
			nodeRef : model.nodeRef,
			transform_uid : model.transform_uuid
		}
	};
	model.widgets = [documentSignature];
}

main();