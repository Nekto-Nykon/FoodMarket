package nykon.foodmarket.utils;

import nykon.foodmarket.model.User;
import nykon.foodmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        SecurityUtils.userRepository = userRepository;
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
            return (String) principal;
        }

        return null;
    }

    public static User getCurrentUser() {
        String email = getCurrentUserEmail();

        if (email == null || userRepository == null) {
            return null;
        }

        return userRepository.findByEmail(email).orElse(null);
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        return !(principal instanceof String && "anonymousUser".equals(principal));
    }

    public static boolean hasRole(String roleName) {
        User user = getCurrentUser();
        return user != null && user.getRole() != null
                && user.getRole().getName().equalsIgnoreCase(roleName);
    }
}



































