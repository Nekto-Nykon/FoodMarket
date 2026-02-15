package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.request.user.ChangePasswordRequest;
import nykon.foodmarket.model.User;
import nykon.foodmarket.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        // TODO: Get user from security context
        // String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // return userRepository.findByEmail(email)
        //         .orElseThrow(() -> new RuntimeException("User not found"));

        // Placeholder implementation
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateCurrentUser(User user) {
        User currentUser = getCurrentUser();

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setPhone(user.getPhone());

        // Email should not be changed through this endpoint
        // Password should be changed through separate endpoint

        return userRepository.save(currentUser);
    }

//    public void changePassword(ChangePasswordRequest request) {
//        User currentUser = getCurrentUser();
//
//        // Verify old password
//        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPasswordHash())) {
//            throw new RuntimeException("Old password is incorrect");
//        }
//
//        // Validate new password
//        if (request.getNewPassword().length() < 8) {
//            throw new RuntimeException("New password must be at least 8 characters long");
//        }
//
//        // Update password
//        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
//        userRepository.save(currentUser);
//    }
}
