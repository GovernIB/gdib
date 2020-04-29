<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/signature-extension/verify-document-signature.js" group="document-details"/>
</@>

<@markup id="widgets">
	<@createWidgets group="document-details"/>
  	<@inlineScript group="document-details">
    	YAHOO.util.Event.onContentReady("${args.htmlid?js_string}-heading", function() {
       		Alfresco.util.createTwister("${args.htmlid?js_string}-heading", "DocumentMigration");
     	});
  	</@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
	 <!-- Parameters and libs -->
	 <#include "../../../org/alfresco/include/alfresco-macros.lib.ftl" />
	 <#assign el=args.htmlid?html>
	 <!-- Markup -->
	 <div class="document-metadata-header document-details-panel">
	    <h2 id="${el}-heading" class="thin dark">
	       ${msg("heading")}
	       <span id="verifyButton" class="alfresco-twister-actions">
	          	<a href="#"
	          		id="verifyDocumentButton"
	          		name=".onVerifyClick"
	          		class="${el} edit"
	          		title="${msg("action.verified")}"
	          		style="background-image:url(/share/res/components/documentlibrary/actions/document-edit-metadata-16.png)"
	     			>&nbsp;
	     		</a>
	       </span>
	    </h2>
	     <div class="form-container" style="display: block;">
	     	<div>Para cambiar el formato de visualizar, es en el fichero verifydocumentsignature.get.html</div>
	    	<div class="form-fields">

			<!-- DIV para mostrar mensaje de documento no verificado -->
				<div id="verify-document-form-not-verified" class="set">
					<h3 id="h3_operationMessage" class="thin dark">${msg("document.not.verified")}</h3>
				</div>


			<!-- DIV para mostrar mensaje de documento transformado -->
				<div id="verify-document-form-transform" class="set" style="display: none;">
					<div class='form-field'>
						<h3 class="thin dark">${msg("document.transformed")}</h3>
					</div>
					<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span id="transform-uid" class='viewmode-label'></span>
						</div>
					</div>
				</div>


			<!-- DIV para mostrar los datos de la verificacion -->
				<div id="verify-document-form-verified" class="set" style="display: none;">
					<div class='form-field'>
	    				<h3 id="operationMessage" class="thin dark"></h3>
					</div>
	    			<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span class='viewmode-label'>${msg("document.verified.validationStatus")}</span>
							<span id="validationStatus" class='viewmode-value'></span>
						</div>
					</div>
					<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span class='viewmode-label'>${msg("document.verified.detailedValidationStatus")}</span>
							<span id="detailedValidationStatus" class='viewmode-value'></span>
						</div>
					</div>
					<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span class='viewmode-label'>${msg("document.verified.validationMessage")}</span>
							<span id="validationMessage" class='viewmode-value'></span>
						</div>
					</div>
					<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span class='viewmode-label'>${msg("document.verified.signatureType")}</span>
							<span id="signatureType" class='viewmode-value'></span>
						</div>
					</div>
					<div class='form-field'>
	    				<div class='viewmode-field'>
				  			<span class='viewmode-label'>${msg("document.verified.signatureForm")}</span>
							<span id="signatureForm" class='viewmode-value'></span>
						</div>
					</div>
				</div>

	    	</div>
	    </div>
	 </div>
   </@>
</@>


