package dev.aries.iijra.module.department;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.HibernateProxy;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "Department.staff", attributeNodes = @NamedAttributeNode("staff"))
@NamedEntityGraph(name = "Department.hod", attributeNodes = @NamedAttributeNode("hod"))
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@ToString.Exclude
	private Staff hod;

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@BatchSize(size = 10)
	@ToString.Exclude
	private Set<Staff> staff = new HashSet<>();

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	@Column(nullable = false)
	private Boolean isArchived;

	private LocalDateTime archivedAt;

	public Department(String name) {
		this.name = name;
		this.hod = null;
		this.staff = new HashSet<>();
		this.isArchived = false;
	}

	public void archive() {
		this.isArchived = true;
		this.archivedAt = LocalDateTime.now();
		if (this.hod != null) {
			this.hod.archive();
		}
		for (Staff member : this.staff) {
			member.archive();
		}
	}

	public void restore() {
		this.isArchived = false;
		this.archivedAt = null;
		if (this.hod != null) {
			this.hod.restore();
		}
		for (Staff member : this.staff) {
			member.restore();
		}
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
