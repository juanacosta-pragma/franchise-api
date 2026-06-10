# Documentación API — OpenAPI / Swagger

La API está documentada con **OpenAPI 3.1** vía **springdoc-openapi 3** sobre Spring WebFlux funcional.

Las descripciones de cada endpoint (paths, parámetros, request/response y schemas) se declaran con `@RouterOperations` en [`RouterRest.java`](infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/RouterRest.java). Los metadatos de la API (título, contacto, servidores) están en [`OpenApiConfig.java`](applications/app-service/src/main/java/co/com/bancolombia/config/OpenApiConfig.java).

## URLs de acceso

Tras arrancar la app con `./gradlew bootRun` (o `gradlew.bat bootRun` en Windows):

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/webjars/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

> Si tu `springdoc.swagger-ui.path` está configurado, también responde en `http://localhost:8080/swagger-ui.html` (alias).

## Tags

| Tag | Descripción |
|---|---|
| **Franchises** | Operaciones sobre franquicias |
| **Branches** | Operaciones sobre sucursales de una franquicia |
| **Products** | Operaciones sobre productos dentro de una sucursal |

---

## Endpoints

### 🏢 Franchises

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/v1/franchises/getAll` | Lista todas las franquicias |
| `POST` | `/api/v1/franchises/create` | Crea una franquicia |
| `GET` | `/api/v1/franchises/getById/{id}` | Obtiene una franquicia por id |
| `PUT` | `/api/v1/franchises/update/{id}` | Actualiza el nombre de la franquicia |
| `DELETE` | `/api/v1/franchises/delete/{id}` | Elimina la franquicia |

**Ejemplo `POST /api/v1/franchises/create`:**
```json
{ "name": "Pizza Company" }
```
Respuesta `201 Created` con `FranchiseResponse`.

### 🏬 Branches

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/api/v1/branches/get/franchise/{franchiseId}/branch/{branchId}` | Obtiene una sucursal |
| `POST` | `/api/v1/branches/create/franchise/{franchiseId}` | Añade una sucursal a la franquicia |
| `PUT` | `/api/v1/branches/update/franchise/{franchiseId}/branch/{branchId}` | Renombra la sucursal |

**Ejemplo `POST .../create/franchise/{franchiseId}`:**
```json
{ "name": "Central Branch" }
```
Respuesta `201 Created` con `FranchiseResponse` (la franquicia con la nueva sucursal incluida).

### 📦 Products

| Método | Path | Descripción |
|---|---|---|
| `POST` | `/api/v1/products/create/franchise/{franchiseId}/branch/{branchId}` | Añade un producto |
| `GET` | `/api/v1/products/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}` | Obtiene un producto |
| `DELETE` | `/api/v1/products/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}` | Elimina un producto |
| `PUT` | `/api/v1/products/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock` | Actualiza el stock |
| `PUT` | `/api/v1/products/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}` | Renombra un producto |
| `GET` | `/api/v1/products/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/` | Producto con mayor stock |

**Ejemplo crear producto:**
```json
{ "name": "Laptop", "stock": 100 }
```

**Ejemplo actualizar stock:**
```json
{ "stock": 150 }
```

**Ejemplo actualizar nombre:**
```json
{ "name": "Desktop" }
```

---

## Schemas

### `FranchiseRequest`
| Campo | Tipo | Requerido | Ejemplo |
|---|---|---|---|
| `name` | string | ✅ no blank | `"Pizza Company"` |

### `FranchiseResponse`
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Pizza Company",
  "branches": [
    {
      "id": "507f1f77bcf86cd799439012",
      "name": "Central Branch",
      "products": [
        { "id": "507f1f77bcf86cd799439013", "name": "Laptop", "stock": 100 }
      ]
    }
  ]
}
```

### `BranchRequest`
| Campo | Tipo | Requerido | Ejemplo |
|---|---|---|---|
| `name` | string | ✅ no blank | `"Central Branch"` |

### `BranchResponse`
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Central Branch",
  "products": [
    { "id": "507f1f77bcf86cd799439013", "name": "Laptop", "stock": 100 }
  ]
}
```

### `ProductRequest`
| Campo | Tipo | Requerido | Ejemplo |
|---|---|---|---|
| `name` | string | ✅ no blank | `"Laptop"` |
| `stock` | long | ➖ (solo en creación) | `100` |

### `ProductResponse`
```json
{ "id": "507f1f77bcf86cd799439013", "name": "Laptop", "stock": 100 }
```

### `StockUpdateRequest`
| Campo | Tipo | Ejemplo |
|---|---|---|
| `stock` | long | `150` |

---

## Códigos de respuesta HTTP

| Código | Significado | Cuándo se devuelve |
|---|---|---|
| `200 OK` | Éxito | GET / PUT / DELETE de productos |
| `201 Created` | Recurso creado | `createFranchise`, `addBranch`, `addProduct` |
| `204 No Content` | Borrado exitoso | `deleteFranchise` |
| `400 Bad Request` | Validación falló | Nombre vacío o nulo en POST/PUT |
| `404 Not Found` | Recurso no existe | `FranchiseNotFoundException`, `BranchNotFoundException`, `ProductNotFoundException` |
| `500 Internal Server Error` | Error inesperado | Fallos no controlados |

---

## Cómo correr y explorar

```powershell
# Arrancar
.\gradlew.bat bootRun --no-daemon

# Abrir Swagger UI (Windows)
start http://localhost:8080/webjars/swagger-ui/index.html

# Descargar el contrato OpenAPI
curl http://localhost:8080/v3/api-docs.yaml -o openapi.yaml
```

## Información de la API

- **Versión:** 1.0.0
- **OpenAPI:** 3.1
- **Stack:** Spring Boot 4.0.5 + WebFlux (functional routing) + Project Reactor
- **Documentación:** springdoc-openapi 3.0.0
- **Arquitectura:** Clean Architecture (model → usecase → entry-points / driven-adapters)
- **Content-Type:** `application/json`
