<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/application.css" group="console"/>
   <@link href="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion.css" group="console"/>
</@>

<@markup id="js">
	<@script src="${url.context}/res/components/expurgo/expurgo.js" />
   <#-- JavaScript Dependencies 
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-seriedocumental.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-subtypedoc.js" />
   <@script src="${url.context}/res/components/cuadro-clasificacion/cuadro-clasificacion-function.js" />
   -->
</@>

<@markup id="widgets">
   <#-- <@createWidgets group="search"/> -->
</@>

<@markup id="html">
   <@uniqueIdDiv>
   		<#assign el=args.htmlid?html>
   		<style>   		
  #tablaprereg tr:nth-child(2n){
     background-color:#ccc;
    }  
   		</style>
		<div id="${el}-body" class="application cuadro-clasificacion">

			<#-- Div Dialog para la creacion de Funciones -->
			<div class="title">${msg("Expurgo.title")}</div>
			<table id="tablaprereg">
			   <tr>
			   	<th>#</th>
			   	<th>Expediente</th>
			   	<th>Serie documental</th>
			   	<th>Estado archivo</th>			   	
			   	<th>Fecha expurgo</th>   	
			   </tr>
			   <#list data as row>
			   <tr>
			   <td><input type="checkbox" name="inputexpedientes" value="${row.expid}"></td>
			   <td>${row.expediente}</td>
			   <td>${row.serie}</td>	
			   <td>${row.estado}</td>		   
			   <td>${row.fecha}</td>   
			   </tr>
			   </#list>
			</table>
			<div id="expurgar_button" class="info"></div>
		</div>
   </@>
</@>