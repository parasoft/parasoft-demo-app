package com.parasoft.demoapp.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GraphQLSchemaController {

    private final GraphQLProvider graphQLProvider;

    @GetMapping(value = "/schema.graphqls", produces = MediaType.TEXT_PLAIN_VALUE)
    public String schemaDefinition() {
        return graphQLProvider.getSchemaDefinition();
    }
}
