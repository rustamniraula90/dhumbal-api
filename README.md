# Dhumbal API

## Description
A backend service for a Nepali card game.

## Tech Stack
- Java (Spring Boot)
- MySQL: persistent data
- Redis: game state cache
- React: ![frontend](https://github.com/rustamniraula90/dhumbal-ui)

## Setup
1. Clone the repo
2. Run `docker-compose up` from the docker folder
3. Check and update the env from `src/main/resources/application.yaml`
4. Then run `./mvnw spring-boot:run`
5. Access the API on `localhost:8080`

## Features
- Online turn-based multiplayer game
- Real-time gameplay using WebSocket
- Game state saved in cache for faster performance
- AI Agent with multiple difficulty levels
- Global leaderboard with real-time ranking

## Architecture
### DFD
![dfd](docs/DFD.png)
### Schema Diagram
![Schema Diagram](docs/schema_diagram.png)
### Overall Flowchart
![Overall Flow Chart](docs/project_flow_chart.png)
### Overall Flowchart
![Game Flow Chart](docs/game_flow_chart.png)
### Pseudocode of Agent Algorithm
![ISMCTS](docs/ISMCTS_algorithm.png)

## Author
![Rustam Niraula](https://github.com/rustamniraula90)
![Sunil Subedi](https://github.com/sunil-jr)
![Bipin Regmi](https://github.com/regmi-bpn)
