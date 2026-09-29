package com.example.kakeibo;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.User;
import com.example.kakeibo.repository.CategoryRepository;
import com.example.kakeibo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DataInitializer の単体テスト。
 *
 * 「初回起動（データなし）」と「2回目以降（データあり）」の
 * 2ケースをモックで検証する。
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    // ---- 初回起動: データが0件 → 初期データを登録する ----

    @Test
    void run_whenNoData_savesDefaultCategoriesAndUser() throws Exception {
        when(categoryRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        dataInitializer.run();

        // 支出8件 + 収入4件 = 12カテゴリ保存
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository, times(12)).save(categoryCaptor.capture());

        List<Category> savedCategories = categoryCaptor.getAllValues();
        assertThat(savedCategories).extracting(Category::getName)
                .contains("食費", "日用品", "交通費", "趣味・娯楽",
                           "住居費", "通信費", "医療費", "その他支出",
                           "給与", "ボーナス", "副業", "その他収入");

        // デフォルトユーザーが1件保存される
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("admin");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("$2a$10$hashed");
    }

    // ---- 2回目以降: データが既に存在する → 何も登録しない ----

    @Test
    void run_whenDataExists_skipsInitialization() throws Exception {
        when(categoryRepository.count()).thenReturn(12L);
        when(userRepository.count()).thenReturn(1L);

        dataInitializer.run();

        verify(categoryRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}
