var app = angular.module('categoryPageApp', ['pascalprecht.translate','ngCookies']);

setLocale(app);
//Initialize controllers of import page
initImportPageControllers(app);
initToastr();

app.controller('categoryController', function($rootScope, $http, $location, $filter, $interval, $cookies) {
    var category = this;
    var items;
    var categoryId = $location.absUrl().substr($location.absUrl().lastIndexOf("/")+1);
    category.categoryId = categoryId;
    category.sort="name";
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

        //Get all items from database
        $http({
            method: 'GET',
            url: '/proxy/v1/assets/items',
            params: {categoryId: categoryId, regions: checkedRegions},
        }).then(function(result) {
            if(!regionFilterDisabled){
                checkedRegions = checkedRegions.split(",");
                category.checkedRegions = checkedRegions;
            }

            items = result.data.data.content;
            category.items = items;

            if(items.length === 0){
                $rootScope.emptyContentError = true;
                category.emptyContentsMessage = true;
            }

            countFooterHeight(items.length);
        }).catch(function(result) {
            console.info(result);
            displayLoadError(result,$rootScope,$filter,$http,false,'items');
            category.itemsLoadError = true;
        });

        //Get category by id
        $http({
            method: 'GET',
            url: '/proxy/v1/assets/categories/' + categoryId,
            params: {categoryId: categoryId},
        }).then(function(result) {
            data = result.data.data;
            category.title = data.name;
        }).catch(function(result) {
            console.info(result);
            displayLoadError(result,$rootScope,$filter,$http,true,'categories');
        });

        function handleRegionError(result,category,$rootScope,$filter,$http){
            displayLoadError(result,$rootScope,$filter,$http,false,'locations');
            category.regionsLoadError = true;
        }
    }, 500);

    category.openRequisitionDetail = function(itemId){
        category.showCategoryRequisitionDetail = true;
        category.loadingAnimation = true;
        category.showQuantity = false;
        category.remainingNumber = 1;

        //Get cartItem by item id
        $http({
            method: 'GET',
            url: '/proxy/v1/cartItems/' + itemId
        }).then(function(result) {
            var cartItem = result.data.data;
            category.item = {
                id: cartItem.itemId,
                name: cartItem.name,
                description: cartItem.description,
                image: cartItem.image,
                inStock: cartItem.realInStock,
                inRequisition: cartItem.quantity
            };

            checkInventory(cartItem.realInStock,itemId,cartItem.quantity);
            $interval(function(){category.loadingAnimation = false;category.showQuantity = true;},500,1);
        }).catch(function(result) {
            category.inRequisition = 0;
            console.info(result);
            $http({
                method: 'GET',
                url: '/proxy/v1/assets/items/' + itemId,
            }).then(function(result) {
                var item = result.data.data;
                category.inStock = item.inStock;
                checkInventory(item.inStock,itemId,0);
            }).catch(function(result) {
                console.info(result);
            });

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
            toastr.error($filter('translate')('ADD_TO_CART_FAIL')+$filter('translate')('EXCEED_INVENTORY_ERROR'));
            return;
        }

        $http({
            method: 'POST',
            url: '/proxy/v1/cartItems',
            params: {itemId:id,itemQty:itemNum},
        }).then(function(result) {
            closeRequisitionDetail(id);
            //Update shopping cart items
            loadShoppingCartData($rootScope,$http,$filter);
            //If the requisition bar is visible, it should be closed after adding successfully
            //This is to avoid some format errors
            angular.element("#requisition_cross").click();
            toastr.success($filter('translate')('ADD_TO_CART_SUCCESS'));
        }).catch(function(result) {
            console.info(result);
            displayLoadError(result,$rootScope,$filter,$http,true,'cart');
        });
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
        $http({
            method: 'GET',
            url: '/proxy/v1/assets/items',
            params: angular.element('.category_filter_container').serializeJSON(),
        }).then(function (result) {
            items = result.data.data.content;
            category.items = items;
            countFooterHeight(items.length);
            var checkedRegions = result.config.params.regions;
            var regionFilterDisabled = checkedRegions === "" || checkedRegions === null || checkedRegions === undefined;

            //Save checked region options to cookie
            $cookies.put("regionFilter", checkedRegions);

            if (items.length === 0 && !$rootScope.emptyContentError) {
                category.showNoResultText = true;
            } else {
                category.showNoResultText = false;
            }
        }).catch(function (result) {
            console.info(result);
            displayLoadError(result, $rootScope, $filter, $http, true, 'items');
        });
    }

    //Calculate the footer's distance from the top based on the amount of items
    function countFooterHeight(itemLength){
        var row = Math.ceil(itemLength/3);
        row = row < 1 ? 1 : row;
        if(row > 1){
            $rootScope.footerHeight = {
                "top" : 348 * (row - 1) + 700 + "px"
            }
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