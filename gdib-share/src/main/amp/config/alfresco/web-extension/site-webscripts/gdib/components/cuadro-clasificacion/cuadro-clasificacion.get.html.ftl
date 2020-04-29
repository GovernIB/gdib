<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/application.css" group="console"/>
   <@link href="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-seriedocumental.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-subtypedoc.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-function.js" />
</@>

<@markup id="widgets">
   <#-- <@createWidgets group="search"/> -->
</@>

<@markup id="html">
   <@uniqueIdDiv>
   		<#assign el=args.htmlid?html>
		<div id="${el}-body" class="application cuadro-clasificacion">

			<#-- Div Dialog para la creacion de Funciones -->
			<div class="title">${msg("cuadro.clasificacion.functions.title")}</div>
			<div id="functions_dialog" class="yui-pe-content">
				<div class="hd">${msg("cuadro.clasificacion.functions.dialog.title")}</div>
				<div class="bd">
					<form method="GET" action="${url.server}${url.context}/proxy/alfresco/gdib/cuadro/functions">
						<div class="field">
							<label for="name">
								${msg("cuadro.clasificacion.functions.dialog.function.name")}
							</label>
							<input type="textbox" name="name" /></br>
						</div>
						<div class="field">
							<label for="description">
								${msg("cuadro.clasificacion.functions.dialog.function.title")}
							</label>
							<input type="textbox" name="description" /></br>
						</div>
					</form>
				</div>
			</div>
			<div id="functions_dataTable" class="row info" style="height: auto"></div>
			<div id="functions_button" class="info"></div>

			<#-- Div Dialog para la creacion de series documentales -->
			<div class="title">${msg("cuadro.clasificacion.seriedocumental.title")}</div>
			<div id="serieDocumental_dialog" class="yui-pe-content">
				<div class="hd">${msg("cuadro.clasificacion.seriedocumental.dialog.title")}</div>
				<div class="bd">
					<form method="GET" action="${url.server}${url.context}/proxy/alfresco/gdib/cuadro/seriedocumental">
						<div class="field">
							<label for="function">${msg("cuadro.clasificacion.seriedocumental.dialog.function")}</label>
							<select id="serieDocumental_select" name="function">
								<#list functions as function>
									<option value="${function.name}">${function.name} ${function.title}</option>
							  	</#list>
							</select></br>
						</div>
						<div class="field">
							<label for="name">
								${msg("cuadro.clasificacion.seriedocumental.dialog.serie.name")}
							</label>
							<input type="textbox" name="name" /></br>
						</div>
						<div class="field">
							<label for="description">
								${msg("cuadro.clasificacion.seriedocumental.dialog.serie.title")}
							</label>
							<input type="textbox" name="description" /></br>
						</div>
					</form>
				</div>
			</div>
			<div id="serieDocumental_dataTable" class="row info" style="height: auto"></div>
			<div id="serieDocumental_button" class="info"></div>

			<#-- Parte para gestionar los subtipos de documentos -->
			<div class="title">${msg("cuadro.clasificacion.subtypedoc.title")}</div>
			<div id="subtypedoc_dataTable" class="row info" style="height: auto"></div>
			<div id="subtypedoc_addRow" class="row info"></div>

			<#-- Parte para gestionar el cuadro de clasificacion -->
			<div class="title">${msg("cuadro.clasificacion.title")}</div>
			<div id="cuadoclasif_dataTable" class="row info" style="height: auto"></div>
			<div id="cuadoclasif_addRow" class="row info"></div>

		</div>
   </@>
</@>