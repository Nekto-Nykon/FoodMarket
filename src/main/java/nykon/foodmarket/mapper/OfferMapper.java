package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OfferDTO;
import nykon.foodmarket.model.Offer;
import nykon.foodmarket.repository.IngredientRepository;
import nykon.foodmarket.repository.SupplierRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OfferMapper {

    private final SupplierMapper supplierMapper;
    private final IngredientMapper ingredientMapper;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Entity -> DTO
     */
    public OfferDTO toDTO(Offer offer) {
        if (offer == null) {
            return null;
        }

        return new OfferDTO(
                offer.getId(),
                supplierMapper.toDTO(offer.getSupplier()),
                ingredientMapper.toDTO(offer.getIngredient()),
                offer.getPrice(),
                offer.getAvailableQuantity(),
                offer.getMinOrderQuantity(),
                offer.getIsActive(),
                offer.getValidFrom(),
                offer.getValidUntil(),
                offer.getCreatedAt(),
                offer.getUpdatedAt()
        );
    }

    /**
     * DTO -> Entity
     */
    public Offer toEntity(OfferDTO dto) {
        if (dto == null) {
            return null;
        }

        Offer offer = new Offer();
        offer.setId(dto.getId());
        offer.setPrice(dto.getPrice());
        offer.setAvailableQuantity(dto.getAvailableQuantity());
        offer.setMinOrderQuantity(dto.getMinOrderQuantity());
        offer.setIsActive(dto.getIsActive());
        offer.setValidFrom(dto.getValidFrom());
        offer.setValidUntil(dto.getValidUntil());

        // Завантажуємо supplier з БД
        if (dto.getSupplier() != null && dto.getSupplier().getId() != null) {
            supplierRepository.findById(dto.getSupplier().getId())
                    .ifPresent(offer::setSupplier);
        }

        // Завантажуємо ingredient з БД
        if (dto.getIngredient() != null && dto.getIngredient().getId() != null) {
            ingredientRepository.findById(dto.getIngredient().getId())
                    .ifPresent(offer::setIngredient);
        }

        return offer;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(OfferDTO dto, Offer offer) {
        if (dto == null || offer == null) {
            return;
        }

        offer.setPrice(dto.getPrice());
        offer.setAvailableQuantity(dto.getAvailableQuantity());
        offer.setMinOrderQuantity(dto.getMinOrderQuantity());
        offer.setIsActive(dto.getIsActive());
        offer.setValidFrom(dto.getValidFrom());
        offer.setValidUntil(dto.getValidUntil());

        if (dto.getSupplier() != null && dto.getSupplier().getId() != null) {
            supplierRepository.findById(dto.getSupplier().getId())
                    .ifPresent(offer::setSupplier);
        }

        if (dto.getIngredient() != null && dto.getIngredient().getId() != null) {
            ingredientRepository.findById(dto.getIngredient().getId())
                    .ifPresent(offer::setIngredient);
        }
    }

    /**
     * List Entity -> List DTO
     */
    public List<OfferDTO> toDTOList(List<Offer> offers) {
        if (offers == null) {
            return null;
        }
        return offers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Offer> toEntityList(List<OfferDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}