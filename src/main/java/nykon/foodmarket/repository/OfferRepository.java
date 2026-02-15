package nykon.foodmarket.repository;

import nykon.foodmarket.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {


    @Query("SELECT o FROM Offer o " +
            "LEFT JOIN FETCH o.supplier " +
            "LEFT JOIN FETCH o.ingredient i " +
            "LEFT JOIN FETCH i.category " +
            "WHERE (:isActive IS NULL OR o.isActive = :isActive)")
    List<Offer> findAllWithDetails(@Param("isActive") Boolean isActive);


    @Query("SELECT o FROM Offer o " +
            "LEFT JOIN FETCH o.supplier " +
            "LEFT JOIN FETCH o.ingredient i " +
            "LEFT JOIN FETCH i.category " +
            "WHERE o.id = :id")
    Optional<Offer> findByIdWithDetails(@Param("id") Long id);


    @Query("SELECT o FROM Offer o " +
            "LEFT JOIN FETCH o.supplier " +
            "LEFT JOIN FETCH o.ingredient i " +
            "LEFT JOIN FETCH i.category " +
            "WHERE o.supplier.id = :supplierId")
    List<Offer> findBySupplierIdWithDetails(@Param("supplierId") Long supplierId);

    List<Offer> findBySupplierId(Long supplierId);

    Page<Offer> findByIsActive(Boolean isActive, Pageable pageable);

    Long countByIsActive(Boolean isActive);

    @Query("SELECT o FROM Offer o " +
            "LEFT JOIN FETCH o.supplier " +
            "LEFT JOIN FETCH o.ingredient i " +
            "LEFT JOIN FETCH i.category " +
            "WHERE o.isActive = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(o.supplier.companyName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Offer> searchByIngredientNameOrSupplierName(@Param("query") String query);

    List<Offer> getAllByIsActive(Boolean isActive);
}

