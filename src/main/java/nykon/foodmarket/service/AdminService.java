package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.dto.UserDTO;
import nykon.foodmarket.dto.UserStatisticsDTO;
import nykon.foodmarket.mapper.OrderItemMapper;
import nykon.foodmarket.mapper.UserMapper;
import nykon.foodmarket.model.*;
import nykon.foodmarket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OfferRepository offerRepository;
    private final SupplierRepository supplierRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;

    // ==================== USER STATISTICS ====================

    /**
     * Отримати статистику всіх користувачів
     */
    @Transactional(readOnly = true)
    public List<UserStatisticsDTO> getUsersStatistics() {
        List<User> users = userRepository.findAll();
        List<UserStatisticsDTO> statistics = new ArrayList<>();

        for (User user : users) {
            UserStatisticsDTO dto = new UserStatisticsDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setRole(user.getRole() != null ? user.getRole().getName() : "unknown");
            dto.setIsActive(user.getIsActive());
            dto.setCreatedAt(user.getCreatedAt());

            // Статистика залежно від ролі
            if ("buyer".equalsIgnoreCase(dto.getRole())) {
                List<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(user.getId());
                dto.setTotalOrders(orders.size());
                BigDecimal totalSpent = orders.stream()
                        .map(Order::getTotalAmount)
                        .filter(amount -> amount != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                dto.setTotalSpent(totalSpent);
            } else if ("supplier".equalsIgnoreCase(dto.getRole())) {
                supplierRepository.findByUserId(user.getId()).ifPresent(supplier -> {
                    List<Offer> offers = offerRepository.findBySupplierId(supplier.getId());
                    dto.setTotalOffers(offers.size());

                    // Підрахунок продажів через OrderItems
                    BigDecimal totalSales = calculateSupplierSales(supplier.getId());
                    dto.setTotalSales(totalSales);
                });
            }

            statistics.add(dto);
        }

        return statistics;
    }

    /**
     * Підрахувати загальні продажі постачальника
     */
    private BigDecimal calculateSupplierSales(Long supplierId) {
        // Отримуємо всі offers цього постачальника
        List<Offer> supplierOffers = offerRepository.findBySupplierId(supplierId);

        if (supplierOffers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Збираємо ID всіх offers
        List<Long> offerIds = supplierOffers.stream()
                .map(Offer::getId)
                .toList();

        // Підраховуємо суму всіх OrderItems для цих offers
        BigDecimal totalSales = BigDecimal.ZERO;

        for (Long offerId : offerIds) {
            List<OrderItem> orderItems = orderItemRepository.findByOfferId(offerId);
            for (OrderItem item : orderItems) {
                if (item.getSubtotal() != null) {
                    totalSales = totalSales.add(item.getSubtotal());
                }
            }
        }

        return totalSales;
    }

    /**
     * Активувати/деактивувати користувача
     */
    public UserDTO setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        user.setIsActive(active);
        return userMapper.toDTO(userRepository.save(user));
    }

    // ==================== SUPPLIER REVIEWS (від buyers) ====================

    /**
     * Отримати відгуки для постачальника (для перегляду адміном)
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getSupplierReviews(Long supplierId) {
        List<Review> reviews = reviewRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId);
        List<ReviewDTO> dtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(review.getId());
            dto.setOrderId(review.getOrder().getId());
            dto.setBuyerId(review.getBuyer().getId());

            String buyerName = review.getBuyer().getFirstName();
            if (buyerName != null && review.getBuyer().getLastName() != null) {
                buyerName += " " + review.getBuyer().getLastName();
            }
            dto.setBuyerName(buyerName != null ? buyerName : review.getBuyer().getEmail());

            dto.setSupplierId(review.getSupplier().getId());
            dto.setSupplierName(review.getSupplier().getCompanyName());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());

            dtos.add(dto);
        }

        return dtos;
    }
}