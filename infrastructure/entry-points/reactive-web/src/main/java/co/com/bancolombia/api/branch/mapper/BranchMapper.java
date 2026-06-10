package co.com.bancolombia.api.branch.mapper;

import co.com.bancolombia.api.branch.dto.BranchRequest;
import co.com.bancolombia.api.branch.dto.BranchResponse;
import co.com.bancolombia.api.product.mapper.ProductMapper;
import co.com.bancolombia.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface BranchMapper {

	// Map BranchRequest DTO to domain Branch model. id and products are managed elsewhere,
	// so we ignore them here to avoid accidental overwrites.
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	Branch toModel(BranchRequest request);

	// Map domain Branch model to BranchResponse DTO. Products are mapped via ProductMapper.
	BranchResponse toResponse(Branch branch);
}
