package co.com.bancolombia.api.product.mapper;

import co.com.bancolombia.api.product.dto.ProductRequest;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	// Map ProductRequest DTO to domain Product model. id is managed elsewhere.
	@Mapping(target = "id", ignore = true)
	Product toModel(ProductRequest request);

	// Map domain Product model to ProductResponse DTO.
	ProductResponse toResponse(Product product);
}
