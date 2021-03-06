let stompClient = null;
let username = null;
let userId = null

function connect() {
    console.log("connecting to chat...")
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);
        stompClient.subscribe("/topic/messages/" + username, function (response) {
            let msg = JSON.parse(response.body);
            processMessage(msg);
        });

        stompClient.subscribe("/topic/update/" + username, function (response) {
            updateMessages(response.body);
        });

        stompClient.subscribe("/topic/userStatus/" + username, function (response) {
            let user = JSON.parse(response.body);
            changeUserStatus(user);
        });
    });
}

function processMessage(message) {
    console.log("Received message from " + message.sender);

    if ($('#user_contacts').html().toString().indexOf(message.sender) === -1 && message.sender !== username) {
        console.log("Updating friends");
        searchFriends();
    }

    console.log(message.sender + " " + username + " " + $('#selected_option').html() + " " + message.sender);
    if ((message.sender === username) ||
        ($('#selected_option').html() === message.sender)) {
        console.log("Rendering message");
        renderMessage(message);
    } else {
        console.log("Updating badge")
        incrementBadge(message.sender);
    }
}

function updateMessages(recipient) {
    console.log("Updating messages " + recipient);
    if ($('#selected_option').html() === recipient) {
        loadMessages(username, recipient);
    }
}

function searchFriends() {
    $.ajax({
        type: "GET",
        url: "/user/friends/" + userId,
        contentType: "application/json",
        data: '',
        success: function(data) {
            let friends = data;
            let status;
            $('#user_contacts').html("");
            for (let i = 0; i < friends.length; i++) {
                if (friends[i].online) {
                    status = "online";
                } else {
                    status = "offline";
                }

                $('#user_contacts').append(
                    "<li class='person person_contacts d-flex align-items-center'>\n" +
                    "    <div class='user'>\n" +
                    "        <img src='https://www.bootdey.com/img/Content/avatar/avatar1.png' alt='Retail Admin'>\n" +
                    "        <span class='status " + status + "'></span>\n" +
                    "    </div>\n" +
                    "    <div class='flex-grow-1 my-auto' id='flex-wrap'>\n" +
                    "        <p class='name-time'>\n" +
                    "            <span class='name'>" + friends[i].login + "</span>\n" +
                    "        </p>\n" +
                    "    </div>\n" +
                    "    <div class='badge bg-success'></div>\n" +
                    "</li>"
                )
            }

            $('.person_contacts').click(function () {
                disableChatBoxElements();

                $('.active-user').removeClass('active-user');
                $(this).addClass('active-user');

                $('#chat_with_user').show();
                let friend = $(this).children('#flex-wrap').children('.name-time').children('.name').html();
                $('#selected_option').html(friend);

                loadMessages(username, friend);
            });

            findUnreadMessages();
        }, error: function(error) {
            console.log(error);
        }
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
                renderMessage(messages[i]);
            }
        }, error: function(error) {
            console.log(error);
        }
    });
}

function renderMessage(msg) {
    let content = msg.content;
    let chatName = msg.sender;
    let status = msg.status;

    let firstPartOfDate = msg.createdOn.toString().split("T")[0].split("-");
    let secondPartOfDate = msg.createdOn.toString().split("T")[1].split(":");

    let year = firstPartOfDate[0];
    let month = firstPartOfDate[1];
    let day = firstPartOfDate[2];
    let hours = secondPartOfDate[0];
    let minutes = secondPartOfDate[1];

    let time = year + "." + month + "." + day + " " + hours + ":" + minutes;

    let checkCircle = "";
    if (status === 'READ') {
        checkCircle = "<span class='fa fa-check-circle'></span>";
    }
    if (msg.recipient === username && status === 'SENT') {
        checkCircle = "<span class='fa fa-check-circle'></span>";
        readMessage(msg);
        decrementBadge(msg.sender);
    }

    if(msg.recipient === username) {
        $('.chat-box').append(
            "<li class='chat-left'>\n" +
            "    <div class='chat-avatar'>\n" +
            "        <img src='https://www.bootdey.com/img/Content/avatar/avatar3.png' alt='Retail Admin'>\n" +
            "        <div class='chat-name'>" + chatName + "</div>\n" +
            "    </div>\n" +
            "    <div class='chat-text' style='max-width: 50%'><pre>" + content + "</pre></div>\n" +
            "    <div class='chat-hour'>" + time + "  " + checkCircle + "</div>\n" +
            "</li>"
        );
    } else {
        $('.chat-box').append(
            "<li class='chat-right'>\n" +
            "    <div class='chat-hour'>" + time + "  " + checkCircle + "</div>\n" +
            "    <div class='chat-text' style='max-width: 50%'><pre>" + content + "</pre></div>\n" +
            "    <div class='chat-avatar'>\n" +
            "        <img src='https://www.bootdey.com/img/Content/avatar/avatar3.png' alt='Retail Admin'>\n" +
            "        <div class='chat-name'>" + chatName + "</div>\n" +
            "    </div>\n" +
            "</li>"
        );
    }

    $('.chat-box-div').scrollTop($('.chat-box').height());
}

function readMessage(msg) {
    console.log("Change status of message with id " + msg.id);

    stompClient.send("/chat/message/read/" + msg.id, {});
}

function findUnreadMessages() {
    console.log("Finding unread messages for " + username);
    $.ajax({
        type: "GET",
        url: "/message/findUnread/" + username,
        contentType: "application/json",
        dataType: "json",
        data: '',
        success: function(messages) {
            for (let i = 0; i < messages.length; i++) {
                incrementBadge(messages[i].sender);
            }
        }, error: function(error) {
            console.log(error);
        }
    });
}

function changeUserStatus(user) {
    let login = user.login;
    console.log("Change user " + login + " status");
    let isOnline = user.online;
    let userElement = $('p.name-time > span.name:contains(' + login + ')');

    let statusElement = userElement.parent().parent().parent().children('div.user').children('span.status');
    if (isOnline) {
        statusElement.removeClass('offline');
        statusElement.addClass('online');
    } else {
        statusElement.removeClass('online');
        statusElement.addClass('offline');
    }
}

function incrementBadge(name) {
    let user = $('p.name-time > span.name:contains(' + name + ')');

    let counter = user.parent().parent().parent().children('div.badge').html();
    if (counter === '') {
        counter = 1;
    } else {
        ++counter;
    }

    user.parent().parent().parent().children('div.badge').html(counter);
}

function decrementBadge(name) {
    console.log("Decrementing badge");
    let user = $('p.name-time > span.name:contains(' + name + ')');

    let counter = user.parent().parent().parent().children('div.badge').html();
    if (counter === '') {
        counter = '';
    } else {
        --counter;
    }

    if (counter == 0) {
        counter = '';
    }
    user.parent().parent().parent().children('div.badge').html(counter);
}

function disableChatBoxElements() {
    $('#chat_with_user').hide();
    $('.chat-box').html("");

    $('#user_search').hide();
    $('#user_search_ul').html("");

    $('.active-user').removeClass('active-user');
}

function sendMessage(recipient, sender, text) {
    console.log("Sending message from " + sender + " to " + recipient);

    stompClient.send("/chat/message/" + recipient + "/" + sender, {}, JSON.stringify({
        recipient: recipient,
        sender: sender,
        content: text
    }));

    $('#message_text').val('');
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
                    "    <div class='flex-grow-1 my-auto'>\n" +
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
        $('#selected_option').html("User search");
        $('#user_search').show();
    });

    $('#refresh_users').click(function () {
        searchFriends();
    });

    $('#send_message').click(function () {
        let text = $('#message_text').val();
        let recipient = $('#selected_option').html();
        let sender = username;
        sendMessage(recipient, sender, text);
    });
});