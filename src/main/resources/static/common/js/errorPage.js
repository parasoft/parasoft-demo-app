var app = angular.module('pdaApp', ['pascalprecht.translate']);
var KEYCLOAK_STATUS_CODE = angular.element("#keycloak_status_code").val();
setLocale(app);

app.controller('error_controller', function($rootScope,$http,$filter) {
    var error = this;

    //Get current industry
    $http({
        method: 'GET',
        url: '/v1/demoAdmin/currentPreferences'
    }).then(function successCallback(response) {
        var preferenceData = response.data.data;
        var industry = preferenceData.industryType;
        industry = industry.toLowerCase();
        $rootScope.industry = industry;
        error.keycloakStatusCode = KEYCLOAK_STATUS_CODE;
    }, function errorCallback(response) {});

    setTimeout(function(){ angular.element("body").css("visibility","visible") }, 500);
});