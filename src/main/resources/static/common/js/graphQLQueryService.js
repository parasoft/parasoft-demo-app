angular
    .module('demoAdminApp')
        .factory('GraphQLQueryService', function($http) {
            var instance = {};
            // categories
            instance.getCategories = function(success, error) {
                $http({
                    method: 'POST',
                    url: '/graphql',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    data: {
                        "query": "{getCategories {totalElements,totalPages,size,number,numberOfElements,sort,content{id,name,description,image}}}"
                    }
                }).then(function(result) {
                    success(result.data.data.getCategories);
                }).catch(function(result) {
                    error(result);
                });
            }
        return instance;
    });