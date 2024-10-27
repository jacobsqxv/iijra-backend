package dev.aries.iijra.module.staff;

import java.util.Objects;

import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
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
@NamedEntityGraph(name = "Staff.user", attributeNodes = @NamedAttributeNode("user"))
@NamedEntityGraph(name = "Staff.department", attributeNodes = @NamedAttributeNode("department"))
public class Staff {

	@Id
	@Column(updatable = false, nullable = false, unique = true)
	private String id;

	private String profileImage;

	@Column(nullable = false)
	private String fullName;

	@Column(columnDefinition = "TEXT", length = 500)
	private String bio;

	@OneToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private User user;

	@ManyToOne
	@ToString.Exclude
	private Department department;

	@Column(nullable = false)
	private Boolean isHod;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Staff(String id, String fullName, User user, Department department) {
		this.id = id;
		this.fullName = fullName;
		this.user = user;
		this.department = department;
		this.isHod = false;
	}

	public void archive() {
		this.user.archive();
	}

	public void restore() {
		this.user.restore();
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
		if (!(o instanceof Staff that)) {
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
