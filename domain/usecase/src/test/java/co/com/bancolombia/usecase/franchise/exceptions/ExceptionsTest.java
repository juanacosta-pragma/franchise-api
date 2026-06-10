package co.com.bancolombia.usecase.franchise.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void franchiseNotFoundException_storesMessage() {
        FranchiseNotFoundException ex = new FranchiseNotFoundException("not found");
        assertThat(ex.getMessage()).isEqualTo("not found");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void branchNotFoundException_storesMessage() {
        BranchNotFoundException ex = new BranchNotFoundException("branch missing");
        assertThat(ex.getMessage()).isEqualTo("branch missing");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void productNotFoundException_storesMessage() {
        ProductNotFoundException ex = new ProductNotFoundException("product missing");
        assertThat(ex.getMessage()).isEqualTo("product missing");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void validationException_storesMessage() {
        ValidationException ex = new ValidationException("invalid");
        assertThat(ex.getMessage()).isEqualTo("invalid");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}

