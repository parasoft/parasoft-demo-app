var app = angular.module('pdaApp', ['pascalprecht.translate']);
setLocale(app);

app.controller('error_controller', function($rootScope,$http) {
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
    }, function errorCallback(response) {});

    setTimeout(function(){ angular.element("body").css("visibility","visible") }, 500);
});