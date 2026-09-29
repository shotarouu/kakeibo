package com.example.kakeibo.service;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * CategoryService の単体テスト。
 * リポジトリをモック化して、サービスのロジックだけを検証する。
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_delegatesToRepository() {
        List<Category> expected = List.of(new Category("食費", TransactionType.EXPENSE));
        when(categoryRepository.findAll()).thenReturn(expected);

        List<Category> result = categoryService.findAll();

        assertThat(result).isEqualTo(expected);
        verify(categoryRepository).findAll();
    }

    @Test
    void findByType_delegatesToRepository() {
        Category expense = new Category("食費", TransactionType.EXPENSE);
        when(categoryRepository.findByType(TransactionType.EXPENSE)).thenReturn(List.of(expense));

        List<Category> result = categoryService.findByType(TransactionType.EXPENSE);

        assertThat(result).containsExactly(expense);
        verify(categoryRepository).findByType(TransactionType.EXPENSE);
    }

    @Test
    void save_delegatesToRepository() {
        Category category = new Category("食費", TransactionType.EXPENSE);
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.save(category);

        assertThat(result).isEqualTo(category);
        verify(categoryRepository).save(category);
    }

    @Test
    void delete_delegatesToRepository() {
        categoryService.delete(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void findById_returnsCategory_whenFound() {
        Category category = new Category("食費", TransactionType.EXPENSE);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(1L);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }
}
