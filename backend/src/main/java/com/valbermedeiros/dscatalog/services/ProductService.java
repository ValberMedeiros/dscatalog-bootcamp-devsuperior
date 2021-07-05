package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.dto.CategoryDto;
import com.valbermedeiros.dscatalog.dto.ProductDto;
import com.valbermedeiros.dscatalog.entities.Category;
import com.valbermedeiros.dscatalog.entities.Product;
import com.valbermedeiros.dscatalog.repositories.CategoryRepository;
import com.valbermedeiros.dscatalog.repositories.ProductRepository;
import com.valbermedeiros.dscatalog.services.exceptions.DataBaseException;
import com.valbermedeiros.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductDto> findAll(Long categoryId, String name, Pageable pageable) {
        List<Category> categories = (categoryId == 0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));
        Page<Product> list =  repository.find(categories, name.trim(), pageable);
        return list
                .map(ProductDto::new);
    }

    public ProductDto findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found."));
        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto dto) {
        var entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ProductDto(entity);
    }

    @Transactional
    public ProductDto update(ProductDto dto, Long id) {
        try {
            Product entity = repository.getOne(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new ProductDto(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(ProductDto dto, Product entity) {

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for (CategoryDto catDto : dto.getCategories()) {
            var category = categoryRepository.getOne(catDto.getId());
            entity.getCategories().add(category);
        }
    }
}
