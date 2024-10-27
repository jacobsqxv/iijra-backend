package dev.aries.iijra.security;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(ExceptionConstant.USER_EMAIL_DOESNT_EXIST + email));
		return new UserDetailsImpl(user);
	}
}
