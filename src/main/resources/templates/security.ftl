<#assign
    isExist = Session.SPRING_SECURITY_CONTEXT??
>

<#if isExist>
    <#assign
        client = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        login_name = client.getUsername()
        user_id = client.getId()
    >
<#else>
    <#assign
        login_name = "anonymous"
        user_id = -1
    >
</#if>