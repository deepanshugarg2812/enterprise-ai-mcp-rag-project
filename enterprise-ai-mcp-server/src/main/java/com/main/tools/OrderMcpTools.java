package com.main.tools;

import com.main.config.OrderApiClient;
import com.main.dto.CreateOrderItemRequest;
import com.main.dto.CreateOrderRequest;
import com.main.dto.OrderPage;
import com.main.dto.OrderResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderMcpTools {

    private final OrderApiClient orderApiClient;

    public OrderMcpTools(OrderApiClient orderApiClient) {
        this.orderApiClient = orderApiClient;
    }

    @McpTool(
            name = "get_order",
            title = "Get Order",
            description = """
                    Read complete order information by order ID.

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
    public OrderResponse getOrder(
            @McpToolParam(
                    description = "Unique order ID",
                    required = true
            )
            Long orderId) {

        return orderApiClient.getOrder(orderId);
    }

    @McpTool(
            name = "search_orders",
            title = "Search Orders",
            description = """
                    Search orders using optional customer and status filters.

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
    public OrderPage searchOrders(
            @McpToolParam(
                    description = "Customer ID. Optional; use null when not filtering by customer.",
                    required = false
            )
            Long customerId,

            @McpToolParam(
                    description = "Order status filter. Optional.",
                    required = false
            )
            String status,

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

        return orderApiClient.searchOrders(
                customerId,
                status,
                page,
                size
        );
    }

    @McpTool(
            name = "get_order_status",
            title = "Get Order Status",
            description = """
                    Read only the current status of an order.

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
    public OrderStatusResponse getOrderStatus(
            @McpToolParam(
                    description = "Unique integer order ID",
                    required = true
            )
            Long orderId) {

        OrderResponse order =
                orderApiClient.getOrder(orderId);

        return new OrderStatusResponse(
                order.id(),
                order.orderNumber(),
                order.status()
        );
    }

    @McpTool(
            name = "create_order",
            title = "Create Order",
            description = """
                    Create a new customer order.

                    Operation: WRITE.
                    Side effects: Creates an order and decreases product inventory.
                    Idempotency: REQUIRED.
                    Confirmation required: YES.
                    The same idempotency key must be reused when retrying the same operation.
                    Do not generate a new idempotency key when retrying the same request.
                    """,
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = false,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public OrderResponse createOrder(
            @McpToolParam(
                    description = "Customer ID placing the order",
                    required = true
            )
            Long customerId,

            @McpToolParam(
                    description = "Product ID",
                    required = true
            )
            Long productId,

            @McpToolParam(
                    description = "Quantity to purchase",
                    required = true
            )
            Integer quantity,

            @McpToolParam(
                    description = """
                            Unique idempotency key for this order creation.
                            Reuse the same key for retries of the same logical operation.
                            """,
                    required = true
            )
            String idempotencyKey) {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        customerId,
                        java.util.List.of(
                                new CreateOrderItemRequest(
                                        productId,
                                        quantity
                                )
                        )
                );

        return orderApiClient.createOrder(
                request,
                idempotencyKey
        );
    }

    @McpTool(
            name = "cancel_order",
            title = "Cancel Order",
            description = """
                    Cancel an existing order.

                    Operation: WRITE.
                    Side effects: Changes the order state and may trigger business cancellation processing.
                    Idempotency: REQUIRED.
                    Confirmation required: YES.
                    This operation is potentially destructive.
                    Reuse the same idempotency key when retrying the same cancellation.
                    """,
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = true,
                    idempotentHint = true,
                    openWorldHint = false
            )
    )
    public OrderResponse cancelOrder(
            @McpToolParam(
                    description = "Unique order ID",
                    required = true
            )
            Long orderId,

            @McpToolParam(
                    description = """
                            Unique idempotency key for this cancellation.
                            Reuse the same key for retries of the same logical operation.
                            """,
                    required = true
            )
            String idempotencyKey) {

        return orderApiClient.cancelOrder(
                orderId,
                idempotencyKey
        );
    }

    public record OrderStatusResponse(
            Long orderId,
            String orderNumber,
            String status
    ) {
    }
}