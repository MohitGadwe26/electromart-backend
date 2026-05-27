package com.electromart.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.electromart.product.Attribute;
import com.electromart.product.Brand;
import com.electromart.product.Product;
import com.electromart.product.ProductAttributeValue;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> filter(
            Long categoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ✅ Category filter
            if (categoryId != null) {
                predicates.add(
                    cb.equal(root.get("category").get("id"), categoryId)
                );
            }

            // ✅ Brand name filter (FIXED)
            if (brand != null && !brand.isBlank()) {
                Join<Product, Brand> brandJoin = root.join("brand");
                predicates.add(
                    cb.equal(
                        cb.lower(brandJoin.get("name")),
                        brand.toLowerCase()
                    )
                );
            }

            // ✅ Price filter
            if (minPrice != null && maxPrice != null) {
                predicates.add(
                    cb.between(root.get("price"), minPrice, maxPrice)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ✅ Attribute filter (unchanged)
    public static Specification<Product> attributeFilter(
            String attributeName,
            String attributeValue
    ) {

        return (root, query, cb) -> {

            Join<Product, ProductAttributeValue> pav =
                    root.join("attributeValues");

            Join<ProductAttributeValue, Attribute> attr =
                    pav.join("attribute");

            return cb.and(
                cb.equal(attr.get("name"), attributeName),
                cb.equal(pav.get("value"), attributeValue)
            );
        };
    }
}
