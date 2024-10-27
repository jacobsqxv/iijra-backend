package dev.aries.iijra.module.token;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.user.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

	List<Token> findByUserAndType(User user, TokenType type);

	@EntityGraph(value = "Token.user")
	Optional<Token> findByUserAndValue(User user, String value);

	void deleteByUserAndValue(User user, String value);
}
