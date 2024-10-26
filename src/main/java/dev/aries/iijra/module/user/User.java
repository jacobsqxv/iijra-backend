package dev.aries.iijra.module.user;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.admin.Admin;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "User.staff", attributeNodes = @NamedAttributeNode("staff"))
@Table(name = "_user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	@ToString.Exclude
	private String password;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Staff staff;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Admin admin;

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
		if (!(o instanceof User that)) {
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