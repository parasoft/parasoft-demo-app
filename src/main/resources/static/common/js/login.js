var app = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controller of import page
initHeaderController(app);
initProductBuildInfo(app);

app.controller('loginController', function($rootScope, $location, $window, $http){
    var login = this;
    login.credentials = {};
    login.onSubmit = onSubmit;

    function onSubmit() {
        let loginFormData = new FormData();
        loginFormData.append('username', login.credentials.username);
        loginFormData.append('password', login.credentials.password);

        $http({
            method: 'POST',
            url: '/v1/login',
            data: loginFormData,
            headers: {'Content-Type': undefined},
        }).then(function(result) {
            login.isError = false;

            var token = $window.btoa(login.credentials.username + ':' + login.credentials.password);
            $window.localStorage.setItem('userToken', token);

            $window.location.href = "/";
        }).catch(function(error) {
            login.isError = true;
        });
    }

    $rootScope.isShowSettingButton = false;
    $rootScope.isShowRequisitionButton = false;
    $rootScope.isShowRequisitionRequestButton = false;
    $rootScope.isShowAccount = false;
    localStorage.setItem("removeRegionFilterInCookie",true);

    login.forgotPassword = () => {
        $http({
            method: 'GET',
            url: '/v1/forgotPassword'
        }).then(function(result) {
            login.primaryUsersInformation = result.data.data;
        }).catch(function(error) {
            // can not reach here
            angular.noop();
        });
    };

    //To avoid displaying page without styles due to the slow loading of CSS files
    setTimeout(function(){ $("body").css("visibility","visible") }, 500);
 });