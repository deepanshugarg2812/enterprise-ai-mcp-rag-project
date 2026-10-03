package com.main.tools;

import com.main.config.CustomerApiClient;
import com.main.config.OrderApiClient;
import com.main.config.ProductApiClient;
import com.main.dto.*;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderWorkflowTools {

    private final OrderApiClient orderApiClient;
    private final CustomerApiClient customerApiClient;
    private final ProductApiClient productApiClient;

    public OrderWorkflowTools(
            OrderApiClient orderApiClient,
            CustomerApiClient customerApiClient,
            ProductApiClient productApiClient) {

        this.orderApiClient = orderApiClient;
        this.customerApiClient = customerApiClient;
        this.productApiClient = productApiClient;
    }

    @McpTool(
            name = "get_order_details",
            title = "Get Complete Order Details",
            description = """
                    Retrieve complete information about an order,
                    including customer information and product information
                    for every order item.

                    This is a READ workflow.
                    It performs multiple read-only backend calls.
                    It does not modify any data.
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
    public OrderDetails getOrderDetails(
            @McpToolParam(
                    description = "Unique order ID",
                    required = true
            )
            Long orderId) {

        OrderResponse order =
                orderApiClient.getOrder(orderId);

        CustomerResponse customer =
                customerApiClient.getCustomer(
                        order.customerId()
                );

        List<ProductResponse> products =
                order.items()
                        .stream()
                        .map(OrderItemResponse::productId)
                        .map(productApiClient::getProduct)
                        .toList();

        return new OrderDetails(
                order,
                customer,
                products
        );
    }

    public record OrderDetails(
            OrderResponse order,
            CustomerResponse customer,
            List<ProductResponse> products
    ) {
    }

    @McpTool(
            name = "get_customer_order_summary",
            title = "Get Customer Order Summary",
            description = """
                Retrieve a customer's information together with their orders.

                This is a READ workflow.
                It does not modify any data.
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
    public CustomerOrderSummary getCustomerOrderSummary(
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

        CustomerResponse customer =
                customerApiClient.getCustomer(customerId);

        var orders =
                customerApiClient.getCustomerOrders(
                        customerId,
                        page,
                        size
                );

        return new CustomerOrderSummary(
                customer,
                orders
        );
    }

    public record CustomerOrderSummary(
            CustomerResponse customer,
            CustomerOrderPage orders
    ) {
    }
}