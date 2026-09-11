---
parasoftVersion: 2026.1.0
productVersion: 10.7.5
schemaVersion: 13
suite:
  $type: TestSuite
  name: Test Suite
  hasEnvironmentConfig: true
  environmentConfig:
    environments:
    - variables:
      - name: BASEURL
        value: http://localhost:8080
      - name: DB_URL
        value: jdbc:hsqldb:hsql://localhost:9001/outdoor
      - name: SWAGGER
        value: http://localhost:8080/api-docs/v1
      - name: GRAPHQL_URL
        value: http://localhost:8080/graphql
      - name: GRAPHQL_SDL
        value: http://localhost:8080/schema.graphqls
      - name: JMS_URL
        value: tcp://localhost:61626
      name: localhost
      active: true
  lastModifiedBy: lyao
  authentications:
  - $type: BasicAuthentication
    name: purchaser
    username:
      fixedValue:
        $type: StringTestValue
        username: purchaser
    password:
      fixedValue:
        $type: PasswordTestValue
        password: AwAAACx4YXhJU3NQdUtXUkFoUCtIMld0c1lib0JWN3EwSW9ialFiaXhxWndPMi9vPQ==
  setUpTests:
  - $type: RESTClientToolTest
    name: REST Client
    testID: 24
    tool:
      $type: RESTClient
      iconName: RESTClient
      name: resetDB
      formJson:
        value:
          $type: ElementValue
          writeType: true
          type:
            $type: ElementType
            localName: root
            bodyType:
              $type: ComplexType
              attributes:
              - ns: ""
                name: type
                fixed: object
                contentType:
                  $type: StringType
                required: true
              name: rootType
              compositor: true
              compositorObj:
                $type: AllCompositor
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            attributes:
            - replacedColumn: ""
              value:
                $type: StringValue
                replacedColumn: ""
                value: object
              useValue: true
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
        elementTypeName: root
      hasServiceInfo: true
      serviceInfo:
        serviceDescriptor:
          $type: StandardServiceDescriptor
          location: http://localhost:8080/api-docs/v1
      jsonBuilder:
        hasValue: true
        value:
          $type: JSONObjectValue
          nameIsNull: true
      schemaURL:
        MessagingClient_SchemaLocation: "${SWAGGER}"
      formInput:
        value:
          $type: ElementValue
          writeType: true
          type:
            $type: ElementType
            localName: ""
            bodyType:
              $type: ComplexType
              name: anonymous
              compositor: true
              compositorObj:
                $type: SequenceCompositor
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
      jmsMessageOutputProvider:
        $type: JMSMessageOutputProvider
        jmsOutputProviderRequest:
          $type: JMSOutputProvider
          name: Request Object
          menuName: Object
        jmsOutputProviderResponse:
          $type: JMSOutputProvider
          name: Response Message Object
          menuName: Message Object
      router:
        values:
        - $type: ScriptedValue
        fixedValue:
          $type: StringTestValue
          HTTPClient_Endpoint: "${BASEURL}/v1/demoAdmin/databaseReset"
      transportProperties:
        manager:
          protocol: 1
          properties:
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            protocol: 1
            keepAlive1_1:
              bool: true
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            keepAlive1_1:
              bool: true
          - $type: NoneTransportProperties
        messageExchangePattern:
          inverted: true
      outputProviders:
        requestHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Request Transport Header
        responseHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Response Transport Header
        xmlRequestOutput:
          $type: NamedXMLToolOutputProvider
          menuName: Traffic
          name: Request Traffic
        trafficOutput:
          m_name: Traffic Stream
        objectOutput:
          $type: ObjectOutputProvider
          outputTools:
          - $type: TrafficViewer
            iconName: TrafficViewer
            name: Traffic Viewer
            showRequestHeaders: true
            showResponseHeaders: true
          name: Traffic Object
      literal:
        use: 1
        text:
          MessagingClient_LiteralMessage: ""
          type: application/json
      mode: Literal
      literalQuery:
        isPropertiesRef: true
      literalPath:
        pathElements:
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: v1
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: demoAdmin
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: databaseReset
      resourceMethod:
        resourceId: /v1/demoAdmin/databaseReset
        httpMethod: PUT
      resourceMode: 3
      baseUrl:
        values:
        - $type: ScriptedValue
        - $type: WadlTestValue
          value: http://localhost:8080
        fixedValue:
          $type: StringTestValue
          value: "${BASEURL}"
  - $type: RESTClientToolTest
    name: /v1/demoAdmin/preferences - PUT - 2
    testID: 25
    performanceGroup: 6
    tool:
      $type: RESTClient
      iconName: RESTClient
      name: set industry - outdoor
      formJson:
        builtFromSchema: true
        value:
          $type: ElementValue
          writeType: true
          type:
            $type: ElementType
            localName: root
            bodyType:
              $type: ComplexType
              attributes:
              - ns: ""
                name: type
                defaultValue: object
                fixed: object
                contentType:
                  $type: StringType
                  _id: 1
                required: true
              name: /components/schemas/GlobalPreferencesDTO
              compositor: true
              compositorObj:
                $type: AllCompositor
                types:
                - $type: ElementType
                  localName: industryType
                  bodyType:
                    $type: ComplexType
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: enum
                      fixed: enum
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    content: true
                    contentObj:
                      $type: EnumType
                      values:
                      - '"DEFENSE"'
                      - '"HEALTHCARE"'
                      - '"GOVERNMENT"'
                      - '"RETAIL"'
                      - '"AEROSPACE"'
                      - '"OUTDOOR"'
                      baseType: anonymous
                    name: /components/schemas/GlobalPreferencesDTO/properties/industryType
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                - $type: ElementType
                  localName: webServiceMode
                  bodyType:
                    $type: ComplexType
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: enum
                      fixed: enum
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    content: true
                    contentObj:
                      $type: EnumType
                      values:
                      - '"REST_API"'
                      - '"GRAPHQL"'
                      baseType: anonymous
                    name: /components/schemas/GlobalPreferencesDTO/properties/webServiceMode
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                - $type: ElementType
                  minOccurs: 0
                  localName: graphQLEndpoint
                  bodyType:
                    $type: ComplexType
                    _id: 2
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: string
                      fixed: string
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    content: true
                    contentObj:
                      $type: StringType
                      _ref: 1
                    namespace: urn:parasoft:json
                    name: string
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                - $type: ElementType
                  localName: advertisingEnabled
                  bodyType:
                    $type: ComplexType
                    _id: 3
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: boolean
                      fixed: boolean
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    content: true
                    contentObj:
                      $type: BooleanType
                    namespace: urn:parasoft:json
                    name: boolean
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                - $type: ElementType
                  minOccurs: 0
                  localName: demoBugs
                  bodyType:
                    $type: ComplexType
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: array
                      fixed: array
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    name: /components/schemas/GlobalPreferencesDTO/properties/demoBugs
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                      types:
                      - $type: ElementType
                        minOccurs: 0
                        maxOccurs: -1
                        localName: item
                        bodyType:
                          $type: ComplexType
                          attributes:
                          - ns: ""
                            name: type
                            defaultValue: enum
                            fixed: enum
                            contentType:
                              $type: StringType
                              _ref: 1
                            required: true
                          content: true
                          contentObj:
                            $type: EnumType
                            values:
                            - '"INCORRECT_LOCATION_FOR_APPROVED_ORDERS"'
                            - '"INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER"'
                            - '"REVERSE_ORDER_OF_ORDERS"'
                            - '"REINITIALIZE_DATASOURCE_FOR_EACH_HTTP_REQUEST"'
                            baseType: anonymous
                          name: /components/schemas/GlobalPreferencesDTO/properties/demoBugs/items
                          compositor: true
                          compositorObj:
                            $type: SequenceCompositor
                - $type: ElementType
                  minOccurs: 0
                  localName: categoriesRestEndpoint
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: itemsRestEndpoint
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: cartItemsRestEndpoint
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: ordersRestEndpoint
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: locationsRestEndpoint
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: useParasoftJDBCProxy
                  bodyType:
                    $type: ComplexType
                    _ref: 3
                - $type: ElementType
                  minOccurs: 0
                  localName: parasoftVirtualizeServerUrl
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: parasoftVirtualizeServerPath
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  minOccurs: 0
                  localName: parasoftVirtualizeGroupId
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  localName: mqType
                  bodyType:
                    $type: ComplexType
                    attributes:
                    - ns: ""
                      name: type
                      defaultValue: enum
                      fixed: enum
                      contentType:
                        $type: StringType
                        _ref: 1
                      required: true
                    content: true
                    contentObj:
                      $type: EnumType
                      values:
                      - '"ACTIVE_MQ"'
                      - '"KAFKA"'
                      - '"RABBIT_MQ"'
                      baseType: anonymous
                    name: /components/schemas/GlobalPreferencesDTO/properties/mqType
                    compositor: true
                    compositorObj:
                      $type: SequenceCompositor
                - $type: ElementType
                  localName: orderServiceSendTo
                  bodyType:
                    $type: ComplexType
                    _ref: 2
                - $type: ElementType
                  localName: orderServiceListenOn
                  bodyType:
                    $type: ComplexType
                    _ref: 2
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            attributes:
            - replacedColumn: ""
              value:
                $type: StringValue
                replacedColumn: ""
                value: object
                xmlEncoding: 2
              useValue: true
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
                  values:
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: enum
                          xmlEncoding: 2
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: EnumValue
                        replacedColumn: ""
                        selectedIdx: 5
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: enum
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: EnumValue
                        replacedColumn: ""
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: boolean
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: BooleanValue
                        replacedColumn: ""
                        value: "true"
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: array
                        useValue: true
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                            values:
                            - $type: ElementValue
                              replacedColumn: ""
                              values:
                              - $type: ComplexValue
                                replacedColumn: ""
                                allowArrayExclude: true
                                attributes:
                                - replacedColumn: ""
                                  value:
                                    $type: StringValue
                                    replacedColumn: ""
                                    value: enum
                                  useValue: true
                                contentValue: true
                                contentValueObj:
                                  $type: EnumValue
                                  replacedColumn: ""
                                compositorValue: true
                                compositorValueObj:
                                  replacedColumn: ""
                                  values:
                                    $type: CompositorValueSetCollectionSet
                                    set:
                                    - $type: CompositorValueSet
                              - $type: ComplexValue
                                replacedColumn: ""
                                allowArrayExclude: true
                                attributes:
                                - replacedColumn: ""
                                  value:
                                    $type: StringValue
                                    replacedColumn: ""
                                    value: enum
                                  useValue: true
                                contentValue: true
                                contentValueObj:
                                  $type: EnumValue
                                  replacedColumn: ""
                                  selectedIdx: 1
                                compositorValue: true
                                compositorValueObj:
                                  replacedColumn: ""
                                  values:
                                    $type: CompositorValueSetCollectionSet
                                    set:
                                    - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: http://localhost:8080/v1/assets/categories
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: http://localhost:8080/v1/assets/items
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: http://localhost:8080/v1/cartItems
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: http://localhost:8080/v1/orders
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      allowArrayExclude: true
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: http://localhost:8080/v1/locations
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                  - $type: ElementValue
                    replacedColumn: ""
                  - $type: ElementValue
                    replacedColumn: ""
                  - $type: ElementValue
                    replacedColumn: ""
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: enum
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: EnumValue
                        replacedColumn: ""
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: inventory.request
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
                  - $type: ElementValue
                    replacedColumn: ""
                    values:
                    - $type: ComplexValue
                      replacedColumn: ""
                      attributes:
                      - replacedColumn: ""
                        value:
                          $type: StringValue
                          replacedColumn: ""
                          value: string
                        useValue: true
                      contentValue: true
                      contentValueObj:
                        $type: StringValue
                        replacedColumn: ""
                        value: inventory.response
                      compositorValue: true
                      compositorValueObj:
                        replacedColumn: ""
                        values:
                          $type: CompositorValueSetCollectionSet
                          set:
                          - $type: CompositorValueSet
        elementTypeName: root
      hasServiceInfo: true
      serviceInfo:
        serviceDescriptor:
          $type: StandardServiceDescriptor
          location: http://localhost:8080/api-docs/v1
      jsonBuilder:
        hasValue: true
        value:
          $type: JSONObjectValue
          nameIsNull: true
      schemaURL:
        MessagingClient_SchemaLocation: "${SWAGGER}"
      formInput:
        value:
          $type: ElementValue
          writeType: true
          hasReference: true
          qnameAsString: ":"
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
      timeout:
        useDefault: false
        timeout: 100000
      jmsMessageOutputProvider:
        $type: JMSMessageOutputProvider
        jmsOutputProviderRequest:
          $type: JMSOutputProvider
          name: Request Object
          menuName: Object
        jmsOutputProviderResponse:
          $type: JMSOutputProvider
          name: Response Message Object
          menuName: Message Object
      validResponseRange:
        validResponseRange:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            validResponseRange: 200
      router:
        values:
        - $type: ScriptedValue
        fixedValue:
          $type: StringTestValue
          HTTPClient_Endpoint: http://localhost:8080/v1/demoAdmin/preferences
      transportProperties:
        manager:
          properties:
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              auth:
                useDefault: false
                authName: Basic-purchaser
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            keepAlive1_1:
              bool: true
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              auth:
                useDefault: false
                authName: Basic-purchaser
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            protocol: 1
            keepAlive1_1:
              bool: true
          - $type: NoneTransportProperties
        messageExchangePattern:
          inverted: true
      outputProviders:
        requestHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Request Transport Header
        responseHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Response Transport Header
        xmlRequestOutput:
          $type: NamedXMLToolOutputProvider
          menuName: Traffic
          name: Request Traffic
        trafficOutput:
          m_name: Traffic Stream
        objectOutput:
          $type: ObjectOutputProvider
          outputTools:
          - $type: TrafficViewer
            iconName: TrafficViewer
            name: Traffic Viewer
            showRequestHeaders: true
            showResponseHeaders: true
          name: Traffic Object
      literal:
        use: 1
        text:
          MessagingClient_LiteralMessage: |-
            {
                "industryType" : "OUTDOOR",
                "webServiceMode" : "REST_API",
                "advertisingEnabled" : true,
                "demoBugs" : [
                    "INCORRECT_LOCATION_FOR_APPROVED_ORDERS",
                    "INCORRECT_NUMBER_OF_ITEMS_IN_SUMMARY_OF_PENDING_ORDER"
                ],
                "categoriesRestEndpoint" : "http://localhost:8080/v1/assets/categories",
                "itemsRestEndpoint" : "http://localhost:8080/v1/assets/items",
                "cartItemsRestEndpoint" : "http://localhost:8080/v1/cartItems",
                "ordersRestEndpoint" : "http://localhost:8080/v1/orders",
                "locationsRestEndpoint" : "http://localhost:8080/v1/locations",
                "mqType" : "ACTIVE_MQ"
            }
          type: application/json
      mode: Form JSON
      literalQuery:
        isPropertiesRef: true
      literalPath:
        pathElements:
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: v1
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: demoAdmin
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: preferences
      resourceMethod:
        resourceId: /v1/demoAdmin/preferences
        httpMethod: PUT
      resourceMode: 3
      baseUrl:
        values:
        - $type: ScriptedValue
        - $type: WadlTestValue
          value: http://localhost:8080
        fixedValue:
          $type: StringTestValue
          value: "${BASEURL}"
  tearDownTests:
  - $type: RESTClientToolTest
    name: resetDB 2
    testID: 45
    tool:
      $type: RESTClient
      iconName: RESTClient
      name: 'resetDB '
      formJson:
        value:
          $type: ElementValue
          writeType: true
          type:
            $type: ElementType
            localName: root
            bodyType:
              $type: ComplexType
              attributes:
              - ns: ""
                name: type
                fixed: object
                contentType:
                  $type: StringType
                required: true
              name: rootType
              compositor: true
              compositorObj:
                $type: AllCompositor
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            attributes:
            - replacedColumn: ""
              value:
                $type: StringValue
                replacedColumn: ""
                value: object
              useValue: true
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
        elementTypeName: root
      hasServiceInfo: true
      serviceInfo:
        serviceDescriptor:
          $type: StandardServiceDescriptor
          location: http://localhost:8080/api-docs/v1
      jsonBuilder:
        hasValue: true
        value:
          $type: JSONObjectValue
          nameIsNull: true
      schemaURL:
        MessagingClient_SchemaLocation: "${SWAGGER}"
      formInput:
        value:
          $type: ElementValue
          writeType: true
          hasReference: true
          qnameAsString: ":"
          replacedColumn: ""
          values:
          - $type: ComplexValue
            replacedColumn: ""
            compositorValue: true
            compositorValueObj:
              replacedColumn: ""
              values:
                $type: CompositorValueSetCollectionSet
                set:
                - $type: CompositorValueSet
      jmsMessageOutputProvider:
        $type: JMSMessageOutputProvider
        jmsOutputProviderRequest:
          $type: JMSOutputProvider
          name: Request Object
          menuName: Object
        jmsOutputProviderResponse:
          $type: JMSOutputProvider
          name: Response Message Object
          menuName: Message Object
      router:
        values:
        - $type: ScriptedValue
        fixedValue:
          $type: StringTestValue
          HTTPClient_Endpoint: "${BASEURL}/v1/demoAdmin/databaseReset"
      transportProperties:
        manager:
          protocol: 1
          properties:
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            protocol: 1
            keepAlive1_1:
              bool: true
          - $type: HTTPClientHTTPProperties
            followRedirects:
              bool: true
            common:
              method:
                values:
                - $type: ScriptedValue
                fixedValue:
                  $type: HTTPMethodTestValue
                  method: PUT
            keepAlive1_1:
              bool: true
          - $type: NoneTransportProperties
        messageExchangePattern:
          inverted: true
      outputProviders:
        requestHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Request Transport Header
        responseHeader:
          $type: HTTPNamedToolOutputProvider
          menuName: Transport Header
          name: Response Transport Header
        xmlRequestOutput:
          $type: NamedXMLToolOutputProvider
          menuName: Traffic
          name: Request Traffic
        trafficOutput:
          m_name: Traffic Stream
        objectOutput:
          $type: ObjectOutputProvider
          outputTools:
          - $type: TrafficViewer
            iconName: TrafficViewer
            name: Traffic Viewer
            showRequestHeaders: true
            showResponseHeaders: true
          name: Traffic Object
      literal:
        use: 1
        text:
          MessagingClient_LiteralMessage: ""
          type: application/json
        dataSource:
          columnName: "iremId2: inStock"
      mode: Literal
      literalQuery:
        isPropertiesRef: true
      literalPath:
        pathElements:
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: v1
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: demoAdmin
        - values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            value: databaseReset
      resourceMethod:
        resourceId: /v1/demoAdmin/databaseReset
        httpMethod: PUT
      resourceMode: 3
      baseUrl:
        values:
        - $type: ScriptedValue
        - $type: WadlTestValue
          value: http://localhost:8080
        fixedValue:
          $type: StringTestValue
          value: "${BASEURL}"
  tests:
  - $type: TestSuite
    name: DB
    testID: 46
    tests:
    - $type: ToolTest
      name: DB Tool
      testID: 47
      tool:
        $type: DbTool
        iconName: DBTool
        name: items
        magicToken:
          fixedValue:
            $type: StringTestValue
            value: GO
        outputProviders:
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            name: SQL Query
          xmlResponseOutput:
            $type: NamedXMLToolOutputProvider
            outputTools:
            - $type: DiffTool
              iconName: Diff
              name: Diff
              differs:
              - $type: BinaryDiffer
                source:
                  optionsSource: 1
              - $type: TextDiffer
              - $type: XMLDifferNew
                ignoredXPaths:
                - xpath: ""
                  diff: 1
                  operation0: 1
                  operation1: 6
                  operation2: 7
                formXml:
                  formXML:
                    rootNodeExists: true
                    rootNode:
                      $type: XMLElementNode
                      xmlEncoding: 2
                      value: ""
                      name: NewElement
                      showAttributes: true
                hasSoapControlSource: true
                soapControlSource:
                  $type: SoapDiffControlSource
              - $type: JSONDiffer
              source:
                controls:
                - keys:
                  - ""
                  - ""
                  row:
                    refs:
                    - dataSourceName: ""
                    last: false
                - |
                  <?xml version="1.0" encoding="UTF-8"?>
                  <results>
                   <resultSet>
                    <rows>
                     <row
                      index="1">
                      <ID>1</ID>
                      <NAME>Blue Sleeping Bag</NAME>
                      <DESCRIPTION>This sleeping bag has a temperature rating of 25 degrees. The water repellent shell keeps out moisture.</DESCRIPTION>
                      <IMAGE>/outdoor/images/sleepingbag-blue-detail.png</IMAGE>
                      <CATEGORY_ID>1</CATEGORY_ID>
                      <REGION>LOCATION_1</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 15:32:23.491</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="2">
                      <ID>2</ID>
                      <NAME>Green Sleeping Bag</NAME>
                      <DESCRIPTION>This light weight sleeping bag has a temperature rating of 35 degrees. It is very compact when rolled up. Good for backpacking trips.</DESCRIPTION>
                      <IMAGE>/outdoor/images/sleepingbag-green-detail.png</IMAGE>
                      <CATEGORY_ID>1</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="3">
                      <ID>3</ID>
                      <NAME>Pink Sleeping Bag</NAME>
                      <DESCRIPTION>This sleeping bag has a temperature rating of 25 degrees. The water repellent shell keeps out moisture.</DESCRIPTION>
                      <IMAGE>/outdoor/images/sleepingbag-pink-detail.png</IMAGE>
                      <CATEGORY_ID>1</CATEGORY_ID>
                      <REGION>LOCATION_1</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="4">
                      <ID>4</ID>
                      <NAME>Light Hike Backpack</NAME>
                      <DESCRIPTION>Lightweight and simple design with minimal features for quick outdoor excursions.</DESCRIPTION>
                      <IMAGE>/outdoor/images/backpack-detail-sm.png</IMAGE>
                      <CATEGORY_ID>2</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="5">
                      <ID>5</ID>
                      <NAME>Day Hike Backpack</NAME>
                      <DESCRIPTION>Plenty of storage space to organize gear for an all-day adventure.</DESCRIPTION>
                      <IMAGE>/outdoor/images/backpack-detail-md.png</IMAGE>
                      <CATEGORY_ID>2</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="6">
                      <ID>6</ID>
                      <NAME>Multi-Day Backpack</NAME>
                      <DESCRIPTION>Storage space, organizing pockets, and features to keep all gear needed for a several-day backpacking trek.</DESCRIPTION>
                      <IMAGE>/outdoor/images/backpack-detail-lg.png</IMAGE>
                      <CATEGORY_ID>2</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="7">
                      <ID>7</ID>
                      <NAME>3 Person Tent</NAME>
                      <DESCRIPTION>A small compact tent good for backpacking trips. This tent comfortably sleeps 3 adults.</DESCRIPTION>
                      <IMAGE>/outdoor/images/tent-3-detail.png</IMAGE>
                      <CATEGORY_ID>3</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row
                      index="8">
                      <ID>8</ID>
                      <NAME>4 Person Tent</NAME>
                      <DESCRIPTION>This tent is good for small group or small family camping trips. Comfortably sleeps 4 adults.</DESCRIPTION>
                      <IMAGE>/outdoor/images/tent-4-detail.png</IMAGE>
                      <CATEGORY_ID>3</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                     <row index="9">
                      <ID>9</ID>
                      <NAME>6 Person Tent</NAME>
                      <DESCRIPTION>A tent for larger groups or long camping stays. This tent comfortably sleeps 6 adults.</DESCRIPTION>
                      <IMAGE>/outdoor/images/tent-6-detail.png</IMAGE>
                      <CATEGORY_ID>3</CATEGORY_ID>
                      <REGION>LOCATION_2</REGION>
                      <LAST_ACCESSED_DATE>2020-07-27 11:33:23.0</LAST_ACCESSED_DATE>
                     </row>
                    </rows>
                   </resultSet>
                  </results>
              mode: 2
            name: Results as XML
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        sqlQuery:
          fixedValue:
            $type: StringTestValue
            useMultipleLines: true
            value: select * from TBL_ITEM
        failOnSQLException: true
        account:
          local:
            $type: DbConfigSettings
            password: AwAAABhGaGswYmZHOGNLYzB0MHBIeEc2clJ3PT0=
            uri: "${DB_URL}"
            username: SA
            driver: org.hsqldb.jdbcDriver
    - $type: ToolTest
      name: items 2
      testID: 48
      tool:
        $type: DbTool
        iconName: DBTool
        name: categories
        magicToken:
          fixedValue:
            $type: StringTestValue
            value: GO
        outputProviders:
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            name: SQL Query
          xmlResponseOutput:
            $type: NamedXMLToolOutputProvider
            outputTools:
            - $type: DiffTool
              iconName: Diff
              name: Diff
              differs:
              - $type: BinaryDiffer
                source:
                  optionsSource: 1
              - $type: TextDiffer
              - $type: XMLDifferNew
                formXml:
                  formXML:
                    rootNodeExists: true
                    rootNode:
                      $type: XMLElementNode
                      xmlEncoding: 2
                      value: ""
                      name: NewElement
                      showAttributes: true
                hasSoapControlSource: true
                soapControlSource:
                  $type: SoapDiffControlSource
              - $type: JSONDiffer
              source:
                controls:
                - keys:
                  - ""
                  - ""
                  row:
                    refs:
                    - dataSourceName: ""
                    last: false
                - |
                  <?xml version="1.0" encoding="UTF-8"?>
                  <results>
                   <resultSet>
                    <rows>
                     <row
                      index="1">
                      <ID>1</ID>
                      <NAME>Sleeping bags</NAME>
                      <DESCRIPTION>Water repellent shell made from polyester with synthetic fill. Easy use zipper for safe and quick open or closing. Temperature ratings of 25 degrees and 35 degrees.</DESCRIPTION>
                      <IMAGE>/outdoor/images/category-sleepingbags.png</IMAGE>
                     </row>
                     <row
                      index="2">
                      <ID>2</ID>
                      <NAME>Backpacks</NAME>
                      <DESCRIPTION>Backpacks suitable for shorter hikes, day hikes, or extended backpacking trips. Adjustable to comfortably fit any body type.</DESCRIPTION>
                      <IMAGE>/outdoor/images/category-backpack.png</IMAGE>
                     </row>
                     <row index="3">
                      <ID>3</ID>
                      <NAME>Tents</NAME>
                      <DESCRIPTION>Sleeping capacity 3, 4, or 6. Features include easy single-person setup and lightweight construction. Large mesh windows keep insects out.</DESCRIPTION>
                      <IMAGE>/outdoor/images/category-tents.png</IMAGE>
                     </row>
                    </rows>
                   </resultSet>
                  </results>
              mode: 2
            name: Results as XML
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        sqlQuery:
          fixedValue:
            $type: StringTestValue
            useMultipleLines: true
            value: select * from TBL_CATEGORY
        failOnSQLException: true
        account:
          local:
            $type: DbConfigSettings
            password: AwAAABhGaGswYmZHOGNLYzB0MHBIeEc2clJ3PT0=
            uri: "${DB_URL}"
            username: SA
            driver: org.hsqldb.jdbcDriver
  - $type: TestSuite
    name: REST API
    testID: 7
    tests:
    - $type: RESTClientToolTest
      name: REST Client
      testID: 179
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: Check OpenAPI
        outputTools:
        - $type: GenericAssertionTool
          iconName: XMLAssertor
          name: JSON Assertor
          wrappedTool:
            $type: XMLAssertionTool
            iconName: XMLAssertor
            name: XML Assertor
            errorsOutput:
              name: Errors
            assertions:
            - $type: ValueAssertion
              timestamp: 1670379190007
              name: url
              extractEntireElement: true
              Assertion_XPath: /root/servers/item/url/string()
              value:
                name: Value
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: /
            - $type: OccurrenceAssertion
              timestamp: 1670379453313
              name: "/v1/orders/{orderNumber}"
              extractEntireElement: true
              Assertion_XPath: /root/paths/_x2f_v1_x2f_orders_x2f__x7b_orderNumber_x7d_
              value:
                name: Count
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: 1
            - $type: OccurrenceAssertion
              timestamp: 1670379484276
              name: "/v1/cartItems/{itemId}"
              extractEntireElement: true
              Assertion_XPath: /root/paths/_x2f_v1_x2f_cartItems_x2f__x7b_itemId_x7d_
              value:
                name: Count
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: 1
            - $type: OccurrenceAssertion
              timestamp: 1670379642884
              name: "/v1/assets/items/{itemId}"
              extractEntireElement: true
              Assertion_XPath: /root/paths/_x2f_v1_x2f_assets_x2f_items_x2f__x7b_itemId_x7d_
              value:
                name: Count
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: 1
            - $type: ValueAssertion
              timestamp: 1670379712709
              name: title
              extractEntireElement: true
              Assertion_XPath: /root/info/title/string()
              value:
                name: Value
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Parasoft Demo App REST API
            - $type: ValueAssertion
              timestamp: 1670379750099
              name: description
              extractEntireElement: true
              Assertion_XPath: /root/info/description/string()
              value:
                name: Value
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: This is the OpenAPI definition of RESTful API Services
                      for the Parasoft Demo Application.
            message:
              $type: ExpectedXMLMessage
              message: true
          conversionStrategy:
            dataFormatName: JSON
            conversionStrategyId: JSON
            conversionStrategyClassName: com.parasoft.xml.convert.json.JsonConversionStrategy
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        constrainToSchema: false
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${SWAGGER}"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  authName: purchaser
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  authName: purchaser
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        resourceMethod:
          httpMethod: GET
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
    - $type: RESTClientToolTest
      name: REST Client
      testID: 9
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: /assets/items - GET
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
            ignored:
            - operations:
              - true
              - true
              - true
              identifier: "data.content[0].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[1].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[2].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[3].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[4].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[5].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[6].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[7].lastAccessedDate"
            - operations:
              - true
              - true
              - true
              identifier: "data.content[8].lastAccessedDate"
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"status\" : 1,\r\n    \"message\" : \"success\",\r\n    \"\
              data\" : {\r\n        \"totalElements\" : 9,\r\n        \"totalPages\"\
              \ : 1,\r\n        \"size\" : 2000,\r\n        \"number\" : 0,\r\n  \
              \      \"numberOfElements\" : 9,\r\n        \"sort\" : \"name: ASC\"\
              ,\r\n        \"content\" : [\r\n            {\r\n                \"\
              id\" : 7,\r\n                \"name\" : \"3 Person Tent\",\r\n     \
              \           \"description\" : \"A small compact tent good for backpacking\
              \ trips. This tent comfortably sleeps 3 adults.\",\r\n             \
              \   \"inStock\" : 8,\r\n                \"image\" : \"/outdoor/images/tent-3-detail.png\"\
              ,\r\n                \"region\" : \"LOCATION_2\",\r\n              \
              \  \"lastAccessedDate\" : \"2020-07-27T03:33:23.000+00:00\",\r\n   \
              \             \"categoryId\" : 3\r\n            },\r\n            {\r\
              \n                \"id\" : 8,\r\n                \"name\" : \"4 Person\
              \ Tent\",\r\n                \"description\" : \"This tent is good for\
              \ small group or small family camping trips. Comfortably sleeps 4 adults.\"\
              ,\r\n                \"inStock\" : 6,\r\n                \"image\" :\
              \ \"/outdoor/images/tent-4-detail.png\",\r\n                \"region\"\
              \ : \"LOCATION_2\",\r\n                \"lastAccessedDate\" : \"2020-07-27T03:33:23.000+00:00\"\
              ,\r\n                \"categoryId\" : 3\r\n            },\r\n      \
              \      {\r\n                \"id\" : 9,\r\n                \"name\"\
              \ : \"6 Person Tent\",\r\n                \"description\" : \"A tent\
              \ for larger groups or long camping stays. This tent comfortably sleeps\
              \ 6 adults.\",\r\n                \"inStock\" : 2,\r\n             \
              \   \"image\" : \"/outdoor/images/tent-6-detail.png\",\r\n         \
              \       \"region\" : \"LOCATION_2\",\r\n                \"lastAccessedDate\"\
              \ : \"2020-07-27T03:33:23.000+00:00\",\r\n                \"categoryId\"\
              \ : 3\r\n            },\r\n            {\r\n                \"id\" :\
              \ 1,\r\n                \"name\" : \"Blue Sleeping Bag\",\r\n      \
              \          \"description\" : \"This sleeping bag has a temperature rating\
              \ of 25 degrees. The water repellent shell keeps out moisture.\",\r\n\
              \                \"inStock\" : 10,\r\n                \"image\" : \"\
              /outdoor/images/sleepingbag-blue-detail.png\",\r\n                \"\
              region\" : \"LOCATION_1\",\r\n                \"lastAccessedDate\" :\
              \ \"2020-07-27T07:32:23.491+00:00\",\r\n                \"categoryId\"\
              \ : 1\r\n            },\r\n            {\r\n                \"id\" :\
              \ 5,\r\n                \"name\" : \"Day Hike Backpack\",\r\n      \
              \          \"description\" : \"Plenty of storage space to organize gear\
              \ for an all-day adventure.\",\r\n                \"inStock\" : 25,\r\
              \n                \"image\" : \"/outdoor/images/backpack-detail-md.png\"\
              ,\r\n                \"region\" : \"LOCATION_2\",\r\n              \
              \  \"lastAccessedDate\" : \"2020-07-27T03:33:23.000+00:00\",\r\n   \
              \             \"categoryId\" : 2\r\n            },\r\n            {\r\
              \n                \"id\" : 2,\r\n                \"name\" : \"Green\
              \ Sleeping Bag\",\r\n                \"description\" : \"This light\
              \ weight sleeping bag has a temperature rating of 35 degrees. It is\
              \ very compact when rolled up. Good for backpacking trips.\",\r\n  \
              \              \"inStock\" : 15,\r\n                \"image\" : \"/outdoor/images/sleepingbag-green-detail.png\"\
              ,\r\n                \"region\" : \"LOCATION_2\",\r\n              \
              \  \"lastAccessedDate\" : \"2020-07-27T03:33:23.000+00:00\",\r\n   \
              \             \"categoryId\" : 1\r\n            },\r\n            {\r\
              \n                \"id\" : 4,\r\n                \"name\" : \"Light\
              \ Hike Backpack\",\r\n                \"description\" : \"Lightweight\
              \ and simple design with minimal features for quick outdoor excursions.\"\
              ,\r\n                \"inStock\" : 20,\r\n                \"image\"\
              \ : \"/outdoor/images/backpack-detail-sm.png\",\r\n                \"\
              region\" : \"LOCATION_2\",\r\n                \"lastAccessedDate\" :\
              \ \"2020-07-27T03:33:23.000+00:00\",\r\n                \"categoryId\"\
              \ : 2\r\n            },\r\n            {\r\n                \"id\" :\
              \ 6,\r\n                \"name\" : \"Multi-Day Backpack\",\r\n     \
              \           \"description\" : \"Storage space, organizing pockets, and\
              \ features to keep all gear needed for a several-day backpacking trek.\"\
              ,\r\n                \"inStock\" : 10,\r\n                \"image\"\
              \ : \"/outdoor/images/backpack-detail-lg.png\",\r\n                \"\
              region\" : \"LOCATION_2\",\r\n                \"lastAccessedDate\" :\
              \ \"2020-07-27T03:33:23.000+00:00\",\r\n                \"categoryId\"\
              \ : 2\r\n            },\r\n            {\r\n                \"id\" :\
              \ 3,\r\n                \"name\" : \"Pink Sleeping Bag\",\r\n      \
              \          \"description\" : \"This sleeping bag has a temperature rating\
              \ of 25 degrees. The water repellent shell keeps out moisture.\",\r\n\
              \                \"inStock\" : 8,\r\n                \"image\" : \"\
              /outdoor/images/sleepingbag-pink-detail.png\",\r\n                \"\
              region\" : \"LOCATION_1\",\r\n                \"lastAccessedDate\" :\
              \ \"2020-07-27T03:33:23.000+00:00\",\r\n                \"categoryId\"\
              \ : 1\r\n            }\r\n        ]\r\n    }\r\n}"
          mode: 3
        formJson:
          builtFromSchema: true
          value:
            $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: root
              bodyType:
                $type: ComplexType
                attributes:
                - ns: ""
                  name: type
                  defaultValue: object
                  fixed: object
                  contentType:
                    $type: StringType
                    _id: 1
                  required: true
                name: /components/schemas/CategoryDTO
                compositor: true
                compositorObj:
                  $type: AllCompositor
                  types:
                  - $type: ElementType
                    minOccurs: 0
                    localName: name
                    bodyType:
                      $type: ComplexType
                      _id: 2
                      attributes:
                      - ns: ""
                        name: type
                        defaultValue: string
                        fixed: string
                        contentType:
                          $type: StringType
                        required: true
                      content: true
                      contentObj:
                        $type: StringType
                        _ref: 1
                      namespace: urn:parasoft:json
                      name: string
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                  - $type: ElementType
                    minOccurs: 0
                    localName: description
                    bodyType:
                      $type: ComplexType
                      _ref: 2
                  - $type: ElementType
                    minOccurs: 0
                    localName: imagePath
                    bodyType:
                      $type: ComplexType
                      _ref: 2
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
                    values:
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/api-docs/v1
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${SWAGGER}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Form JSON
        literalQuery:
          isPropertiesRef: true
        constrainedQuery:
          parameters:
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: 0
              minOccurs: 0
              localName: categoryId
              bodyType:
                $type: LongType
            replacedColumn: ""
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: LOCATION_1
              minOccurs: 0
              maxOccurs: -1
              localName: regions
              bodyType:
                $type: EnumType
                values:
                - LOCATION_1
                - LOCATION_2
                - LOCATION_3
                - LOCATION_4
                - LOCATION_5
                - LOCATION_6
                - LOCATION_7
                - LOCATION_8
                baseType: string
            replacedColumn: ""
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              minOccurs: 0
              localName: searchString
              bodyType:
                $type: StringType
            replacedColumn: ""
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: 0
              minOccurs: 0
              localName: page
              bodyType:
                $type: BigIntegerType
            replacedColumn: ""
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: 2147483647
              minOccurs: 0
              localName: size
              bodyType:
                $type: BigIntegerType
            replacedColumn: ""
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: "[\"name,ASC\"]"
              minOccurs: 0
              maxOccurs: -1
              localName: sort
              bodyType:
                $type: StringType
            replacedColumn: ""
        resourceMethod:
          resourceId: /v1/assets/items
          httpMethod: GET
        resourceMode: 3
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
            value: http://localhost:8080
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
    - $type: RESTClientToolTest
      name: /assets/items - GET 2
      testID: 133
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: "/assets/items/name/{itemName} - GET"
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
            ignored:
            - operations:
              - true
              - true
              - true
              identifier: data.lastAccessedDate
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"status\" : 1,\r\n    \"message\" : \"success\",\r\n    \"\
              data\" : {\r\n        \"id\" : 8,\r\n        \"name\" : \"4 Person Tent\"\
              ,\r\n        \"description\" : \"This tent is good for small group or\
              \ small family camping trips. Comfortably sleeps 4 adults.\",\r\n  \
              \      \"inStock\" : 6,\r\n        \"image\" : \"/outdoor/images/tent-4-detail.png\"\
              ,\r\n        \"region\" : \"LOCATION_2\",\r\n        \"lastAccessedDate\"\
              \ : \"2020-07-27T03:33:23.000+00:00\",\r\n        \"categoryId\" : 3\r\
              \n    }\r\n}"
          mode: 3
        formJson:
          builtFromSchema: true
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
                    values:
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/api-docs/v1
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${SWAGGER}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
                httpHeaders:
                  properties:
                  - name: Accept
                    value:
                      values:
                      - $type: ScriptedValue
                      fixedValue:
                        $type: StringTestValue
                        value: application/json
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
                httpHeaders:
                  properties:
                  - name: Accept
                    value:
                      values:
                      - $type: ScriptedValue
                      fixedValue:
                        $type: StringTestValue
                        value: application/json
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Form JSON
        literalQuery:
          isPropertiesRef: true
        constrainedPath:
          pathParameters:
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: itemName
              bodyType:
                $type: StringType
            replacedColumn: ""
            values:
            - $type: StringValue
              replacedColumn: ""
              value: 4 Person Tent
        resourceMethod:
          resourceId: "/v1/assets/items/name/{itemName}"
          httpMethod: GET
        resourceMode: 3
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
            value: http://localhost:8080
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
  - $type: TestSuite
    name: GraphQL
    testID: 13
    tests:
    - $type: RESTClientToolTest
      name: REST Client
      testID: 216
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: Check GraphQL schema
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - |
              scalar DateTime
              scalar Long

              type Query {
                  # category
                  getCategoryById(categoryId: Long!): Category
                  getCategoryByName(categoryName: String!): Category
                  getCategories(searchString: String, page: Int, size: Int, sort: [String]): CategoryPageInfo
                  # location
                  getLocation(region: RegionType!): Location
                  getAllRegionTypesOfCurrentIndustry: [RegionType]
                  # order
                  getOrderByOrderNumber(orderNumber: String!): Order
                  getOrders(page: Int, size: Int, sort: [String]): OrderPageInfo
                  getUnreviewedNumber: UnreviewedNumberResponse
                  #item
                  getItems(categoryId: Long, regions: [RegionType], searchString: String, page: Int, size: Int, sort: [String]): ItemPageInfo
                  getItemByItemId(itemId: Long!): Item
                  getItemByName(itemName: String!): Item
                  # cartItem
                  getCartItems: [CartItem]
                  getCartItemByItemId(itemId: Long!): CartItem
              }
              type Mutation {
                  # category
                  addCategory(categoryDTO: CategoryDTO!): Category
                  deleteCategoryById(categoryId: Long!): Int
                  updateCategory(categoryId: Long!, categoryDto: CategoryDTO!): Category
                  # order
                  createOrder(orderDTO: OrderDTO!): Order
                  updateOrderByOrderNumber(orderNumber: String!, orderStatusDTO: OrderStatusDTO!): Order
                  #item
                  updateItemInStockByItemId(itemId: Long!, newInStock: Int!): Item
                  deleteItemByName(itemName: String!): String
                  addNewItem(itemsDTO: ItemsDTO!): Item
                  deleteItemByItemId(itemId: Long!): Long
                  updateItemByItemId(itemId: Long!, itemsDTO: ItemsDTO!): Item
                  # cartItem
                  addItemInCart(shoppingCartDTO: ShoppingCartDTO!): CartItem
                  removeCartItem(itemId: Long!): Long
                  removeAllCartItems: Boolean
                  updateItemInCart(itemId: Long!, updateShoppingCartItemDTO: UpdateShoppingCartItemDTO!): CartItem
              }

              # category
              type Category {
                  id: Long
                  name: String
                  description: String
                  image: String
              }
              type CategoryPageInfo {
                  totalElements: Long
                  totalPages: Int
                  size: Int
                  number: Int
                  numberOfElements: Int
                  sort: String
                  content: [Category]
              }
              input CategoryDTO {
                  name: String!
                  description: String!
                  imagePath: String
              }
              # location
              type Location {
                  id: Long
                  locationInfo: String
                  locationImage: String
              }
              enum RegionType {
                  # Values are generated from the active skin by GraphQLProvider.
                  LOCATION_1
                  LOCATION_2
                  LOCATION_3
                  LOCATION_4
                  LOCATION_5
                  LOCATION_6
                  LOCATION_7
                  LOCATION_8
              }
              # order
              type Order {
                  id: Long
                  orderNumber: String
                  requestedBy: String
                  status: String
                  reviewedByAPV: Boolean
                  reviewedByPRCH: Boolean
                  respondedBy: String
                  orderItems: [OrderItem]
                  region: String
                  location: String
                  orderImage: String
                  receiverId: String
                  eventId: String
                  eventNumber: String
                  submissionDate: DateTime
                  approverReplyDate: DateTime
                  comments: String
              }
              type OrderItem {
                  id: Long
                  name: String
                  description: String
                  image: String
                  itemId: Long
                  quantity: Int
              }
              input OrderDTO {
                  region: RegionType!
                  location: String!
                  receiverId: String!
                  eventId: String!
                  eventNumber: String!
              }
              type OrderPageInfo {
                  totalElements: Long
                  totalPages: Int
                  size: Int
                  number: Int
                  numberOfElements: Int
                  sort: String
                  content: [Order]
              }
              input OrderStatusDTO {
                  status: OrderStatus
                  comments: String
                  reviewedByPRCH: Boolean
                  reviewedByAPV: Boolean
              }
              enum OrderStatus {
                  SUBMITTED
                  PROCESSED
                  CANCELED
                  APPROVED
                  DECLINED
              }
              type UnreviewedNumberResponse{
                  unreviewedByApprover: Int
                  unreviewedByPurchaser: Int
              }
              #item
              type Item {
                  id: Long
                  name: String
                  description: String
                  inStock: Int
                  image: String
                  region: RegionType
                  lastAccessedDate: DateTime
                  categoryId: Long
              }
              type ItemPageInfo {
                  totalElements: Long
                  totalPages: Int
                  size: Int
                  number: Int
                  numberOfElements: Int
                  sort: String
                  content: [Item]
              }
              input ItemsDTO {
                  name: String!
                  description: String!
                  categoryId: Long!
                  inStock: Int!
                  imagePath: String
                  region: RegionType!
              }
              #cartItems
              type CartItem {
                  id: Long
                  userId: Long
                  itemId: Long
                  name: String
                  description: String
                  image: String
                  realInStock: Int
                  quantity: Int
              }
              input ShoppingCartDTO {
                  itemId: Long!
                  itemQty: Int!
              }
              input UpdateShoppingCartItemDTO {
                  itemQty: Int!
              }
          mode: 1
        formJson:
          value:
            $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: root
              bodyType:
                $type: ComplexType
                attributes:
                - ns: ""
                  name: type
                  fixed: object
                  contentType:
                    $type: StringType
                  required: true
                name: rootType
                compositor: true
                compositorObj:
                  $type: AllCompositor
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        constrainToSchema: false
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${GRAPHQL_SDL}"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        resourceMethod:
          httpMethod: GET
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
    - $type: GraphQLClientToolTest
      name: GraphQL Client
      testID: 16
      tool:
        $type: GraphQLClient
        iconName: GraphQLClient
        name: getCategoryById
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"data\" : {\r\n        \"getCategoryById\" : {\r\n      \
              \      \"id\" : 1,\r\n            \"name\" : \"Sleeping bags\",\r\n\
              \            \"description\" : \"Water repellent shell made from polyester\
              \ with synthetic fill. Easy use zipper for safe and quick open or closing.\
              \ Temperature ratings of 25 degrees and 35 degrees.\"\r\n        }\r\
              \n    }\r\n}"
          mode: 3
        formJson:
          value:
            $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: GraphQLQuery
              bodyType:
                $type: ComplexType
                name: anonymous
                compositor: true
                compositorObj:
                  $type: SequenceCompositor
                  types:
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: Query
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _id: 1
                        ns: ""
                        name: name
                        contentType:
                          $type: StringType
                          _id: 2
                        required: true
                      name: QueryType
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          maxOccurs: -1
                          localName: Variable
                          bodyType:
                            $type: ComplexType
                            attributes:
                            - _ref: 1
                            - ns: ""
                              name: type
                              contentType:
                                $type: StringType
                                _ref: 2
                              required: true
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: SequenceCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                localName: Directives
                                bodyType:
                                  $type: ComplexType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: Directives
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - ns: ""
                                    name: "on"
                                    contentType:
                                      $type: StringType
                                      _id: 3
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _id: 4
                                                ns: ""
                                                name: ref
                                                contentType:
                                                  $type: StringType
                                                  _ref: 3
                                                required: true
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 4
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: Mutation
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _ref: 1
                      name: MutationType
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          maxOccurs: -1
                          localName: Variable
                          bodyType:
                            $type: ComplexType
                            attributes:
                            - _ref: 1
                            - ns: ""
                              name: type
                              contentType:
                                $type: StringType
                                _ref: 2
                              required: true
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: SequenceCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                localName: Directives
                                bodyType:
                                  $type: ComplexType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: Directives
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - ns: ""
                                    name: "on"
                                    contentType:
                                      $type: StringType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 4
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 4
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: Subscription
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _ref: 1
                      name: SubscriptionType
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          maxOccurs: -1
                          localName: Variable
                          bodyType:
                            $type: ComplexType
                            attributes:
                            - _ref: 1
                            - ns: ""
                              name: type
                              contentType:
                                $type: StringType
                                _ref: 3
                              required: true
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: SequenceCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                localName: Directives
                                bodyType:
                                  $type: ComplexType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: Directives
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - ns: ""
                                    name: "on"
                                    contentType:
                                      $type: StringType
                                      _ref: 2
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 4
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 4
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: FragmentDefinition
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _ref: 1
                      - ns: ""
                        name: "on"
                        contentType:
                          $type: StringType
                          _id: 5
                        required: true
                      name: FragmentDefinitionType
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          localName: Directives
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                        - $type: ElementType
                          minOccurs: 0
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - ns: ""
                                    name: "on"
                                    contentType:
                                      $type: StringType
                                      _ref: 5
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 4
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 4
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
                    values:
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
          elementTypeName: GraphQLQuery
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/schema.graphqls
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${GRAPHQL_SDL}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${GRAPHQL_URL}"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  authName: purchaser
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  authName: purchaser
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: "query GetCategoryById{\r\n  getCategoryById(categoryId:1)\
              \ {\r\n   id,\r\n   name,\r\n   description\r\n   }\r\n}"
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        resourceMethod:
          httpMethod: GET
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
    - $type: GraphQLClientToolTest
      name: getCategories 2
      testID: 38
      tool:
        $type: GraphQLClient
        iconName: GraphQLClient
        name: 'updateCategory '
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"data\" : {\r\n        \"updateCategory\" : {\r\n       \
              \     \"id\" : 1,\r\n            \"name\" : \"Sleeping bags\",\r\n \
              \           \"description\" : \"lala1\",\r\n            \"image\" :\
              \ \"/outdoor/images/defaultImage.png\"\r\n        }\r\n    }\r\n}"
          mode: 3
        formJson:
          builtFromSchema: true
          value:
            $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: GraphQLQuery
              bodyType:
                $type: ComplexType
                name: anonymous
                compositor: true
                compositorObj:
                  $type: SequenceCompositor
                  types:
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: Query
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _id: 1
                        ns: ""
                        name: name
                        contentType:
                          $type: StringType
                          _id: 2
                        required: true
                      name: anonymous
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          maxOccurs: -1
                          localName: Variable
                          bodyType:
                            $type: ComplexType
                            concretes:
                            - _id: 3
                              attributes:
                              - _ref: 1
                              - ns: ""
                                name: type
                                defaultValue: Boolean!
                                fixed: Boolean!
                                contentType:
                                  $type: StringType
                                  _id: 4
                                required: true
                              name: Boolean!
                              compositor: true
                              compositorObj:
                                $type: SequenceCompositor
                                types:
                                - $type: ElementType
                                  minOccurs: 0
                                  localName: default
                                  bodyType:
                                    $type: ComplexType
                                    _id: 5
                                    attributes:
                                    - ns: ""
                                      name: type
                                      defaultValue: Boolean
                                      fixed: Boolean
                                      contentType:
                                        $type: StringType
                                        _ref: 2
                                      required: true
                                    content: true
                                    contentObj:
                                      $type: BooleanType
                                    name: Boolean
                            - _id: 6
                              attributes:
                              - _ref: 1
                              - ns: ""
                                name: type
                                defaultValue: ID
                                fixed: ID
                                contentType:
                                  $type: StringType
                                  _ref: 2
                                required: true
                              name: ID
                              compositor: true
                              compositorObj:
                                $type: SequenceCompositor
                                types:
                                - $type: ElementType
                                  minOccurs: 0
                                  nillable: true
                                  localName: default
                                  bodyType:
                                    $type: ComplexType
                                    _id: 7
                                    attributes:
                                    - ns: ""
                                      name: type
                                      defaultValue: String
                                      fixed: String
                                      contentType:
                                        $type: StringType
                                        _id: 8
                                      required: true
                                    content: true
                                    contentObj:
                                      $type: StringType
                                      _ref: 4
                                    name: ID
                            - _id: 9
                              attributes:
                              - _ref: 1
                              - ns: ""
                                name: type
                                defaultValue: String
                                fixed: String
                                contentType:
                                  $type: StringType
                                  _ref: 8
                                required: true
                              name: String
                              compositor: true
                              compositorObj:
                                $type: SequenceCompositor
                                types:
                                - $type: ElementType
                                  minOccurs: 0
                                  nillable: true
                                  localName: default
                                  bodyType:
                                    $type: ComplexType
                                    _id: 10
                                    attributes:
                                    - ns: ""
                                      name: type
                                      defaultValue: String
                                      fixed: String
                                      contentType:
                                        $type: StringType
                                        _ref: 4
                                      required: true
                                    content: true
                                    contentObj:
                                      $type: StringType
                                      _ref: 2
                                    name: String
                            isDerived: true
                            isAbstract: true
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: SequenceCompositor
                        - $type: ElementType
                          _id: 11
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _id: 12
                                            minOccurs: 0
                                            localName: include
                                            bodyType:
                                              $type: ComplexType
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                                                types:
                                                - $type: ElementType
                                                  concretes:
                                                  - localName: if
                                                    bodyType:
                                                      $type: ComplexType
                                                      _id: 13
                                                      attributes:
                                                      - ns: ""
                                                        name: type
                                                        defaultValue: VarRef
                                                        fixed: VarRef
                                                        contentType:
                                                          $type: StringType
                                                        required: true
                                                      content: true
                                                      contentObj:
                                                        $type: StringType
                                                        _ref: 8
                                                      name: VarRef
                                                  localName: if
                                                  bodyType:
                                                    $type: ComplexType
                                                    _ref: 5
                                          - $type: ElementType
                                            _id: 14
                                            minOccurs: 0
                                            localName: skip
                                            bodyType:
                                              $type: ComplexType
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                                                types:
                                                - $type: ElementType
                                                  concretes:
                                                  - localName: if
                                                    bodyType:
                                                      $type: ComplexType
                                                      _ref: 13
                                                  localName: if
                                                  bodyType:
                                                    $type: ComplexType
                                                    _ref: 5
                                    - $type: ElementType
                                      _ref: 11
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _id: 15
                                    ns: ""
                                    name: ref
                                    contentType:
                                      $type: StringType
                                      _ref: 4
                                    required: true
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                                    types:
                                    - $type: ElementType
                                      _ref: 12
                                    - $type: ElementType
                                      _ref: 14
                              - $type: ElementType
                                minOccurs: 0
                                localName: getCategoryById
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _id: 16
                                    ns: ""
                                    name: alias
                                    contentType:
                                      $type: StringType
                                      _ref: 2
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Arguments
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            concretes:
                                            - nillable: true
                                              localName: categoryId
                                              bodyType:
                                                $type: ComplexType
                                                _ref: 13
                                            minOccurs: 0
                                            nillable: true
                                            localName: categoryId
                                            bodyType:
                                              $type: ComplexType
                                              _ref: 7
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _ref: 12
                                          - $type: ElementType
                                            _ref: 14
                                    - $type: ElementType
                                      _id: 17
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: InlineFragment
                                            bodyType:
                                              $type: ComplexType
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                                - $type: ElementType
                                                  _ref: 17
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 15
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                                                types:
                                                - $type: ElementType
                                                  _ref: 12
                                                - $type: ElementType
                                                  _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: status
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: message
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: data
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                                - $type: ElementType
                                                  _id: 18
                                                  localName: SelectionSet
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        maxOccurs: -1
                                                        localName: InlineFragment
                                                        bodyType:
                                                          $type: ComplexType
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: SequenceCompositor
                                                            types:
                                                            - $type: ElementType
                                                              minOccurs: 0
                                                              localName: Directives
                                                              bodyType:
                                                                $type: ComplexType
                                                                name: anonymous
                                                                compositor: true
                                                                compositorObj:
                                                                  $type: AllCompositor
                                                                  types:
                                                                  - $type: ElementType
                                                                    _ref: 12
                                                                  - $type: ElementType
                                                                    _ref: 14
                                                            - $type: ElementType
                                                              _ref: 18
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        maxOccurs: -1
                                                        localName: FragmentSpread
                                                        bodyType:
                                                          $type: ComplexType
                                                          attributes:
                                                          - _ref: 15
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: AllCompositor
                                                            types:
                                                            - $type: ElementType
                                                              _ref: 12
                                                            - $type: ElementType
                                                              _ref: 14
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        localName: id
                                                        bodyType:
                                                          $type: ComplexType
                                                          attributes:
                                                          - _ref: 16
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: SequenceCompositor
                                                            types:
                                                            - $type: ElementType
                                                              minOccurs: 0
                                                              localName: Directives
                                                              bodyType:
                                                                $type: ComplexType
                                                                name: anonymous
                                                                compositor: true
                                                                compositorObj:
                                                                  $type: AllCompositor
                                                                  types:
                                                                  - $type: ElementType
                                                                    _ref: 12
                                                                  - $type: ElementType
                                                                    _ref: 14
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        localName: name
                                                        bodyType:
                                                          $type: ComplexType
                                                          attributes:
                                                          - _ref: 16
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: SequenceCompositor
                                                            types:
                                                            - $type: ElementType
                                                              minOccurs: 0
                                                              localName: Directives
                                                              bodyType:
                                                                $type: ComplexType
                                                                name: anonymous
                                                                compositor: true
                                                                compositorObj:
                                                                  $type: AllCompositor
                                                                  types:
                                                                  - $type: ElementType
                                                                    _ref: 12
                                                                  - $type: ElementType
                                                                    _ref: 14
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        localName: description
                                                        bodyType:
                                                          $type: ComplexType
                                                          attributes:
                                                          - _ref: 16
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: SequenceCompositor
                                                            types:
                                                            - $type: ElementType
                                                              minOccurs: 0
                                                              localName: Directives
                                                              bodyType:
                                                                $type: ComplexType
                                                                name: anonymous
                                                                compositor: true
                                                                compositorObj:
                                                                  $type: AllCompositor
                                                                  types:
                                                                  - $type: ElementType
                                                                    _ref: 12
                                                                  - $type: ElementType
                                                                    _ref: 14
                                                      - $type: ElementType
                                                        minOccurs: 0
                                                        localName: image
                                                        bodyType:
                                                          $type: ComplexType
                                                          attributes:
                                                          - _ref: 16
                                                          name: anonymous
                                                          compositor: true
                                                          compositorObj:
                                                            $type: SequenceCompositor
                                                            types:
                                                            - $type: ElementType
                                                              minOccurs: 0
                                                              localName: Directives
                                                              bodyType:
                                                                $type: ComplexType
                                                                name: anonymous
                                                                compositor: true
                                                                compositorObj:
                                                                  $type: AllCompositor
                                                                  types:
                                                                  - $type: ElementType
                                                                    _ref: 12
                                                                  - $type: ElementType
                                                                    _ref: 14
                              - $type: ElementType
                                minOccurs: 0
                                localName: getCategoryByName
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 16
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Arguments
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            concretes:
                                            - nillable: true
                                              localName: categoryName
                                              bodyType:
                                                $type: ComplexType
                                                _ref: 13
                                            minOccurs: 0
                                            nillable: true
                                            localName: categoryName
                                            bodyType:
                                              $type: ComplexType
                                              _ref: 10
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _ref: 12
                                          - $type: ElementType
                                            _ref: 14
                                    - $type: ElementType
                                      _ref: 18
                              - $type: ElementType
                                minOccurs: 0
                                localName: getCategories
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 16
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _ref: 12
                                          - $type: ElementType
                                            _ref: 14
                                    - $type: ElementType
                                      _id: 19
                                      localName: SelectionSet
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: InlineFragment
                                            bodyType:
                                              $type: ComplexType
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                                - $type: ElementType
                                                  _ref: 19
                                          - $type: ElementType
                                            minOccurs: 0
                                            maxOccurs: -1
                                            localName: FragmentSpread
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 15
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: AllCompositor
                                                types:
                                                - $type: ElementType
                                                  _ref: 12
                                                - $type: ElementType
                                                  _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: totalElements
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: totalPages
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: size
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: number
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: numberOfElements
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: sort
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                          - $type: ElementType
                                            minOccurs: 0
                                            localName: content
                                            bodyType:
                                              $type: ComplexType
                                              attributes:
                                              - _ref: 16
                                              name: anonymous
                                              compositor: true
                                              compositorObj:
                                                $type: SequenceCompositor
                                                types:
                                                - $type: ElementType
                                                  minOccurs: 0
                                                  localName: Directives
                                                  bodyType:
                                                    $type: ComplexType
                                                    name: anonymous
                                                    compositor: true
                                                    compositorObj:
                                                      $type: AllCompositor
                                                      types:
                                                      - $type: ElementType
                                                        _ref: 12
                                                      - $type: ElementType
                                                        _ref: 14
                                                - $type: ElementType
                                                  _ref: 18
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: Mutation
                    bodyType:
                      $type: ComplexType
                      attributes:
                      - _ref: 1
                      name: anonymous
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
                        types:
                        - $type: ElementType
                          minOccurs: 0
                          maxOccurs: -1
                          localName: Variable
                          bodyType:
                            $type: ComplexType
                            concretes:
                            - _ref: 3
                            - attributes:
                              - _ref: 1
                              - ns: ""
                                name: type
                                defaultValue: CategoryDTO
                                fixed: CategoryDTO
                                contentType:
                                  $type: StringType
                                  _id: 20
                                required: true
                              name: CategoryDTO
                              compositor: true
                              compositorObj:
                                $type: SequenceCompositor
                                types:
                                - $type: ElementType
                                  minOccurs: 0
                                  nillable: true
                                  localName: default
                                  bodyType:
                                    $type: ComplexType
                                    _id: 21
                                    attributes:
                                    - ns: ""
                                      name: type
                                      defaultValue: Object
                                      fixed: Object
                                      contentType:
                                        $type: StringType
                                        _ref: 20
                                      required: true
                                    name: CategoryDTO
                                    compositor: true
                                    compositorObj:
                                      $type: AllCompositor
                                      types:
                                      - $type: ElementType
                                        concretes:
                                        - nillable: true
                                          localName: name
                                          bodyType:
                                            $type: ComplexType
                                            _ref: 13
                                        minOccurs: 0
                                        nillable: true
                                        localName: name
                                        bodyType:
                                          $type: ComplexType
                                          _ref: 10
                                      - $type: ElementType
                                        concretes:
                                        - nillable: true
                                          localName: description
                                          bodyType:
                                            $type: ComplexType
                                            _ref: 13
                                        minOccurs: 0
                                        nillable: true
                                        localName: description
                                        bodyType:
                                          $type: ComplexType
                                          _ref: 10
                                      - $type: ElementType
                                        concretes:
                                        - nillable: true
                                          localName: imagePath
                                          bodyType:
                                            $type: ComplexType
                                            _ref: 13
                                        minOccurs: 0
                                        nillable: true
                                        localName: imagePath
                                        bodyType:
                                          $type: ComplexType
                                          _ref: 10
                            - _ref: 6
                            - _ref: 9
                            isDerived: true
                            isAbstract: true
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: SequenceCompositor
                        - $type: ElementType
                          _id: 22
                          localName: SelectionSet
                          bodyType:
                            $type: ComplexType
                            name: anonymous
                            compositor: true
                            compositorObj:
                              $type: AllCompositor
                              types:
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: InlineFragment
                                bodyType:
                                  $type: ComplexType
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _ref: 12
                                          - $type: ElementType
                                            _ref: 14
                                    - $type: ElementType
                                      _ref: 22
                              - $type: ElementType
                                minOccurs: 0
                                maxOccurs: -1
                                localName: FragmentSpread
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 15
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: AllCompositor
                                    types:
                                    - $type: ElementType
                                      _ref: 12
                                    - $type: ElementType
                                      _ref: 14
                              - $type: ElementType
                                minOccurs: 0
                                localName: updateCategory
                                bodyType:
                                  $type: ComplexType
                                  attributes:
                                  - _ref: 16
                                  name: anonymous
                                  compositor: true
                                  compositorObj:
                                    $type: SequenceCompositor
                                    types:
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Arguments
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            concretes:
                                            - nillable: true
                                              localName: categoryId
                                              bodyType:
                                                $type: ComplexType
                                                _ref: 13
                                            minOccurs: 0
                                            nillable: true
                                            localName: categoryId
                                            bodyType:
                                              $type: ComplexType
                                              _ref: 7
                                          - $type: ElementType
                                            concretes:
                                            - nillable: true
                                              localName: categoryDto
                                              bodyType:
                                                $type: ComplexType
                                                _ref: 13
                                            minOccurs: 0
                                            nillable: true
                                            localName: categoryDto
                                            bodyType:
                                              $type: ComplexType
                                              _ref: 21
                                    - $type: ElementType
                                      minOccurs: 0
                                      localName: Directives
                                      bodyType:
                                        $type: ComplexType
                                        name: anonymous
                                        compositor: true
                                        compositorObj:
                                          $type: AllCompositor
                                          types:
                                          - $type: ElementType
                                            _ref: 12
                                          - $type: ElementType
                                            _ref: 14
                                    - $type: ElementType
                                      _ref: 18
                  - $type: ElementType
                    minOccurs: 0
                    maxOccurs: -1
                    localName: FragmentDefinition
                    bodyType:
                      $type: ComplexType
                      concretes:
                      - attributes:
                        - ns: ""
                          name: "on"
                          defaultValue: Category
                          fixed: Category
                          contentType:
                            $type: StringType
                            _ref: 20
                          required: true
                        - _ref: 1
                        name: Category
                        compositor: true
                        compositorObj:
                          $type: SequenceCompositor
                          types:
                          - $type: ElementType
                            _ref: 18
                      - attributes:
                        - ns: ""
                          name: "on"
                          defaultValue: CategoryPageInfo
                          fixed: CategoryPageInfo
                          contentType:
                            $type: StringType
                            _ref: 20
                          required: true
                        - _ref: 1
                        name: CategoryPageInfo
                        compositor: true
                        compositorObj:
                          $type: SequenceCompositor
                          types:
                          - $type: ElementType
                            _ref: 19
                      - attributes:
                        - ns: ""
                          name: "on"
                          defaultValue: CategoryResult
                          fixed: CategoryResult
                          contentType:
                            $type: StringType
                            _ref: 20
                          required: true
                        - _ref: 1
                        name: CategoryResult
                        compositor: true
                        compositorObj:
                          $type: SequenceCompositor
                          types:
                          - $type: ElementType
                            _ref: 17
                      - attributes:
                        - ns: ""
                          name: "on"
                          defaultValue: Mutation
                          fixed: Mutation
                          contentType:
                            $type: StringType
                            _ref: 20
                          required: true
                        - _ref: 1
                        name: Mutation
                        compositor: true
                        compositorObj:
                          $type: SequenceCompositor
                          types:
                          - $type: ElementType
                            _ref: 22
                      - attributes:
                        - ns: ""
                          name: "on"
                          defaultValue: Query
                          fixed: Query
                          contentType:
                            $type: StringType
                            _ref: 20
                          required: true
                        - _ref: 1
                        name: Query
                        compositor: true
                        compositorObj:
                          $type: SequenceCompositor
                          types:
                          - $type: ElementType
                            _ref: 11
                      isDerived: true
                      isAbstract: true
                      name: anonymous
                      compositor: true
                      compositorObj:
                        $type: SequenceCompositor
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
                    values:
                    - $type: ElementValue
                      replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
                      values:
                      - $type: ComplexValue
                        replacedColumn: ""
                        allowArrayExclude: true
                        attributes:
                        - replacedColumn: ""
                          value:
                            $type: StringValue
                            replacedColumn: ""
                            value: a
                          useValue: true
                        compositorValue: true
                        compositorValueObj:
                          replacedColumn: ""
                          values:
                            $type: CompositorValueSetCollectionSet
                            set:
                            - $type: CompositorValueSet
                              values:
                              - $type: ElementValue
                                replacedColumn: ""
                              - $type: ElementValue
                                replacedColumn: ""
                                values:
                                - $type: ComplexValue
                                  replacedColumn: ""
                                  compositorValue: true
                                  compositorValueObj:
                                    replacedColumn: ""
                                    values:
                                      $type: CompositorValueSetCollectionSet
                                      set:
                                      - $type: CompositorValueSet
                                        values:
                                        - $type: ElementValue
                                          replacedColumn: ""
                                        - $type: ElementValue
                                          replacedColumn: ""
                                        - $type: ElementValue
                                          replacedColumn: ""
                                          values:
                                          - $type: ComplexValue
                                            replacedColumn: ""
                                            allowArrayExclude: true
                                            attributes:
                                            - replacedColumn: ""
                                              value:
                                                $type: StringValue
                                                replacedColumn: ""
                                                value: ""
                                            compositorValue: true
                                            compositorValueObj:
                                              replacedColumn: ""
                                              values:
                                                $type: CompositorValueSetCollectionSet
                                                set:
                                                - $type: CompositorValueSet
                                                  values:
                                                  - $type: ElementValue
                                                    replacedColumn: ""
                                                    values:
                                                    - $type: ComplexValue
                                                      replacedColumn: ""
                                                      allowArrayExclude: true
                                                      compositorValue: true
                                                      compositorValueObj:
                                                        replacedColumn: ""
                                                        values:
                                                          $type: CompositorValueSetCollectionSet
                                                          set:
                                                          - $type: CompositorValueSet
                                                            values:
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: String
                                                                  useValue: true
                                                                contentValue: true
                                                                contentValueObj:
                                                                  $type: StringValue
                                                                  replacedColumn: ""
                                                                  value: 1
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: Object
                                                                  useValue: true
                                                                compositorValue: true
                                                                compositorValueObj:
                                                                  replacedColumn: ""
                                                                  values:
                                                                    $type: CompositorValueSetCollectionSet
                                                                    set:
                                                                    - $type: CompositorValueSet
                                                                      values:
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                                        values:
                                                                        - $type: ComplexValue
                                                                          replacedColumn: ""
                                                                          allowArrayExclude: true
                                                                          attributes:
                                                                          - replacedColumn: ""
                                                                            value:
                                                                              $type: StringValue
                                                                              replacedColumn: ""
                                                                              value: String
                                                                            useValue: true
                                                                          contentValue: true
                                                                          contentValueObj:
                                                                            $type: StringValue
                                                                            replacedColumn: ""
                                                                            value: haha
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                                        values:
                                                                        - $type: ComplexValue
                                                                          replacedColumn: ""
                                                                          allowArrayExclude: true
                                                                          attributes:
                                                                          - replacedColumn: ""
                                                                            value:
                                                                              $type: StringValue
                                                                              replacedColumn: ""
                                                                              value: String
                                                                            useValue: true
                                                                          contentValue: true
                                                                          contentValueObj:
                                                                            $type: StringValue
                                                                            replacedColumn: ""
                                                                            value: lala
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                                        values:
                                                                        - $type: ComplexValue
                                                                          replacedColumn: ""
                                                                          allowArrayExclude: true
                                                                          attributes:
                                                                          - replacedColumn: ""
                                                                            value:
                                                                              $type: StringValue
                                                                              replacedColumn: ""
                                                                              value: String
                                                                            useValue: true
                                                                          contentValue: true
                                                                          contentValueObj:
                                                                            $type: StringValue
                                                                            replacedColumn: ""
                                                                            value: tata
                                                  - $type: ElementValue
                                                    replacedColumn: ""
                                                  - $type: ElementValue
                                                    replacedColumn: ""
                                                    values:
                                                    - $type: ComplexValue
                                                      replacedColumn: ""
                                                      compositorValue: true
                                                      compositorValueObj:
                                                        replacedColumn: ""
                                                        values:
                                                          $type: CompositorValueSetCollectionSet
                                                          set:
                                                          - $type: CompositorValueSet
                                                            values:
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: ""
                                                                compositorValue: true
                                                                compositorValueObj:
                                                                  replacedColumn: ""
                                                                  values:
                                                                    $type: CompositorValueSetCollectionSet
                                                                    set:
                                                                    - $type: CompositorValueSet
                                                                      values:
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: ""
                                                                compositorValue: true
                                                                compositorValueObj:
                                                                  replacedColumn: ""
                                                                  values:
                                                                    $type: CompositorValueSetCollectionSet
                                                                    set:
                                                                    - $type: CompositorValueSet
                                                                      values:
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: ""
                                                                compositorValue: true
                                                                compositorValueObj:
                                                                  replacedColumn: ""
                                                                  values:
                                                                    $type: CompositorValueSetCollectionSet
                                                                    set:
                                                                    - $type: CompositorValueSet
                                                                      values:
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                                                            - $type: ElementValue
                                                              replacedColumn: ""
                                                              values:
                                                              - $type: ComplexValue
                                                                replacedColumn: ""
                                                                allowArrayExclude: true
                                                                attributes:
                                                                - replacedColumn: ""
                                                                  value:
                                                                    $type: StringValue
                                                                    replacedColumn: ""
                                                                    value: ""
                                                                compositorValue: true
                                                                compositorValueObj:
                                                                  replacedColumn: ""
                                                                  values:
                                                                    $type: CompositorValueSetCollectionSet
                                                                    set:
                                                                    - $type: CompositorValueSet
                                                                      values:
                                                                      - $type: ElementValue
                                                                        replacedColumn: ""
                    - $type: ElementValue
                      replacedColumn: ""
          elementTypeName: GraphQLQuery
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/schema.graphqls
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${GRAPHQL_SDL}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${GRAPHQL_URL}"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: |
              mutation UpdateCategory {
                updateCategory(categoryId: "1", categoryDto: {name : "Sleeping bags", description : "lala1"}) {
                  id
                  name
                  description
                  image
                }
              }
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        resourceMethod:
          httpMethod: GET
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
  - $type: TestSuite
    name: JMS
    testID: 19
    tests:
    - $type: ToolTest
      name: DB Tool
      testID: 53
      tool:
        $type: DbTool
        iconName: DBTool
        name: Create order in DB
        magicToken:
          fixedValue:
            $type: StringTestValue
            value: GO
        outputProviders:
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            name: SQL Query
          xmlResponseOutput:
            $type: NamedXMLToolOutputProvider
            name: Results as XML
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        sqlQuery:
          fixedValue:
            $type: StringTestValue
            useMultipleLines: true
            value: "INSERT INTO PUBLIC.PUBLIC.TBL_ORDER\r\n(ID, REQUESTED_BY, STATUS,\
              \ REVIEWED_BY_PRCH, REVIEWED_BY_APV, RESPONDED_BY, ORDER_NUMBER, REGION,\
              \ LOCATION, ORDER_IMAGE, RECEIVER_ID, EVENT_ID, EVENT_NUMBER, SUBMISSION_DATE,\
              \ APPROVER_REPLY_DATE, COMMENTS)\r\nVALUES(2, 'purchaser', 'SUBMITTED',\
              \ 1, 0, NULL, '23-456-002', 'UNITED_KINGDOM', '0.1° E, 51.30° N', '/defense/images/united_kingdom_map.jpg',\
              \ 'demo', 'demo', 'demo', '2022-11-03 17:07:57.818', NULL, NULL);\r\n\
              \r\n\r\n\r\n"
        failOnSQLException: true
        account:
          local:
            $type: DbConfigSettings
            password: AwAAABhGaGswYmZHOGNLYzB0MHBIeEc2clJ3PT0=
            uri: "${DB_URL}"
            username: SA
            driver: org.hsqldb.jdbcDriver
    - $type: RESTClientToolTest
      name: "/v1/assets/items/{itemId} - GET"
      testID: 64
      performanceGroup: 7
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: "GET  /v1/assets/items/{itemId} - get current stock"
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
            ignored:
            - operations:
              - true
              - true
              - true
              identifier: data.lastAccessedDate
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"status\" : 1,\r\n    \"message\" : \"success\",\r\n    \"\
              data\" : {\r\n        \"id\" : 1,\r\n        \"name\" : \"Blue Sleeping\
              \ Bag\",\r\n        \"description\" : \"This sleeping bag has a temperature\
              \ rating of 25 degrees. The water repellent shell keeps out moisture.\"\
              ,\r\n        \"inStock\" : 10,\r\n        \"image\" : \"/outdoor/images/sleepingbag-blue-detail.png\"\
              ,\r\n        \"region\" : \"LOCATION_1\",\r\n        \"lastAccessedDate\"\
              \ : \"2020-07-27T07:32:23.491+00:00\",\r\n        \"categoryId\" : 1\r\
              \n    }\r\n}"
          mode: 3
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                  xmlEncoding: 2
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/api-docs/v1
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${SWAGGER}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        validResponseRange:
          validResponseRange:
            values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              validResponseRange: 200
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "http://localhost:8080/v1/assets/items/{itemId}"
        transportProperties:
          manager:
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        literalPath:
          pathElements:
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: v1
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: assets
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: items
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: "{itemId}"
        constrainedPath:
          pathParameters:
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              defaultValue: 0
              localName: itemId
              bodyType:
                $type: LongType
            replacedColumn: ""
            values:
            - $type: LongValue
              replacedColumn: ""
              value: 1
        resourceMethod:
          resourceId: "/v1/assets/items/{itemId}"
          httpMethod: GET
        resourceMode: 3
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
            value: http://localhost:8080
          fixedValue:
            $type: StringTestValue
            value: http://localhost:8080
          selectedIndex: 1
    - $type: HTTPClientToolTest
      name: DECREASE - itemOutOfStock 2
      testID: 65
      tool:
        $type: HTTPClient
        iconName: HTTPClient
        name: DECREASE - item in stock
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"operation\" : \"DECREASE\",\r\n    \"orderNumber\" : \"\
              23-456-002\",\r\n    \"status\" : \"SUCCESS\"\r\n}"
          mode: 3
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: ""
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        transportProperties:
          manager:
            protocol: 2
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: POST
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: JNDIProperties
              msgProperties:
              - $type: StringNameValuePair
                name: qualify_class_name
                value:
                  values:
                  - $type: ScriptedValue
                  - $type: UniqueValue
                  fixedValue:
                    $type: StringTestValue
                    value: com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO
                string: String
              msgExpiration:
                $type: JMSMessageExpiration
              bytesMessageReadMethod:
                string: readUTF()
              createConsumerBeforeSend: true
              responseCorrelation:
                correlationId:
                  name: JMSCorrelationID
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                messageSelector: ""
              queueProperties:
                jmsDestination:
                  name: JMSDestination
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                      value: inventory.request
                jmsReplyTo:
                  name: JMSReplyTo
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                      value: JMS_ResponseQueue
              msgType:
                string: javax.jms.TextMessage
              useP2P:
                bool: true
              connProperties:
                jmsFactory:
                  name: Connection Factory
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: ConnectionFactory
                jndiFactory:
                  name: Initial Context
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: org.apache.activemq.jndi.ActiveMQInitialContextFactory
                providerURL:
                  name: Provider URL
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: "${JMS_URL}"
                auth:
                  service:
                    name: Service
                    value:
                      fixedValue:
                        $type: StringTestValue
                  useAuth: true
                  username:
                    name: Username
                    value:
                      fixedValue:
                        $type: StringTestValue
                        value: admin
                  password:
                    name: Password
                    value:
                      fixedValue:
                        $type: PasswordTestValue
                        password: AwAAABh0SEJJU3JQcnhjODNQOEJwNXJlb3N3PT0=
              useSync: true
            - $type: SonicMQJNDIProperties
              msgExpiration:
                $type: JMSMessageExpiration
              bytesMessageReadMethod:
                string: readUTF()
              responseCorrelation:
                correlationId:
                  name: JMSCorrelationID
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                messageSelector: ""
              queueProperties:
                jmsDestination:
                  name: JMSDestination
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                      value: JMS_RequestQueue
                jmsReplyTo:
                  name: JMSReplyTo
                  value:
                    values:
                    - $type: ScriptedValue
                    fixedValue:
                      $type: StringTestValue
                      value: JMS_ResponseQueue
              msgType:
                string: progress.message.jclient.MultipartMessage
              useP2P:
                bool: true
              connProperties:
                initialContextProps:
                - name: java.naming.security.credentials
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: Administrator
                - name: java.naming.security.principal
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: Administrator
                jmsFactory:
                  name: Connection Factory
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: ConnectionFactory
                jndiFactory:
                  name: Initial Context
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: com.sonicsw.jndi.mfcontext.MFContextFactory
                providerURL:
                  name: Provider URL
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: "tcp://[hostname]:2506"
                auth:
                  service:
                    name: Service
                    value:
                      fixedValue:
                        $type: StringTestValue
                  username:
                    name: Username
                    value:
                      fixedValue:
                        $type: StringTestValue
                        value: Administrator
                  password:
                    name: Password
                    value:
                      fixedValue:
                        $type: PasswordTestValue
              useSync: true
              partContentID:
                string: part1
            - $type: MQProperties
              mqrfh2fields:
                version:
                  name: Version
                  value:
                    fixedValue:
                      $type: StringTestValue
                encoding:
                  name: Encoding
                  value:
                    fixedValue:
                      $type: StringTestValue
                codedCharSetId:
                  name: CodedCharSetId
                  value:
                    fixedValue:
                      $type: StringTestValue
                format:
                  name: Format
                  value:
                    fixedValue:
                      $type: StringTestValue
                flags:
                  name: Flags
                  value:
                    fixedValue:
                      $type: StringTestValue
                nameValueCodedCharSetId:
                  name: NameValueCodedCharSetId
                  value:
                    fixedValue:
                      $type: StringTestValue
              mcd:
                msd:
                  name: Message Service Domain
                  value:
                    fixedValue:
                      $type: StringTestValue
                set:
                  name: Message Set
                  value:
                    fixedValue:
                      $type: StringTestValue
                type:
                  name: Message Type
                  value:
                    fixedValue:
                      $type: StringTestValue
                fmt:
                  name: Output Format
                  value:
                    fixedValue:
                      $type: StringTestValue
              psc:
                subIdentity:
                  name: SubIdentity
                  value:
                    fixedValue:
                      $type: StringTestValue
                qName:
                  name: QName
                  value:
                    fixedValue:
                      $type: StringTestValue
                qMgrName:
                  name: QMgrName
                  value:
                    fixedValue:
                      $type: StringTestValue
                subPoint:
                  name: SubPoint
                  value:
                    fixedValue:
                      $type: StringTestValue
                filter:
                  name: Filter
                  value:
                    fixedValue:
                      $type: StringTestValue
                subName:
                  name: SubName
                  value:
                    fixedValue:
                      $type: StringTestValue
                subUserData:
                  name: SubUserData
                  value:
                    fixedValue:
                      $type: StringTestValue
              jms:
                destination:
                  name: Destination
                  value:
                    fixedValue:
                      $type: StringTestValue
                replyTo:
                  name: Reply To
                  value:
                    fixedValue:
                      $type: StringTestValue
                timestamp:
                  name: Timestamp
                  value:
                    fixedValue:
                      $type: StringTestValue
                expiration:
                  name: Expiration
                  value:
                    fixedValue:
                      $type: StringTestValue
                correlationId:
                  name: Correlation Id
                  value:
                    fixedValue:
                      $type: StringTestValue
                priority:
                  name: Priority
                  value:
                    fixedValue:
                      $type: StringTestValue
                deliveryMode:
                  name: Delivery Mode
                  value:
                    fixedValue:
                      $type: StringTestValue
              mqQueueManager:
                ccsid:
                  name: CCSID
                  value:
                    fixedValue:
                      $type: StringTestValue
              mqSSL:
                peerName:
                  name: Peer Name
                  value:
                    fixedValue:
                      $type: StringTestValue
                cipherSuite:
                  $type: EnumNameValuePair
                  name: CipherSuite
                  value:
                    fixedValue:
                      $type: StringTestValue
              conn:
                keepAliveConnection: false
                putManagerId:
                  name: Put Manager Id
                  value:
                    fixedValue:
                      $type: StringTestValue
                getManagerId:
                  name: Get Manager Id
                  value:
                    fixedValue:
                      $type: StringTestValue
                channel:
                  name: Channel
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: CHANNEL1
                host:
                  name: Host
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: host
                port:
                  name: Port
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: 1414
                manager:
                  name: Queue Manager
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: queue.manager.1
                putQueue:
                  name: Put Queue
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: PutQueue
                getQueue:
                  name: Get Queue
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: GetQueue
                auth:
                  service:
                    name: Service
                    value:
                      fixedValue:
                        $type: StringTestValue
                  username:
                    name: Username
                    value:
                      fixedValue:
                        $type: StringTestValue
                  password:
                    name: Password
                    value:
                      fixedValue:
                        $type: PasswordTestValue
              mqpmMisc:
                applicationIdData:
                  name: Application ID data
                  value:
                    fixedValue:
                      $type: StringTestValue
                correlationID:
                  name: Correlation ID
                  value:
                    fixedValue:
                      $type: StringTestValue
                expiry:
                  name: Expiry (in tenths of a second)
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: -1
                messageSequenceNumber:
                  name: Message sequence number
                  value:
                    fixedValue:
                      $type: StringTestValue
                replyToQueueManagerName:
                  name: Reply queue manager name
                  value:
                    fixedValue:
                      $type: StringTestValue
                replyToQueueName:
                  name: Reply queue name
                  value:
                    fixedValue:
                      $type: StringTestValue
                putApplicationName:
                  name: Put application name
                  value:
                    fixedValue:
                      $type: StringTestValue
                applicationOriginData:
                  name: Originating application data
                  value:
                    fixedValue:
                      $type: StringTestValue
                userId:
                  name: User ID
                  value:
                    fixedValue:
                      $type: StringTestValue
              mqgmMisc:
                correlationID:
                  name: Correlation Id
                  value:
                    fixedValue:
                      $type: StringTestValue
                waitInterval:
                  name: Wait Interval(in milliseconds)
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: -1
            - $type: RMIProperties
              hostName:
                name: Host
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Host
              portNumber:
                name: Port
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Port
              bindingName:
                name: Binding
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Binding
              remoteInterface:
                name: Remote Interface
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Remote Interface
              methodName:
                name: Method Name
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Method Name
            - $type: SMTPConnectionProperties
              port:
                name: Port
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: 25
              host:
                name: SMTP Host
                value:
                  fixedValue:
                    $type: StringTestValue
              user:
                name: Username
                value:
                  fixedValue:
                    $type: StringTestValue
              pass:
                name: Password
                value:
                  fixedValue:
                    $type: PasswordTestValue
                    password: AwAAAAA=
              fromEmail:
                name: From Email
                value:
                  fixedValue:
                    $type: StringTestValue
              toEmail:
                name: To Email
                value:
                  fixedValue:
                    $type: StringTestValue
              subject:
                name: Subject
                value:
                  fixedValue:
                    $type: StringTestValue
            - $type: TIBCOProperties
              timeLimit:
                name: Time Limit
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: 0
              service:
                name: ""
                value:
                  fixedValue:
                    $type: StringTestValue
              network:
                name: ""
                value:
                  fixedValue:
                    $type: StringTestValue
              daemon:
                name: DAEMON
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: DAEMON
              sendSubject:
                name: Send Subject
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Send Subject
              replySubject:
                name: Reply Field Name
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Reply Field Name
              sendFieldName:
                name: Send Field Name
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Send Field Name
              replyFieldName:
                name: Reply Field Name
                value:
                  fixedValue:
                    $type: StringTestValue
                    value: Reply Field Name
            - $type: NoneTransportProperties
            - $type: CustomTransportProperties
              selected: 2
              configs:
              - $type: CustomTransportConfigurationImpl
                ConfigClassName: com.parasoft.soavirt.transport.websocket.WebSocketTransport
                values:
                  customValueMap:
                  - connectionId
                  - $type: MultiValueCustomValue
                  - connectionTimeout
                  - $type: MultiValueCustomValue
                  - frame_type
                  - $type: MultiValueCustomValue
                  - httpHeader1
                  - $type: MultiValueCustomValue
                  - httpHeader10
                  - $type: MultiValueCustomValue
                  - httpHeader2
                  - $type: MultiValueCustomValue
                  - httpHeader3
                  - $type: MultiValueCustomValue
                  - httpHeader4
                  - $type: MultiValueCustomValue
                  - httpHeader5
                  - $type: MultiValueCustomValue
                  - httpHeader6
                  - $type: MultiValueCustomValue
                  - httpHeader7
                  - $type: MultiValueCustomValue
                  - httpHeader8
                  - $type: MultiValueCustomValue
                  - httpHeader9
                  - $type: MultiValueCustomValue
                  - log_level
                  - $type: MultiValueCustomValue
                  - mode
                  - $type: MultiValueCustomValue
                  - readTimeout
                  - $type: MultiValueCustomValue
                  - responseFormat
                  - $type: MultiValueCustomValue
                  - uri
                  - $type: MultiValueCustomValue
                  - xpath
                  - $type: MultiValueCustomValue
                className: com.parasoft.soavirt.transport.websocket.WebSocketTransport
                name: WebSocket
              - $type: CustomTransportConfigurationImpl
                ConfigClassName: com.parasoft.soavirt.transport.someip.SomeIpTransport
                values:
                  customValueMap:
                  - application_name
                  - $type: MultiValueCustomValue
                  - ecu_name
                  - $type: MultiValueCustomValue
                  - log_level
                  - $type: MultiValueCustomValue
                  - operation
                  - $type: MultiValueCustomValue
                className: com.parasoft.soavirt.transport.someip.SomeIpTransport
                name: SOME/IP
              - $type: CustomTransportConfigurationImpl
                ConfigClassName: com.parasoft.soavirt.transport.grpc.GrpcTransport
                values:
                  customValueMap:
                  - auth
                  - $type: MultiValueCustomValue
                  - callId
                  - $type: MultiValueCustomValue
                  - cert
                  - $type: MultiValueCustomValue
                  - host
                  - $type: MultiValueCustomValue
                  - log_level
                  - $type: MultiValueCustomValue
                  - methodName
                  - $type: MultiValueCustomValue
                  - mode
                  - $type: MultiValueCustomValue
                  - notification
                  - $type: MultiValueCustomValue
                  - port
                  - $type: MultiValueCustomValue
                  - serviceName
                  - $type: MultiValueCustomValue
                configVersion: 1
                className: com.parasoft.soavirt.transport.grpc.GrpcTransport
                name: gRPC
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: "{\r\n    \"orderNumber\" : \"23-456-002\"\
              ,\r\n    \"inventoryInfos\" : [\r\n        {\r\n            \"itemId\"\
              \ : 1,\r\n            \"quantity\" : 1\r\n        }\r\n    ],\r\n  \
              \  \"info\" : null,\r\n    \"operation\" : \"DECREASE\"\r\n}\r\n"
            type: application/json
          dataSource:
            columnName: "itemId1: inStock"
        mode: Literal
    - $type: RESTClientToolTest
      name: "/v1/assets/items/{itemId} - GET - 2"
      testID: 66
      performanceGroup: 7
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: "GET /v1/assets/items/{itemId} - verify stock decreased"
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
            ignored:
            - operations:
              - true
              - true
              - true
              identifier: data.lastAccessedDate
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"status\" : 1,\r\n    \"message\" : \"success\",\r\n    \"\
              data\" : {\r\n        \"id\" : 1,\r\n        \"name\" : \"Blue Sleeping\
              \ Bag\",\r\n        \"description\" : \"This sleeping bag has a temperature\
              \ rating of 25 degrees. The water repellent shell keeps out moisture.\"\
              ,\r\n        \"inStock\" : 9,\r\n        \"image\" : \"/outdoor/images/sleepingbag-blue-detail.png\"\
              ,\r\n        \"region\" : \"LOCATION_1\",\r\n        \"lastAccessedDate\"\
              \ : \"2020-07-27T07:32:23.491+00:00\",\r\n        \"categoryId\" : 1\r\
              \n    }\r\n}"
          mode: 3
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                  xmlEncoding: 2
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/api-docs/v1
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${SWAGGER}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        validResponseRange:
          validResponseRange:
            values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              validResponseRange: 200
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "http://localhost:8080/v1/assets/items/{itemId}"
        transportProperties:
          manager:
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
              showResponseHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
          dataSource:
            columnName: "itemId1: inStock"
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        literalPath:
          pathElements:
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: v1
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: assets
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: items
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: "{itemId}"
        constrainedPath:
          pathParameters:
          - $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :itemId
            replacedColumn: ""
            values:
            - $type: LongValue
              replacedColumn: ""
              value: 1
        resourceMethod:
          resourceId: "/v1/assets/items/{itemId}"
          httpMethod: GET
        resourceMode: 3
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
            value: http://localhost:8080
          fixedValue:
            $type: StringTestValue
            value: http://localhost:8080
          selectedIndex: 1
    - $type: RESTClientToolTest
      name: REST Client
      testID: 327
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: "GET /v1/orders/{orderNumber} - verify order status"
        outputTools:
        - $type: DiffTool
          iconName: Diff
          name: Diff
          differs:
          - $type: BinaryDiffer
            source:
              optionsSource: 1
          - $type: TextDiffer
          - $type: XMLDifferNew
            formXml:
              formXML:
                rootNodeExists: true
                rootNode:
                  $type: XMLElementNode
                  xmlEncoding: 2
                  value: ""
                  name: NewElement
                  showAttributes: true
            hasSoapControlSource: true
            soapControlSource:
              $type: SoapDiffControlSource
          - $type: JSONDiffer
            ignored:
            - operations:
              - true
              - true
              - true
              identifier: data.submissionDate
          source:
            controls:
            - keys:
              - ""
              - ""
              row:
                refs:
                - dataSourceName: ""
                last: false
            - "{\r\n    \"status\" : 1,\r\n    \"message\" : \"success\",\r\n    \"\
              data\" : {\r\n        \"id\" : 2,\r\n        \"orderNumber\" : \"23-456-002\"\
              ,\r\n        \"requestedBy\" : \"purchaser\",\r\n        \"status\"\
              \ : \"SUBMITTED\",\r\n        \"reviewedByAPV\" : false,\r\n       \
              \ \"reviewedByPRCH\" : true,\r\n        \"respondedBy\" : null,\r\n\
              \        \"orderItems\" : [\r\n        ],\r\n        \"region\" : \"\
              UNITED_KINGDOM\",\r\n        \"location\" : \"0.1° E, 51.30° N\",\r\n\
              \        \"orderImage\" : \"/defense/images/united_kingdom_map.jpg\"\
              ,\r\n        \"receiverId\" : \"demo\",\r\n        \"eventId\" : \"\
              demo\",\r\n        \"eventNumber\" : \"demo\",\r\n        \"submissionDate\"\
              \ : \"2022-11-03T09:07:57.818+00:00\",\r\n        \"approverReplyDate\"\
              \ : null,\r\n        \"comments\" : null\r\n    }\r\n}"
          mode: 3
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        hasServiceInfo: true
        serviceInfo:
          serviceDescriptor:
            $type: StandardServiceDescriptor
            location: http://localhost:8080/api-docs/v1
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        schemaURL:
          MessagingClient_SchemaLocation: "${SWAGGER}"
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${swagger}"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        constrainedPath:
          pathParameters:
          - $type: ElementValue
            writeType: true
            type:
              $type: ElementType
              localName: orderNumber
              bodyType:
                $type: StringType
            replacedColumn: ""
            values:
            - $type: StringValue
              replacedColumn: ""
              value: 23-456-002
        resourceMethod:
          resourceId: "/v1/orders/{orderNumber}"
          httpMethod: GET
        resourceMode: 3
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
            value: http://localhost:8080
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
  - $type: TestSuite
    name: Web
    testID: 79
    tests:
    - $type: RESTClientToolTest
      name: REST Client
      testID: 105
      tool:
        $type: RESTClient
        iconName: RESTClient
        name: web
        outputTools:
        - $type: HTMLCleanup
          iconName: HTMLCleanup
          name: HTML Cleanup
          updateDTD: true
          verbose: false
          messageOutputs:
            $type: ErrorViewOutputProvider
            name: Messages
          addVersionHeader: false
          docType: 1
          sourceOutputs:
            $type: MIMESpecificNamedToolOutputProvider
            outputTools:
            - $type: XMLAssertionTool
              iconName: XMLAssertor
              name: XML Assertor
              errorsOutput:
                name: Errors
              assertions:
              - $type: OccurrenceAssertion
                timestamp: 1669774724506
                name: page-container
                extractEntireElement: true
                Assertion_XPath: "//div[@class=\"page-container\"]"
                value:
                  name: Count
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: 1
              - $type: OccurrenceAssertion
                timestamp: 1669774978112
                name: footer
                extractEntireElement: true
                Assertion_XPath: /html/body/div/footer
                value:
                  name: Count
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: 1
              - $type: ValueAssertion
                timestamp: 1669961744291
                name: Username input box
                extractEntireElement: true
                Assertion_XPath: "/html/body/div/div[2]/div[1]/div/div[3]/form/div[2]/input/@name"
                value:
                  name: Value
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: username
              - $type: ValueAssertion
                timestamp: 1669961843823
                name: Password input box
                extractEntireElement: true
                Assertion_XPath: "/html/body/div/div[2]/div[1]/div/div[3]/form/div[4]/input/@name"
                value:
                  name: Value
                  value:
                    fixedValue:
                      $type: StringTestValue
                      value: password
              message:
                $type: ExpectedXMLMessage
                message: true
            name: Transformed Source
            type: application/xhtml+xml
        formJson:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: :root
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              attributes:
              - replacedColumn: ""
                value:
                  $type: StringValue
                  replacedColumn: ""
                  value: object
                useValue: true
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
          elementTypeName: root
        jsonBuilder:
          hasValue: true
          value:
            $type: JSONObjectValue
            nameIsNull: true
        formInput:
          value:
            $type: ElementValue
            writeType: true
            hasReference: true
            qnameAsString: ":"
            replacedColumn: ""
            values:
            - $type: ComplexValue
              replacedColumn: ""
              compositorValue: true
              compositorValueObj:
                replacedColumn: ""
                values:
                  $type: CompositorValueSetCollectionSet
                  set:
                  - $type: CompositorValueSet
        constrainToSchema: false
        jmsMessageOutputProvider:
          $type: JMSMessageOutputProvider
          jmsOutputProviderRequest:
            $type: JMSOutputProvider
            name: Request Object
            menuName: Object
          jmsOutputProviderResponse:
            $type: JMSOutputProvider
            name: Response Message Object
            menuName: Message Object
        router:
          values:
          - $type: ScriptedValue
          fixedValue:
            $type: StringTestValue
            HTTPClient_Endpoint: "${BASEURL}/loginPage"
        transportProperties:
          manager:
            protocol: 1
            properties:
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
                resetCookies:
                  resetExistingCookiesProvider:
                    resetExistingCookies: true
                  resetExistingCookies:
                    useDefault: false
              protocol: 1
              keepAlive1_1:
                bool: true
            - $type: HTTPClientHTTPProperties
              followRedirects:
                bool: true
              common:
                auth:
                  useDefault: false
                  customType: 1
                method:
                  values:
                  - $type: ScriptedValue
                  fixedValue:
                    $type: HTTPMethodTestValue
                    method: GET
                resetCookies:
                  resetExistingCookiesProvider:
                    resetExistingCookies: true
                  resetExistingCookies:
                    useDefault: false
              keepAlive1_1:
                bool: true
            - $type: NoneTransportProperties
          messageExchangePattern:
            inverted: true
        outputProviders:
          requestHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Request Transport Header
          responseHeader:
            $type: HTTPNamedToolOutputProvider
            menuName: Transport Header
            name: Response Transport Header
          xmlRequestOutput:
            $type: NamedXMLToolOutputProvider
            menuName: Traffic
            name: Request Traffic
          trafficOutput:
            m_name: Traffic Stream
          objectOutput:
            $type: ObjectOutputProvider
            outputTools:
            - $type: TrafficViewer
              iconName: TrafficViewer
              name: Traffic Viewer
              showRequestHeaders: true
            name: Traffic Object
        literal:
          use: 1
          text:
            MessagingClient_LiteralMessage: ""
            type: application/json
        mode: Literal
        literalQuery:
          isPropertiesRef: true
        literalPath:
          pathElements:
          - values:
            - $type: ScriptedValue
            fixedValue:
              $type: StringTestValue
              value: loginPage
        resourceMethod:
          httpMethod: GET
        baseUrl:
          values:
          - $type: ScriptedValue
          - $type: WadlTestValue
          fixedValue:
            $type: StringTestValue
          selectedIndex: 1
