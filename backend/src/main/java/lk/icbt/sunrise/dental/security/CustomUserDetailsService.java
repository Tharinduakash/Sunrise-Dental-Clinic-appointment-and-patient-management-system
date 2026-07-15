package lk.icbt.sunrise.dental.security;

import lk.icbt.sunrise.dental.domain.StaffUser;
import lk.icbt.sunrise.dental.repository.StaffUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffUserRepository staffUserRepository;

    public CustomUserDetailsService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser staffUser = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

        return User.builder()
                .username(staffUser.getUsername())
                .password(staffUser.getPasswordHash())
                .disabled(!staffUser.isEnabled())
                .authorities("ROLE_" + staffUser.getRole().name())
                .build();
    }
}
