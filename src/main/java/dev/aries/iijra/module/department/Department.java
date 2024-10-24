package dev.aries.iijra.module.department;

import java.util.HashSet;
import java.util.Set;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
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

	@ManyToOne
	@ToString.Exclude
	private Staff hod;

	@OneToMany
	@BatchSize(size = 10)
	@ToString.Exclude
	private Set<Staff> staff = new HashSet<>();

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Department(String name) {
		this.name = name;
		this.hod = null;
		this.staff = new HashSet<>();
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

		Department department = (Department) o;
		return id != null && id.equals(department.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 31;
	}
}
