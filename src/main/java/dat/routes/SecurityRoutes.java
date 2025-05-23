package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.controllers.security.SecurityController;
import dat.entities.enums.Role;
import dat.utils.Utils;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();

    public static EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                get("/healthcheck", securityController::healthCheck, Role.ANYONE);
                get("/test", ctx -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from Open Deployment")), Role.ADMIN);
                post("/login", securityController.login(), Role.ANYONE);
                post("/register", securityController.register(), Role.ANYONE);
                post("/user/addrole", securityController.addRole(), Role.ADMIN);
            });
        };
    }
}
