{
	"functions":[
	<#list functions as function>
		{
	   		"name": "${function.name}",
	   		"title": "${function.title}"
		}<#if function_has_next>,</#if>
	</#list>
]}