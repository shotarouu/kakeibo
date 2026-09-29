package com.example.kakeibo.controller;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.security.AppUserDetailsService;
import com.example.kakeibo.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CategoryController の Web 層テスト。
 * 認証済みユーザーとして、カテゴリ一覧・追加・削除の各エンドポイントを検証する。
 */
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    // ---- GET /categories ----

    @Test
    @WithMockUser
    void getCategories_returns200AndListView() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(
                new Category("食費", TransactionType.EXPENSE)
        ));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"))
                .andExpect(model().attributeExists("categories", "category", "types"));
    }

    // ---- POST /categories ----

    @Test
    @WithMockUser
    void postCategories_savesAndRedirects() throws Exception {
        Category saved = new Category("交際費", TransactionType.EXPENSE);
        when(categoryService.save(any())).thenReturn(saved);

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .param("name", "交際費")
                        .param("type", "EXPENSE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).save(any(Category.class));
    }

    // ---- POST /categories/{id}/delete ----

    @Test
    @WithMockUser
    void deleteCategory_deletesAndRedirects() throws Exception {
        mockMvc.perform(post("/categories/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).delete(1L);
    }
}
