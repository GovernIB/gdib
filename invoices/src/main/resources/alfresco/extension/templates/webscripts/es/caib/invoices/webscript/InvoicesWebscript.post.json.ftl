<#escape x as jsonUtils.encodeJSONString(x)>
{
	"status":
		{
			"code": <#if code??>"${code}"<#else>""</#if>,
			"message":	<#if message??>"${message}"<#else>""</#if>
		}
}
</#escape>