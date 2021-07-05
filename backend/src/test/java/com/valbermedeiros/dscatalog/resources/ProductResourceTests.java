package com.valbermedeiros.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valbermedeiros.dscatalog.dto.ProductDto;
import com.valbermedeiros.dscatalog.services.ProductService;
import com.valbermedeiros.dscatalog.services.exceptions.DataBaseException;
import com.valbermedeiros.dscatalog.services.exceptions.ResourceNotFoundException;
import com.valbermedeiros.dscatalog.tests.Factory;
import com.valbermedeiros.dscatalog.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private ProductDto productDto;
    private PageImpl<ProductDto> page;
    private long existingId;
    private long nonExistingId;
    private long dependetId;

    private String username;
    private String password;

    @BeforeEach
    void setUp() {

        username = "maria@gmail.com";
        password = "123456";

        productDto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDto));
        existingId = 1L;
        nonExistingId = 1000L;
        dependetId = 3L;

        when(productService.findAll(any(), any(), any())).thenReturn(page);
        
        when(productService.findById(existingId)).thenReturn(productDto);
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(productService.update(any(), eq(existingId))).thenReturn(productDto);
        when(productService.update(any(), eq(nonExistingId))).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        doThrow(DataBaseException.class).when(productService).delete(dependetId);

        when(productService.insert(any())).thenReturn(productDto);
        
    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductDtopWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        mockMvc
                .perform(get("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        mockMvc
                .perform(get("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnProductDtopWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc
                .perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc
                .perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnProductDto() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc
                .perform(post("/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        mockMvc
            .perform(delete("/products/{id}", existingId)
                    .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        mockMvc
                .perform(delete("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnConflictWhenIdDendent() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        mockMvc
                .perform(delete("/products/{id}", dependetId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }
}
