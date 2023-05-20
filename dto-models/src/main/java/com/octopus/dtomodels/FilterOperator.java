package com.octopus.dtomodels;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public enum FilterOperator {

    $eq {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Expression<?> key;
            try {
                key = root.get(request.getKey());
            } catch (Exception e) {
                key = join.get(request.getKey());
            }
            return cb.equal(key, value);
        }
    },

    $ne {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Expression<?> key;
            try {
                key = root.get(request.getKey());
            } catch (Exception e) {
                key = join.get(request.getKey());
            }
            return cb.notEqual(key, value);
        }
    },

    $contains {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            if (request.getKey().equals("name")) {
                Expression<String> firstNameKey;
                try {
                    firstNameKey = root.get(request.getKey());
                } catch (Exception e) {
                    firstNameKey = join.get(request.getKey());
                }
                Expression<String> lastNameKey;
                try {
                    lastNameKey = root.get(request.getKey());
                } catch (Exception e) {
                    lastNameKey = join.get(request.getKey());
                }
                return cb.like(cb.upper(cb.concat(cb.concat(firstNameKey, " "), lastNameKey)), "%" + request.getValue().toString().toUpperCase() + "%");
            } else {
                Expression<String> key;
                try {
                    key = root.get(request.getKey());
                } catch (Exception e) {
                    key = join.get(request.getKey());
                }
                return cb.like(cb.upper(key), "%" + request.getValue().toString().toUpperCase() + "%");
            }
        }
    },

    $in {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            List<Object> values = request.getValues();
            Expression<?> key;
            try {
                key = root.get(request.getKey());
            } catch (Exception e) {
                key = join.get(request.getKey());
            }
            CriteriaBuilder.In<Object> inClause = cb.in(key);
            for (Object value : values) {
                inClause.value(request.getFieldType().parse(value.toString()));
            }
            return inClause;
        }
    },

    $nin {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter filter, Predicate predicate, Join<Object, T> join) {
            List<Object> values = filter.getValues();
            Expression<?> key;
            try {
                key = root.get(filter.getKey());
            } catch (Exception e) {
                key = join.get(filter.getKey());
            }
            CriteriaBuilder.In<Object> inClause = cb.in(key);
            for (Object value: values) {
                inClause.value(filter.getFieldType().parse(value.toString()));
            }
            return inClause.not();
        }
    },

    $between {
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Object valueTo = request.getFieldType().parse(request.getValueTo().toString());
            if (request.getFieldType() == FieldType.DATE) {
                LocalDateTime startDate = (LocalDateTime) value;
                LocalDateTime endDate = (LocalDateTime) valueTo;
                Expression<LocalDateTime> key = root.get(request.getKey());
                System.out.println(key);
                return cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate));
            }

            if (request.getFieldType() != FieldType.CHAR && request.getFieldType() != FieldType.BOOLEAN) {
                Number start = (Number) value;
                Number end = (Number) valueTo;
                Expression<Number> key = root.get(request.getKey());
                return cb.and(cb.ge(key, start), cb.le(key, end));
            }

            log.info("Can not use between for {} field type.", request.getFieldType());
            return predicate;
        }
    },

    $and {
        @Override
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            List<Filter> filters = request.getValues().stream().map(o -> {
                Map<String, Object> json = (LinkedHashMap<String, Object>) o;
                var objectMapper = new ObjectMapper();
                return objectMapper.convertValue(json, Filter.class);
            }).collect(Collectors.toList());
            for (Filter filter: filters) {
                if (predicate != null) {
                    predicate = cb.and(predicate, filter.getOperator().build(root, cb, filter, predicate, join));
                } else {
                    predicate = cb.and(filter.getOperator().build(root, cb, filter, predicate, join));
                }
            }
            predicate = cb.and(predicate, cb.equal(cb.literal(Boolean.TRUE), Boolean.TRUE));
            return predicate;
        }
    },

    $or {
        @Override
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            List<Filter> filters = request.getValues().stream().map(o -> {
                Map<String, Object> json = (LinkedHashMap<String, Object>) o;
                var objectMapper = new ObjectMapper();
                return objectMapper.convertValue(json, Filter.class);
            }).collect(Collectors.toList());
            for (Filter filter: filters) {
                if (predicate != null) {
                    predicate = cb.or(filter.getOperator().build(root, cb, filter, predicate, join), predicate);
                } else {
                    predicate = cb.or(filter.getOperator().build(root, cb, filter, predicate, join));
                }
            }
            return predicate;
        }
    },

    $join {
        @Override
        public <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join) {
            var joinType = request.getJoinType() == null ? JoinType.INNER : request.getJoinType();
            join = root.join(request.getKey(), joinType);
            Map<String, Object> json = (LinkedHashMap<String, Object>) request.getValue();
            var objectMapper = new ObjectMapper();
            var filter = objectMapper.convertValue(json, Filter.class);
            return filter.getOperator().build(root, cb, filter, predicate, join);
        }
    };

    public abstract <T> Predicate build(Root<T> root, CriteriaBuilder cb, Filter request, Predicate predicate, Join<Object, T> join);
}
