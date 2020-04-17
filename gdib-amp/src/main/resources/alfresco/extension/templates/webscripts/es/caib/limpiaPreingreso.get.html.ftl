<html>
<head><title>Webscript de limpieza de Preingreso</title>
<style>
  html{
   background-color:#ccc;
  }
  body{
    margin:auto;
    width: 1024px;
    background-color:white;
    padding: 20px;
  }
    ul{
     margin:0px;
     padding:0px;
    }
   ul > li {
    list-style-type:none;
    padding: 5px;
   }
   ul > li:nth-child(odd){
     background-color:#eee;
   }
</style>
</head>
<body>
<h1>Lista de expedientes modificados</h1>
<#assign result=false />
<ul>
<#list data as row>
<#assign result=true />
<li>  El expediente ${row[0]} con nombre ${row[1]} perteneciente a la serie ${row[2]} se ha modificado a Ingresado</li>
</#list>
</ul>
<#if result == false >
<p>No hay nodos en el repositorio con el estado Preingreso</p>
</#if>
</body>
</html>