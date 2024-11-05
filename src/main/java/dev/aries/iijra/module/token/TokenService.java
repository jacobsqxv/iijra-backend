package dev.aries.iijra.module.token;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.exception.InvalidTokenException;
import dev.aries.iijra.module.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepo;
	private final SecureRandom secureRandom = new SecureRandom();

	@Transactional
	public void addNewToken(User user, TokenType type) {
		cleanUpPreviousToken(user, type);
		String value = generateToken();
		Token newToken = new Token(
				user,
				value,
				type,
				LocalDateTime.now().plusMinutes(15)
		);
		tokenRepo.save(newToken);
		log.info("Token saved successfully: {}", value);
	}

	private void cleanUpPreviousToken(User user, TokenType type) {
		tokenRepo.deleteAllByUserAndType(user, type);
	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();
		IntStream.range(0, 6).forEach(i -> token.append(secureRandom.nextInt(0, 10)));
		return token.toString();
	}

	public User validateToken(String email, String value) {
		Token token = getTokenByUserAndValue(email, value);
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException();
		}
		return token.getUser();
	}

	private Token getTokenByUserAndValue(String email, String value) {
		return tokenRepo.findByUser_EmailAndValue(email, value)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.TOKEN_USER_DOESNT_EXIST + email));
	}

	public void deleteUsedToken(User user, String value) {
		tokenRepo.deleteByUserAndValue(user, value);
		log.info("Token deleted successfully");
	}
}
