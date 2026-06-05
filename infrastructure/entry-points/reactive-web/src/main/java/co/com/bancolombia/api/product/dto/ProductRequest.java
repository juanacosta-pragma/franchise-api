package co.com.bancolombia.api.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @Schema(description = "Product name", example = "Laptop")
    private String name;
    @Schema(description = "Product stock quantity", example = "100")
    private Long stock;
}

