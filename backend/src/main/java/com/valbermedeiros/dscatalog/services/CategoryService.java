package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.dto.CategoryDto;
import com.valbermedeiros.dscatalog.entities.Category;
import com.valbermedeiros.dscatalog.repositories.CategoryRepository;
import com.valbermedeiros.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<CategoryDto> findAll() {
        List<Category> list =  repository.findAll();
        return list.stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        return obj.map(CategoryDto::new).orElseThrow(() -> {
            throw new EntityNotFoundException("Entity not found.");
        });
    }
}
