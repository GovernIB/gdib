{
	"data":[
	<#list data as row>
	{"expid":"${row[0]}","expediente":"${row[1]}","fecha":"<#if row?size = 4>${row[3]?datetime}</#if>","serie":"${row[2]}"}<#if row_has_next>,</#if>
	</#list>
]}