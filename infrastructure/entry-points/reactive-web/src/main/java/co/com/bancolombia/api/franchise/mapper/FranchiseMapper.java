package co.com.bancolombia.api.franchise.mapper;

import co.com.bancolombia.api.branch.mapper.BranchMapper;
import co.com.bancolombia.api.franchise.dto.FranchiseRequest;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BranchMapper.class})
public interface FranchiseMapper {

	// Map FranchiseRequest DTO to domain Franchise model. id and branches are managed elsewhere.
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "branches", ignore = true)
	Franchise toModel(FranchiseRequest request);

	// Map domain Franchise model to FranchiseResponse DTO. Branches are mapped via BranchMapper.
	FranchiseResponse toResponse(Franchise franchise);
}
