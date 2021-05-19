package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.entities.Category;
import com.valbermedeiros.dscatalog.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

}
