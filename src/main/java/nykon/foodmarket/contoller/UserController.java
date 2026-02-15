package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.request.user.ChangePasswordRequest;
import nykon.foodmarket.model.User;
import nykon.foodmarket.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User user) {
        User updated = userService.updateCurrentUser(user);
        return ResponseEntity.ok(updated);
    }

//    @PutMapping("/me/password")
//    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
//        userService.changePassword(request);
//        return ResponseEntity.noContent().build();
//    }
}
