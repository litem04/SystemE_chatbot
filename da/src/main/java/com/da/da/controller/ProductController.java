package com.da.da.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    @GetMapping("/products")
    public String listProducts() {
        return "redirect:/";
    }

    @GetMapping("/product-detail")
    public String productDetail(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            return "redirect:/product/" + id;
        }
        return "redirect:/";
    }
}