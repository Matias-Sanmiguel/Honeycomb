# 🎨 Honeycomb Frontend

Frontend web moderno para el sistema de análisis forense de criptomonedas Honeycomb.

## 🚀 Tecnologías

- **React 18** - Framework UI
- **Vite** - Build tool y dev server
- **React Router** - Navegación
- **Axios** - Cliente HTTP
- **Recharts** - Visualización de datos
- **React Icons** - Iconografía

## 📦 Instalación

```bash
cd frontend
npm install
```

## 🔧 Configuración

1. Copia el archivo de ejemplo de variables de entorno:
```bash
cp .env.example .env
```

2. Configura la URL del backend (por defecto: `http://localhost:8080/api`)

## 🏃 Ejecución

### Modo Desarrollo
```bash
npm run dev
```

El frontend estará disponible en `http://localhost:3000`

### Build para Producción
```bash
npm run build
```

Los archivos se generarán en la carpeta `dist/`

### Preview del Build
```bash
npm run preview
```

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── components/        # Componentes reutilizables
│   │   ├── Navbar.jsx
│   │   └── Navbar.css
│   ├── pages/            # Páginas/Vistas principales
│   │   ├── Home.jsx
│   │   ├── Backtracking.jsx
│   │   ├── BranchBound.jsx
│   │   ├── Greedy.jsx
│   │   ├── GraphAlgorithms.jsx
│   │   ├── PatternMatching.jsx
│   │   └── WalletAnalysis.jsx
│   ├── services/         # Servicios de API
│   │   └── api.js
│   ├── styles/          # Estilos globales
│   │   ├── index.css
│   │   └── App.css
│   ├── App.jsx          # Componente principal
│   └── main.jsx         # Punto de entrada
├── index.html
├── vite.config.js
└── package.json
```

## 🎯 Características

### Páginas Implementadas

1. **Home** (`/`)
   - Dashboard principal
   - Descripción de algoritmos
   - Inicio rápido

2. **Backtracking** (`/backtracking`)
   - Detección de cadenas sospechosas
   - Detección de ciclos
   - Visualización de rutas

3. **Branch & Bound** (`/branch-bound`)
   - Búsqueda de ruta óptima
   - Mejores N rutas
   - Visualización de caminos

4. **Greedy Algorithms** (`/greedy`)
   - Detección de peel chains
   - Clustering de cadenas
   - Métricas en tiempo real

5. **Graph Algorithms** (`/graph`)
   - Análisis de centralidad
   - Detección de comunidades
   - Importancia de nodos
   - Gráficos interactivos

6. **Pattern Matching** (`/patterns`)
   - Detección de mixing
   - Detección de ciclos
   - Transacciones rápidas
   - Detección de outliers

7. **Wallet Analysis** (`/wallet/:address`)
   - Información detallada de wallet
   - Historial de transacciones
   - Análisis de red
   - Evaluación de riesgo

## 🎨 Diseño

- **Tema oscuro** por defecto
- **Diseño responsivo** para móviles y tablets
- **Animaciones suaves** y transiciones
- **Paleta de colores** profesional:
  - Primary: `#6366f1` (Indigo)
  - Success: `#10b981` (Green)
  - Warning: `#f59e0b` (Amber)
  - Danger: `#ef4444` (Red)

## 🔌 Integración con Backend

El frontend se comunica con el backend a través de la API REST en `http://localhost:8080/api`

### Endpoints utilizados:

- `/api/forensic/backtracking/*` - Algoritmos de backtracking
- `/api/forensic/branch-bound/*` - Branch & Bound
- `/api/algorithms/greedy/*` - Algoritmos greedy
- `/api/algorithms/dynamic-programming/*` - Programación dinámica
- `/api/network/*` - Análisis de grafos y patrones
- `/api/wallets/*` - Información de wallets

## 🧪 Desarrollo

### Agregar una Nueva Página

1. Crea el componente en `src/pages/`:
```jsx
import React from 'react'
import './NuevaPagina.css'

function NuevaPagina() {
  return (
    <div className="nueva-pagina">
      <h1>Nueva Página</h1>
    </div>
  )
}

export default NuevaPagina
```

2. Agrega la ruta en `App.jsx`:
```jsx
<Route path="/nueva" element={<NuevaPagina />} />
```

3. Agrega el enlace en `Navbar.jsx`

### Agregar un Nuevo Servicio de API

Edita `src/services/api.js`:
```javascript
export const nuevoServicioAPI = {
  metodo: (params) => api.get('/endpoint', { params })
}
```

## 🐛 Troubleshooting

### El frontend no se conecta al backend
- Verifica que el backend esté corriendo en el puerto 8080
- Revisa la configuración de CORS en el backend
- Comprueba la variable `VITE_API_URL` en `.env`

### Error de compilación
```bash
rm -rf node_modules package-lock.json
npm install
```

### Puerto 3000 ya está en uso
Cambia el puerto en `vite.config.js`:
```javascript
server: {
  port: 3001
}
```

## 📱 Responsive Design

El frontend está optimizado para:
- 📱 Móviles (< 768px)
- 💻 Tablets (768px - 1024px)
- 🖥️ Desktop (> 1024px)

## 🚀 Despliegue

### Build de Producción
```bash
npm run build
```

### Servir con Nginx
```nginx
server {
    listen 80;
    server_name tu-dominio.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

### Docker
```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 📄 Licencia

Ver archivo LICENSE en la raíz del proyecto.

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu rama de feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Soporte

Para problemas o preguntas, abre un issue en GitHub.
VITE_API_URL=http://localhost:8080/api

