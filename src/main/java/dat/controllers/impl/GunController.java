package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.dao.impl.GameDAO;
import dat.dao.impl.GunDAO;
import dat.dtos.GunDTO;
import dat.entities.Game;
import dat.entities.Gun;
import dat.services.mappers.GunMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class GunController implements IController<GunDTO, Long> {

    private final GunDAO gunDao;
    private final GameDAO gameDao;
    private final EntityManagerFactory emf;

    public GunController() {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.gunDao = GunDAO.getInstance(emf);
        this.gameDao = GameDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        Gun gun = gunDao.read(id);
        if (gun != null) {
            ctx.status(200).json(GunMapper.toDTO(gun));
        } else {
            ctx.status(404);
        }
    }

    @Override
    public void readAll(Context ctx) {
        ctx.status(200).json(gunDao.readAll().stream()
                .map(GunMapper::toDTO)
                .toList());
    }

    @Override
    public void create(Context ctx) {
        GunDTO gunDTO = validateEntity(ctx);
        Gun gun = GunMapper.toEntity(gunDTO);
        Gun createdGun = gunDao.create(gun);
        ctx.status(201).json(GunMapper.toDTO(createdGun));
    }

    @Override
    public void update(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        GunDTO gunDTO = validateEntity(ctx);
        Gun existing = gunDao.read(id);

        if (existing == null) {
            ctx.status(404);
            return;
        }

        existing.setName(gunDTO.getName());
        existing.setTeamId(gunDTO.isTeamId());

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Game game = em.find(Game.class, gunDTO.getGameId());
            existing.setGame(game);
            em.getTransaction().commit();
        }

        Gun updatedGun = gunDao.update(id, existing);
        ctx.status(200).json(GunMapper.toDTO(updatedGun));
    }

    @Override
    public void delete(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        gunDao.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Long id) {
        return id != null && id > 0;
    }

    @Override
    public GunDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(GunDTO.class)
                .check(g -> g.getName() != null && !g.getName().isEmpty(), "Gun name is required")
                .check(g -> g.getGameId() != null, "Game ID must be provided")
                .get();
    }

    public void getRandomByGame(Context ctx) {
        Long gameId = ctx.pathParamAsClass("gameId", Long.class).get();
        Gun randomGun = gunDao.getRandomByGameId(gameId);
        ctx.json(randomGun != null ? GunMapper.toDTO(randomGun) : null);
    }
}