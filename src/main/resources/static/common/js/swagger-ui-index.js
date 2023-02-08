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
        plugins : [ SwaggerUIBundle.plugins.DownloadUrl, OAuthLogoutPlugin ],
        layout : "StandaloneLayout",
        <script type="text/javascript" src="/lib/jquery/jquery-3.3.1.min.js"></script>
    })

    function OAuthLogoutPlugin() {

        var lastAuthUrl = null;
        var idToken = null;

        return {
          statePlugins: {
            auth: {
              wrapActions: {

                authorizeOauth2: (originalAction, system) => (payload) => {
                  originalAction(payload);

                  var auth = payload.auth;

                  if (auth) {
                    lastAuthUrl = auth.schema.get('authorizationUrl');
                  }

                  if (payload.token.id_token != null) {
                    idToken = payload.token.id_token;
                  }
                },

                logout: (originalAction, system) => (payload) => {
                  originalAction(payload);

                  if (lastAuthUrl) {
                    var logoutUrl = lastAuthUrl.substring(0, lastAuthUrl.lastIndexOf('/')) + '/logout';
                    if (idToken != null) {
                        logoutUrl += "?id_token_hint=" + idToken;
                    }

                    try {
                        $.ajax({
                            url: logoutUrl,
                            type: "GET"
                        })
                    } finally {
                        window.location.reload();
                    }
                  }
                }
              }
            }
          }
        }
      }

    window.ui = ui
}