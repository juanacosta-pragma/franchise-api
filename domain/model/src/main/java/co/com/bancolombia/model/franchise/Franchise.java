package co.com.bancolombia.model.franchise;
import co.com.bancolombia.model.branch.Branch;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {
    private String id;
    private String name;
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}
