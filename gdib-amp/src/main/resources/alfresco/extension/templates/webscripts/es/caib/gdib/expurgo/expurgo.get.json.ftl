{
	"data":[
	<#list data as row>
	{"expid":"${row[0]}","expediente":"${row[1]}","fecha":"<#if row?size = 5>${row[4]?datetime}</#if>","serie":"${row[2]}","estado":"${row[3]}"}<#if row_has_next>,</#if>
	</#list>
]}