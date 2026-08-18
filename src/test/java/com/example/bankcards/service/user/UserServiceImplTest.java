package com.example.bankcards.service.user;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.GetUsersRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("searchUsers method tests")
    class SearchUsersTests {

        @Test
        @DisplayName("Should pass exact parameters to repository when both are valid strings")
        void searchUsers_WithValidParameters() {
            GetUsersRequest request = GetUsersRequest.builder()
                .userName("super_admin")
                .holderName("JOHN DOE")
                .build();
            Pageable pageable = mock(Pageable.class);
            Page<UserEntity> expectedPage = new PageImpl<>(List.of(new UserEntity()));

            when(userRepository.searchByUserNameAndCardHolderName("super_admin", "JOHN DOE", pageable))
                .thenReturn(expectedPage);

            Page<UserEntity> actualPage = userService.searchUsers(request, pageable);

            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalElements());
            verify(userRepository, times(1))
                .searchByUserNameAndCardHolderName("super_admin", "JOHN DOE", pageable);
        }

        @Test
        @DisplayName("Should convert empty and blank fields to null parameters before querying repository")
        void searchUsers_WithBlankParameters_ConvertsToNull() {
            GetUsersRequest request = GetUsersRequest.builder()
                .userName("   ")
                .holderName("")
                .build();
            Pageable pageable = mock(Pageable.class);
            Page<UserEntity> expectedPage = new PageImpl<>(Collections.emptyList());

            when(userRepository.searchByUserNameAndCardHolderName(null, null, pageable))
                .thenReturn(expectedPage);

            Page<UserEntity> actualPage = userService.searchUsers(request, pageable);

            assertNotNull(actualPage);
            assertTrue(actualPage.isEmpty());
            verify(userRepository, times(1))
                .searchByUserNameAndCardHolderName(null, null, pageable);
        }

        @Test
        @DisplayName("Should safely handle null input parameters and forward them as null properties")
        void searchUsers_WithNullParameters_ForwardsAsNull() {
            GetUsersRequest request = GetUsersRequest.builder()
                .userName(null)
                .holderName(null)
                .build();
            Pageable pageable = mock(Pageable.class);
            Page<UserEntity> expectedPage = new PageImpl<>(Collections.emptyList());

            when(userRepository.searchByUserNameAndCardHolderName(null, null, pageable))
                .thenReturn(expectedPage);

            Page<UserEntity> actualPage = userService.searchUsers(request, pageable);

            assertNotNull(actualPage);
            verify(userRepository, times(1))
                .searchByUserNameAndCardHolderName(null, null, pageable);
        }
    }
}
