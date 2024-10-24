package dev.aries.iijra.module.staffprofile;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.utility.Auditing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraph(name = "StaffProfile.staff", attributeNodes = @NamedAttributeNode("staff"))
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
	@ToString.Exclude
	private Staff staff;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	public StaffProfile(String id, String fullName, Staff staff) {
		this.id = id;
		this.fullName = fullName;
		this.staff = staff;
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

		StaffProfile profile = (StaffProfile) o;
		return id != null && id.equals(profile.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 31;
	}
}
