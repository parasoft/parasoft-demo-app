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
	
	home.loadStyle = function(index){
		var row = $rootScope.isShowAdvertising ? Math.ceil((index + 1) / 3) : Math.ceil((index + 1) / 4);
		var line = $rootScope.isShowAdvertising ? index % 3 + 1 : index % 4 + 1;
		
		return{
			"top": 348 * (row-1) + "px",
			"left": 295 * (line - 1) + "px",
			"position": "absolute"
		};
	}
	
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
	        
	        //The amount of rows
	        var row = $rootScope.isShowAdvertising ? Math.ceil(categories.length/3) : Math.ceil(categories.length/4);
	        //If the number of rows exceeds three, the total height should add item's height
	        changeFooterHeight($rootScope.industry,row);
	        
	    }).catch(function(result) {
	    	console.info(result);
	    	displayLoadError(result,$rootScope,$filter,$http,false,'categories');
	    	home.categoriesLoadError = true;
	    });
	}, 500);

	function changeFooterHeight(industry,row) {

		if(!$rootScope.isShowAdvertising){
			$rootScope.footerHeight = {
		 		 "top" : "1080px"
		 	}
		    		
		    if(row < 2){
		 		$rootScope.footerHeight = {
		 			"top" : "874px"
		 		}
		 	}
			
			home.errorMessages = {
				"width" : "1200px"
			}
		}
		    	
		if(row > 2){
			$rootScope.footerHeight = {
		 		"top" : 358 * (row - 3) + 1423 + "px"
		 	}
		}
	}
		
	// To avoid displaying page without styles due to the slow loading of CSS files
	setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});