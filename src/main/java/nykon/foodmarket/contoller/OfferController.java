package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.OfferDTO;
import nykon.foodmarket.model.Offer;
import nykon.foodmarket.service.OfferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public ResponseEntity<List<OfferDTO>> getAllOffers(
            @RequestParam(required = false) Boolean isActive) {
        List<OfferDTO> offers = offerService.getAllOffers(isActive);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/my-offers")
    public ResponseEntity<List<OfferDTO>> getMyOffers() {
        List<OfferDTO> offers = offerService.getMyOffers();
        return ResponseEntity.ok(offers);
    }
    @PostMapping
    public ResponseEntity<OfferDTO> createOffer(@RequestBody Offer offer) {
        OfferDTO created = offerService.createOffer(offer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {
        OfferDTO offer = offerService.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDTO> updateOffer(
            @PathVariable Long id,
            @RequestBody Offer offer) {
        OfferDTO updated = offerService.updateOffer(id, offer);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }


//    @GetMapping("/search")
//    public ResponseEntity<Page<Offer>> searchOffers(
//            @RequestParam String query,
//            Pageable pageable) {
//        Page<Offer> offers = offerService.searchOffers(query, pageable);
//        return ResponseEntity.ok(offers);
//    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Offer> activateOffer(@PathVariable Long id) {
        Offer activated = offerService.activateOffer(id);
        return ResponseEntity.ok(activated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Offer> deactivateOffer(@PathVariable Long id) {
        Offer deactivated = offerService.deactivateOffer(id);
        return ResponseEntity.ok(deactivated);
    }
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<OfferDTO>> getOffersBySupplierId(@PathVariable Long supplierId) {
        List<OfferDTO> offers = offerService.findBySupplierId(supplierId);
        return ResponseEntity.ok(offers);
    }
}