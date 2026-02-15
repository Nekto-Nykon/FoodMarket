package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OrderDTO;
import nykon.foodmarket.dto.OrderItemDTO;
import nykon.foodmarket.model.Order;
import nykon.foodmarket.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;

    /**
     * Entity -> DTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> items = null;
        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream()
                    .map(orderItemMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return new OrderDTO(
                order.getId(),
                order.getBuyer() != null ? order.getBuyer().getId() : null,
                order.getBuyer() != null ? order.getBuyer().getEmail() : null,
                order.getTotalAmount(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getDeliveryAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }

    /**
     * DTO -> Entity
     */
    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setTotalAmount(dto.getTotalAmount());
        order.setDeliveryAddress(dto.getDeliveryAddress());

        // Встановлюємо статус
        if (dto.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(dto.getStatus()));
        }

        // Завантажуємо buyer з БД
        if (dto.getBuyerId() != null) {
            userRepository.findById(dto.getBuyerId())
                    .ifPresent(order::setBuyer);
        }

        return order;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(OrderDTO dto, Order order) {
        if (dto == null || order == null) {
            return;
        }

        order.setTotalAmount(dto.getTotalAmount());
        order.setDeliveryAddress(dto.getDeliveryAddress());

        if (dto.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(dto.getStatus()));
        }
    }

    /**
     * List Entity -> List DTO
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Order> toEntityList(List<OrderDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}