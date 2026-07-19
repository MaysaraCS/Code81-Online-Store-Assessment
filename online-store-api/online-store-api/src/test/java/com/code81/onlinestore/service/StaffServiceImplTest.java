package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.staff.StaffCreateRequest;
import com.code81.onlinestore.entity.Role;
import com.code81.onlinestore.entity.StaffUser;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.repository.StaffUserRepository;
import com.code81.onlinestore.service.impl.StaffServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock private StaffUserRepository staffUserRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ActivityLogService activityLogService;

    @InjectMocks
    private StaffServiceImpl staffService;

    @Test
    void create_savesStaff_withHashedPassword() {
        StaffCreateRequest request = new StaffCreateRequest("alice", "alice@code81.local", "Password123", Role.STORE_MANAGER);
        when(staffUserRepository.existsByUsernameIgnoreCase("alice")).thenReturn(false);
        when(staffUserRepository.existsByEmailIgnoreCase("alice@code81.local")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed-password");
        when(staffUserRepository.save(any(StaffUser.class))).thenAnswer(inv -> {
            StaffUser s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        var response = staffService.create(request);

        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getRole()).isEqualTo(Role.STORE_MANAGER);
        verify(passwordEncoder).encode("Password123");
        verify(activityLogService).log(eq("CREATE_STAFF"), eq("StaffUser"), any(), any());
    }

    @Test
    void create_throwsDuplicateResourceException_whenUsernameTaken() {
        StaffCreateRequest request = new StaffCreateRequest("alice", "alice@code81.local", "Password123", Role.STORE_MANAGER);
        when(staffUserRepository.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        assertThatThrownBy(() -> staffService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
        verify(staffUserRepository, never()).save(any());
    }
}
