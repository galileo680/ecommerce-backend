package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.LoginRequest;
import com.bartek.ecommerce.dto.RegisterRequest;
import com.bartek.ecommerce.dto.UserDto;
import com.bartek.ecommerce.entity.ActivationToken;
import com.bartek.ecommerce.entity.Role;
import com.bartek.ecommerce.entity.User;
import com.bartek.ecommerce.exception.InvalidCredentialsException;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.UserMapper;
import com.bartek.ecommerce.repository.ActivationTokenRepository;
import com.bartek.ecommerce.repository.RoleRepository;
import com.bartek.ecommerce.repository.UserRepository;
import com.bartek.ecommerce.security.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ActivationTokenRepository activationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private final String TEST_EMAIL = "user@test.com";
    private final String TEST_PASSWORD = "password";


    // ---------------------------------------------
    // TESTS FOR registerUser
    // ---------------------------------------------
    @Test
    void registerUser_Success_WhenUserIsNew() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode(TEST_PASSWORD))
                .thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(TEST_EMAIL);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(emailService).sendAccountActivationEmail(anyString(), anyString(), anyString());

        userService.registerUser(
                new RegisterRequest("Bob", "Newman", TEST_EMAIL, TEST_PASSWORD, "123456789")
        );

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendAccountActivationEmail(eq(TEST_EMAIL), eq("Bob"), anyString());

    }

    @Test
    void registerUser_ThrowsException_WhenEmailAlreadyExists() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(
                    new RegisterRequest("Bob", "Newman", TEST_EMAIL, TEST_PASSWORD, "123456789")
            );
        });

        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsException_WhenRoleNotInitialized() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser(
                    new RegisterRequest("Bob", "Newman", TEST_EMAIL, TEST_PASSWORD, "123456789")
            );
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mocked_jwt_token");

        var response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
    }

    @Test
    void loginUser_ThrowsNotFound_WhenEmailDoesNotExist() {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.loginUser(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginUser_ThrowsInvalidCredentials_WhenPasswordMismatch() {
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void getAllUsers_ReturnsListOfUserDtos() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@test.com");

        List<User> userList = List.of(user1, user2);

        UserDto userDto1 = new UserDto();
        userDto1.setEmail("user1@test.com");
        UserDto userDto2 = new UserDto();
        userDto2.setEmail("user2@test.com");

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        var result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
        assertEquals("user2@test.com", result.get(1).getEmail());
    }


    @Test
    void getCurrentUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);

        when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        var currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals(TEST_EMAIL, currentUser.getEmail());
    }

    @Test
    void getCurrentUser_ThrowsUsernameNotFound() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getCurrentUser());
    }


    @Test
    void activateAccount_Success() throws MessagingException {
        String tokenValue = "ValidToken";
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(tokenValue);
        activationToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        User user = new User();
        user.setId(10L);
        user.setEnabled(false);
        activationToken.setUser(user);

        when(activationTokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(activationToken));

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        userService.activateAccount(tokenValue);

        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
        verify(activationTokenRepository, times(1)).save(activationToken);
    }

    @Test
    void activateAccount_ThrowsRuntime_WhenTokenNotFound() {
        String tokenValue = "nonExistingToken";
        when(activationTokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.activateAccount(tokenValue));
    }

    /*@Test
    void activateAccount_ThrowsRuntime_WhenTokenExpired() throws MessagingException {
        // given
        String tokenValue = "expiredToken";
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(tokenValue);
        activationToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        User user = new User();
        user.setId(10L);
        user.setEnabled(false);
        activationToken.setUser(user);

        when(activationTokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(activationToken));

        // resend email
        doNothing().when(emailService).sendAccountActivationEmail(
                anyString(), anyString(), anyString()
        );

        // when + then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.activateAccount(tokenValue));
        assertTrue(ex.getMessage().contains("Activation token has expired"));

        // verify
        verify(emailService, times(1)).sendAccountActivationEmail(anyString(), anyString(), anyString());
    }*/
}