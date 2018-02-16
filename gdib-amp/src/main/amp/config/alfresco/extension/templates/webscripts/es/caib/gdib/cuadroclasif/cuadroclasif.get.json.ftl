{
	"root":[
	<#list data as row>
	{
	   		"code_clasificacion": "${row.documentarySeries}",
	   		"code_subtype": "${row.subtypeDoc}",
	   		"lopd": "${row.lopd!""}",
	   		"confidentiality": "${row.confidentiality!""}",
	   		"accesstype": "${row.accessType!""}",
	   		"causelimitationcode": "${row.causeLimitationCode!""}",
	   		"normative": "${row.normative!""}",
	   		"reutilizationcond": "${row.reutilizationCond!""}",
	   		"valuetype": "${row.valueType!""}",
	   		"term": "${row.timeLimit!""}",
	   		"secundaryvalue": "${row.secundaryValue!""}",
	   		"dictumtype": "${row.dictumType!""}",
	   		"dictatedaction": "${row.dictatedaction!""}",
	   		"termdictatedaction": "${row.termDictatedAction!""}",
	   		"vital_document": "${row.vitalDocument?c}",
	   		"designationclass": "${row.designationClass!""}",
	   		"classificationtype": "${row.classificationType!""}",
	   		"resealing": "${row.resealing!""}"
	   	}<#if row_has_next>,</#if>
	</#list>
]}

