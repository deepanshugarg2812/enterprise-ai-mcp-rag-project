package com.main.tools;

import com.main.config.CustomerApiClient;
import com.main.dto.CustomerOrderPage;
import com.main.dto.CustomerResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CustomerMcpTools {

    private final CustomerApiClient customerApiClient;

    public CustomerMcpTools(CustomerApiClient customerApiClient) {
        this.customerApiClient = customerApiClient;
    }

    @McpTool(name = "get_customer", title = "Get Customer",
            description = """
            Read customer information using the customer ID.
            Operation: READ.
            Side effects: NONE.
            Idempotent: YES.
            Confirmation required: NO.
            """, generateOutputSchema = true, annotations = @McpTool.McpAnnotations(readOnlyHint = true,
            idempotentHint = true, destructiveHint = false, openWorldHint = false))
    public CustomerResponse getCustomer(
            @McpToolParam(description = "Unique customer id", required = true) Long customerId) {
        return customerApiClient.getCustomer(customerId);
    }

    @McpTool(
            name = "get_customer_orders",
            title = "Get Customer Orders",
            description = """
                    Read orders belonging to a customer.

                    Operation: READ.
                    Side effects: NONE.
                    Idempotent: YES.
                    Confirmation required: NO.
                    """,
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = true,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public CustomerOrderPage getCustomerOrders(
            @McpToolParam(
                    description = "Unique customer ID",
                    required = true
            )
            Long customerId,

            @McpToolParam(
                    description = "Zero-based page number",
                    required = true
            )
            int page,

            @McpToolParam(
                    description = "Number of orders to return",
                    required = true
            )
            int size) {

        return customerApiClient.getCustomerOrders(
                customerId,
                page,
                size
        );
    }
}
