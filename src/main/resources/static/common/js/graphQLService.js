angular
    .module('pdaApp')
        .factory('graphQLService', function($http) {
            let instance = {};
            let formatError = (response) => {
                return {
                    data: {
                        data: response.data.errors[0].extensions.data,
                        message: response.data.errors[0].message
                    },
                    status: response.data.errors[0].extensions.statusCode,
                    config: response.config
                };
            };

            let makeCall = function(requestBody, success, error) {
                $http({
                    method: 'POST',
                    url: '/proxy/graphql',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    data: requestBody
                }).then(function(response) {
                    if (response.data.errors) {
                        error(formatError(response));
                    } else {
                        success(response);
                    }
                }).catch(function(response) {
                    error(response)
                });
            };
            // categories
            instance.getCategories = function(success, error) {
                let requestBody = {"query": "query GetCategories{getCategories{totalElements,totalPages,size,number,numberOfElements,sort,content{id,name,description,image}}}"}
                makeCall(requestBody, function(response) {
                    success(response.data.data.getCategories);
                }, error);
            }
            instance.deleteCategoryById = function(variables, success, error) {
                let requestBody = {"query": "mutation DeleteCategoryById($categoryId:Long!){deleteCategoryById(categoryId:$categoryId)}", "variables": variables}
                makeCall(requestBody, function(response) {
                    success(response.data.data.deleteCategoryById);
                }, error);
            }
            instance.getCategoryById = function(variables, success, error, selectionSet) {
                if(!selectionSet) {
                    selectionSet = "{id,name,description,image}";
                }
                let requestBody = {
                    "query": "query getCategoryById($categoryId:Long!){getCategoryById(categoryId:$categoryId)"+ selectionSet +"}",
                    "variables": variables
                }
                makeCall(requestBody, function(response) {
                    success(response.data.data.getCategoryById);
                }, error);
            }
            instance.addCategory = function(categoryData, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id,name,description,image}"
                }
                let requestBody = {
                    "query": "mutation AddCategory($categoryDTO: CategoryDTO!){addCategory(categoryDTO: $categoryDTO)" + selectionSet + "}",
                    "variables": { "categoryDTO": categoryData }
                }
                makeCall(requestBody, function(response) {
                    success(response.data.data.addCategory);
                }, error);
            }
            instance.updateCategory = function(variables, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id,name,description,image}"
                }
                let requestBody = {
                    "query": "mutation UpdateCategory($categoryId:Long!,$categoryDto:CategoryDTO!){updateCategory(categoryId:$categoryId,categoryDto:$categoryDto)" + selectionSet + "}",
                    "variables": variables
                }
                makeCall(requestBody, function(response) {
                    success(response.data.data.updateCategory);
                }, error);
            }
            // locations
            instance.getLocation = function(variables, success, error) {
                let requestBody = {"query": "query GetLocation($region:RegionType!){getLocation(region:$region){id,locationInfo,locationImage}}", "variables": variables}
                makeCall(requestBody, function(response) {
                    success(response.data.data.getLocation);
                }, error);
            }
            instance.getAllRegionTypesOfCurrentIndustry = function(success, error) {
                let requestBody = {"query": "query GetAllRegionTypesOfCurrentIndustry{getAllRegionTypesOfCurrentIndustry}"};
                makeCall(requestBody, function(response){
                    success(response.data.data.getAllRegionTypesOfCurrentIndustry);
                }, error);
            }
            // Orders
            instance.createOrder = function(variables, success, error, selectionSet) {
                if(!selectionSet) {
                    selectionSet = "{" +
                                    "id,orderNumber,requestedBy,status,reviewedByAPV,reviewedByPRCH,respondedBy," +
                                    "orderItems" +
                                    "{" +
                                        "id,name,description,image,itemId,quantity" +
                                    "}," +
                                    "region,location,orderImage,receiverId,eventId,eventNumber,submissionDate,approverReplyDate,comments" +
                                "}";
                }
                let requestBody = {
                    "query": "mutation CreateOrder($orderDTO:OrderDTO!){createOrder(orderDTO: $orderDTO)" + selectionSet + "}", "variables": variables}
                makeCall(requestBody, function(response) {
                    success(response.data.data.createOrder);
                }, error);
            }
            instance.getOrderByOrderNumber = function (variables, success, error, selectionSet) {
                if(!selectionSet) {
                    selectionSet = "{" +
                        "id,orderNumber,requestedBy,status,reviewedByAPV,reviewedByPRCH,respondedBy," +
                        "orderItems" +
                        "{" +
                            "id,name,description,image,itemId,quantity" +
                        "}," +
                        "region,location,orderImage,receiverId,eventId,eventNumber,submissionDate,approverReplyDate,comments" +
                        "}";
                }
                let requestBody = {"query": "query GetOrderByOrderNumber($orderNumber:String!){getOrderByOrderNumber(orderNumber:$orderNumber)" + selectionSet + "}", "variables": variables};
                makeCall(requestBody, function (response) {
                    success(response.data.data.getOrderByOrderNumber);
                }, error)
            }
            // items
            instance.getItems = function (variables, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{totalElements,totalPages,size,number,numberOfElements,sort,content{id,name,description,inStock,image,region,lastAccessedDate,categoryId}}";
                }
                let requestBody = {"query": "query GetItems($categoryId: Long, $regions: [RegionType], $searchString: String, $page: Int, $size: Int, $sort: [String])" +
                        "{getItems(categoryId: $categoryId, regions: $regions, searchString: $searchString, page: $page, size: $size, sort: $sort)" + selectionSet + "}", "variables": variables};
                makeCall(requestBody, function (response) {
                    success(response.data.data.getItems);
                }, error);
            }
            instance.addNewItem = function (variables, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id,name,description,inStock,image,region,lastAccessedDate,categoryId}";
                }
                let requestBody = {
                    "query": "mutation AddNewItem($itemsDTO: ItemsDTO!){ " +
                    "addNewItem(itemsDTO: $itemsDTO)" + selectionSet + "}",
                    "variables": {"itemsDTO": variables}
                };
                makeCall(requestBody, function(response) {
                    success(response.data.data.addNewItem);
                }, error);
            }
            //cartItems
            instance.getCartItems = function(success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id, userId, itemId, name, description, image, realInStock, quantity}"
                }
                let requestBody = {"query": "query GetCartItems{getCartItems" + selectionSet + "}"}
                makeCall(requestBody, function(response) {
                    success(response.data.data.getCartItems);
                }, error);
            }
            instance.removeCartItem = function(itemId, success, error) {
                let requestBody = {
                    "query": "mutation RemoveCartItem($itemId: Long!){removeCartItem(itemId: $itemId)}",
                    "variables": { "itemId": itemId }
                }
                makeCall(requestBody, function(response) {
                    success(response.data.data.removeCartItem);
                }, error);
            }
            instance.addItemInCart = function(variables, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id, userId, itemId, name, description, image, realInStock, quantity}"
                }
                let requestBody = {"query": "mutation AddItemInCart($shoppingCartDTO:ShoppingCartDTO!){addItemInCart(shoppingCartDTO:$shoppingCartDTO)" + selectionSet + "}", "variables": variables}
                makeCall(requestBody, function(response) {
                    success(response.data.data.addItemInCart);
                }, error);
            }
            // items
            instance.getItemByItemId = function (variables, success, error, selectionSet) {
                if (!selectionSet) {
                    selectionSet = "{id,name,description,inStock,image,region,lastAccessedDate,categoryId}";
                }
                let requestBody = {
                    "query": "query GetItemByItemId($itemId: Long!){getItemByItemId(itemId: $itemId)" + selectionSet + "}",
                    "variables": variables
                }
                makeCall(requestBody, function(response) {
                    success(response.data.data.getItemByItemId);
                }, error);
            }
            // customized call
            instance.makeGraphQLCall = makeCall;
            return instance;
        });