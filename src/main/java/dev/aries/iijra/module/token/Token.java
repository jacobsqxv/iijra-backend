package dev.aries.iijra.module.token;

import java.time.LocalDateTime;
import java.util.Objects;

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
import org.hibernate.proxy.HibernateProxy;

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
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (!thisEffectiveClass.equals(oEffectiveClass)) {
			return false;
		}
		if (!(o instanceof Token that)) {
			return false;
		}
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
