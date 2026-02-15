package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.mapper.ReviewMapper;
import nykon.foodmarket.model.*;
import nykon.foodmarket.repository.*;
import nykon.foodmarket.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Створити відгук (тільки buyer після виконаного замовлення)
     */
    public ReviewDTO createReview(Long orderId, Long supplierId, Integer rating, String comment) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований");
        }

        // Перевіряємо замовлення
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Замовлення не знайдено"));

        // Перевіряємо що це замовлення поточного користувача
        User buyer = order.getBuyer();
        if (buyer == null || !buyer.getId().equals(currentUserId)) {
            // Додаткова перевірка - можливо треба порівняти по-іншому
            System.out.println("DEBUG: currentUserId=" + currentUserId + ", buyerId=" + (buyer != null ? buyer.getId() : "null"));
            throw new RuntimeException("Це не ваше замовлення");
        }

        // Перевіряємо статус замовлення (можна залишити відгук тільки після доставки)
        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Відгук можна залишити тільки після доставки замовлення");
        }

        // Перевіряємо чи вже є відгук для цього замовлення І постачальника
        if (reviewRepository.existsByOrderIdAndSupplierId(orderId, supplierId)) {
            throw new RuntimeException("Ви вже залишили відгук для цього постачальника");
        }

        // Перевіряємо постачальника
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Постачальника не знайдено"));

        // Валідація рейтингу
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Рейтинг має бути від 1 до 5");
        }

        User someBuyer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        // Створюємо відгук
        Review review = new Review();
        review.setOrder(order);
        review.setBuyer(someBuyer);
        review.setSupplier(supplier);
        review.setRating(rating);
        review.setComment(comment);

        review = reviewRepository.save(review);

        // Оновлюємо рейтинг постачальника
        updateSupplierRating(supplierId);

        return reviewMapper.toDTO(review);
    }

    /**
     * Отримати відгуки постачальника
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getSupplierReviews(Long supplierId) {
        List<Review> reviews = reviewRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId);
        return reviewMapper.toDTOList(reviews);
    }

    /**
     * Отримати мої відгуки (для buyer)
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getMyReviews() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return new ArrayList<>();
        }
        List<Review> reviews = reviewRepository.findByBuyerIdOrderByCreatedAtDesc(currentUserId);
        return reviewMapper.toDTOList(reviews);
    }

    /**
     * Отримати відгуки по замовленню
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getOrderReviews(Long orderId) {
        List<Review> reviews = reviewRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return reviewMapper.toDTOList(reviews);
    }

    /**
     * Оновити рейтинг постачальника
     */
    private void updateSupplierRating(Long supplierId) {
        Double avgRating = reviewRepository.getAverageRatingBySupplier(supplierId);
        if (avgRating != null) {
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            if (supplier != null) {
                supplier.setRating(java.math.BigDecimal.valueOf(avgRating));
                supplierRepository.save(supplier);
            }
        }
    }



}