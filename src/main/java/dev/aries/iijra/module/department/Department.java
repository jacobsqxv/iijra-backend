package dev.aries.iijra.module.department;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	@Column(nullable = false)
	private Boolean isArchived;

	private LocalDateTime archivedAt;

	public Department(String name) {
		this.name = name;
		this.isArchived = false;
	}

	public void archive() {
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
		if (!(o instanceof Department that)) {
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
