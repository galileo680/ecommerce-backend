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
        // given
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

        // when
        userService.registerUser(
                new RegisterRequest("Bob", "Newman", TEST_EMAIL, TEST_PASSWORD, "123456789")
        );

        // then
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendAccountActivationEmail(eq(TEST_EMAIL), eq("Bob"), anyString());

    }

    @Test
    void registerUser_ThrowsException_WhenEmailAlreadyExists() throws MessagingException {
        // given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(new User()));

        // when + then
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
        // given
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser(
                    new RegisterRequest("Bob", "Newman", TEST_EMAIL, TEST_PASSWORD, "123456789")
            );
        });

        verify(userRepository, never()).save(any(User.class));
    }

    // ---------------------------------------------
    // TESTS FOR loginUser
    // ---------------------------------------------
    @Test
    void loginUser_Success() {
        // given
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mocked_jwt_token");

        // when
        var response = userService.loginUser(loginRequest);

        // then
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
    }

    @Test
    void loginUser_ThrowsNotFound_WhenEmailDoesNotExist() {
        // given
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // when + then
        assertThrows(NotFoundException.class, () -> userService.loginUser(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginUser_ThrowsInvalidCredentials_WhenPasswordMismatch() {
        // given
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "encodedPassword")).thenReturn(false);

        // when + then
        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    // ---------------------------------------------
    // TESTS FOR getAllUsers
    // ---------------------------------------------
    @Test
    void getAllUsers_ReturnsListOfUserDtos() {
        // given
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

        // when
        var result = userService.getAllUsers();

        // then
        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
        assertEquals("user2@test.com", result.get(1).getEmail());
    }

    // ---------------------------------------------
    // TESTS FOR getCurrentUser
    // ---------------------------------------------
    @Test
    void getCurrentUser_Success() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);

        // Mock SecurityContextHolder
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // when
        var currentUser = userService.getCurrentUser();

        // then
        assertNotNull(currentUser);
        assertEquals(TEST_EMAIL, currentUser.getEmail());
    }

    @Test
    void getCurrentUser_ThrowsUsernameNotFound() {
        // given
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsernameNotFoundException.class,
                () -> userService.getCurrentUser());
    }

    // ---------------------------------------------
    // TESTS FOR activateAccount
    // ---------------------------------------------
    @Test
    void activateAccount_Success() throws MessagingException {
        // given
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

        // when
        userService.activateAccount(tokenValue);

        // then
        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
        verify(activationTokenRepository, times(1)).save(activationToken);
    }

    @Test
    void activateAccount_ThrowsRuntime_WhenTokenNotFound() {
        // given
        String tokenValue = "nonExistingToken";
        when(activationTokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.empty());

        // when + then
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