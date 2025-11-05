# 🚀 Guía de Inicio Rápido - Honeycomb

## ⚠️ Si encuentras el error "JSON.parse: unexpected end of data"

Este error significa que el **backend no está respondiendo** o **devuelve respuestas vacías**. Sigue estos pasos:

---

## 📋 Pasos para Iniciar Correctamente

### 1️⃣ Abrir 3 Terminales

Necesitarás 3 terminales separadas para ejecutar cada servicio:

#### **Terminal 1 - Neo4j**
```bash
# Verificar si Neo4j está corriendo
docker ps | grep neo4j

# Si no está corriendo, iniciarlo:
docker start honeycomb-neo4j

# O si es la primera vez:
docker run -d --name honeycomb-neo4j \
  -p 7474:7474 -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password123 \
  neo4j:latest

# Esperar 10-15 segundos para que inicie
# Verificar que responda:
curl http://localhost:7474
```

#### **Terminal 2 - Backend (Spring Boot)**
```bash
cd /home/cauchothegaucho/Repositorios/Honeycomb/demo

# Iniciar el backend
mvn spring-boot:run

# Deberías ver logs iniciando...
# Espera a ver el mensaje: "Started CryptoForensicApplication"
# Esto puede tardar 30-60 segundos

# En otra terminal, verifica que responda:
curl http://localhost:8080/api/health
```

#### **Terminal 3 - Frontend (Vite)**
```bash
cd /home/cauchothegaucho/Repositorios/Honeycomb/frontend

# Iniciar el frontend en puerto 3000
npm run dev

# Deberías ver:
# VITE v5.x.x ready in xxx ms
# ➜ Local: http://localhost:3000/

# En otra terminal, verifica:
curl http://localhost:3000
```

---

## 🔍 Verificación

Una vez que los 3 servicios estén corriendo, verifica:

```bash
# Neo4j
curl http://localhost:7474
# Debería devolver HTML

# Backend
curl http://localhost:8080/api/health
# Debería devolver JSON (puede ser vacío o con status)

# Frontend (PUERTO 3000)
curl http://localhost:3000
# Debería devolver HTML

# Probar un endpoint real
curl http://localhost:8080/api/greedy/peel-chains?threshold=0.7&minChainLength=2&limit=10
# Debería devolver JSON con datos
```

---

## 🎯 Acceder a la Aplicación

Una vez que todo esté corriendo:

1. **Abre tu navegador** en: `http://localhost:3000`
2. **Navega a cualquier sección** (ej: "Greedy - Peel Chains")
3. **Ejecuta un análisis** con los parámetros por defecto

**NOTA:** El frontend está en puerto 3000 y tiene proxy configurado para `/api` que apunta al backend en puerto 8080. Esto evita problemas de CORS.

---

## 🐛 Solución de Problemas

### Problema: Backend no inicia

```bash
# Ver los errores:
cd /home/cauchothegaucho/Repositorios/Honeycomb/demo
mvn spring-boot:run

# Errores comunes:
# - "Port 8080 already in use": Matar el proceso anterior
pkill -9 -f "spring-boot:run"

# - "Cannot connect to Neo4j": Verificar que Neo4j esté corriendo
docker ps | grep neo4j
```

### Problema: Frontend muestra "JSON.parse error"

Esto significa que el backend no está respondiendo correctamente.

```bash
# 1. Verificar que el backend esté corriendo:
curl http://localhost:8080/api/health

# 2. Si no responde, revisar los logs en la Terminal 2

# 3. Probar un endpoint específico:
curl http://localhost:8080/api/greedy/peel-chains?threshold=0.7&minChainLength=2&limit=10

# 4. Si devuelve JSON vacío {}, significa que no hay datos en Neo4j
```

### Problema: Puerto 3000 en uso

```bash
# Si el puerto 3000 está ocupado:
fuser -k 3000/tcp

# O cambiar el puerto en vite.config.js
```

### Problema: No hay datos

```bash
# Cargar datos de prueba en Neo4j:
cd /home/cauchothegaucho/Repositorios/Honeycomb/demo

docker exec -i honeycomb-neo4j cypher-shell -u neo4j -p password123 \
  < src/main/resources/new-test-data.cypher

# Verificar que se cargaron:
docker exec -i honeycomb-neo4j cypher-shell -u neo4j -p password123 \
  -c "MATCH (n) RETURN count(n);"
```

---

## 🛑 Detener Todo

```bash
# Terminal 2 (Backend): Ctrl+C
# Terminal 3 (Frontend): Ctrl+C

# Detener Neo4j (opcional):
docker stop honeycomb-neo4j

# O forzar todo:
pkill -9 -f "spring-boot:run"
pkill -9 -f "vite"
docker stop honeycomb-neo4j
```

---

## ⚡ Scripts Rápidos (si funcionan)

Si los scripts funcionan en tu terminal:

```bash
cd /home/cauchothegaucho/Repositorios/Honeycomb

# Iniciar todo:
./quick-start.sh

# Detener todo:
./quick-stop.sh

# Ver estado:
./status-honeycomb.sh
```

---

## 📝 Orden de Inicio Correcto

**Siempre seguir este orden:**

1. ✅ **Neo4j** (esperar 10 segundos)
2. ✅ **Backend** en puerto 8080 (esperar hasta ver "Started..." ~30-60 segundos)
3. ✅ **Frontend** en puerto 3000 (esperar 5 segundos)

**NO abrir el navegador hasta que los 3 estén listos.**

---

## 🎨 Verificar las Visualizaciones

Una vez que todo esté funcionando:

1. Ir a `http://localhost:3000`
2. Navegar a **"Greedy - Peel Chains"**
3. Dejar los parámetros por defecto
4. Click en **"Analizar Peel Chains"**
5. Deberías ver:
   - 🕸️ Un grafo de red interactivo
   - 📊 Tres gráficos (barras, pie, líneas)
   - 💳 Tarjetas con detalles de cada cadena

Si en lugar de eso ves:
- ❌ "JSON.parse error" → El backend no está respondiendo
- ❌ Pantalla en blanco → El frontend no está corriendo
- ❌ "Cannot connect" → Neo4j no está corriendo

---

## 💡 Consejo Final

**La forma más confiable es usar las 3 terminales separadas** para ver los logs en tiempo real y detectar problemas inmediatamente.

**Configuración CORS:** El frontend en puerto 3000 tiene configurado un proxy en `vite.config.js` que redirige todas las peticiones `/api/*` al backend en `localhost:8080`. Esto evita problemas de CORS completamente.

Los scripts automáticos son útiles pero pueden ocultar errores.
