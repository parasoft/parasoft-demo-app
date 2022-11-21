var app = angular.module('pdaApp', ['pascalprecht.translate','ngCookies']);

setLocale(app);
//Initialize controllers of import page
initImportPageControllers(app);
initToastr();

app.controller('categoryController', function($rootScope, $http, $location, $filter, $interval, $cookies, graphQLService) {
    var category = this;
    var items;
    var categoryId = $location.absUrl().substr($location.absUrl().lastIndexOf("/")+1);
    category.categoryId = categoryId;
    category.sort="name";
    let getItemsSelectionSet = "{content{id,name,description,image}}"
    getUnreviewedAmount($http,$rootScope,$filter);
    connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope, $filter);

    //When the industry is changed, remove the old region filter cookie
    if(localStorage.getItem("removeRegionFilterInCookie")){
        $cookies.remove("regionFilter");
        localStorage.removeItem("removeRegionFilterInCookie");
    }

    // Set time out for avoiding getting the key when using $filter('translate') filter.
    setTimeout(function(){
        //Get regions from database
        $http({
            method: 'GET',
            url: '/proxy/v1/locations/regions',
        }).then(function(result) {
            var regions = result.data.data;
            category.regions = regions;
        },function error(result){
            console.info(result);
            handleRegionError(result,category,$rootScope,$filter,$http);
        }).catch(function(result) {
            console.info(result);
            handleRegionError(result,category,$rootScope,$filter,$http);
        });

        //Obtain the checked options from cookie
        var checkedRegions = $cookies.get("regionFilter");
        checkedRegions = checkedRegions === 'undefined' ? null : checkedRegions;
        var regionFilterDisabled = checkedRegions === "" || checkedRegions === null || checkedRegions === undefined;

        if(!regionFilterDisabled){
            checkedRegions = checkedRegions.split(",");
            category.checkedRegions = checkedRegions;
        }

        let getItemsSuccessfully = (data) => {
            items = data.content;
            category.items = items;

            if(items.length === 0){
                if(checkedRegions) {
                    category.showNoResultText = true;
                } else {
                    $rootScope.emptyContentError = true;
                    category.emptyContentsMessage = true;
                }
            }
        };

        let failToGetItems = (data, endpointType) => {
            category.checkedRegions = null;
            console.info(data);
            displayLoadError(data,$rootScope,$filter,$http,false,endpointType);
            category.itemsLoadError = true;
        }

        let getItemsParams = {
            categoryId: categoryId,
            regions: checkedRegions
        }
        //Get all items from database
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getItems(getItemsParams, getItemsSuccessfully,
                (data) => {failToGetItems(data, "graphQL")}, getItemsSelectionSet);
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/items',
                params: getItemsParams,
            }).then(function(result) {
                getItemsSuccessfully(result.data.data);
            }).catch(function(result) {
                failToGetItems(result, "items");
            })
        }

        //Get category by id
        let success = (data) => {
            category.title = data.name;
        }
        let error = (result, endpointType) => {
            console.info(result);
            displayLoadError(result, $rootScope, $filter, $http, true, endpointType);
        }

        let getCategoryByIdParams = {"categoryId": categoryId};
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getCategoryById(getCategoryByIdParams, success, (data) => {error(data, "graphQL")}, "{name}");
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/categories/' + categoryId,
            }).then(function (result) {
                success(result.data.data);
            }).catch(function (result) {
                error(result, "categories");
            });
        }

        function handleRegionError(result,category,$rootScope,$filter,$http){
            displayLoadError(result,$rootScope,$filter,$http,false,'locations');
            category.regionsLoadError = true;
        }
    }, 500);

    category.openRequisitionDetail = function(item){
        category.loadingAnimation = true;
        category.showQuantity = false;
        category.remainingNumber = 1;
        const itemId = item.id;
        category.currentItem = {
            id: itemId,
            name: item.name,
            description: item.description,
            image: item.image
        };
        category.showCategoryRequisitionDetail = true;

        //Get cartItem by item id
        $http({
            method: 'GET',
            url: '/proxy/v1/cartItems/' + itemId
        }).then(function(result) {
            var cartItem = result.data.data;
            category.currentItem.inStock = cartItem.realInStock;
            category.currentItem.inRequisition = cartItem.quantity;
            checkInventory(cartItem.realInStock,itemId,cartItem.quantity);
            $interval(function(){category.loadingAnimation = false;category.showQuantity = true;},500,1);
        }).catch(function(result) {
            category.currentItem.inRequisition = 0;
            console.info(result);
            let success = (data) => {
                category.currentItem.inStock = data.inStock;
                checkInventory(data.inStock,itemId,0);
            };
            let error = (data) => {
                console.info(data);
            };
            let param = {"itemId": itemId};

            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                let selectionSet = "{inStock}"
                graphQLService.getItemByItemId(param, success, (data) => {error(data, "graphQL")}, selectionSet);
            } else {
                $http({
                    method: 'GET',
                    url: '/proxy/v1/assets/items/' + itemId,
                }).then(function(result) {
                    success(result.data.data);
                }).catch(function(result) {
                    error(result, 'items')
                });
            }

            $interval(function(){category.loadingAnimation = false;category.showQuantity = true;},500,1);
        });
    }

    category.closeRequisitionDetail = function(index){
        closeRequisitionDetail(index);
    }

    category.addItemToRequisition = function(id,itemNum,inventory,quantity){
        quantity = quantity === undefined ? 0 : quantity;
        var total = Number(quantity) + Number(itemNum);
        if(total > inventory){
            toastrService().error($filter('translate')('ADD_TO_CART_FAIL')+$filter('translate')('EXCEED_INVENTORY_ERROR'));
            return;
        }

        let params = {itemId:id,itemQty:itemNum};
        let success = (data) => {
            closeRequisitionDetail(id);
            //Update shopping cart items
            loadShoppingCartItemQuantity($rootScope,$http,$filter,graphQLService);
            //If the requisition bar is visible, it should be closed after adding successfully
            //This is to avoid some format errors
            angular.element("#requisition_cross").click();
            toastr.success($filter('translate')('ADD_TO_CART_SUCCESS'));
        }
        let error = (data, endpointType) => {
            console.info(data);
            displayLoadError(data,$rootScope,$filter,$http,true,endpointType);
        }

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.addItemInCart({"shoppingCartDTO": params}, success, (data) => {error(data, "graphQL")}, "{quantity}");
        } else {
            $http({
                method: 'POST',
                url: '/proxy/v1/cartItems',
                data: params,
                headers: {'Content-Type': 'application/json'}
            }).then(function(result) {
                success(result.data.data);
            }).catch(function(result) {
                error(result, "cart");
            });
        }
    }

    category.itemNumMinus = function(itemNum){
        clearPlusDisabled();
        if(itemNum - 1 < 2){
            setMinusDisabled();
            return 1;
        }else{
            return itemNum - 1;
        }
    }

    category.itemNumPlus = function(itemNum, inventory, quantity){
        clearMinusDisabled();
        if(itemNum < inventory - quantity - 1){
            return itemNum + 1;
        }else{
            setPlusDisabled();
            return inventory - quantity;
        }
    }

    category.calItemNum = function(itemNum, inventory, quantity){
        if(inventory - quantity <= 0){
            return 0;
        }else if(itemNum < 2){
            setMinusDisabled();
            clearPlusDisabled();
            return 1;
        }else if(itemNum < inventory - quantity){
            clearPlusDisabled();
            clearMinusDisabled();
            return itemNum;
        }else{
            setPlusDisabled();
            clearMinusDisabled();
            return inventory - quantity;
        }
    }

    category.search = function() {
        let params = angular.element('.category_filter_container').serializeJSON();
        let searchSucceeded = (data) => {
            items = data.content;
            category.items = items;
            var checkedRegions = params.regions;

            //Save checked region options to cookie
            $cookies.put("regionFilter", checkedRegions);

            if (items.length === 0 && !$rootScope.emptyContentError) {
                category.showNoResultText = true;
            } else {
                category.showNoResultText = false;
            }
        };

        let searchFailed = (data, endpointType) => {
            console.info(data);
            displayLoadError(data, $rootScope, $filter, $http, true, endpointType);
        };

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.getItems(params, searchSucceeded,
                (data) => {searchFailed(data, "graphQL")}, getItemsSelectionSet);
        } else {
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/items',
                params: params,
            }).then(function(result) {
                searchSucceeded(result.data.data);
            }).catch(function(result) {
               searchFailed(result, "items");
            });
        }
    }

    function closeRequisitionDetail(index){
        category.showCategoryRequisitionDetail = false;
        category.remainingNumber = 1;
        clearPlusDisabled();
        setMinusDisabled();
    }

    //Make the plus button disabled in requisition detail part
    function setPlusDisabled(){
        category.plusDisabled = {
            "opacity" : ".35",
            "cursor" : "not-allowed"
        };
        category.plusBtnDisabled = true;
    }

    //Make the minus button disabled in requisition detail part
    function setMinusDisabled(){
        category.minusDisabled = {
            "opacity" : ".35",
            "cursor" : "not-allowed"
        };
        category.minusBtnDisabled = true;
    }

    //Make 'add to requisition' button disabled in requisition detail part
    function setAddToBtnDisabled(){
        category.btnDisabled = {
            "opacity" : ".35",
            "cursor" : "not-allowed"
        };
        category.addToBtnDisabled = true;
    }

    //Clear the plus button disabled in requisition detail part
    function clearPlusDisabled(){
        category.plusDisabled = {};
        category.plusBtnDisabled = false;
    }

    //Clear the minus button disabled in requisition detail part
    function clearMinusDisabled(){
        category.minusDisabled = {};
        category.minusBtnDisabled = false;
    }

    //Clear 'add to requisition' button disabled in requisition detail part
    function clearAddToBtnDisabled(){
        category.btnDisabled = {};
        category.addToBtnDisabled = false;
    }

    function checkInventory(inventory,id,quantity){
        if(inventory === 0 || inventory - quantity <= 0){
            category.remainingNumber = 0;
            setPlusDisabled();
            setMinusDisabled();
            setAddToBtnDisabled();
            angular.element(".requisitionDetail"+id).find("input").attr("readonly",true);
        }else if(inventory - quantity === 1){
            category.remainingNumber = 1;
            setMinusDisabled();
            setPlusDisabled();
            clearAddToBtnDisabled();
            angular.element(".requisitionDetail"+id).find("input").attr("readonly",true);
        }else{
            category.remainingNumber = 1;
            setMinusDisabled();
            clearAddToBtnDisabled();
            clearPlusDisabled();
            angular.element(".requisitionDetail"+id).find("input").attr("readonly",false);
        }
    }

    // To avoid displaying page without styles due to the slow loading of CSS files
    setTimeout(function(){ angular.element("body").css("visibility", "visible") }, 500);
});