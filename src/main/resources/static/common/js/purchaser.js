var app = angular.module('homepageApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);

initToastr();

app.controller('homepageController', function($rootScope, $http, $filter) {
	var home = this;
	var categories;
	getUnreviewedAmount($http,$rootScope,$filter);
	connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope, $filter);

	// Set time out for avoiding to get the key when using $filter('translate') filter.
	setTimeout(function(){
		// get all categories from database
		$http({
	        method: 'GET',
	        url: '/proxy/v1/assets/categories',
	    }).then(function(result) {
	    	categories = result.data.data.content;
	    	if(categories.length < 1){
	    		$rootScope.emptyContentError = true;
	    	}
	        home.categories = categories;
	        if(home.categories.length<1){
	        	home.emptyContentsMessage = true;
	        }

	    }).catch(function(result) {
	    	/*console.info(result);*/
	    	displayLoadError(result,$rootScope,$filter,$http,false,'categories');
	    	home.categoriesLoadError = true;
	    });
	}, 500);

	// To avoid displaying page without styles due to the slow loading of CSS files
	setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});