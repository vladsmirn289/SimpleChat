<#macro commonPage links...>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
        <meta name="csrf-token" content="${_csrf.token}">

        <title>Chat</title>

        <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/4.6.0/css/bootstrap.min.css"/>
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

        <#if links??>
            <#list links as l>
                ${l}
            </#list>
        </#if>
    </head>

    <body>
        <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
        <script src="/webjars/bootstrap/4.6.0/js/bootstrap.min.js"></script>
        <script src="/webjars/popper.js/2.5.4/umd/popper.min.js"></script>

        <#nested>
    </body>

    </html>
</#macro>