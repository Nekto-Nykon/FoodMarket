package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OfferDTO;
import nykon.foodmarket.mapper.OfferMapper;
import nykon.foodmarket.model.Offer;
import nykon.foodmarket.model.Supplier;
import nykon.foodmarket.repository.OfferRepository;
import nykon.foodmarket.repository.SupplierRepository;
import nykon.foodmarket.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;
    private final SupplierRepository supplierRepository;
    private final OfferMapper offerMapper;

    /**
     * Створити нову пропозицію
     */
    public OfferDTO createOffer(Offer offer) {
        // Автоматично встановлюємо supplier для поточного користувача
        Long currentUserId = SecurityUtils.getCurrentUserId();

        System.out.println("=== CREATE OFFER DEBUG ===");
        System.out.println("Current User ID: " + currentUserId);
        System.out.println("Current User Email: " + SecurityUtils.getCurrentUserEmail());

        if (currentUserId == null) {
            throw new RuntimeException("Користувач не авторизований. Будь ласка, увійдіть в систему.");
        }

        Supplier supplier = supplierRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Профіль постачальника не знайдено. Спочатку створіть профіль компанії."));

        System.out.println("Supplier found: " + supplier.getCompanyName());

        offer.setSupplier(supplier);

        if (offer.getIsActive() == null) {
            offer.setIsActive(true);
        }
        return offerMapper.toDTO(offerRepository.save(offer));
    }

    /**
     * Отримати offer по ID (entity)
     */
    @Transactional(readOnly = true)
    public OfferDTO getOfferById(Long id) {
        return offerMapper.toDTO(offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id)));
    }

    /**
     * Отримати offer по ID як DTO з повними даними
     */
    @Transactional(readOnly = true)
    public OfferDTO getOfferDTOById(Long id) {
        Offer offer = offerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return offerMapper.toDTO(offer);
    }

    /**
     * Оновити пропозицію
     */
    public OfferDTO updateOffer(Long id, Offer offer) {
        OfferDTO existingDTO = getOfferById(id);
        Offer existing = offerMapper.toEntity(existingDTO);
        existing.setPrice(offer.getPrice());
        existing.setAvailableQuantity(offer.getAvailableQuantity());
        existing.setMinOrderQuantity(offer.getMinOrderQuantity());
        existing.setValidFrom(offer.getValidFrom());
        existing.setValidUntil(offer.getValidUntil());

        if (offer.getIsActive() != null) {
            existing.setIsActive(offer.getIsActive());
        }
        return offerMapper.toDTO(offerRepository.save(existing));
    }

    /**
     * Видалити (деактивувати) пропозицію
     */
    public void deleteOffer(Long id) {
        OfferDTO offer = getOfferById(id);
        offer.setIsActive(false);
        offerRepository.save(offerMapper.toEntity(offer));
    }

    /**
     * Пошук пропозицій
     */
    @Transactional(readOnly = true)
    public List<OfferDTO> searchOffers(String query) {
        List<Offer> offers = offerRepository.searchByIngredientNameOrSupplierName(query);
        return offerMapper.toDTOList(offers);
    }

    /**
     * Активувати пропозицію
     */
    public Offer activateOffer(Long id) {
        OfferDTO offer = getOfferById(id);
        offer.setIsActive(true);
        return offerRepository.save(offerMapper.toEntity(offer));
    }

    /**
     * Деактивувати пропозицію
     */
    public Offer deactivateOffer(Long id) {
        OfferDTO offer = getOfferById(id);
        offer.setIsActive(false);
        return offerRepository.save(offerMapper.toEntity(offer));
    }

    /**
     * Отримати всі пропозиції як DTO (з повними даними supplier та ingredient)
     */
    @Transactional(readOnly = true)
    public List<OfferDTO> getAllOffers(Boolean isActive) {
        List<Offer> offers = offerRepository.findAllWithDetails(isActive);
        return offerMapper.toDTOList(offers);
    }

    /**
     * Отримати пропозиції поточного постачальника як DTO
     */
    @Transactional(readOnly = true)
    public List<OfferDTO> getMyOffers() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Supplier supplier = supplierRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Supplier profile not found"));

        List<Offer> offers = offerRepository.findBySupplierIdWithDetails(supplier.getId());
        return offerMapper.toDTOList(offers);
    }
    @Transactional(readOnly = true)
    public List<OfferDTO> findBySupplierId(Long supplierId) {
        return offerMapper.toDTOList(offerRepository.findBySupplierId(supplierId));
    }
}
