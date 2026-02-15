package nykon.foodmarket.repository;

import nykon.foodmarket.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByUserId(Long userId);

    Page<Supplier> findByRatingGreaterThanEqual(BigDecimal rating, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Supplier s JOIN s.offers o WHERE o.ingredient.category.id = :categoryId")
    Page<Supplier> findByOffersIngredientCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Supplier s JOIN s.offers o " +
            "WHERE s.rating >= :rating AND o.ingredient.category.id = :categoryId")
    Page<Supplier> findByRatingGreaterThanEqualAndOffersIngredientCategoryId(
            @Param("rating") BigDecimal rating,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}
