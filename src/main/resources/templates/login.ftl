<#import "common.ftl" as c>

<@c.commonPage '<link href="/css/login.css" rel="stylesheet">'>
    <script src="/js/login.js" type="text/javascript"></script>

    <div class="form">

        <ul class="tab-group">
            <li class="tab active"><a href="#signup">Sign Up</a></li>
            <li class="tab"><a href="#login_tab">Log In</a></li>
        </ul>

        <div class="tab-content">
            <div id="signup">

                <#if errorMessage??>
                    <div class="bg-danger text-white text-center">
                        <h3>${errorMessage}</h3>
                    </div>
                </#if>

                <h1>Sign Up for Free</h1>

                <form action="/register" name="newUser" method="post">

                    <div class="field-wrap">
                        <label for="login">
                            Login<span class="req">*</span>
                        </label>
                        <input type="text" id="login" name="login" required="required" autocomplete="off" />
                    </div>

                    <div class="field-wrap">
                        <label for="email">
                            Email Address
                        </label>
                        <input type="email" id="email" name="email" autocomplete="off"/>
                    </div>

                    <div class="field-wrap">
                        <label for="pass">
                            Set A Password<span class="req">*</span>
                        </label>
                        <input type="password" id="pass" name="password" required="required" autocomplete="off"/>
                    </div>

                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>

                    <button type="submit" class="button button-block">Get Started</button>

                </form>

            </div>

            <div id="login_tab">

                <#if springMacroRequestContext.queryString?? && springMacroRequestContext.queryString?contains("error")>
                    <div class="bg-danger text-white text-center">
                        <h3>Wrong login or password!</h3>
                    </div>
                </#if>

                <h1>Welcome Back!</h1>

                <form action="/login" method="post">

                    <div class="field-wrap">
                        <label for="username">
                            Login<span class="req">*</span>
                        </label>
                        <input type="text" id="username" name="username" required="required" autocomplete="off"/>
                    </div>

                    <div class="field-wrap">
                        <label for="password">
                            Password<span class="req">*</span>
                        </label>
                        <input type="password" id="password" name="password" required="required" autocomplete="off"/>
                    </div>

                    <p class="forgot"><a href="#">Forgot Password?</a></p>

                    <input type="hidden" name="_csrf" value="${_csrf.token}"/>

                    <button type="submit" class="button button-block">Log In</button>

                </form>

            </div>

        </div><!-- tab-content -->

    </div> <!-- /form -->
</@c.commonPage>