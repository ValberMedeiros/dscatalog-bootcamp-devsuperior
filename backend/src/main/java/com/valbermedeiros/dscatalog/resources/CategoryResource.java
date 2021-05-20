package com.valbermedeiros.dscatalog.resources;

import com.valbermedeiros.dscatalog.dto.CategoryDto;
import com.valbermedeiros.dscatalog.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    private CategoryService service;

    public CategoryResource(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll() {
        List<CategoryDto> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
        CategoryDto dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

}
