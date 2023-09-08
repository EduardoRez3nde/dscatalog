package com.rezende.DsCatalog.tests;

import com.rezende.DsCatalog.dto.ProductDTO;
import com.rezende.DsCatalog.entities.Category;
import com.rezende.DsCatalog.entities.Product;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "celular iphone", 4800.0, "www.apple.com");
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDTO createProductDto() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());

    }
}
