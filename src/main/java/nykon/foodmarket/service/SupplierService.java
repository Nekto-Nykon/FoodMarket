package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.model.Offer;
import nykon.foodmarket.model.Supplier;
import nykon.foodmarket.repository.OfferRepository;
import nykon.foodmarket.repository.SupplierRepository;
import nykon.foodmarket.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final OfferRepository offerRepository;

    public Supplier createSupplier(Supplier supplier) {
        // Validate that user doesn't already have a supplier profile
        if (supplier.getUser() != null &&
                supplierRepository.findByUserId(supplier.getUser().getId()).isPresent()) {
            throw new RuntimeException("User already has a supplier profile");
        }
        return supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existing = getSupplierById(id);
        existing.setCompanyName(supplier.getCompanyName());
        existing.setAddress(supplier.getAddress());
        existing.setTaxId(supplier.getTaxId());
        existing.setDescription(supplier.getDescription());
        return supplierRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Page<Supplier> getAllSuppliers(BigDecimal minRating, Long categoryId, Pageable pageable) {
        if (minRating != null && categoryId != null) {
            return supplierRepository.findByRatingGreaterThanEqualAndOffersIngredientCategoryId(
                    minRating, categoryId, pageable);
        } else if (minRating != null) {
            return supplierRepository.findByRatingGreaterThanEqual(minRating, pageable);
        } else if (categoryId != null) {
            return supplierRepository.findByOffersIngredientCategoryId(categoryId, pageable);
        }
        return supplierRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Offer> getSupplierOffers(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        return offerRepository.findBySupplierId(id);
    }

    public Supplier getCurrentUserSupplier() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Найти поставщика по user_id
        return supplierRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Supplier profile not found. Please create one first."));
    }
}
