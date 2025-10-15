# 🔍 Crypto Forensic Analysis System

Sistema avanzado de análisis forense para blockchain utilizando Neo4j Graph Database y Spring Boot. Permite detectar patrones sospechosos como **peel chains**, analizar redes de transacciones y rastrear flujos de criptomonedas.

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Neo4j](https://img.shields.io/badge/Neo4j-5.13-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🚀 Características

### Análisis Forense
- ✅ **Detección de Peel Chains** - Identifica patrones de lavado de dinero (>95% spending)
- ✅ **Análisis de Flujos** - Rastrea el movimiento de fondos entre wallets
- ✅ **Network Analysis** - Visualiza redes de transacciones complejas
- ✅ **Path Finding** - Encuentra caminos entre wallets sospechosas

### Integración Blockchain Real
- 🔗 **BlockCypher API** - Datos reales de Bitcoin y Ethereum
- 📊 **Graph Database** - Almacenamiento eficiente en Neo4j
- ⚡ **Queries Optimizadas** - 19 Cypher queries avanzadas
- 🔄 **Análisis en Tiempo Real** - Procesamiento de transacciones en vivo

### Arquitectura
- 🏗️ **Spring Boot 3.2** - Framework moderno y robusto
- 🗄️ **Neo4j 5.13** - Base de datos de grafos nativa
- 🐳 **Docker Compose** - Despliegue fácil y reproducible
- 📡 **REST API** - 16 endpoints documentados

## 📋 Requisitos

- **Java 17** o superior
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Git**

## 🔧 Instalación Rápida

### 1. Clonar el Repositorio
```bash
git clone https://github.com/TU_USUARIO/CryptoProject.git
cd CryptoProject
```

### 2. Iniciar Neo4j
```bash
cd demo
docker-compose up -d
```

### 3. Compilar y Ejecutar
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Verificar Instalación
```bash
# API REST
curl http://localhost:8080/api/blockcypher/health

# Neo4j Browser
open http://localhost:7474
# Usuario: neo4j / Contraseña: password
```

## 🎯 Uso Rápido

### Cargar Wallet de Bitcoin
```bash
# Wallet de Satoshi Nakamoto
curl -X POST "http://localhost:8080/api/blockcypher/wallet/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa?chain=BTC"
```

### Analizar Red de Transacciones
```bash
curl "http://localhost:8080/api/network/analyze/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa" | jq
```

### Detectar Peel Chains
```bash
curl "http://localhost:8080/api/forensic/peel-chains" | jq
```

### Encontrar Caminos
```bash
curl "http://localhost:8080/api/path/shortest/{wallet1}/{wallet2}" | jq
```

## 📚 Documentación

La documentación completa está en la carpeta `demo/docs/`:

- **[QUICK_START.md](demo/docs/QUICK_START.md)** - Guía de inicio rápido
- **[TUTORIAL_COMPLETO.md](demo/docs/TUTORIAL_COMPLETO.md)** - Tutorial detallado paso a paso
- **[NEO4J_QUERIES.md](demo/docs/NEO4J_QUERIES.md)** - 19 queries Cypher avanzadas
- **[API_ENDPOINTS.md](demo/docs/API_ENDPOINTS.md)** - Documentación completa de la API
- **[COMANDOS_UTILES.md](demo/docs/COMANDOS_UTILES.md)** - Comandos útiles

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐      REST API       ┌──────────────────┐
│   Frontend/CLI  │ ◄────────────────► │  Spring Boot App  │
└─────────────────┘                     └──────────────────┘
                                               │
                                               │ Neo4j Driver
                                               ▼
                                        ┌──────────────────┐
                                        │   Neo4j Graph    │
                                        │     Database     │
                                        └──────────────────┘
                                               ▲
                                               │
                                        ┌──────────────────┐
                                        │  BlockCypher API │
                                        │  (Bitcoin/ETH)   │
                                        └──────────────────┘
```

## 📊 API Endpoints

### BlockCypher (Carga de Datos)
- `GET /api/blockcypher/health` - Verificar estado del servicio
- `POST /api/blockcypher/wallet/{address}` - Cargar wallet desde blockchain
- `POST /api/blockcypher/transaction/{hash}` - Cargar transacción

### Network Analysis
- `GET /api/network/analyze/{address}` - Analizar red de una wallet
- `GET /api/network/graph/{address}` - Obtener grafo de conexiones

### Forensic Analysis
- `GET /api/forensic/peel-chains` - Detectar todas las peel chains
- `GET /api/forensic/peel-chains/{address}` - Peel chains de una wallet

### Path Analysis
- `GET /api/path/shortest/{addr1}/{addr2}` - Camino más corto
- `GET /api/path/hops/{address}/{hops}` - Wallets a N saltos

## 🔍 Queries Cypher Incluidas

El sistema incluye 19 queries avanzadas para análisis forense:

1. **Network Analysis** - Análisis de redes de transacciones
2. **Peel Chain Detection** - Detección de cadenas peel (lavado)
3. **Shortest Path** - Caminos más cortos entre wallets
4. **Community Detection** - Identificación de comunidades
5. **PageRank** - Ranking de wallets importantes
6. **Transaction Flow** - Rastreo de flujos de fondos
7. Y 13 más...

Ver [NEO4J_QUERIES.md](demo/docs/NEO4J_QUERIES.md) para detalles completos.

## 🧪 Testing

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar tests de integración
mvn verify

# Ver cobertura
mvn jacoco:report
```

## 🐳 Docker

```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Limpiar todo (incluyendo volúmenes)
docker-compose down -v
```

## 📁 Estructura del Proyecto

```
CryptoProject/
├── demo/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── controller/      # 4 REST Controllers
│   │   │   │   ├── service/         # 4 Services
│   │   │   │   ├── repository/      # 3 Neo4j Repositories
│   │   │   │   ├── model/           # 4 Domain Models
│   │   │   │   ├── dto/             # 3 DTOs
│   │   │   │   └── config/          # Configuration
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── docs/                        # 📚 Documentación completa
│   ├── docker-compose.yml
│   └── pom.xml
├── .gitignore
└── README.md
```

## 🔐 Configuración

El archivo `application.properties` contiene la configuración principal:

```properties
# Neo4j Configuration
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password

# BlockCypher API
blockcypher.api.token=YOUR_API_TOKEN_HERE
blockcypher.api.base-url=https://api.blockcypher.com/v1

# Server Configuration
server.port=8080
```

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

**CauchoTheGaucho**

- GitHub: [@cauchothegaucho](https://github.com/cauchothegaucho)

## 🙏 Agradecimientos

- [Neo4j](https://neo4j.com/) - Graph Database
- [Spring Boot](https://spring.io/projects/spring-boot) - Framework
- [BlockCypher](https://www.blockcypher.com/) - Blockchain API
- [Maven](https://maven.apache.org/) - Build Tool

## 📧 Contacto

¿Preguntas o sugerencias? Abre un [Issue](https://github.com/TU_USUARIO/CryptoProject/issues)

---

⭐ **Si este proyecto te resulta útil, dale una estrella en GitHub!** ⭐
