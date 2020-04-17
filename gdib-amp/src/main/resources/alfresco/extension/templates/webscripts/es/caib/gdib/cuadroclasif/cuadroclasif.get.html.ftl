<html>
 <head>
	<title>Cuadro Clasificacion</title>
  <style type="text/css">
   H1 {border-width: 1; border: solid; text-align: center}
   html{background-color: #eee;
    margin: auto; padding:50px;}
	body{
	  margin:auto;
	  width:80%;
	  background-color:white;
	}
	th{
	   background-color:#777;
	   color:white;
	}
	tr:nth-child(even) {
	 background-color:#ccd;
	}
	
 </style>
  
    

  </head>
 <body>
   <h1>Cuadro conservaci&oacute;n CAIB</h1>  
  <table>
   <tr>
   	<th>Serie documental</th>
   	<th>Subtipo documental</th>
   	<th>Lopd</th>
   	<th>Confidencialidad</th>
   	<th>Tipo de Acceso</th>
   	<th>C&oacute;digo causa de limitaci&oacute;n</th>
   	<th>Normativa</th>
   	<th>Condici&oacute;n reutilizaci&oacute;n</th>
   	<th>Tipo valor</th>
   	
   	<th>Valor secundario</th>
   	<th>Tipo dictamen</th>
   	<th>Acci&oacute;n dictaminada</th>
   	<th>Plazo acci&oacute;n dictaminada</th>
   	<th>Denominaci&oacute;n clase</th>
   	<th>Tipo Clasificaci&oacute;n</th>
   	<th>Resellado</th>
   </tr>
   <#list data as row>
   <tr>
   <td>${row.documentarySeries}</td>
   <td><#if row.subtypeDoc??>${row.subtypeDoc}</#if></td>
   <td><#if row.lopd??>${row.lopd}</#if></td>
   <td><#if row.confidentiality??>${row.confidentiality}</#if></td>
   <td><#if row.accessType??>${row.accessType}</#if></td>
   <td><#if row.causeLimitationCode??>${row.causeLimitationCode}</#if></td>
   <td><#if row.normative??>${row.normative}</#if></td>
   <td><#if row.reutilizationCond??>${row.reutilizationCond}</#if></td>
   <td><#if row.valueType??>${row.valueType}</#if></td>
   
   <td><#if row.secundaryValue??>${row.secundaryValue}</#if></td>
   <td><#if row.dictumType??>${row.dictumType}</#if></td>
   <td><#if row.dictatedAction??>${row.dictatedAction}</#if></td>
   <td><#if row.termDictatedAction??>${row.termDictatedAction}</#if></td>   
   <td><#if row.designationClass??>${row.designationClass}</#if></td>
   <td><#if row.classificationType??>${row.classificationType}</#if></td>
   <td><#if row.resealing??>${row.resealing}</#if></td>
   </tr>
   </#list>
  </table>
 </body>
</html>                              
                        