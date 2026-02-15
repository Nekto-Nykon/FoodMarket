package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.model.Offer;
import nykon.foodmarket.model.Supplier;
import nykon.foodmarket.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        Supplier created = supplierService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @RequestBody Supplier supplier) {
        Supplier updated = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/me")
    public ResponseEntity<Supplier> getCurrentUserSupplier() {
        Supplier supplier = supplierService.getCurrentUserSupplier();
        return ResponseEntity.ok(supplier);
    }
    @GetMapping
    public ResponseEntity<Page<Supplier>> getAllSuppliers(
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        Page<Supplier> suppliers = supplierService.getAllSuppliers(minRating, categoryId, pageable);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}/offers")
    public ResponseEntity<List<Offer>> getSupplierOffers(@PathVariable Long id) {
        List<Offer> offers = supplierService.getSupplierOffers(id);
        return ResponseEntity.ok(offers);
    }
}