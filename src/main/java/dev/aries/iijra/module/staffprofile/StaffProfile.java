package dev.aries.iijra.module.staffprofile;

import java.util.Objects;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class StaffProfile {
	@Id
	@Column(updatable = false, nullable = false)
	private String id;

	private String profileImage;

	@Column(nullable = false)
	private String fullName;

	@Column(columnDefinition = "TEXT", length = 500)
	private String bio;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "profile")
	@JoinColumn(nullable = false)
	private Staff staff;

	@Embedded @Column(nullable = false)
	private Auditing auditing = new Auditing();

	public StaffProfile(String id, String fullName, Staff staff) {
		this.id = id;
		this.fullName = fullName;
		this.staff = staff;
	}


	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StaffProfile that)) {
			return false;
		}

		return id.equals(that.id) && fullName.equals(that.fullName);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + fullName.hashCode();
		result = 31 * result + Objects.hashCode(auditing);
		return result;
	}
}
