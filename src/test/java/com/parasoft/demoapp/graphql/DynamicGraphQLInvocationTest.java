package com.parasoft.demoapp.graphql;

import tools.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQL;
import org.dataloader.DataLoaderRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class DynamicGraphQLInvocationTest {

    @Mock
    private GraphQLProvider graphQLProvider;

    @Mock
    private ObjectProvider<DataLoaderRegistry> dataLoaderRegistryProvider;

    @Mock
    private GraphQL graphQL;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        ExecutionResult result = ExecutionResultImpl.newExecutionResult()
                .data(Map.of("getOrders", Map.of()))
                .build();
        when(graphQL.executeAsync(any(ExecutionInput.class)))
                .thenReturn(CompletableFuture.completedFuture(result));
        mockMvc = MockMvcBuilders.standaloneSetup(new DynamicGraphQLInvocation(
                graphQLProvider, dataLoaderRegistryProvider, new ObjectMapper())).build();
    }

    @Test
    public void invokeQuery_acceptsGetQueriesWithVariables() throws Exception {
        String query = "query GetOrders($page: Int) { getOrders(page: $page) { totalElements } }";
        MvcResult result = mockMvc.perform(get("/graphql")
                        .param("query", query)
                        .param("operationName", "GetOrders")
                        .param("variables", "{\"page\":1}"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ArgumentCaptor<ExecutionInput> executionInput = ArgumentCaptor.forClass(ExecutionInput.class);
        verify(graphQL).executeAsync(executionInput.capture());
        assertEquals(query, executionInput.getValue().getQuery());
        assertEquals("GetOrders", executionInput.getValue().getOperationName());
        assertEquals(1, executionInput.getValue().getVariables().get("page"));
    }

    @Test
    public void invokeGraphQLDocument_acceptsApplicationGraphQLWithCharset() throws Exception {
        String query = "query GetOrder { getOrderByOrderNumber(orderNumber: \"23-456-001\") { orderNumber } }";
        MvcResult result = mockMvc.perform(post("/graphql")
                        .contentType(MediaType.parseMediaType("application/graphql;charset=UTF-8"))
                        .content(query)
                        .param("operationName", "GetOrder"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ArgumentCaptor<ExecutionInput> executionInput = ArgumentCaptor.forClass(ExecutionInput.class);
        verify(graphQL).executeAsync(executionInput.capture());
        assertEquals(query, executionInput.getValue().getQuery());
        assertEquals("GetOrder", executionInput.getValue().getOperationName());
    }
}
