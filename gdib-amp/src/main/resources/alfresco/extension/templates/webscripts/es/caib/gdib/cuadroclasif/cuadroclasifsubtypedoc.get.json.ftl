{
	"root":[
	<#list data as row>
	{
			"code_clasificacion": "${row.documentarySeries}",
	   		"code_subtype": "${row.subtypeDoc}",
	   		"description": "${row.description!""}"
	   	}<#if row_has_next>,</#if>
	</#list>
]}

