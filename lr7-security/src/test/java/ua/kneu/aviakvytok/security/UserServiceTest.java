package ua.kneu.aviakvytok.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.kneu.aviakvytok.security.entity.Role;
import ua.kneu.aviakvytok.security.entity.User;
import ua.kneu.aviakvytok.security.repository.RoleRepository;
import ua.kneu.aviakvytok.security.repository.UserRepository;
import ua.kneu.aviakvytok.security.service.UserService;
import ua.kneu.aviakvytok.security.service.UsernameTakenException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Модульні тести UserService: BCrypt, роль за замовчуванням, перетворення ролей на GrantedAuthority. */
class UserServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final RoleRepository roles = mock(RoleRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserService service = new UserService(users, roles, encoder, new SessionRegistryImpl());

    @Test
    void registerHashesPasswordAndAssignsRoleUser() {
        when(users.existsByUsername("olena")).thenReturn(false);
        when(roles.findByName(Role.USER)).thenReturn(Optional.of(new Role(Role.USER)));
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User u = service.register("olena", "secret1");

        assertNotEquals("secret1", u.getPassword());
        assertTrue(u.getPassword().startsWith("$2a$"));            // формат BCrypt
        assertTrue(encoder.matches("secret1", u.getPassword()));
        assertEquals(1, u.getRoles().size());
        assertEquals(Role.USER, u.getRoles().iterator().next().getName());
    }

    @Test
    void duplicateUsernameIsRejected() {
        when(users.existsByUsername("admin")).thenReturn(true);
        assertThrows(UsernameTakenException.class, () -> service.register("admin", "secret1"));
        verify(users, never()).save(any());
    }

    @Test
    void loadUserByUsernameConvertsRolesToAuthorities() {
        User u = new User("manager", encoder.encode("manager123"));
        u.addRole(new Role(Role.MANAGER));
        when(users.findByUsername("manager")).thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("manager");

        assertEquals("manager", details.getUsername());
        assertEquals(1, details.getAuthorities().size());
        GrantedAuthority a = details.getAuthorities().iterator().next();
        assertEquals("ROLE_MANAGER", a.getAuthority());
    }

    @Test
    void unknownUserThrowsUsernameNotFound() {
        when(users.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ghost"));
    }
}
