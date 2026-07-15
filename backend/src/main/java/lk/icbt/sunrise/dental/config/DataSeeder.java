package lk.icbt.sunrise.dental.config;

import lk.icbt.sunrise.dental.domain.StaffRole;
import lk.icbt.sunrise.dental.domain.StaffUser;
import lk.icbt.sunrise.dental.repository.StaffUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default admin and reception account on first run so the login screen
 * is usable out of the box. Passwords are hashed with the same {@link PasswordEncoder}
 * used at login time, never stored or logged in plain text.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(StaffUserRepository staffUserRepository, PasswordEncoder passwordEncoder) {
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (staffUserRepository.count() > 0) {
            return;
        }

        staffUserRepository.save(new StaffUser("admin", passwordEncoder.encode("Admin@123"),
                "System Administrator", StaffRole.ADMIN));
        staffUserRepository.save(new StaffUser("reception", passwordEncoder.encode("Reception@123"),
                "Front Desk Reception", StaffRole.STAFF));

        log.warn("Seeded default staff accounts (admin/Admin@123, reception/Reception@123). " +
                "Change these credentials before any real deployment.");
    }
}
