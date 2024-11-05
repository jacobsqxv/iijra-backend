package dev.aries.iijra.module.token;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private User user;

	@Column(nullable = false)
	private String value;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TokenType type;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Token(User user, String value, TokenType type, LocalDateTime expiresAt) {
		this.user = user;
		this.value = value;
		this.type = type;
		this.expiresAt = expiresAt;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Token that = (Token) o;
		return Objects.equals(getId(), that.getId()) &&
				Objects.equals(getValue(), that.getValue()) &&
				Objects.equals(getUser(),that.getUser());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getId(), getValue(), getUser());
	}
}
