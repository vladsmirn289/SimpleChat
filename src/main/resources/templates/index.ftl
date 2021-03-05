<#import "common.ftl" as c>
<#include "security.ftl">

<@c.commonPage '<link href="/css/main.css" rel="stylesheet">'>
    <script type="text/javascript" src="/js/chat.js"></script>

    <div>
        <input type="hidden" id="login_name" value="${login_name}"/>
        <input type="hidden" id="user_id" value="${user_id}"/>
    </div>

    <div class="container">
        <!-- Page header start -->
        <div class="page-title">
            <div class="row gutters">
                <div class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
                    <h5 class="title">Chat App</h5>
                </div>
                <div class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
                    <div class="daterange-container">
                        <div class="date-range">
                            <div id="reportrange">
                                <i class="fa fa-calendar cal"></i>
                                <span class="range-text" id="calendar_date"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Page header end -->

        <!-- Content wrapper start -->
        <div class="content-wrapper">

            <!-- Row start -->
            <div class="row gutters">

                <div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">

                    <div class="card m-0">

                        <!-- Row start -->
                        <div class="row no-gutters">
                            <div class="col-xl-4 col-lg-4 col-md-4 col-sm-3 col-3">
                                <div class="users-container">
                                    <div class="chat-search-box">
                                        <div class="input-group">
                                            <input class="form-control" placeholder="Search">
                                            <div class="input-group-btn">
                                                <button type="button" id="friends_search" class="btn btn-info mr-1">
                                                    <i class="fa fa-search"></i>
                                                </button>
                                            </div>

                                            <button type="button" id="refresh_users" class="btn btn-info">
                                                <i class="fa fa-refresh"></i>
                                            </button>
                                        </div>
                                    </div>
                                    <ul class="users" id="user_contacts">
                                        <#--<li class="person" data-chat="person1">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                <span class="status busy"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Steve Bangalter</span>
                                                <span class="time">15/02/2019</span>
                                            </p>
                                        </li>
                                        <li class="person" data-chat="person1">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar1.png" alt="Retail Admin">
                                                <span class="status offline"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Steve Bangalter</span>
                                                <span class="time">15/02/2019</span>
                                            </p>
                                        </li>
                                        <li class="person active-user" data-chat="person2">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar2.png" alt="Retail Admin">
                                                <span class="status away"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Peter Gregor</span>
                                                <span class="time">12/02/2019</span>
                                            </p>
                                        </li>
                                        <li class="person" data-chat="person3">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                <span class="status busy"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Jessica Larson</span>
                                                <span class="time">11/02/2019</span>
                                            </p>
                                        </li>
                                        <li class="person" data-chat="person4">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar4.png" alt="Retail Admin">
                                                <span class="status offline"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Lisa Guerrero</span>
                                                <span class="time">08/02/2019</span>
                                            </p>
                                        </li>
                                        <li class="person" data-chat="person5">
                                            <div class="user">
                                                <img src="https://www.bootdey.com/img/Content/avatar/avatar5.png" alt="Retail Admin">
                                                <span class="status away"></span>
                                            </div>
                                            <p class="name-time">
                                                <span class="name">Michael Jordan</span>
                                                <span class="time">05/02/2019</span>
                                            </p>
                                        </li>-->
                                    </ul>
                                </div>
                            </div>
                            <div class="col-xl-8 col-lg-8 col-md-8 col-sm-9 col-9">

                                <div class="py-2 px-4 border-bottom d-none d-lg-block">
                                    <div class="d-flex align-items-center py-1">
                                        <#--<div class="selected-user">
                                            <span class="name">Settings</span>
                                            &lt;#&ndash;<span>To: <span class="name">Emily Russell</span></span>&ndash;&gt;
                                        </div>-->
                                        <div class="flex-grow-1 pl-3">
                                            <span class="name"><strong id="selected_option">User search</strong></span>
                                            <#--<div class="text-muted small"><em>Typing...</em></div>-->
                                        </div>

                                        <div class="dropdown">
                                            <button class="btn btn-light border btn-lg px-3 dropdown-toggle"
                                                    type="button" id="dropdownMenuButton"
                                                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                <svg xmlns="http://www.w3.org/2000/svg"
                                                     width="24" height="24" viewBox="0 0 24 24"
                                                     fill="none" stroke="currentColor" stroke-width="2"
                                                     stroke-linecap="round" stroke-linejoin="round"
                                                     class="feather feather-more-horizontal feather-lg">

                                                    <circle cx="12" cy="12" r="1"></circle>
                                                    <circle cx="19" cy="12" r="1"></circle>
                                                    <circle cx="5" cy="12" r="1"></circle>

                                                </svg>
                                            </button>

                                            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                                                <a class="dropdown-item" id="user_search_dropdown" href="#">User search</a>
                                                <a class="dropdown-item" id="profile_dropdown" href="#">Profile</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="chat-box-div" id="user_search">
                                    <div class="chat-search-box">
                                        <div class="input-group">
                                            <input class="form-control" id="users_search_input" placeholder="Search"/>
                                            <div class="input-group-btn">
                                                <button type="button" id="users_search" class="btn btn-info">
                                                    <i class="fa fa-search"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    <ul class="users" id="user_search_ul"></ul>
                                </div>

                                <div class="chat-container" id="chat_with_user">
                                    <div class="chat-box-div">

                                        <ul class="chat-box chatContainerScroll">

                                        </ul>
                                    </div>
                                    <#--
<li class="chat-left">
    <div class="chat-avatar">
        <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
        <div class="chat-name">Russell</div>
    </div>
    <div class="chat-text">Hello, I'm Russell.
        <br>How can I help you today?</div>
    <div class="chat-hour">08:55 <span class="fa fa-check-circle"></span></div>
</li>
<li class="chat-right">
    <div class="chat-hour">08:56 <span class="fa fa-check-circle"></span></div>
    <div class="chat-text">Hi, Russell
        <br> I need more information about Developer Plan.</div>
    <div class="chat-avatar">
        <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
        <div class="chat-name">Sam</div>
    </div>
</li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Are we meeting today?
                                                    <br>Project has been already finished and I have results to show you.</div>
                                                <div class="chat-hour">08:57 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                            <li class="chat-right">
                                                <div class="chat-hour">08:59 <span class="fa fa-check-circle"></span></div>
                                                <div class="chat-text">Well I am not sure.
                                                    <br>I have results to show you.</div>
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar5.png" alt="Retail Admin">
                                                    <div class="chat-name">Joyse</div>
                                                </div>
                                            </li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">The rest of the team is not here yet.
                                                    <br>Maybe in an hour or so?</div>
                                                <div class="chat-hour">08:57 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                            <li class="chat-right">
                                                <div class="chat-hour">08:59 <span class="fa fa-check-circle"></span></div>
                                                <div class="chat-text">Have you faced any problems at the last phase of the project?</div>
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar4.png" alt="Retail Admin">
                                                    <div class="chat-name">Jin</div>
                                                </div>
                                            </li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Actually everything was fine.
                                                    <br>I'm very excited to show this to our team.</div>
                                                <div class="chat-hour">07:00 <span class="fa fa-check-circle"></span></div>
                                            </li>

                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Actually everything was fine.
                                                    <br>I'm very excited to show this to our team.</div>
                                                <div class="chat-hour">07:00 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Actually everything was fine.
                                                    <br>I'm very excited to show this to our team.</div>
                                                <div class="chat-hour">07:00 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Actually everything was fine.
                                                    <br>I'm very excited to show this to our team.</div>
                                                <div class="chat-hour">07:00 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                            <li class="chat-left">
                                                <div class="chat-avatar">
                                                    <img src="https://www.bootdey.com/img/Content/avatar/avatar3.png" alt="Retail Admin">
                                                    <div class="chat-name">Russell</div>
                                                </div>
                                                <div class="chat-text">Actually everything was fine.
                                                    <br>I'm very excited to show this to our team.</div>
                                                <div class="chat-hour">07:00 <span class="fa fa-check-circle"></span></div>
                                            </li>
                                        -->
                                    <div class="input-group mt-3 mb-0">
                                        <textarea class="form-control mr-1" id="message_text"
                                                  rows="3" placeholder="Type your message here..."></textarea>
                                        <div class="input-group-btn my-auto">
                                            <button type="button" id="send_message" class="btn btn-info form-control">
                                                <i class="fa fa-paper-plane"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- Row end -->
                    </div>

                </div>

            </div>
            <!-- Row end -->

        </div>
        <!-- Content wrapper end -->
    </div>
</@c.commonPage>