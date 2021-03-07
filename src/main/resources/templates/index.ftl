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

                                    </ul>
                                </div>
                            </div>
                            <div class="col-xl-8 col-lg-8 col-md-8 col-sm-9 col-9">

                                <div class="py-2 px-4 border-bottom d-none d-lg-block">
                                    <div class="d-flex align-items-center py-1">
                                        <div class="flex-grow-1 pl-3">
                                            <span class="name"><strong id="selected_option">User search</strong></span>
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