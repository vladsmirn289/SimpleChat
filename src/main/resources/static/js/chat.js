let stompClient = null;
let username = null;
let userId = null

function connect() {
    console.log("connecting to chat...")
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);
        //stompClient.subscribe();
    });
}

function loadMessages(user1, user2) {
    $.ajax({
        type: "GET",
        url: "/message/" + user1 + "/" + user2,
        contentType: "application/json",
        dataType: "json",
        data: '',
        success: function(messages) {
            for (let i = 0; i < messages.length; i++) {
                let msg = messages[i];

                console.log(msg);

                let content = msg.content;
                let chatName = msg.sender;

                let firstPartOfDate = msg.createdOn.toString().split("T")[0].split("-");
                let secondPartOfDate = msg.createdOn.toString().split("T")[1].split(":");

                let year = firstPartOfDate[0];
                let month = firstPartOfDate[1];
                let day = firstPartOfDate[2];
                let hours = secondPartOfDate[0];
                let minutes = secondPartOfDate[1];

                let time = year + "." + month + "." + day + " " + hours + ":" + minutes;

                if(messages[i].recipient === username) {
                    $('.chat-box').append(
                        "<li class='chat-left'>\n" +
                        "    <div class='chat-avatar'>\n" +
                        "        <img src='https://www.bootdey.com/img/Content/avatar/avatar3.png' alt='Retail Admin'>\n" +
                        "        <div class='chat-name'>" + chatName + "</div>\n" +
                        "    </div>\n" +
                        "    <div class='chat-text' style='max-width: 50%'>" + content + "</div>\n" +
                        "    <div class='chat-hour'>" + time + " <span class='fa fa-check-circle'></span></div>\n" +
                        "</li>"
                    );
                } else {
                    $('.chat-box').append(
                        "<li class='chat-right'>\n" +
                        "    <div class='chat-hour'>" + time + " <span class='fa fa-check-circle'></span></div>\n" +
                        "    <div class='chat-text' style='max-width: 50%'>" + content + "</div>\n" +
                        "    <div class='chat-avatar'>\n" +
                        "        <img src='https://www.bootdey.com/img/Content/avatar/avatar3.png' alt='Retail Admin'>\n" +
                        "        <div class='chat-name'>" + chatName + "</div>\n" +
                        "    </div>\n" +
                        "</li>"
                    );
                }
            }
        }, error: function(error) {
            console.log(error);
        }
    });
}

function searchNewUsers() {
    $.ajax({
        type: "GET",
        url: "/user?search=" + $('#users_search_input').val(),
        contentType: "application/json",
        dataType: "json",
        data: '',
        success: function(users) {
            $('#user_search_ul').html("");
            let buttonClass = "";
            for (let i = 0; i < users.length; i++) {
                if (users[i].id == userId) {
                    continue;
                }

                if ($('#user_contacts').html().toString().indexOf(users[i].login) !== -1) {
                    buttonClass = "disabled='disabled'";
                }

                $('#user_search_ul').append(
                    "<li class='person d-flex align-items-center'>\n" +
                    "    <input type='hidden' id='contact_id' value='" + users[i].id + "'/>" +
                    "    <div class='user'>\n" +
                    "        <img src='https://www.bootdey.com/img/Content/avatar/avatar1.png' alt='Retail Admin'>\n" +
                    "        <span class='status offline'></span>\n" +
                    "    </div>\n" +
                    "    <div class='flex-grow-1 my-auto'>" +
                    "        <p class='name-time'>\n" +
                    "            <span class='name'>" + users[i].login + "</span>\n" +
                    "        </p>\n" +
                    "    </div>" +
                    "    <button class='btn btn-primary' " + buttonClass + " id='addNewFriend_" + users[i].id + "'>Add</button>" +
                    "</li>"
                )
            }

            $('[id^=addNewFriend_]').click(function () {
                let idName = $(this).attr('id').toString();
                addNewUserToFriends(idName.split("_")[1], $(this));
            })
        }, error: function(error) {
            console.log(error);
        }
    });
}

function searchFriends() {
    $.ajax({
        type: "GET",
        url: "/user/friends/" + userId,
        contentType: "application/json",
        data: '',
        success: function(data) {
            let friends = data;
            $('#user_contacts').html("");
            for (let i = 0; i < friends.length; i++) {
                $('#user_contacts').append(
                    "<li class='person person_contacts'>\n" +
                    "    <div class='user'>\n" +
                    "        <img src='https://www.bootdey.com/img/Content/avatar/avatar1.png' alt='Retail Admin'>\n" +
                    "        <span class='status offline'></span>\n" +
                    "    </div>\n" +
                    "    <p class='name-time'>\n" +
                    "        <span class='name'>" + friends[i].login + "</span>\n" +
                    "    </p>\n" +
                    "</li>"
                )
            }

            $('.person_contacts').click(function () {
                disableChatBoxElements();

                $('.active-user').removeClass('active-user');
                $(this).addClass('active-user');

                $('#chat_with_user').show();
                let friend = $(this).children('.name-time').children('.name').html();
                $('.name').html(friend);

                loadMessages(username, friend);
            });
        }, error: function(error) {
            console.log(error);
        }
    });
}

function addNewUserToFriends(friend_id, button) {
    $.ajax({
        type: "POST",
        url: "/user/" + userId + "/addFriend/" + friend_id,
        data: {'_csrf': $('meta[name="csrf-token"]').attr('content')},
        success: function() {
            button.prop("disabled", true);
        }, error: function(error) {
            console.log(error);
        }
    });

    setTimeout(searchFriends, 1000);
}

function disableChatBoxElements() {
    $('#chat_with_user').hide();
    $('.chat-box').html("");

    $('#user_search').hide();
    $('#user_search_ul').html("");

    $('.active-user').removeClass('active-user');
}

$(function () {
    disableChatBoxElements();
    $('#user_search').show();

    username = $('#login_name').val();
    connect();
    userId = $('#user_id').val();

    let currDate = new Date();
    $('#calendar_date').html(currDate.toDateString());

    searchFriends();

    $('#users_search').click(function () {
        searchNewUsers();
    });

    $('#user_search_dropdown').click(function () {
        disableChatBoxElements();
        $('#user_search').show();
    });

    $('#refresh_users').click(function () {
        searchFriends();
    });
});