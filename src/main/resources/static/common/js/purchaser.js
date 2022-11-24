var app = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);

initToastr();

app.controller('homepageController', function($rootScope, $http, $filter, graphQLService) {
    var home = this;
    var categories;
    getUnreviewedAmount($http,$rootScope,$filter,graphQLService);
    connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope, $filter,null,null,graphQLService);

    // Set time out for avoiding to get the key when using $filter('translate') filter.
    setTimeout(function(){
        // get all categories from database
        let success = (data) => {
            categories = data.content;
            if(categories.length < 1){
                $rootScope.emptyContentError = true;
            }
            home.categories = categories;
            if(home.categories.length<1){
                home.emptyContentsMessage = true;
            }
        }
        let error = (data, endpointType) => {
            console.info(data);
            displayLoadError(data,$rootScope,$filter,$http,false,endpointType);
            home.categoriesLoadError = true;
        }

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getCategories(success, (data) => {error(data, "graphQL")});
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/categories',
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, "categories");
            });
        }
    }, 500);

    // To avoid displaying page without styles due to the slow loading of CSS files
    setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});