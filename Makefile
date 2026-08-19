COMPOSE_LOCAL = docker compose -f infra/docker/docker-compose.local.yml --env-file infra/docker/.env

.PHONY: up down restart logs ps clean generate-client

up:
	$(COMPOSE_LOCAL) up -d

down:
	$(COMPOSE_LOCAL) down

restart: down up

logs:
	$(COMPOSE_LOCAL) logs -f

ps:
	$(COMPOSE_LOCAL) ps

clean:
	$(COMPOSE_LOCAL) down -v

generate-client:
	bash scripts/generate-api-client.sh
