# Gateway Service

**Grupo:** Alex Chequer · Carlos · Lucas
**Disciplina:** Plataformas, Microserviços, DevOps e APIs — Insper 2026.1

---

## Sobre o serviço

Single point of entry da plataforma. Faz roteamento por path para os
serviços internos, valida JWT (via `auth-service /auth/solve`) e injeta
o header `id-account` para downstream.

Em produção (EKS), recebe tráfego do **nginx-ingress** (via NLB AWS).
Em dev local, expõe `:8080` direto via docker compose.

## Stack

| Item | Detalhe |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Cloud Gateway (WebFlux) |
| Filtro de auth | `AuthorizationFilter` (`GlobalFilter` reativo) |
| Cliente HTTP interno | `WebClient` para `/auth/solve` |

## Configuração

| Env var | Origem | Descrição |
|---|---|---|
| `CORS_ALLOWED_ORIGINS` | ConfigMap | lista de origens permitidas |
| `CORS_ALLOWED_CREDENTIALS` | ConfigMap | `true` para enviar cookies |

## Rotas abertas (sem JWT)

- `/auth/login`
- `/auth/register`
- `/auth/health-check`
- `/accounts/health-check`
- `/health-check` (próprio gateway)

Todo o resto exige cookie `__store_jwt_token`.

## Status de entrega

| Atividade | Status |
|---|---|
| Roteamento para 5 services + Insper | ✅ |
| `AuthorizationFilter` + `RouterValidator` | ✅ |
| CORS configurado via env | ✅ |
| k8s manifests (deployment, service, configmap, ingress, hpa) | ✅ |
| HPA target 50% CPU, 1–5 réplicas | ✅ |
| Jenkinsfile + Deploy to EKS | ✅ |

## Docker Hub

`cheqr/gateway:latest`
