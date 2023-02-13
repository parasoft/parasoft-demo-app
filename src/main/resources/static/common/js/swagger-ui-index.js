window.onload = function () {
  var urls;
  var searchTxt = window.location.search;

  if (searchTxt === '?type=gateway') {
    urls = [{
      url: "/pda/api-docs/v1-proxy",
      name: "v1-proxy"
    }];
  } else if (searchTxt === '?type=demoApp') {
    urls = [{
      url: "/pda/api-docs/v1",
      name: "v1"
    }];
  }

  const ui = SwaggerUIBundle({
    urls: urls,
    dom_id: '#swagger-ui',
    deepLinking: true,
    defaultModelsExpandDepth: -1,
    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
    plugins: [SwaggerUIBundle.plugins.DownloadUrl, OAuthLogoutPlugin],
    layout: "StandaloneLayout",
    oauth2RedirectUrl: "".concat(window.location.protocol, "//").concat(window.location.host, "/swagger-ui/oauth2-redirect.html")
  });

  ui.initOAuth({
    scopes: "openid"
  });

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
              if (payload[0] == 'oAuth2AuthCode') {
                $.ajax({
                  url: "/v1/swaggerOAuth2Logout",
                  data: JSON.stringify({"idToken": idToken}),
                  type: "POST",
                  headers: {'Content-Type': 'application/json'},
                  success: function() {
                    system.authActions.showDefinitions(false);
                  },
                  error: function() {
                    // Do nothing
                  }
                })
              }

            }
          }
        }
      }
    }
  }

  window.ui = ui;
};