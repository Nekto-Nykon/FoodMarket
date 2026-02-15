package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.SupplierDTO;
import nykon.foodmarket.model.Supplier;
import nykon.foodmarket.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplierMapper {

    private final UserRepository userRepository;

    /**
     * Entity -> DTO
     */
    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return new SupplierDTO(
                supplier.getId(),
                supplier.getCompanyName(),
                supplier.getAddress(),
                supplier.getTaxId(),
                supplier.getRating(),
                supplier.getDescription()

        );
    }

    /**
     * DTO -> Entity
     */
    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setCompanyName(dto.getCompanyName());
        supplier.setAddress(dto.getAddress());
        supplier.setAddress(dto.getAddress());
        supplier.setRating(dto.getRating());
        supplier.setDescription(dto.getDescription());

        return supplier;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(SupplierDTO dto, Supplier supplier) {
        if (dto == null || supplier == null) {
            return;
        }

        supplier.setCompanyName(dto.getCompanyName());
        supplier.setAddress(dto.getAddress());
        supplier.setAddress(dto.getAddress());
        supplier.setRating(dto.getRating());
        supplier.setDescription(dto.getDescription());
    }

    /**
     * List Entity -> List DTO
     */
    public List<SupplierDTO> toDTOList(List<Supplier> suppliers) {
        if (suppliers == null) {
            return null;
        }
        return suppliers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Supplier> toEntityList(List<SupplierDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
