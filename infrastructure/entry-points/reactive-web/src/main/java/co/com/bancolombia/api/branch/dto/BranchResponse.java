package co.com.bancolombia.api.branch.dto;

import co.com.bancolombia.api.product.dto.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponse {
    @Schema(description = "Branch unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    @Schema(description = "Branch name", example = "Central Branch")
    private String name;
    @Schema(description = "List of products in the branch")
    private List<ProductResponse> products;
}

