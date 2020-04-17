<html>
 <head>
	<title>Preregistro</title>
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
   <h1<Preregistro CAIB</h1>  
  <table>
   <tr>
   	<th>Expediente</th>
   	<th>Fecha preregistro</th>   	
   </tr>
   <#list data as row>
   <tr>
   <td>${row.expediente}</td>
   <td>${row.fecha}</td>   
   </tr>
   </#list>
  </table>
 </body>
</html>                              