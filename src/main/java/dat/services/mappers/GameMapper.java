package dat.services.mappers;

import dat.dtos.GameDTO;
import dat.entities.Game;

import java.util.stream.Collectors;

public class GameMapper {

    public static GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getName()
        );
    }

    public static Game toEntity(GameDTO gameDTO) {
        Game game = new Game();
        game.setId(gameDTO.getId());
        game.setName(gameDTO.getName());
        // Maps and Guns should be set later by the service layer, as only IDs are available here.
        return game;
    }
}