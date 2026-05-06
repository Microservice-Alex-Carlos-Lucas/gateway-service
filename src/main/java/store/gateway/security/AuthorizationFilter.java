package store.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Autowired
    private RouterValidator routerValidator;

    private final WebClient webClient = WebClient.builder()
        .baseUrl("http://auth:8080")
        .build();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!routerValidator.isSecured(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        HttpCookie cookie = exchange.getRequest().getCookies()
            .getFirst("__store_jwt_token");

        if (cookie == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        final String token = cookie.getValue();

        return webClient.post()
            .uri("/auth/solve")
            .header("Authorization", token)
            .retrieve()
            .bodyToMono(SolveOut.class)
            .flatMap(solve -> {
                ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.header("id-account", solve.idAccount()))
                    .build();
                return chain.filter(mutated);
            })
            .onErrorResume(ex -> {
                logger.error("Token validation failed: {}", ex.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }

}
