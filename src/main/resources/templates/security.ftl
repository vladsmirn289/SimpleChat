<#assign
    isExist = Session.SPRING_SECURITY_CONTEXT??
>

<#if isExist>
    <#assign
        client = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        login_name = client.getUsername()
    >
<#else>
    <#assign
        login_name = "anonymous"
    >
</#if>