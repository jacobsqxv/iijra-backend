package dev.aries.iijra.module.auth;

import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.exception.InvalidTokenException;
import dev.aries.iijra.exception.UnauthorizedAccessException;
import dev.aries.iijra.module.token.TokenService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserRepository;
import dev.aries.iijra.security.JwtService;
import dev.aries.iijra.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@InjectMocks
	private AuthService authService;
	@Mock
	private UserRepository userRepo;
	@Mock
	private AuthenticationManager authManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private TokenService tokenService;
	@Mock
	private PasswordEncoder passwordEncoder;

	private User testUser;

	@Nested
	@DisplayName("Login Tests")
	class LoginTests {
		@Test
		@DisplayName("Should login successfully when valid credentials for existing user")
		void login_WithExistingUser_SuccessTest() {
			LoginRequest loginRequest = new LoginRequest("test@email.com", "Test123");
			testUser = TestDataFactory.newUser();
			UserDetailsImpl userDetails = new UserDetailsImpl(testUser);

			Authentication auth = new UsernamePasswordAuthenticationToken(
					userDetails, loginRequest.password()
			);
			when(authManager.authenticate(any(Authentication.class)))
					.thenReturn(auth);

			String mockToken = "mock.jwt.token";
			when(jwtService.generateToken(auth)).thenReturn(mockToken);

			LoginResponse response = authService.login(loginRequest);

			assertNotNull(response);
			assertNotNull(response.token());

			verify(authManager, times(1)).authenticate(any(Authentication.class));
			verify(jwtService, times(1)).generateToken(auth);
		}

		@Test
		@DisplayName("Should throw exception when invalid credentials")
		void login_WithInvalidCredentials_ShouldThrowException() {
			LoginRequest loginRequest = new LoginRequest("test@email.com", "Test123");
			testUser = TestDataFactory.newUser();

			when(authManager.authenticate(any(Authentication.class)))
					.thenThrow(new BadCredentialsException(ExceptionConstant.INVALID_CREDENTIALS));

			assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

			verify(authManager, times(1)).authenticate(any(Authentication.class));
			verify(jwtService, never()).generateToken(any());
		}

		@Test
		@DisplayName("Should throw exception when staff is deleted")
		void login_InactiveUser_ShouldThrowException() {
			LoginRequest loginRequest = new LoginRequest("test@email.com", "Test123");
			testUser = TestDataFactory.newUser();
			testUser.setArchived(true);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					new UserDetailsImpl(testUser), loginRequest.password());
			when(authManager.authenticate(any(Authentication.class))).thenReturn(authToken);

			assertThrows(UnauthorizedAccessException.class, () -> authService.login(loginRequest));

			verify(authManager, times(1)).authenticate(any(Authentication.class));
			verify(jwtService, never()).generateToken(any());
		}

		@Test
		@DisplayName("Should throw exception when user not found")
		void login_WithNonexistentUser_ShouldThrowException() {
			LoginRequest loginRequest = new LoginRequest("test@email.com", "Test123");

			when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
					.thenThrow(new BadCredentialsException(ExceptionConstant.INVALID_CREDENTIALS));

			assertThrows(BadCredentialsException.class, () ->
					authService.login(loginRequest));

			verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
			verify(jwtService, never()).generateToken(any());
		}
	}

	@Nested
	@DisplayName("Forgot Password Tests")
	class ForgotPasswordTests {
		@Test
		@DisplayName("Should successfully generate reset token when email exists")
		void forgotPassword_WithValidEmail_ShouldGenerateToken() {
			ForgotPassword request = new ForgotPassword("test@email.com");
			testUser = TestDataFactory.newUser();

			when(userRepo.findByEmail(request.email()))
					.thenReturn(Optional.of(testUser));

			String response = authService.forgotPassword(request);

			assertEquals("Code has been sent to your email address", response);
			verify(userRepo).findByEmail(request.email());
			verify(tokenService).addNewToken(testUser, TokenType.PASSWORD_RESET);
		}

		@Test
		@DisplayName("Should throw exception when email not found")
		void forgotPassword_WithInvalidEmail_ShouldThrowException() {
			// Arrange
			ForgotPassword request = new ForgotPassword("nonexistent@email.com");

			when(userRepo.findByEmail(request.email()))
					.thenReturn(Optional.empty());

			// Act & Assert
			assertThrows(EntityNotFoundException.class, () ->
					authService.forgotPassword(request));

			verify(userRepo).findByEmail(request.email());
			verify(tokenService, never()).addNewToken(any(), any());
		}
	}

	@Nested
	@DisplayName("Reset Password Tests")
	class ResetPasswordTests {
		@Test
		@DisplayName("Should successfully reset password with valid token")
		void resetPassword_WithValidToken_ShouldUpdatePassword() {
			String token = "valid-token";
			ResetPassword request = new ResetPassword("test@email.com", "newPassword123");
			testUser = TestDataFactory.newUser();

			when(passwordEncoder.encode(request.password()))
					.thenReturn("encodedPassword");
			doReturn(testUser).when(tokenService).validateToken(request.email(), token);

			String response = authService.resetPassword(token, request);

			assertEquals("Password reset successfully! Proceed to login", response);

			verify(userRepo).save(testUser);
			verify(passwordEncoder).encode(request.password());
			verify(tokenService).validateToken(request.email(), token);
			verify(tokenService).deleteUsedToken(testUser, token);
		}

		@Test
		@DisplayName("Should throw exception when token is invalid")
		void resetPassword_WithInvalidToken_ShouldThrowException() {
			String token = "invalid-token";
			ResetPassword request = new ResetPassword("test@email.com", "newPassword123");
			testUser = TestDataFactory.newUser();

			doThrow(new InvalidTokenException())
					.when(tokenService).validateToken(request.email(), token);

			assertThrows(InvalidTokenException.class, () ->
					authService.resetPassword(token, request));

			verify(userRepo, never()).save(any());
			verify(passwordEncoder, never()).encode(any());
			verify(tokenService, never()).deleteUsedToken(any(), any());
		}

		@Test
		@DisplayName("Should throw exception when email not found")
		void resetPassword_WithNonexistentEmail_ShouldThrowException() {
			String token = "valid-token";
			ResetPassword request = new ResetPassword("nonexistent@email.com", "newPassword123");

			doThrow(new EntityNotFoundException())
					.when(tokenService).validateToken(request.email(), token);

			assertThrows(EntityNotFoundException.class, () ->
					authService.resetPassword(token, request));

			verify(userRepo, never()).save(any());
			verify(passwordEncoder, never()).encode(any());
			verify(tokenService, times(1)).validateToken(request.email(), token);
			verify(tokenService, never()).deleteUsedToken(any(), any());
		}
	}
}
