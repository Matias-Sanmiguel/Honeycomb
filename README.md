# 📚 Índice de Documentación - Honeycomb

Guía de navegación de toda la documentación del proyecto de análisis forense de criptomonedas.

---

## 🚀 Para Empezar

### 1. **README.md** - Introducción al Proyecto
Comienza aquí para entender qué es Honeycomb y sus capacidades principales.

### 2. **INICIO_RAPIDO.md** - Guía de Inicio Rápido
Tutorial paso a paso para levantar el proyecto y hacer tu primera consulta.
- ⏱️ Tiempo estimado: 10 minutos
- 🎯 Ideal para: Nuevos usuarios

### 3. **README_SETUP.md** - Configuración Detallada
Instrucciones completas de instalación y configuración del entorno.
- ⏱️ Tiempo estimado: 20 minutos
- 🎯 Ideal para: Configuración inicial

---

## 🐳 Docker

### **DOCKER_README.md** - Guía de Docker
Todo sobre la configuración de Docker y docker-compose:
- Configuración de Neo4j
- Variables de entorno
- Persistencia de datos
- Troubleshooting

### **docker-manager.sh** - Script de Gestión
Script para administrar los contenedores de Docker fácilmente.

```bash
./docker-manager.sh start    # Iniciar servicios
./docker-manager.sh stop     # Detener servicios
./docker-manager.sh restart  # Reiniciar servicios
./docker-manager.sh logs     # Ver logs
```

---

## 🧬 Algoritmos Implementados

### **DOCUMENTACION_ALGORITMOS.md** - Documentación Completa de Algoritmos
Documentación técnica de todos los algoritmos implementados:
- ✅ Backtracking - Detección de cadenas sospechosas
- ✅ Branch & Bound - Rutas óptimas con restricciones
- ✅ Greedy - Peel chains y clustering
- ✅ Dynamic Programming - Max flow y caminos óptimos
- ✅ Graph Algorithms - Centralidad, comunidades, PageRank
- ✅ Pattern Matching - Mixing, ciclos, outliers
- ⏱️ Tiempo estimado: 45 minutos
- 🎯 Ideal para: Desarrolladores y analistas

### **BACKTRACKING_BRANCH_BOUND_GUIDE.md** - Guía Específica
Guía detallada para los algoritmos de Backtracking y Branch & Bound:
- Casos de uso
- Ejemplos prácticos
- Interpretación de resultados
- Comparación de rendimiento

### **RESUMEN_IMPLEMENTACION_ALGORITMOS.md** - Resumen Técnico
Resumen ejecutivo de la implementación de algoritmos:
- Estado de implementación
- Métricas de rendimiento
- Arquitectura del código
- Tests implementados

---

## 🧪 Testing

### **TESTING_GUIDE.md** - Guía Completa de Testing
Documentación completa sobre testing del proyecto:
- Tests unitarios (24+ tests)
- Tests de integración
- Cobertura de código
- Cómo ejecutar tests
- Cómo escribir nuevos tests
- ⏱️ Tiempo estimado: 30 minutos
- 🎯 Ideal para: Desarrolladores

### **TEST_BACKTRACKING_BRANCH_BOUND.sh** - Script de Testing
Script para ejecutar tests de los algoritmos principales.

```bash
chmod +x TEST_BACKTRACKING_BRANCH_BOUND.sh
./TEST_BACKTRACKING_BRANCH_BOUND.sh
```

---

## 📊 Datos de Prueba

### **TEST_DATA_README.md** - Guía de Datos de Prueba
Instrucciones completas para cargar y usar datos de prueba en Neo4j:
- 8 Wallets con diferentes características
- 10 Transacciones interconectadas
- Patrones de peel chains, ciclos y mixing
- Consultas de ejemplo
- Troubleshooting

### **LOAD_TEST_DATA.sh** - Script de Carga de Datos
Script automático para cargar datos de prueba en Neo4j.

```bash
./LOAD_TEST_DATA.sh
```

El script detecta automáticamente:
- Docker (método preferido)
- Python con driver neo4j
- HTTP API con curl
- Instrucciones manuales si nada funciona

---

## 🔧 Endpoints y Ejemplos

### **EJEMPLOS_ENDPOINTS.sh** - Script de Ejemplos
Script con ejemplos de todas las llamadas a los endpoints de la API:
- Backtracking
- Branch & Bound
- Greedy
- Dynamic Programming
- Graph Algorithms
- Pattern Matching

```bash
chmod +x EJEMPLOS_ENDPOINTS.sh
./EJEMPLOS_ENDPOINTS.sh
```

---

## 📖 Cómo Usar Esta Documentación

### Para Usuarios Nuevos:
1. Lee **README.md** (5 min)
2. Sigue **INICIO_RAPIDO.md** (10 min)
3. Carga datos de prueba con **LOAD_TEST_DATA.sh** (2 min)
4. Prueba los ejemplos en **EJEMPLOS_ENDPOINTS.sh** (10 min)

### Para Desarrolladores:
1. Lee **README_SETUP.md** para configuración (20 min)
2. Revisa **DOCUMENTACION_ALGORITMOS.md** (45 min)
3. Lee **TESTING_GUIDE.md** (30 min)
4. Ejecuta los tests con **TEST_BACKTRACKING_BRANCH_BOUND.sh**

### Para DevOps/Deployment:
1. Lee **DOCKER_README.md** (15 min)
2. Usa **docker-manager.sh** para gestión
3. Configura variables de entorno según necesidad

### Para Analistas/Investigadores:
1. Lee **BACKTRACKING_BRANCH_BOUND_GUIDE.md** (20 min)
2. Carga datos de prueba (**TEST_DATA_README.md**)
3. Experimenta con **EJEMPLOS_ENDPOINTS.sh**
4. Lee casos de uso en **DOCUMENTACION_ALGORITMOS.md**

---

## 📂 Estructura de Archivos

```
Honeycomb/
│
├── 📘 Documentación Principal
│   ├── README.md                              # Introducción
│   ├── INICIO_RAPIDO.md                       # Quick start
│   ├── README_SETUP.md                        # Setup completo
│   └── INDEX.md                               # Este archivo
│
├── 🐳 Docker
│   ├── DOCKER_README.md                       # Guía Docker
│   ├── docker-compose.yml                     # Configuración
│   └── docker-manager.sh                      # Script de gestión
│
├── 🧬 Algoritmos
│   ├── DOCUMENTACION_ALGORITMOS.md            # Documentación completa
│   ├── BACKTRACKING_BRANCH_BOUND_GUIDE.md     # Guía específica
│   └── RESUMEN_IMPLEMENTACION_ALGORITMOS.md   # Resumen técnico
│
├── 🧪 Testing
│   ├── TESTING_GUIDE.md                       # Guía de testing
│   └── TEST_BACKTRACKING_BRANCH_BOUND.sh      # Script de tests
│
├── 📊 Datos de Prueba
│   ├── TEST_DATA_README.md                    # Guía de datos
│   ├── LOAD_TEST_DATA.sh                      # Script de carga
│   └── demo/src/main/resources/
│       └── test-data.cypher                   # Datos en Cypher
│
├── 🔧 Scripts y Ejemplos
│   └── EJEMPLOS_ENDPOINTS.sh                  # Ejemplos de API
│
└── 💻 Código Fuente
    └── demo/
        └── src/
            ├── main/java/com/example/         # Código principal
            └── test/java/com/example/         # Tests
```

---

## 🔍 Buscar Información Rápida

### "¿Cómo inicio el proyecto?"
→ **INICIO_RAPIDO.md**

### "¿Cómo funciona el algoritmo X?"
→ **DOCUMENTACION_ALGORITMOS.md**

### "¿Cómo ejecuto los tests?"
→ **TESTING_GUIDE.md**

### "¿Cómo cargo datos de prueba?"
→ **TEST_DATA_README.md** + `./LOAD_TEST_DATA.sh`

### "¿Cómo uso Docker?"
→ **DOCKER_README.md** + `./docker-manager.sh`

### "¿Qué endpoints están disponibles?"
→ **EJEMPLOS_ENDPOINTS.sh**

### "¿Cómo interpreto los resultados de Backtracking?"
→ **BACKTRACKING_BRANCH_BOUND_GUIDE.md**

---

## 📝 Notas

- Todos los scripts `.sh` necesitan permisos de ejecución: `chmod +x nombre_script.sh`
- La mayoría de los comandos asumen que estás en la raíz del proyecto
- Para ver logs de Neo4j: `./docker-manager.sh logs`
- Para limpiar la base de datos: Ejecuta en Neo4j Browser `MATCH (n) DETACH DELETE n`

---

## 🆘 Soporte

Si encuentras problemas:
1. Revisa la sección de **Troubleshooting** en el archivo relevante
2. Verifica que Docker esté corriendo: `docker ps`
3. Revisa los logs: `./docker-manager.sh logs`
4. Consulta **DOCKER_README.md** para problemas de configuración

---

## 📜 Licencia

Ver archivo **LICENSE** en la raíz del proyecto.

---

**Última actualización:** 2025-11-04
**Versión de documentación:** 2.0

