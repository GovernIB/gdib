
<#if data??>
{
	"root":[
	<#list data as row>
	{
	   		"code_clasificacion": "${row.documentarySeries}",
	   		"description": "${row.description!""}"
	   	}<#if row_has_next>,</#if>
	</#list>
]}
<#else>
{
	"code_clasificacion": "${code_clasificacion}",
	"description": "${description}"
}
</#if>
