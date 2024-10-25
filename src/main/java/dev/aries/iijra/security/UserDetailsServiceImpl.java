package dev.aries.iijra.security;

import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final StaffRepository staffRepo;

	@Override
	public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
		Staff staff = staffRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Staff does not exist"));
		return new UserDetailsImpl(staff);
	}
}
