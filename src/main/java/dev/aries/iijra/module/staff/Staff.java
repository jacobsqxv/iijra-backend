package dev.aries.iijra.module.staff;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.department.Department;
import dev.aries.iijra.module.staffprofile.StaffProfile;
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
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Staff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@OneToOne(cascade = CascadeType.ALL)
	private StaffProfile profile;

	@OneToOne
	private Department department;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Embedded @Column(nullable = false)
	private Auditing auditing = new Auditing();

	public Staff(String email, String password, Department department, Role role) {
		this.email = email;
		this.password = password;
		this.department = department;
		this.role = role;
		this.status = Status.ACTIVE;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Staff staff)) {
			return false;
		}

		return id.equals(staff.id) && email.equals(staff.email) && profile.equals(staff.profile) && department.equals(staff.department) && role == staff.role;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + email.hashCode();
		result = 31 * result + profile.hashCode();
		result = 31 * result + department.hashCode();
		result = 31 * result + role.hashCode();
		return result;
	}
}
