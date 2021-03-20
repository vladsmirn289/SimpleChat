<#macro profile>

<div class="container light-style flex-grow-1 container-p-y">

    <h4 class="font-weight-bold py-3 mb-4">
        Account settings
    </h4>

    <div class="card overflow-hidden">
        <div class="bg-danger text-white text-center" id="profile_errors">

        </div>

        <div class="row no-gutters row-bordered row-border-light">
            <div class="col-md-3 pt-0">
                <div class="list-group list-group-flush account-settings-links">
                    <a class="list-group-item list-group-item-action active" data-toggle="list" href="#account-general">General</a>
                    <a class="list-group-item list-group-item-action" data-toggle="list" href="#account-change-password">Change password</a>
                    <a class="list-group-item list-group-item-action" data-toggle="list" href="#account-info">Info</a>
                    <a class="list-group-item list-group-item-action" data-toggle="list" href="#account-notifications">Notifications</a>
                </div>
            </div>
            <div class="col-md-9">
                <div class="tab-content">
                    <div class="tab-pane fade active show" id="account-general">

                        <div class="card-body media align-items-center">
                            <span id="avatar"></span>
                            <div class="media-body ml-4">
                                <label class="btn btn-outline-primary">
                                    Upload new photo
                                    <input type="file" name="avatarFile" id="change_avatar" class="account-settings-fileinput">
                                </label> &nbsp;

                                <div class="text-light small mt-1">Allowed JPG, GIF or PNG. Max size of 800K</div>
                            </div>
                        </div>
                        <hr class="border-light m-0">

                        <div class="card-body">
                            <div class="form-group">
                                <label class="form-label">Login</label>
                                <input type="text" name="login" id="change_login" class="form-control mb-1">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Name</label>
                                <input type="text" name="realName" id="change_realName" class="form-control">
                            </div>
                            <div class="form-group">
                                <label class="form-label">E-mail</label>
                                <input type="email" name="email" id="change_email" class="form-control mb-1">
                                <div class="alert alert-warning mt-3">
                                    Your email is not confirmed. Please check your inbox.<br>
                                    <a href="javascript:void(0)">Resend confirmation</a>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="tab-pane fade" id="account-change-password">
                        <div class="card-body pb-2">

                            <div class="form-group">
                                <label class="form-label">New password</label>
                                <input type="password" name="newPassword" id="newPassword" class="form-control">
                            </div>

                            <div class="form-group">
                                <label class="form-label">Repeat new password</label>
                                <input type="password" name="repeatPassword" id="repeatPassword" class="form-control">
                            </div>

                        </div>
                    </div>
                    <div class="tab-pane fade" id="account-info">
                        <div class="card-body pb-2">

                            <div class="form-group">
                                <label class="form-label">Bio</label>
                                <textarea class="form-control" name="change_bio" id="change_bio" rows="5"></textarea>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Birthday</label>
                                <input type="text" name="birthday" id="change_birthday" class="form-control">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Country</label>
                                <select name="country" id="change_country" class="custom-select">
                                    <option></option>
                                    <option>USA</option>
                                    <option>Canada</option>
                                    <option>UK</option>
                                    <option>Germany</option>
                                    <option>France</option>
                                    <option>Russian Federation</option>
                                </select>
                            </div>


                        </div>
                        <hr class="border-light m-0">
                        <div class="card-body pb-2">

                            <div class="form-group">
                                <label class="form-label">Phone</label>
                                <input type="text" name="phoneNumber" id="change_phoneNumber" class="form-control">
                            </div>

                        </div>

                    </div>
                    <div class="tab-pane fade" id="account-notifications">
                        <div class="card-body pb-2">

                            <h6 class="mb-4">Email</h6>

                            <div class="form-group">
                                <label class="switcher">
                                    <input type="checkbox" name="emailOffline" id="emailOffline" class="switcher-input">
                                    <span class="switcher-indicator">
                                      <span class="switcher-yes"></span>
                                      <span class="switcher-no"></span>
                                    </span>
                                    <span class="switcher-label">Email me when i offline and someone send me message</span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="text-right mt-3">
        <button type="submit" id="updatingInfo" class="btn btn-primary">Save changes</button>&nbsp;
    </div>

</div>

</#macro>