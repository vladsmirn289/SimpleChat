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
                    "<li class='person'>\n" +
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