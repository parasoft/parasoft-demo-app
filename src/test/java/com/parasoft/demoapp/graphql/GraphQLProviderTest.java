package com.parasoft.demoapp.graphql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GraphQLProviderTest {

    @Test
    public void getters_whenSchemaHasNotBeenInitialized_throwClearException() {
        GraphQLProvider provider = new GraphQLProvider(null, null, null, null, null);

        assertSchemaAccessFails(provider::getGraphQL);
        assertSchemaAccessFails(provider::getSchemaDefinition);
    }

    private void assertSchemaAccessFails(Runnable schemaAccess) {
        try {
            schemaAccess.run();
            fail("Expected access to an uninitialized GraphQL schema to fail.");
        } catch (IllegalStateException exception) {
            assertEquals("GraphQL schema has not been initialized yet.", exception.getMessage());
        }
    }
}
