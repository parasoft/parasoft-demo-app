var loginApp = angular.module('loginApp', ['pascalprecht.translate']);

setLocale(loginApp);
//initialize controller of import page
initHeaderController(loginApp);
initProductBuildInfo(loginApp);

loginApp.controller('loginController', function($rootScope, $location, $window){
	var login = this;
	login.credentials = {};
	login.onSubmit = onSubmit;
	
	function onSubmit() {
		var token = $window.btoa(login.credentials.username + ':' + login.credentials.password);
		$window.localStorage.setItem('userToken', token);
	}

	$rootScope.isShowAccount = false;
	$rootScope.isShowOtherButtons = false;
	login.isError = $location.absUrl().indexOf('error') != -1;
	localStorage.setItem("removeRegionFilterInCookie",true);
	
	//To avoid displaying page without styles due to the slow loading of CSS files
	setTimeout(function(){ $("body").css("visibility","visible") }, 500);
 });