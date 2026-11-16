package ua.kneu.aviakvytok.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.kneu.aviakvytok.security.config.SecurityConfig;
import ua.kneu.aviakvytok.security.controller.UserController;
import ua.kneu.aviakvytok.security.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Перевірка правил доступу SecurityFilterChain для різних ролей (без бази даних). */
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecurityAccessTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithAnonymousUser
    void homePageIsPublic() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void anonymousIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/user")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotOpenAdminPage() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotOpenManagerPage() throws Exception {
        mvc.perform(get("/manager")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userOpensUserPage() throws Exception {
        mvc.perform(get("/user")).andExpect(status().isOk()).andExpect(view().name("user-page"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerOpensManagerAndUserPages() throws Exception {
        when(userService.activeSessions()).thenReturn(List.of());
        mvc.perform(get("/manager")).andExpect(status().isOk());
        mvc.perform(get("/user")).andExpect(status().isOk());
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminOpensAdminPageButNotManagerPage() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of());
        when(userService.activeSessions()).thenReturn(List.of());
        mvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(view().name("admin-page"));
        mvc.perform(get("/manager")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void loginAndRegistrationAreOnlyForAnonymous() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isForbidden());
        mvc.perform(get("/registration")).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void registrationCreatesUserAndRedirectsToLogin() throws Exception {
        mvc.perform(post("/registration").with(csrf())
                        .param("username", "olena").param("password", "secret1").param("confirmPassword", "secret1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
        verify(userService).register("olena", "secret1");
    }

    @Test
    @WithAnonymousUser
    void registrationWithDifferentPasswordsIsRejected() throws Exception {
        mvc.perform(post("/registration").with(csrf())
                        .param("username", "olena").param("password", "secret1").param("confirmPassword", "other12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));
        verify(userService, never()).register(anyString(), eq("secret1"));
    }

    @Test
    @WithAnonymousUser
    void registrationWithoutCsrfTokenIsForbidden() throws Exception {
        mvc.perform(post("/registration").param("username", "x").param("password", "y"))
                .andExpect(status().isForbidden());
    }
}
