package dev.aries.iijra.module.token;

import java.util.Optional;

import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.user.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

	@EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
	void deleteAllByUserAndType(User user, TokenType type);

	@EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.FETCH)
	Optional<Token> findByUser_EmailAndValue(String email, String value);

	@EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
	void deleteByUserAndValue(User user, String value);
}
