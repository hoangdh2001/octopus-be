package com.octopus.authservice.specification;

import com.octopus.authservice.model.User;
import com.octopus.dtomodels.Filter;
import com.octopus.dtomodels.Payload;
import com.octopus.dtomodels.SortDirection;
import com.octopus.dtomodels.SortRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private final Payload payload;
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;

        predicate = payload.getFilterConditions().getOperator().build(root, criteriaBuilder, payload.getFilterConditions(), predicate);

        List<Order> orders = new ArrayList<>();
        for (SortRequest sort : this.payload.getSort()) {
            orders.add(SortDirection.getSimpleOperation(sort.getDirection()).build(root, criteriaBuilder, sort));
        }

        query.orderBy(orders);
        return predicate;
    }

    public static Pageable getPageable(Integer page, Integer size) {
        return PageRequest.of(Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 100));
    }
}
