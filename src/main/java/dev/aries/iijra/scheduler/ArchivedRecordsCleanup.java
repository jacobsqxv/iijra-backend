package dev.aries.iijra.scheduler;

import java.time.LocalDateTime;

import dev.aries.iijra.module.department.DepartmentRepository;
import dev.aries.iijra.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchivedRecordsCleanup {
	private final UserRepository userRepo;
	private final DepartmentRepository departmentRepo;

	@Scheduled(cron = "0 0 2 * * ?")
	@Transactional
	public void cleanupArchivedRecords() {
		LocalDateTime cutOff = LocalDateTime.now().minusDays(30);
		try {
			int deletedStaffCount = deleteUsers(cutOff);
			int deletedDepartmentCount = deleteDepartments(cutOff);

			log.info("Archived records cleanup completed. deletedUsers={}, deletedDepartments={}, cutOffDate={}",
					deletedStaffCount,
					deletedDepartmentCount,
					cutOff);
		} catch (Exception e) {
			log.error("Failed to cleanup archived records", e);
		}
	}

	private int deleteDepartments(LocalDateTime cutOff) {
		int count = departmentRepo.deleteByIsArchivedTrueAndArchivedAtBefore(cutOff);
		log.debug("Deleted archived departments. count={}", count);
		return count;
	}

	private int deleteUsers(LocalDateTime cutOff) {
		int count = userRepo.deleteByIsArchivedTrueAndArchivedAtBefore(cutOff);
		log.debug("Deleted archived user records. count={}", count);
		return count;
	}

}
