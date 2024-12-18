package dev.aries.iijra.module.token;

import java.time.LocalDateTime;
import java.util.Optional;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.exception.InvalidTokenException;
import dev.aries.iijra.module.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	private static final String TEST_TOKEN_VALUE = "123456";
	@InjectMocks
	private TokenService tokenService;
	@Mock
	private TokenRepository tokenRepo;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("test@email.com");
	}

	@Nested
	@DisplayName("Add token tests")
	class AddTokenTests {
		@Test
		@DisplayName("Should successfully return six-digit token")
		void addNewToken_ShouldGenerateSixDigitToken() {
			TokenType type = TokenType.PASSWORD_RESET;

			tokenService.addNewToken(testUser, type);

			ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
			verify(tokenRepo).save(tokenCaptor.capture());

			Token savedToken = tokenCaptor.getValue();
			String tokenValue = savedToken.getValue();

			assertEquals(6, tokenValue.length());
			assertTrue(tokenValue.matches("\\d{6}")); // Verify it's 6 digits
		}

		@Test
		@DisplayName("Should clean up previous tokens when adding new token")
		void addNewToken_ShouldCleanUpPreviousTokens() {
			TokenType type = TokenType.PASSWORD_RESET;

			tokenService.addNewToken(testUser, type);

			verify(tokenRepo).deleteAllByUserAndType(testUser, type);
			verify(tokenRepo).save(any(Token.class));
		}

		@Test
		@DisplayName("Should create new token with correct attributes")
		void addNewToken_ShouldSaveTokenWithCorrectAttributes() {
			TokenType type = TokenType.PASSWORD_RESET;
			LocalDateTime beforeTest = LocalDateTime.now();

			tokenService.addNewToken(testUser, type);

			ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
			verify(tokenRepo).save(tokenCaptor.capture());

			Token savedToken = tokenCaptor.getValue();
			LocalDateTime expirationTime = savedToken.getExpiresAt();

			assertEquals(testUser, savedToken.getUser());
			assertEquals(type, savedToken.getType());
			assertNotNull(savedToken.getValue());
			assertNotNull(savedToken.getExpiresAt());
			assertTrue(expirationTime.isAfter(beforeTest));
		}
	}

	@Nested
	@DisplayName("Validate token tests")
	class ValidateTokenTests {
		@Test
		@DisplayName("Should successfully validate valid non-expired token")
		void validateToken_WithValidTokenAndNotExpired_ShouldNotThrowException() {
			LocalDateTime futureTime = LocalDateTime.now().plusMinutes(5);
			Token validToken = new Token(testUser, TEST_TOKEN_VALUE, TokenType.PASSWORD_RESET, futureTime);
			when(tokenRepo.findByUser_EmailAndValue(testUser.getEmail(), TEST_TOKEN_VALUE))
					.thenReturn(Optional.of(validToken));

			assertDoesNotThrow(() -> tokenService.validateToken(testUser.getEmail(), TEST_TOKEN_VALUE));
		}

		@Test
		@DisplayName("Should throw exception with expired token")
		void validateToken_WithExpiredToken_ShouldThrowInvalidTokenException() {
			String email = testUser.getEmail();
			LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
			Token expiredToken = new Token(testUser, TEST_TOKEN_VALUE, TokenType.PASSWORD_RESET, pastTime);

			when(tokenRepo.findByUser_EmailAndValue(testUser.getEmail(), TEST_TOKEN_VALUE)).thenReturn(Optional.of(expiredToken));

			assertThrows(InvalidTokenException.class,
					() -> tokenService.validateToken(email, TEST_TOKEN_VALUE));
		}

		@Test
		@DisplayName("Should throw exception with non-existing token")
		void validateToken_WithNonexistentToken_ShouldThrowEntityNotFoundException() {
			String email = testUser.getEmail();
			when(tokenRepo.findByUser_EmailAndValue(email, TEST_TOKEN_VALUE))
					.thenReturn(Optional.empty());

			EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
					() -> tokenService.validateToken(email, TEST_TOKEN_VALUE));

			assertTrue(exception.getMessage().contains(ExceptionConstant.TOKEN_USER_DOESNT_EXIST));
			assertTrue(exception.getMessage().contains(email));
		}

		@Test
		@DisplayName("Should throw exception with token expiring at time of validation")
		void validateToken_WithBoundaryExpirationTime_ShouldBehaveCorrectly() {
			LocalDateTime exactlyNow = LocalDateTime.now();
			String email = testUser.getEmail();
			Token tokenExactlyNow = new Token(testUser, TEST_TOKEN_VALUE, TokenType.PASSWORD_RESET, exactlyNow);
			when(tokenRepo.findByUser_EmailAndValue(testUser.getEmail(), TEST_TOKEN_VALUE))
					.thenReturn(Optional.of(tokenExactlyNow));

			assertThrows(InvalidTokenException.class,
					() -> tokenService.validateToken(email, TEST_TOKEN_VALUE),
					"Token expiring exactly now should be considered expired");
		}
	}

	@Nested
	class DeleteTokenTests {
		@Test
		@DisplayName("Should delete token successfully using correct repository method")
		void deleteUsedToken_ShouldCallRepositoryDelete() {
			doNothing().when(tokenRepo).deleteByUserAndValue(testUser, TEST_TOKEN_VALUE);

			tokenService.deleteUsedToken(testUser, TEST_TOKEN_VALUE);

			verify(tokenRepo).deleteByUserAndValue(testUser, TEST_TOKEN_VALUE);
		}
	}

}
