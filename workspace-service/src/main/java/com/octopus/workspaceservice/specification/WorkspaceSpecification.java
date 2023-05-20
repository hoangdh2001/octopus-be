package com.octopus.workspaceservice.specification;

import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.SortDirection;
import com.octopus.dtomodels.SortRequest;
import com.octopus.workspaceservice.models.Workspace;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class WorkspaceSpecification implements Specification<Workspace> {

    private final Payload payload;

    @Override
    public Predicate toPredicate(Root<Workspace> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;

        if (payload.getFilterConditions() != null) {
            predicate = payload.getFilterConditions().getOperator().build(root, criteriaBuilder, payload.getFilterConditions(), predicate, null);
        }

        List<Order> orders = new ArrayList<>();
        for (SortRequest sort: this.payload.getSort()) {
            orders.add(SortDirection.getSimpleOperation(sort.getDirection()).build(root, criteriaBuilder, sort));
        }

        query.orderBy(orders);

        return predicate;
    }

    public static Pageable getPageable(Integer page, Integer size) {
        return PageRequest.of(Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 100));
    }
}
