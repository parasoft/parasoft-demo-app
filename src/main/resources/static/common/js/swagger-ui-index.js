window.onload = function() {
	var urls;
	var searchTxt = window.location.search;
	
	if(searchTxt === '?type=gateway'){
		urls = [{
			url : "/pda/api-docs/v1-proxy",
			name : "v1-proxy"
		}];
	}else if(searchTxt === '?type=demoApp'){
		urls = [{
			url : "/pda/api-docs/v1",
			name : "v1"
		}];
	}
	
	const ui = SwaggerUIBundle({
		urls : urls,
		dom_id : '#swagger-ui',
		deepLinking : true,
		defaultModelsExpandDepth: -1,
		presets : [ SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset ],
		plugins : [ SwaggerUIBundle.plugins.DownloadUrl ],
		layout : "StandaloneLayout"
	})

	window.ui = ui
}