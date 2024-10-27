package dev.aries.iijra.module.admin;

import dev.aries.iijra.enums.Role;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserService userService;
	private final AdminRepository adminRepo;

	@Transactional
	public AdminResponse addNewSystemAdmin(AdminRequest request) {
		User user = userService.createUser(request.email(), Role.SYS_ADMIN);
		Admin newAdmin = new Admin(user, request.fullName());
		adminRepo.save(newAdmin);
		return AdminResponse.newResponse(newAdmin);
	}
}
