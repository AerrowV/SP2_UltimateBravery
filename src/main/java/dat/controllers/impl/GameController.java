package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.controllers.IController;
import dat.dao.impl.GameDAO;
import dat.dtos.GameDTO;
import dat.dtos.StrategyDTO;
import dat.entities.Game;
import dat.entities.Strategy;
import dat.services.mappers.GameMapper;
import dat.services.mappers.StrategyMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GameController implements IController<GameDTO, Long> {

    private final GameDAO dao;
    private final EntityManagerFactory emf;

    public GameController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.dao = GameDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        Game game = dao.read(id);
        if (game != null) {
            ctx.status(200).json(GameMapper.toDTO(game));
        } else {
            ctx.status(404);
        }
    }

    @Override
    public void readAll(Context ctx) {
        List<Game> games = dao.readAll();
        List<GameDTO> gameDTOs = games.stream()
                .map(GameMapper::toDTO)
                .collect(Collectors.toList());
        ctx.status(200).json(gameDTOs);
    }

    @Override
    public void create(Context ctx) {
        GameDTO gameDTO = validateEntity(ctx);
        Game game = GameMapper.toEntity(gameDTO);
        Game createdGame = dao.create(game);
        ctx.status(201).json(GameMapper.toDTO(createdGame));
    }

    @Override
    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        GameDTO gameDTO = validateEntity(ctx);
        Game existing = dao.read(id);
        if (existing == null) {
            ctx.status(404);
            return;
        }
        existing.setName(gameDTO.getName());
        Game updatedGame = dao.update(id, existing);
        ctx.status(200).json(GameMapper.toDTO(updatedGame));
    }

    @Override
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        dao.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        return id != null && id > 0;
    }

    @Override
    public GameDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(GameDTO.class)
                .check(g -> g.getName() != null && !g.getName().isEmpty(),
                        "Game name is required")
                .get();
    }

    public void getByMap(Context ctx) {
        Long mapId = ctx.pathParamAsClass("mapId", Long.class)
                .check(id -> id != null && id > 0, "Invalid map ID")
                .get();
        try (EntityManager em = emf.createEntityManager()) {
            List<Game> games = em.createQuery(
                            "SELECT g FROM Game g JOIN g.maps m WHERE m.id = :mapId", Game.class)
                    .setParameter("mapId", mapId)
                    .getResultList();
            ctx.status(200).json(games.stream().map(GameMapper::toDTO).collect(Collectors.toList()));
        }
    }

    public void getByGun(Context ctx) {
        Long gunId = ctx.pathParamAsClass("gunId", Long.class)
                .check(id -> id != null && id > 0, "Invalid gun ID")
                .get();
        try (EntityManager em = emf.createEntityManager()) {
            List<Game> games = em.createQuery(
                            "SELECT g FROM Game g JOIN g.guns gu WHERE gu.id = :gunId", Game.class)
                    .setParameter("gunId", gunId)
                    .getResultList();
            ctx.status(200).json(games.stream().map(GameMapper::toDTO).collect(Collectors.toList()));
        }
    }

    public void getStrategiesByGameId(Context ctx) {
        Long gameId = ctx.pathParamAsClass("id", Long.class)
                .check(id -> id != null && id > 0, "Invalid game ID")
                .get();

        try (EntityManager em = emf.createEntityManager()) {
            List<Strategy> strategies = em.createQuery(
                            "SELECT s FROM Strategy s WHERE s.game.id = :gameId", Strategy.class)
                    .setParameter("gameId", gameId)
                    .getResultList();

            List<StrategyDTO> strategyDTOs = strategies.stream()
                    .map(StrategyMapper::toDTO)
                    .collect(Collectors.toList());

            ctx.status(200).json(strategyDTOs);
        }
    }


    public void populate(Context ctx) {
        Populate.populate(HibernateConfig.getEntityManagerFactory());
        ctx.result("Populated database with game, maps, guns, and strategies.");
    }
}