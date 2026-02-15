package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OrderDTO;
import nykon.foodmarket.dto.OrderItemDTO;
import nykon.foodmarket.dto.SupplierDTO;
import nykon.foodmarket.dto.request.order.OrderItemRequest;
import nykon.foodmarket.mapper.OrderItemMapper;
import nykon.foodmarket.mapper.OrderMapper;
import nykon.foodmarket.model.*;
import nykon.foodmarket.repository.OfferRepository;
import nykon.foodmarket.repository.OrderItemRepository;
import nykon.foodmarket.repository.OrderRepository;
import nykon.foodmarket.repository.UserRepository;
import nykon.foodmarket.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static nykon.foodmarket.utils.SecurityUtils.getCurrentUserId;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    /**
     * Створити нове замовлення
     */
    public OrderDTO createOrder(String deliveryAddress, List<OrderItemRequest> items) {
        // Отримуємо поточного користувача (buyer)
        Long currentUserId = getCurrentUserId();


        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований");
        }

        User buyer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        System.out.println("Buyer found: " + buyer.getEmail());

        // Створюємо замовлення
        Order order = new Order();
        order.setBuyer(buyer);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);

        // Зберігаємо замовлення щоб отримати ID
        order = orderRepository.save(order);

        System.out.println("Order created with ID: " + order.getId());

        // Додаємо items до замовлення
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : items) {
            Offer offer = offerRepository.findById(itemRequest.getOfferId())
                    .orElseThrow(() -> new RuntimeException("Пропозицію не знайдено: " + itemRequest.getOfferId()));

            // Перевіряємо доступність
            if (BigDecimal.valueOf(itemRequest.getQuantity().doubleValue()).compareTo(offer.getAvailableQuantity())>0){
                throw new RuntimeException("Недостатня кількість товару: " + offer.getIngredient().getName());
            }

            // Створюємо OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setOffer(offer);
            orderItem.setQuantity(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setPricePerUnit(offer.getPrice());

            BigDecimal subtotal = offer.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);

            // Зменшуємо доступну кількість
            offer.setAvailableQuantity(offer.getAvailableQuantity().add(BigDecimal.valueOf((-1)*itemRequest.getQuantity())));
            offerRepository.save(offer);

            System.out.println("Added item: " + offer.getIngredient().getName() + " x " + itemRequest.getQuantity());
        }

        // Зберігаємо всі items
        orderItemRepository.saveAll(orderItems);

        // Оновлюємо загальну суму
        order.setTotalAmount(totalAmount);
        order.setOrderItems(( new HashSet<>(orderItems)));

        System.out.println("Order total: " + totalAmount);

        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getOrderSuppliers(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        // Збираємо унікальних постачальників
        Map<Long, Supplier> suppliersMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            Offer offer = item.getOffer();
            if (offer != null && offer.getSupplier() != null) {
                Supplier supplier = offer.getSupplier();
                suppliersMap.put(supplier.getId(), supplier);
            }
        }

        // Конвертуємо в DTO
        List<SupplierDTO> result = new ArrayList<>();
        for (Supplier supplier : suppliersMap.values()) {
            SupplierDTO dto = new SupplierDTO();
            dto.setId(supplier.getId());
            dto.setCompanyName(supplier.getCompanyName());
            dto.setAddress(supplier.getAddress());
            dto.setTaxId(supplier.getTaxId());
            dto.setRating(supplier.getRating());
            dto.setDescription(supplier.getDescription());
            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Optional<Order> orderDTO = orderRepository.findById(id);
        if (!orderDTO.isPresent()) {throw new RuntimeException("Order not found with id: " + id);}
        return orderMapper.toDTO(orderDTO.get());
    }

    @Transactional(readOnly = true)
    public Page<Order> getMyOrders(Pageable pageable) {
        // TODO: Get current user from security context
         Long currentUserId = getCurrentUserId();
         return orderRepository.findByBuyerId(currentUserId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Order.OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public OrderDTO updateOrder(Long id, Order order) {
        OrderDTO existing = getOrderById(id);
        if (!Order.OrderStatus.PENDING.equals(existing.getStatus())) {
            throw new RuntimeException("Cannot update order that is not in PENDING status");
        }

        existing.setDeliveryAddress(order.getDeliveryAddress());
        return orderMapper.toDTO(orderRepository.save(orderMapper.toEntity(existing)));
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        OrderDTO order = getOrderById(id);
        order.setStatus(status.toString());
        return orderRepository.save(orderRepository.save(orderMapper.toEntity(order)));
    }

    public void cancelOrder(Long id) {
        OrderDTO order = getOrderById(id);

        if (!Order.OrderStatus.PENDING.equals(order.getStatus())){
            throw new RuntimeException("Can only cancel orders in PENDING status");
        }

        order.setStatus(Order.OrderStatus.CANCELLED.toString());
        orderRepository.save(orderMapper.toEntity(order));
    }

    // Order Items methods
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
        return orderItemRepository.findByOrderId(orderId);
    }
    @Transactional(readOnly = true)
    public List<OrderDTO> getMyOrders() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований");
        }

        List<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(currentUserId);

        // Конвертуємо в DTO (використовуйте ваш існуючий mapper або метод)
        return orderMapper.toDTOList(orders);
    }


    /**
     * Buyer скасовує замовлення (тільки PENDING)
     */

    /**
     * Buyer підтверджує отримання (SHIPPED → DELIVERED)
     */
    @Transactional
    public OrderDTO confirmDelivery(Long orderId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Замовлення не знайдено"));

        // Перевіряємо що це замовлення поточного buyer
        if (order.getBuyer() == null || !order.getBuyer().getId().equals(currentUserId)) {
            throw new RuntimeException("Це не ваше замовлення");
        }


        order.setStatus(Order.OrderStatus.DELIVERED);
        order = orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    /**
     * Buyer скасовує замовлення (тільки PENDING)
     */
    @Transactional
    public OrderDTO cancelOrderByBuyer(Long orderId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Замовлення не знайдено"));

        // Перевіряємо що це замовлення поточного buyer
        if (order.getBuyer() == null || !order.getBuyer().getId().equals(currentUserId)) {
            throw new RuntimeException("Це не ваше замовлення");
        }

        // Скасувати можна тільки PENDING замовлення
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Скасувати можна тільки замовлення в статусі 'Очікує'");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return orderMapper.toDTO(order);
    }
    public OrderItemDTO addOrderItem(Long orderId, OrderItem orderItem) {
        OrderDTO order = getOrderById(orderId);

        if (!Order.OrderStatus.PENDING.equals(order.getStatus())) {
            throw new RuntimeException("Cannot add items to order that is not in PENDING status");
        }

        orderItem.setOrder(orderMapper.toEntity(order));

        // Calculate subtotal
        if (orderItem.getPricePerUnit() != null && orderItem.getQuantity() != null) {
            orderItem.setSubtotal(
                    orderItem.getPricePerUnit().multiply(orderItem.getQuantity())
            );
        }

        OrderItem saved = orderItemRepository.save(orderItem);

        // Recalculate order total
        recalculateOrderTotal(orderId);

        return orderItemMapper.toDTO(saved);
    }

    public OrderItemDTO updateOrderItem(Long orderId, Long itemId, OrderItem orderItem) {
        Order order = orderMapper.toEntity(getOrderById(orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot update items in order that is not in PENDING status");
        }

        OrderItem existing = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found with id: " + itemId));

        if (!existing.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("OrderItem does not belong to this order");
        }

        existing.setQuantity(orderItem.getQuantity());
        existing.setPricePerUnit(orderItem.getPricePerUnit());

        // Recalculate subtotal
        if (existing.getPricePerUnit() != null && existing.getQuantity() != null) {
            existing.setSubtotal(
                    existing.getPricePerUnit().multiply(existing.getQuantity())
            );
        }

        OrderItem saved = orderItemRepository.save(existing);

        // Recalculate order total
        recalculateOrderTotal(orderId);

        return orderItemMapper.toDTO(saved);
    }

    public void deleteOrderItem(Long orderId, Long itemId) {
        Order order = orderMapper.toEntity(getOrderById(orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot delete items from order that is not in PENDING status");
        }

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found with id: " + itemId));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new RuntimeException("OrderItem does not belong to this order");
        }

        orderItemRepository.deleteById(itemId);

        // Recalculate order total
        recalculateOrderTotal(orderId);
    }

    private void recalculateOrderTotal(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderDTO order = getOrderById(orderId);
        order.setTotalAmount(totalAmount);
        orderRepository.save(orderMapper.toEntity(order));
    }
}