package dev.aries.iijra.module.department;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@OneToOne
	private Staff hod;

	@OneToMany
	@BatchSize(size = 10)
	private List<Staff> staff = new ArrayList<>();
	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Department(String name) {
		this.name = name;
		this.hod = null;
		this.staff = new ArrayList<>();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Department that)) {
			return false;
		}

		return id.equals(that.id) && name.equals(that.name) && hod.equals(that.hod) && staff.equals(that.staff);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + hod.hashCode();
		result = 31 * result + staff.hashCode();
		result = 31 * result + Objects.hashCode(auditing);
		return result;
	}
}
