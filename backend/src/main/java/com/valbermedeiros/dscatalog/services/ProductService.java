package com.valbermedeiros.dscatalog.services;

import com.valbermedeiros.dscatalog.dto.ProductDto;
import com.valbermedeiros.dscatalog.entities.Product;
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
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<ProductDto> findAll(Pageable pageable) {
        Page<Product> list =  repository.findAll(pageable);
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
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDto(entity);
    }

    @Transactional
    public ProductDto update(ProductDto dto, Long id) {
        try {
            Product entity = repository.getOne(id);
            entity.setName(dto.getName());
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
}
