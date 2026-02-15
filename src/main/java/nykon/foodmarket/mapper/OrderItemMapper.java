package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OrderItemDTO;
import nykon.foodmarket.model.OrderItem;
import nykon.foodmarket.repository.OfferRepository;
import nykon.foodmarket.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final OfferMapper offerMapper;
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;

    /**
     * Entity -> DTO
     */
    public OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getOrder() != null ? orderItem.getOrder().getId() : null,
                offerMapper.toDTO(orderItem.getOffer()),
                orderItem.getQuantity(),
                orderItem.getPricePerUnit(),
                orderItem.getSubtotal()
        );
    }

    /**
     * DTO -> Entity
     */
    public OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setId(dto.getId());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPricePerUnit(dto.getPricePerUnit());
        orderItem.setSubtotal(dto.getSubtotal());

        // Завантажуємо order з БД
        if (dto.getOrderId() != null) {
            orderRepository.findById(dto.getOrderId())
                    .ifPresent(orderItem::setOrder);
        }

        // Завантажуємо offer з БД
        if (dto.getOffer() != null && dto.getOffer().getId() != null) {
            offerRepository.findById(dto.getOffer().getId())
                    .ifPresent(orderItem::setOffer);
        }

        return orderItem;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(OrderItemDTO dto, OrderItem orderItem) {
        if (dto == null || orderItem == null) {
            return;
        }

        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPricePerUnit(dto.getPricePerUnit());
        orderItem.setSubtotal(dto.getSubtotal());

        if (dto.getOffer() != null && dto.getOffer().getId() != null) {
            offerRepository.findById(dto.getOffer().getId())
                    .ifPresent(orderItem::setOffer);
        }
    }

    /**
     * List Entity -> List DTO
     */
    public List<OrderItemDTO> toDTOList(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        return orderItems.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<OrderItem> toEntityList(List<OrderItemDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

