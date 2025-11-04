# 🐳 Docker Setup - Honeycomb Crypto Forensic

## Requisitos Previos

- Docker 20.10+
- Docker Compose 2.0+
- 4GB RAM mínimo
- 10GB de espacio en disco

## 🚀 Inicio Rápido

### Opción 1: Usando el script de gestión (Recomendado)

```bash
# Construir imágenes
./docker-manager.sh build

# Iniciar servicios
./docker-manager.sh start

# Ver logs
./docker-manager.sh logs

# Ver estado
./docker-manager.sh status

# Detener servicios
./docker-manager.sh stop
```

### Opción 2: Usando Docker Compose directamente

```bash
# Construir e iniciar
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

## 📋 Servicios Disponibles

### API REST (Puerto 8080)
- URL: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui.html

### Neo4j Database (Puertos 7474, 7687)
- Browser: http://localhost:7474
- Usuario: `neo4j`
- Contraseña: `password`
- Bolt: `bolt://localhost:7687`

## 🔧 Comandos Útiles

### Ver logs en tiempo real
```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app

# Solo Neo4j
docker-compose logs -f neo4j
```

### Reiniciar servicios
```bash
docker-compose restart
```

### Reconstruir solo la aplicación
```bash
docker-compose up -d --build app
```

### Acceder a un contenedor
```bash
# Aplicación
docker exec -it honeycomb-forensic-app sh

# Neo4j
docker exec -it honeycomb-neo4j bash
```

### Verificar salud de los servicios
```bash
# Estado general
docker-compose ps

# Salud de Neo4j
docker exec honeycomb-neo4j cypher-shell -u neo4j -p password "RETURN 1"

# Salud de la API
curl http://localhost:8080/actuator/health
```

## 🗂️ Volúmenes

Los datos persisten en volúmenes Docker:

- `neo4j-data`: Base de datos Neo4j
- `neo4j-logs`: Logs de Neo4j
- `neo4j-import`: Directorio de importación
- `neo4j-plugins`: Plugins de Neo4j (APOC, GDS)

### Limpiar volúmenes (¡CUIDADO! Elimina todos los datos)
```bash
docker-compose down -v
```

## 🐛 Solución de Problemas

### La aplicación no inicia
1. Verificar que Neo4j esté saludable:
   ```bash
   docker-compose logs neo4j
   ```
2. Verificar logs de la aplicación:
   ```bash
   docker-compose logs app
   ```

### Neo4j no se conecta
1. Verificar que el puerto 7687 esté libre:
   ```bash
   sudo netstat -tlnp | grep 7687
   ```
2. Reiniciar el contenedor:
   ```bash
   docker-compose restart neo4j
   ```

### Errores de memoria
Aumentar memoria disponible en `docker-compose.yml`:
```yaml
environment:
  - NEO4J_dbms_memory_heap_max__size=4G
```

### Puerto 8080 ya en uso
Cambiar el puerto en `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Usar 8081 en lugar de 8080
```

## 🔄 Actualizar la Aplicación

1. Hacer cambios en el código
2. Reconstruir la imagen:
   ```bash
   docker-compose build app
   ```
3. Reiniciar el servicio:
   ```bash
   docker-compose up -d app
   ```

## 📊 Monitoreo

### Ver uso de recursos
```bash
docker stats
```

### Ver tamaño de imágenes
```bash
docker images | grep honeycomb
```

### Ver volúmenes
```bash
docker volume ls | grep honeycomb
```

## 🧹 Limpieza

### Limpiar contenedores detenidos
```bash
docker container prune
```

### Limpiar imágenes no utilizadas
```bash
docker image prune -a
```

### Limpiar todo el sistema
```bash
docker system prune -a --volumes
```

## 🔐 Seguridad

**IMPORTANTE**: Para producción, cambiar las credenciales por defecto:

1. En `docker-compose.yml`:
   ```yaml
   environment:
     - NEO4J_AUTH=usuario_seguro/contraseña_segura
   ```

2. En las variables de entorno de la app:
   ```yaml
   environment:
     - SPRING_NEO4J_AUTHENTICATION_PASSWORD=contraseña_segura
   ```

## 📝 Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_NEO4J_URI` | URI de Neo4j | `bolt://neo4j:7687` |
| `SPRING_NEO4J_AUTHENTICATION_USERNAME` | Usuario Neo4j | `neo4j` |
| `SPRING_NEO4J_AUTHENTICATION_PASSWORD` | Contraseña Neo4j | `password` |
| `LOGGING_LEVEL_COM_EXAMPLE` | Nivel de logging | `INFO` |
| `BLOCKCYPHER_API_BASE_URL` | URL API BlockCypher | `https://api.blockcypher.com/v1` |

## 🎯 Arquitectura

```
┌─────────────────┐
│   Browser       │
│  (localhost)    │
└────────┬────────┘
         │
         │ :8080 (REST API)
         │
┌────────▼────────────┐
│  Spring Boot App    │
│  (honeycomb-app)    │
└────────┬────────────┘
         │
         │ :7687 (Bolt)
         │
┌────────▼────────────┐
│   Neo4j Database    │
│  (honeycomb-neo4j)  │
└─────────────────────┘
```

## 📚 Más Información

- [Documentación de Docker](https://docs.docker.com/)
- [Neo4j Docker](https://neo4j.com/developer/docker/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)

