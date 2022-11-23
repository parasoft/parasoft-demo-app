var app = angular.module('pdaApp', ['pascalprecht.translate']);

setLocale(app);
//initialize controllers of import page
initImportPageControllers(app);
initToastr();

app.controller('itemDetailController', function($rootScope, $http, $location, $filter, $interval, graphQLService) {
	//Get some data
	var itemDetail = this;
	var itemId = $location.absUrl().substr($location.absUrl().lastIndexOf("/")+1);

	$rootScope.itemNum = 1;
	getUnreviewedAmount($http,$rootScope,$filter);
	connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope, $filter);

	//Load 'loading' animation
	itemDetail.loadingAnimation = true;
	itemDetail.showQuantity = false;

	//get related assets data from database TODO
	var testNums = [0,1,2];
	itemDetail.relatedItems = testNums;
	itemDetail.itemLineExp = function(index){
		return index % 3 + 1;
	}

	// Set time out for avoiding to get the key when using $filter('translate') filter.
	setTimeout(function(){
        let success = (data) => {
            itemDetail.item = data;
            itemDetail.showItemDetail = true;

            let success = (result) => {
                itemDetail.categoryName = result.name;
            }
            let error = (result, endpointType) => {
                console.info(result);
                displayLoadError(result, $rootScope, $filter, $http, true, endpointType);
            }

            let getCategoryByIdParams = {"categoryId": data.categoryId};
            if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
                graphQLService.getCategoryById(getCategoryByIdParams, success, (data) => {error(data, "graphQL")}, "{name}");
            } else {
                $http({
                    method: 'GET',
                    url: '/proxy/v1/assets/categories/' + data.categoryId,
                }).then(function (result) {
                    success(result.data.data);
                }).catch(function (result) {
                    error(result, "categories");
                });
            }
        }
        let error = (data, endpointType) => {
            console.info(data);
            itemDetail.itemDetailError = true;
            displayLoadError(data,$rootScope,$filter,$http,false,endpointType);
        }
        let param = {"itemId": itemId};

		//Get item by item id
        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            let selectionSet = "{name,description,image,categoryId}"
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
	}, 500);
	
	//Get cartItem by item id
    let getCartItemByIdSuccess = (cartItem) => {
        var quantity = cartItem.quantity;
        var inventory = cartItem.realInStock;

        if(inventory === 0) {$rootScope.itemNum = 0;}
        $rootScope.itemInventory = inventory;
        $rootScope.inRequisition = quantity;
        checkInventory(inventory, quantity, 1);
        $interval(function() {itemDetail.loadingAnimation = false;itemDetail.showQuantity = true;}, 500, 1);
    }
    let getCartItemByIdError = (result) => {
        $rootScope.inRequisition = 0;
        console.info(result);
        var data = result.data;
        var status = result.status;
        if(status === 500) {
            console.log(data.message);
        }
        $interval(function() {itemDetail.loadingAnimation = false;itemDetail.showQuantity = true;}, 500, 1);
    }

    let getCartItemByIdParams = {"itemId": itemId}
    if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
        graphQLService.getCartItemByItemId(getCartItemByIdParams, getCartItemByIdSuccess,
            (data) => {getCartItemByIdError(data)}, "{realInStock, quantity}");
    } else {
        $http({
            method: 'GET',
            url: '/proxy/v1/cartItems/' + itemId
        }).then(function(result) {
            getCartItemByIdSuccess(result.data.data);
        }).catch(function(result) {
            getCartItemByIdError(result)
        });
    }

	itemDetail.minusItemNum = function(itemNum, inventory, quantity){
		clearPlusDisabled();
		if(itemNum - 1 < 2){
			setMinusDisabled();
			return 1;
		}else if(itemNum + quantity > inventory){
			setPlusDisabled();
			clearMinusDisabled();
			return inventory - quantity;
		}else{
			return itemNum - 1;
		}
	}

	itemDetail.plusItemNum = function(itemNum, inventory, quantity){
		clearMinusDisabled();
		if(itemNum < inventory - quantity - 1){
			return itemNum + 1;
		}else{
			setPlusDisabled();
			return inventory - quantity;
		}
	}

	itemDetail.inputItemNum = function(itemNum, inventory, quantity){
		if(inventory - quantity <= 0){
			return 0;
		}else if(inventory - quantity === 1){
			return 1;
		}else if(itemNum < 2){
			setMinusDisabled();
			clearPlusDisabled();
			return 1;
		}else if(itemNum < inventory && itemNum + quantity < inventory){
			clearPlusDisabled();
			clearMinusDisabled();
			return itemNum;
		}else{
			setPlusDisabled();
			clearMinusDisabled();
			return inventory - quantity;
		}
	}

	itemDetail.addItemToReqisition = function(itemNum,inventory,quantity){
		quantity = quantity === undefined ? 0 : quantity;
		var total = quantity + itemNum;
		if(inventory - quantity === 0){
			return;
		}else if(total > inventory){
			toastrService().error($filter('translate')('ADD_TO_CART_FAIL')+$filter('translate')('EXCEED_INVENTORY_ERROR'));
			return;
		}

		let params = {itemId:itemId,itemQty:itemNum};
        let success = (data) => {
            //Update shopping cart items
            loadShoppingCartItemQuantity($rootScope,$http,$filter,graphQLService);
            $rootScope.inRequisition = data.quantity;
            checkInventory(data.realInStock,data.quantity,itemNum);
            toastr.success($filter('translate')('ADD_TO_CART_SUCCESS'));
        }
        let error = (data, endpointType) => {
            console.info(data);
        }

        if (CURRENT_WEB_SERVICE_MODE === "GraphQL") {
            graphQLService.addItemInCart({"shoppingCartDTO": params}, success, (data) => {error(data, "graphQL")}, "{realInStock, quantity}");
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


	//Make the plus button disabled in detail page
	function setPlusDisabled(){
		$rootScope.plusDisabled = {
			"opacity" : ".35",
			"cursor" : "not-allowed"
		};
		$rootScope.plusBtnDisabled = true;
	}

	//Make the minus button disabled in detail page
	function setMinusDisabled(){
		$rootScope.minusDisabled = {
			"opacity" : ".35",
			"cursor" : "not-allowed"
		};
		$rootScope.minusBtnDisabled = true;
	}

	//Make 'add to requisition' button disabled in detail page
	function setAddToBtnDisabled(){
		$rootScope.btnDisabled = {
			"opacity" : ".35",
			"cursor" : "not-allowed"
		};
		$rootScope.addToBtnDisabled = true;
	}

	//Clear the plus button disabled in detail page
	function clearPlusDisabled(){
		$rootScope.plusDisabled = {};
		$rootScope.plusBtnDisabled = false;
	}

	//Clear the minus button disabled in detail page
	function clearMinusDisabled(){
		$rootScope.minusDisabled = {};
		$rootScope.minusBtnDisabled = false;
	}

	//Clear 'add to requisition' button disabled in detail page
	function clearAddToBtnDisabled(){
		$rootScope.btnDisabled = {};
		$rootScope.addToBtnDisabled = false;
	}

	function checkInventory(inventory,quantity,inputNum){
		if(inventory === 0 || inventory - quantity <= 0){
			setPlusDisabled();
			setMinusDisabled();
			setAddToBtnDisabled();
			angular.element("#item_number_input").attr("readonly",true);
		}else if(inventory - quantity === 1){
			setMinusDisabled();
			setPlusDisabled();
			clearAddToBtnDisabled();
			angular.element("#item_number_input").attr("readonly",true);
		}else if(inputNum + quantity >= inventory){
			setPlusDisabled();
			clearMinusDisabled();
			clearAddToBtnDisabled();
		}else if(inputNum + quantity < inventory && inputNum > 1){
			clearMinusDisabled();
			clearPlusDisabled();
			clearAddToBtnDisabled();
		}else{
			setMinusDisabled();
			clearAddToBtnDisabled()
			clearPlusDisabled();
			angular.element("#item_number_input").attr("readonly",false);
		}
	}

	// To avoid displaying page without styles due to the slow loading of CSS files
	setTimeout(function(){ angular.element("body").css("visibility", "visible") }, 500);
});