package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.dto.UserDTO;
import nykon.foodmarket.dto.UserStatisticsDTO;
import nykon.foodmarket.model.User;
import nykon.foodmarket.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== USER MANAGEMENT ====================

    /**
     * Отримати статистику всіх користувачів
     */
    @GetMapping("/users/statistics")
    public ResponseEntity<List<UserStatisticsDTO>> getUsersStatistics() {
        return ResponseEntity.ok(adminService.getUsersStatistics());
    }

    /**
     * Активувати користувача
     */
    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setUserActive(id, true));
    }

    /**
     * Деактивувати користувача
     */
    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setUserActive(id, false));
    }

    // ==================== SUPPLIER REVIEWS (від buyers) ====================

    /**
     * Отримати відгуки постачальника (для перегляду)
     */
    @GetMapping("/suppliers/{supplierId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getSupplierReviews(@PathVariable Long supplierId) {
        return ResponseEntity.ok(adminService.getSupplierReviews(supplierId));
    }
}