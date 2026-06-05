# Documentación API - OpenAPI/Swagger

La API está documentada usando OpenAPI 3.0 (Swagger) y es accesible en:

## URLs de Acceso

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## Descripción de Endpoints

### Franchises (Franquicias)

#### 1. Obtener todas las franquicias
```
GET /api/v1/franchises/getAll
```
Retorna una lista de todas las franquicias registradas.

#### 2. Crear franquicia
```
POST /api/v1/franchises/create
```
Crea una nueva franquicia.

**Body (JSON):**
```json
{
  "name": "Piza Company"
}
```

#### 3. Obtener franquicia por ID
```
GET /api/v1/franchises/getById/{id}
```
Obtiene los detalles de una franquicia específica.

#### 4. Actualizar nombre de franquicia
```
PUT /api/v1/franchises/update/{id}
```
Actualiza el nombre de una franquicia.

**Body (JSON):**
```json
{
  "name": "Nuevo nombre"
}
```

#### 5. Eliminar franquicia
```
DELETE /api/v1/franchises/delete/{id}
```
Elimina una franquicia.

---

### Branches (Sucursales)

#### 1. Obtener sucursal
```
GET /api/v1/branches/get/franchise/{franchiseId}/branch/{branchId}
```
Obtiene los detalles de una sucursal específica.

#### 2. Crear sucursal
```
POST /api/v1/branches/create/franchise/{franchiseId}
```
Crea una nueva sucursal en una franquicia.

**Body (JSON):**
```json
{
  "name": "Central Branch"
}
```

#### 3. Actualizar nombre de sucursal
```
PUT /api/v1/branches/update/franchise/{franchiseId}/branch/{branchId}
```
Actualiza el nombre de una sucursal.

**Body (JSON):**
```json
{
  "name": "Nuevo nombre"
}
```

---

### Products (Productos)

#### 1. Crear producto
```
POST /api/v1/products/create/franchise/{franchiseId}/branch/{branchId}
```
Crea un nuevo producto en una sucursal.

**Body (JSON):**
```json
{
  "name": "Laptop",
  "stock": 100
}
```

#### 2. Obtener producto
```
GET /api/v1/products/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}
```
Obtiene los detalles de un producto específico.

#### 3. Eliminar producto
```
DELETE /api/v1/products/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}
```
Elimina un producto.

#### 4. Actualizar stock de producto
```
PUT /api/v1/products/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock
```
Actualiza el stock de un producto.

**Body (JSON):**
```json
{
  "stock": 150
}
```

#### 5. Actualizar nombre de producto
```
PUT /api/v1/products/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}
```
Actualiza el nombre de un producto.

**Body (JSON):**
```json
{
  "name": "Desktop"
}
```

#### 6. Obtener producto con mayor stock
```
GET /api/v1/products/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/
```
Obtiene el producto con mayor cantidad en stock de una sucursal.

---

## Estructura de Respuestas

### Respuesta de Franquicia
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Piza Company",
  "branches": [
    {
      "id": "507f1f77bcf86cd799439012",
      "name": "Central Branch",
      "products": [
        {
          "id": "507f1f77bcf86cd799439013",
          "name": "Laptop",
          "stock": 100
        }
      ]
    }
  ]
}
```

### Respuesta de Sucursal
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Central Branch",
  "products": [
    {
      "id": "507f1f77bcf86cd799439013",
      "name": "Laptop",
      "stock": 100
    }
  ]
}
```

### Respuesta de Producto
```json
{
  "id": "507f1f77bcf86cd799439013",
  "name": "Laptop",
  "stock": 100
}
```

---

## Códigos de Respuesta HTTP

- **200 OK**: Solicitud exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Recurso eliminado exitosamente
- **400 Bad Request**: Error en la solicitud
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error en el servidor

## Cómo Acceder a Swagger UI

1. Inicia la aplicación: `./gradlew bootRun`
2. Abre tu navegador en: http://localhost:8080/swagger-ui.html
3. Podrás ver todos los endpoints documentados y probarlos directamente desde la interfaz

## Información de API

- **Versión**: 1.0.0
- **Protocolo**: HTTP REST
- **Content-Type**: application/json
- **Arquitectura**: Clean Architecture con Spring Boot 5.0 y WebFlux (Reactivo)

