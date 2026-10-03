package com.main.tools;

import com.main.config.ProductApiClient;
import com.main.dto.ProductPage;
import com.main.dto.ProductResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ProductMcpTools {

    private final ProductApiClient productApiClient;

    public ProductMcpTools(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    @McpTool(
            name = "get_product",
            title = "Get Product",
            description = """
                    Read product information using product ID.

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
    public ProductResponse getProduct(
            @McpToolParam(
                    description = "Unique product ID",
                    required = true
            )
            Long productId) {

        return productApiClient.getProduct(productId);
    }

    @McpTool(
            name = "search_products",
            title = "Search Products",
            description = """
                    Search products by a text query.

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
    public ProductPage searchProducts(
            @McpToolParam(
                    description = "Text to search for in products",
                    required = true
            )
            String query,

            @McpToolParam(
                    description = "Zero-based page number",
                    required = true
            )
            int page,

            @McpToolParam(
                    description = "Number of products to return",
                    required = true
            )
            int size) {

        return productApiClient.searchProducts(
                query,
                page,
                size
        );
    }

    @McpTool(
            name = "check_product_inventory",
            title = "Check Product Inventory",
            description = """
                    Check the currently available inventory for a product.

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
    public InventoryResponse checkInventory(
            @McpToolParam(
                    description = "Unique product ID",
                    required = true
            )
            Long productId) {

        ProductResponse product =
                productApiClient.getProduct(productId);

        return new InventoryResponse(
                product.id(),
                product.sku(),
                product.inventory()
        );
    }

    public record InventoryResponse(
            Long productId,
            String sku,
            Integer availableQuantity
    ) {
    }
}