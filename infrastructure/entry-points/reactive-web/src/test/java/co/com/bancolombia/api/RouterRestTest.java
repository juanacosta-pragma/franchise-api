package co.com.bancolombia.api;

import co.com.bancolombia.api.branch.BranchHandler;
import co.com.bancolombia.api.franchise.FranchiseHandler;
import co.com.bancolombia.api.product.ProductHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private FranchiseHandler franchiseHandler;

    @Mock
    private BranchHandler branchHandler;

    @Mock
    private ProductHandler productHandler;

    private final RouterRest router = new RouterRest();

    @Test
    void routerFranchiseFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerFranchiseFunction(franchiseHandler);
        assertThat(rf).isNotNull();
    }

    @Test
    void routerBranchFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerBranchFunction(branchHandler);
        assertThat(rf).isNotNull();
    }

    @Test
    void routerProductFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerProductFunction(productHandler);
        assertThat(rf).isNotNull();
    }
}

