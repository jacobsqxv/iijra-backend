package dev.aries.iijra.module.token;

import java.time.LocalDateTime;

import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.staff.Staff;
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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
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
@NamedEntityGraph(name = "Token.staff", attributeNodes = @NamedAttributeNode("staff"))
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Staff staff;
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

	public Token(Staff staff, String value, TokenType type, LocalDateTime expiresAt) {
		this.staff = staff;
		this.value = value;
		this.type = type;
		this.expiresAt = expiresAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}

		Token token = (Token) o;
		return id != null && id.equals(token.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 31;
	}
}
