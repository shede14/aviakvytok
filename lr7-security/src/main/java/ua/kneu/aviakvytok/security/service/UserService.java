package ua.kneu.aviakvytok.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.security.dto.SessionView;
import ua.kneu.aviakvytok.security.dto.UserView;
import ua.kneu.aviakvytok.security.entity.Role;
import ua.kneu.aviakvytok.security.entity.User;
import ua.kneu.aviakvytok.security.repository.RoleRepository;
import ua.kneu.aviakvytok.security.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

/**
 * Сервіс користувачів: завантаження користувача для Spring Security (UserDetailsService)
 * і реєстрація з хешуванням пароля BCrypt.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    /** Знаходить користувача за логіном і перетворює його ролі на GrantedAuthority. */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Користувача «" + username + "» не знайдено"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getName()))
                        .toList())
                .build();
    }

    /** Реєстрація: пароль кодується BCryptPasswordEncoder, роль за замовчуванням — ROLE_USER. */
    @Transactional
    public User register(String username, String rawPassword) {
        String name = username.trim();
        if (userRepository.existsByUsername(name)) {
            throw new UsernameTakenException(name);
        }
        User user = new User(name, passwordEncoder.encode(rawPassword));
        user.addRole(findOrCreateRole(Role.USER));
        User saved = userRepository.save(user);
        log.info("Зареєстровано користувача {} з роллю {}", name, Role.USER);
        return saved;
    }

    @Transactional
    public Role findOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new Role(name)));
    }

    /** Користувачі для панелі адміністратора (з хешем пароля і кількістю активних сесій). */
    @Transactional(readOnly = true)
    public List<UserView> findAllUsers() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(u -> new UserView(u.getId(), u.getUsername(), roleNames(u), u.getPassword(), u.getCreatedAt(),
                        activeSessionsOf(u.getUsername())))
                .toList();
    }

    /** Активні користувачі за даними SessionRegistry. */
    public List<SessionView> activeSessions() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof UserDetails)
                .map(p -> (UserDetails) p)
                .map(p -> new SessionView(p.getUsername(),
                        p.getAuthorities().stream().map(a -> a.getAuthority()).sorted().toList(),
                        sessionRegistry.getAllSessions(p, false).stream()
                                .sorted(Comparator.comparing(SessionInformation::getLastRequest).reversed())
                                .map(s -> new SessionView.Session(shortId(s.getSessionId()), s.getLastRequest(), s.isExpired()))
                                .toList()))
                .filter(v -> !v.sessions().isEmpty())
                .sorted(Comparator.comparing(SessionView::username))
                .toList();
    }

    private int activeSessionsOf(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof UserDetails d && d.getUsername().equals(username))
                .mapToInt(p -> sessionRegistry.getAllSessions(p, false).size())
                .sum();
    }

    private static List<String> roleNames(User u) {
        return u.getRoles().stream().map(Role::getName).sorted().toList();
    }

    private static String shortId(String id) {
        return id.length() > 10 ? id.substring(0, 6) + "…" + id.substring(id.length() - 4) : id;
    }
}
