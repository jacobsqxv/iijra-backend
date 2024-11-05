package dev.aries.iijra.module.category;

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

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private String name;

	@Embedded
	@Column(nullable = false)
	private Auditing auditing = new Auditing();

	@Column(nullable = false)
	private Boolean isArchived;

	private LocalDateTime archivedAt;

	public Category(String name) {
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Category that = (Category) o;
		return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName());
	}
}
