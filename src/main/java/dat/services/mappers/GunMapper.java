package dat.services.mappers;

import dat.dtos.GunDTO;
import dat.entities.Gun;

public class GunMapper {

    public static GunDTO toDTO(Gun gun) {
        return new GunDTO(
                gun.getId(),
                gun.getName(),
                gun.getGame() != null ? gun.getGame().getId() : null,
                gun.isTeamId()
        );
    }

    public static Gun toEntity(GunDTO gunDTO) {
        Gun gun = new Gun();
        gun.setId(gunDTO.getId());
        gun.setName(gunDTO.getName());
        gun.setTeamId(gunDTO.isTeamId());
        return gun;
    }
}