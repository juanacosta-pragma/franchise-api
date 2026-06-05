package co.com.bancolombia.api.franchise.dto;

import co.com.bancolombia.api.branch.dto.BranchResponse;
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
public class FranchiseResponse {
    @Schema(description = "Franchise unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    @Schema(description = "Franchise name", example = "Piza Company")
    private String name;
    @Schema(description = "List of branches")
    private List<BranchResponse> branches;
}

