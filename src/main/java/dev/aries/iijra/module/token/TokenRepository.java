package dev.aries.iijra.module.token;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.staff.Staff;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

	List<Token> findByStaffAndType(Staff staff, TokenType type);

	@EntityGraph(value = "Token.staff")
	Optional<Token> findByStaffAndValue(Staff staff, String value);

	void deleteByStaffAndValue(Staff staff, String value);
}
