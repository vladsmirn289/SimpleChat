let EMAIL_REGEX = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

$(function () {
    $('#updatingInfo').click(function () {
        let formData = new FormData();
        let login = $('#change_login').val();
        let realName = $('#change_realName').val();
        let email = $('#change_email').val();
        let newPassword = $('#newPassword').val();
        let repeatPassword = $('#repeatPassword').val();
        let bio = $('#change_bio').val();
        let birthday = $('#change_birthday').val();
        let country = $('#change_country').val();
        let phoneNumber = $('#change_phoneNumber').val();
        let emailOffline = $('#emailOffline').is(":checked");
        let avatarFile = $('#change_avatar')[0].files[0];

        if (login == null || login.toString().length == 0) {
            console.log("Login cannot be empty");
        }
        formData.append('login', login);
        formData.append('realName', realName);
        if (email != null) {
            if (email.toString().length != 0 && !EMAIL_REGEX.test(email.toString())) {
                console.log("Email incorrect");
            }
            formData.append('email', email);
        }
        if (!(newPassword == null || newPassword.toString().length == 0)) {
            if (newPassword.toString() != repeatPassword.toString()) {
                console.log("Password mismatch");
            }
            formData.append('newPassword', newPassword);
            formData.append('repeatPassword', repeatPassword);
        }
        formData.append('bio', bio);
        formData.append('birthday', birthday);
        formData.append('country', country);
        formData.append('phoneNumber', phoneNumber);
        formData.append('emailOffline', emailOffline);
        if (avatarFile != null) {
            formData.append('avatarFile', avatarFile);
        }
        formData.append('_csrf', $('meta[name="csrf-token"]').attr('content'));

        $.ajax({
            url: '/user/changePersonalInfo',
            type: 'POST',
            contentType: false,
            processData: false,
            data: formData,
            success: function(messages) {
                $('#profile_errors').html('');
                for (let type in messages) {
                    $('#profile_errors').append(document.createTextNode(
                        messages[type]));
                    $('#profile_errors').append(document.createElement("br"));
                }
            }, error: function (error) {
                console.log(error);
            }
        });

        setTimeout(fillProfile, 1000);
    });
});