package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OrderDTO;
import nykon.foodmarket.dto.OrderItemDTO;
import nykon.foodmarket.dto.SupplierDTO;
import nykon.foodmarket.dto.request.order.CreateOrderRequest;
import nykon.foodmarket.model.Order;
import nykon.foodmarket.model.OrderItem;
import nykon.foodmarket.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(request.getDeliveryAddress(), request.getItems());
        return ResponseEntity.ok(order);
    }
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    /**
     * Buyer підтверджує отримання замовлення (SHIPPED → DELIVERED)
     */
    @PatchMapping("/{orderId}/confirm-delivery")
    public ResponseEntity<OrderDTO> confirmDelivery(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.confirmDelivery(orderId));
    }

    /**
     * Buyer скасовує замовлення (тільки PENDING)
     */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrderByBuyer(orderId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/suppliers")
    public ResponseEntity<List<SupplierDTO>> getOrderSuppliers(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderSuppliers(orderId));
    }
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            Pageable pageable) {
        Page<Order> orders = orderService.getAllOrders(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody Order order) {
        OrderDTO updated = orderService.updateOrder(id, order);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }


    // Order Items endpoints
    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        List<OrderItem> items = orderService.getOrderItems(id);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemDTO> addOrderItem(
            @PathVariable Long orderId,
            @RequestBody OrderItem orderItem) {
        OrderItemDTO created = orderService.addOrderItem(orderId, orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody OrderItem orderItem) {
        OrderItemDTO updated = orderService.updateOrderItem(orderId, itemId, orderItem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        orderService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
