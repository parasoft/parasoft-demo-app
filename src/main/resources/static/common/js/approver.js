var app = angular.module('approverHomePageApp', ['pascalprecht.translate']);

setLocale(app);
initHeaderController(app);
initProductBuildInfo(app);
initAuthorizationHeader(app);
initToastr();

app.controller('approverHomePageController', function($rootScope, $http, $filter) {
	var approver = this;

	var mqConsumeCallback = function mqConsumeCallback(){
		if(approver.totalPages === 0){
			approver.totalPages = 1;
		}
		if(approver.currentPage === 0){
			approver.currentPage = 1;
		}
		approver.selectPage(approver.currentPage);
	}
	
	connectAndSubscribeMQ(CURRENT_ROLE,$http,$rootScope,$filter,mqConsumeCallback);
	
	//To control whether the order detail is visible or hidden
	approver.showOrderDetail = {'show':false}
	approver.openOrderDetail = function(orderNum) {
		
		$http({
			method: 'GET',
			url: '/proxy/v1/orders/'+orderNum,
		}).then(function(result) {
			handleOrderDetail(result.data.data);
		}).catch(function(result) {
			console.log(result);
		});
	}
	
	function handleOrderDetail(orderDetail){
		approver.orderDetail = orderDetail;
		var orderStatus = approver.orderDetail.status;
		
		if(!approver.orderDetail.reviewedByAPV){
			approver.changeReviewStatus(approver.orderDetail);
		}
		
		approver.response = {
			'showResponseSelect' : false,
			'showResponseResult' : false,
			'showResponseCommentsLabel' : true,
			'showResponseCommentsTextarea' : false,
			'showResponseCommentsResult' : false,
			'showResponseSaveAndCancel' : false,
			'responseSelectValue' : '',
			'responseCommentsTextarea' : '',
			'saveButtonDisabled' : true,
			'toggleElements' : function(){

                if(approver.response.responseSelectValue === ''){
                    approver.response.saveButtonDisabled = true;
                    approver.response.responseCommentsTextarea = '';
                }else{
                    approver.response.saveButtonDisabled = false;
                }
			},
		};
		
		if(orderStatus === 'SUBMITTED'){
			approver.response.showResponseSelect = true;
			approver.response.showResponseCommentsTextarea = true;
			approver.response.showResponseSaveAndCancel = true;
			approver.response.showResponseSaveAndCancel = true;
		}
		
		if(orderStatus === 'DECLINED' || approver.orderDetail.status === 'APPROVED'){
			approver.response.showResponseResult = true;
			approver.response.showResponseCommentsResult = true;
		}
		
		approver.showOrderDetail['show'] = true;
	}
	
	approver.closeOrderDetail = function(){
		approver.showOrderDetail['show'] = false;
	}
	
	approver.changeReviewStatus = function(orderDetail){
		
		var orderNumber = orderDetail.orderNumber;
		var orderStatus = orderDetail.status;
		var reviewedByAPV = true;
		
		$http({
			method: 'PUT',
			url: '/proxy/v1/orders/'+orderNumber,
			data: {"status":orderStatus,"reviewedByAPV":reviewedByAPV},
			headers: { 'Content-Type': 'application/json' }
		}).then(function(result) {
			//Update icon
			approver.showNewLabel[orderNumber] = false;
			getUnreviewedAmount($http,$rootScope);
		}).catch(function(result) {
			console.log(result);
		});
	}
	
	approver.loadStyles = function(index){
		return {
			"top":64*index+"px",
			"position":"absolute"
		};
	}
	
	approver.calItemsAmount = function(items){
		var total = 0;
		
		$.each(items,function(i,item){
			total += item.quantity;
		});
		
		return total;
	}
	
	approver.sendResponse = function(orderNumber){
		$http({
			method: 'PUT',
			url: '/proxy/v1/orders/'+orderNumber,
			data: angular.element('#order_response_form').serializeJSON(),
			headers: { 'Content-Type': 'application/json' }
		}).then(function(result) {
			handleOrderDetail(result.data.data);
			approver.selectPage(approver.currentPage);
		}).catch(function(result) {
			console.log(result);
		});
	}
	
	/**For pagination**/
	// The item number for per page
	approver.pageSize = 10; 
	
	// Load data for previous page 
	approver.prev = function(currentPage){
		approver.selectPage(currentPage-1);
	}
	
	// Load data for next page 
	approver.next = function(currentPage){
		approver.selectPage(currentPage+1);
	}

	// Load data for given page number
	approver.selectPage = function(page) {
		
		// Situation for page number is more than legal range
		if((approver.totalPages !== 0 || approver.totalPages !== undefined) && (page < 1 || page > approver.totalPages)){
			return ;
		}
		
		$http({
			method: 'GET',
			url: '/proxy/v1/orders',
			params: {'sort':'orderNumber,desc'}
		}).then(function(result) {
			var orders = result.data.data.content;
			var totalOrders = result.data.data.totalElements;
			orders = $filter('orderBy')(orders,"orderNumber",true);
			
			if(totalOrders < 1){
				$rootScope.emptyContentError = true;
			}
			
			approver.totalPages = Math.ceil(totalOrders / approver.pageSize);
			approver.currentPage = approver.totalPages === 0 ? 0 : page;
			
			var begin; //The index of the first order
			var end; //The index of the last order
			begin = 10 * (page - 1);
			end = begin + 10; 
			
			// Situation for the last index is more than legal range
			if(end > totalOrders){
				end = totalOrders;
			}
			
			//Obtain the orders' list shown on the page
			var ordersList = new Array();
			for(var i = begin; i < end; i++){
				ordersList.push(orders[i]);
			}
			
			//Fill in with blank lines if the total amount of orders less than 10 
			if(ordersList.length < approver.pageSize && ordersList.length != 0){
				var blank = approver.pageSize - ordersList.length
				for(var i = 0; i < blank; i++){
					ordersList.push("");
				}
			}
			
			approver.showNewLabel = {};
			$.each(ordersList,function(i,order){
				approver.showNewLabel[order.orderNumber] = !order.reviewedByAPV;
			});
			
			if(ordersList.length < 1){
				approver.emptyContentsMessage = true;
			}
			
			// Update the new data on web
			approver.orders = ordersList;
		}).catch(function(result) {
			console.log(result);
			approver.totalPages = 0;
			approver.currentPage = 0;
			displayLoadError(result,$rootScope,$filter,$http,false,'orders');
			approver.ordersLoadError = true;
		});
	}
	
	// Set time out for avoiding to get the key when using $filter('translate') filter.
	setTimeout(function(){
		// Initialize pagination
		approver.selectPage(1);
	}, 500);
	
	// To avoid displaying page without styles due to the slow loading of CSS files
	setTimeout(function(){ $("body").css("visibility","visible") }, 500);
});