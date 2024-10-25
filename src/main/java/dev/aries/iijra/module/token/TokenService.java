package dev.aries.iijra.module.token;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.exception.InvalidTokenException;
import dev.aries.iijra.module.staff.Staff;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepo;
	private final SecureRandom secureRandom = new SecureRandom();

	public void addNewToken(Staff staff, TokenType type) {
		cleanUpPreviousToken(staff, type);
		String value = generateToken();
		Token newToken = new Token(
				staff,
				value,
				type,
				LocalDateTime.now().plusMinutes(15)
		);
		tokenRepo.save(newToken);
		log.info("Token saved successfully: {}", value);
	}

	private void cleanUpPreviousToken(Staff staff, TokenType type) {
		tokenRepo.deleteAll(tokenRepo.findByStaffAndType(staff, type));
	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();
		IntStream.range(0, 6).forEach(i -> token.append(secureRandom.nextInt(0, 10)));
		return token.toString();
	}

	public void validateToken(Staff staff, String value) {
		Token token = getTokenByStaffAndValue(staff, value);
		if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException();
		}
	}

	private Token getTokenByStaffAndValue(Staff staff, String value) {
		return tokenRepo.findByStaffAndValue(staff, value)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.TOKEN_VALUE_DOESNT_EXIST + value));
	}

	public void deleteUsedToken(Staff staff, String value) {
		tokenRepo.deleteByStaffAndValue(staff, value);
		log.info("Token deleted successfully");
	}
}
