package co.com.bancolombia.api.branch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchRequest {
    @Schema(description = "Branch name", example = "Central Branch")
    private String name;
}

