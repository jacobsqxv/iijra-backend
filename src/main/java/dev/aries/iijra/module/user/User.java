package dev.aries.iijra.module.user;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@Table(name = "_user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@ToString.Exclude
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private Boolean isArchived;

	private LocalDateTime archivedAt;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public User(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = Status.ACTIVE;
		this.isArchived = false;
	}

	public void archive() {
		this.status = Status.INACTIVE;
		this.isArchived = true;
		this.archivedAt = LocalDateTime.now();
	}

	public void restore() {
		this.isArchived = false;
		this.archivedAt = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User that = (User) o;
		return Objects.equals(getId(), that.getId()) && Objects.equals(getEmail(), that.getEmail());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getEmail());
	}
}
