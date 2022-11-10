angular
    .module('pdaApp')
        .factory('makeQueryCallService', function($http) {
            var instance = {};
            var formatError = (response) => {
                return {
                    data: {
                        data: response.data.errors[0].extensions.data,
                        message: response.data.errors[0].message
                    },
                    status: response.data.errors[0].extensions.statusCode,
                    config: response.config
                };
            };
            var makeQueryCall = function(query, success, error) {
                $http({
                    method: 'POST',
                    url: '/graphql',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    data: query
                }).then(function(response) {
                    if (response.data.errors) {
                        error(formatError(response));
                    } else {
                        success(response);
                    }
                }).catch(function(response) {
                    // TODO toaster.error
                });
            };
            // categories
            instance.getCategories = function(success, error) {
                var query = {"query": "{getCategories{totalElements,totalPages,size,number,numberOfElements,sort,content{id,name,description,image}}}"}
                makeQueryCall(query, function(response) {
                    success(response.data.data.getCategories);
                }, error);
            }
            return instance;
        });