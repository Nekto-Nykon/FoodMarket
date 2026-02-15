package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.UserDTO;
import nykon.foodmarket.model.User;
import nykon.foodmarket.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;

    /**
     * Entity -> DTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getIsActive(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * DTO -> Entity (без пароля!)
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setIsActive(dto.getIsActive());

        // Завантажуємо роль з БД якщо вказана
        if (dto.getRoleName() != null) {
            roleRepository.findByName(dto.getRoleName())
                    .ifPresent(user::setRole);
        }

        return user;
    }

    /**
     * Update Entity from DTO (без пароля!)
     */
    public void updateEntityFromDTO(UserDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setIsActive(dto.getIsActive());

        if (dto.getRoleName() != null) {
            roleRepository.findByName(dto.getRoleName())
                    .ifPresent(user::setRole);
        }
    }

    /**
     * List Entity -> List DTO
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
