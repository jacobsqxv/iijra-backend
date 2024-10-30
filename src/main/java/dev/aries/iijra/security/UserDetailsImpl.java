package dev.aries.iijra.security;

import java.util.Collection;
import java.util.List;

import dev.aries.iijra.enums.Status;
import dev.aries.iijra.module.user.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserDetailsImpl(User user) implements UserDetails {

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isEnabled() {
		return !user.getIsArchived() && Status.ACTIVE.equals(user.getStatus());
	}


}
