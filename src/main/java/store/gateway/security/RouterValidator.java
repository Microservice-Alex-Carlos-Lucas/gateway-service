package store.gateway.security;

import java.util.List;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    private static final List<String> OPEN_ROUTES = List.of(
        "/auth/login",
        "/auth/register",
        "/auth/health-check",
        "/accounts/health-check"
    );

    public boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return OPEN_ROUTES.stream()
            .noneMatch(route -> path.startsWith(route));
    }

}
