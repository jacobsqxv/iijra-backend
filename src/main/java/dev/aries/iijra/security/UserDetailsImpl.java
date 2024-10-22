package dev.aries.iijra.security;

import java.util.Collection;
import java.util.List;

import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.staff.Staff;
import lombok.AllArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private final transient Staff staff;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(staff.getRole().name()));
	}

	@Override
	public String getPassword() {
		return staff.getPassword();
	}

	@Override
	public String getUsername() {
		return staff.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return staff.getStatus().equals(Status.ACTIVE);
	}

}
