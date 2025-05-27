package com.fioletowi.farma.user;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("Test")
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock Mapper<User, UserResponse> userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserServiceImpl userService;

    private User sampleUser;
    private UserResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(42L)
                .firstName("Sam")
                .lastName("Ple")
                .email("sam@example.com")
                .password("encoded")
                .birthDate(LocalDateTime.of(1990,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("Addr")
                .phoneNumber("123")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(0.9)
                .build();
        sampleResponse = new UserResponse();
        sampleResponse.setId(42L);
    }

    @Test
    void findAllUsers_noRole() {
        var page = new PageImpl<>(List.of(sampleUser), PageRequest.of(0,1),1);
        given(userRepository.findAll(any(Pageable.class))).willReturn(page);
        given(userMapper.mapTo(sampleUser, UserResponse.class)).willReturn(sampleResponse);

        Page<UserResponse> out = userService.findAllUsers(PageRequest.of(0,1), null);
        assertThat(out.getTotalElements()).isEqualTo(1);
        assertThat(out.getContent().get(0).getId()).isEqualTo(42L);
    }

    @Test
    void findAllUsers_withRole() {
        var page = new PageImpl<>(List.of(sampleUser));
        given(userRepository.findAllByUserRole(any(Pageable.class), eq(UserRole.WORKER)))
                .willReturn(page);
        given(userMapper.mapTo(sampleUser, UserResponse.class)).willReturn(sampleResponse);

        Page<UserResponse> out = userService.findAllUsers(PageRequest.of(0,1), UserRole.WORKER);
        assertThat(out.getContent()).hasSize(1);
    }

    @Test
    void findUserById_found() {
        given(userRepository.findById(42L)).willReturn(Optional.of(sampleUser));
        given(userMapper.mapTo(sampleUser, UserResponse.class)).willReturn(sampleResponse);

        UserResponse resp = userService.findUserById(42L);
        assertThat(resp.getId()).isEqualTo(42L);
    }

    @Test
    void findUserById_notFound() {
        given(userRepository.findById(7L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserById(7L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void hireUser() {
        given(userRepository.findById(42L)).willReturn(Optional.of(sampleUser));
        given(userMapper.mapTo(any(), eq(UserResponse.class))).willReturn(sampleResponse);

        UserResponse resp = userService.hireUser(42L);
        assertThat(resp.getId()).isEqualTo(42L);
        then(userRepository).should().save(argThat(u -> u.getHiredAt() != null));
    }

    @Test
    void updateUserSettings() {
        var req = new UserSettingsRequest(false);
        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        // Authentication jest trudniejsze do mokowania, ale możemy pominąć – zakładamy, że principal to sampleUser
        // tutaj testujemy jedynie zapis allowNotifications
        given(userRepository.save(any())).willReturn(sampleUser);
        given(userMapper.mapTo(any(), eq(UserResponse.class))).willReturn(sampleResponse);

        // podstaw usera jako principal
        var authentication = Mockito.mock(org.springframework.security.core.Authentication.class);
        given(authentication.getPrincipal()).willReturn(sampleUser);

        UserResponse resp = userService.updateUserSettings(req, authentication);
        assertThat(resp.getId()).isEqualTo(42L);
        then(userRepository).should().save(argThat(u -> u.getAllowNotifications().equals(false)));
    }

    @Test
    void getNumberOfUsers() {
        given(userRepository.countByIsArchivedFalseAndHiredAtIsNotNull()).willReturn(5L);
        var cnt = userService.getNumberOfUsers();
        assertThat(cnt.getCount()).isEqualTo(5L);
    }
}
