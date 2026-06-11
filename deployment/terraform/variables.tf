# --- Generales ---
variable "aws_region" {
  description = "AWS region donde se desplegará la infraestructura"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nombre base usado como prefijo en los recursos"
  type        = string
  default     = "franchise-pragma"
}

variable "environment" {
  description = "Entorno (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "tags" {
  description = "Tags comunes aplicados a todos los recursos"
  type        = map(string)
  default = {
    Project   = "franchise-pragma"
    ManagedBy = "Terraform"
  }
}

# --- Contenedor ---
variable "container_image" {
  description = "Imagen Docker (ECR) a desplegar"
  type        = string
  default     = "437952802980.dkr.ecr.us-east-1.amazonaws.com/franchise-pragma-api:latest"
}

variable "container_port" {
  description = "Puerto que expone el contenedor"
  type        = number
  default     = 8080
}

variable "task_cpu" {
  description = "CPU asignada a la task (unidades de CPU; 1024 = 1 vCPU)"
  type        = string
  default     = "512"
}

variable "task_memory" {
  description = "Memoria asignada a la task (MB)"
  type        = string
  default     = "1024"
}

variable "desired_count" {
  description = "Número de instancias deseadas del servicio (una por AZ)"
  type        = number
  default     = 2
}

variable "az_count" {
  description = "Número de Availability Zones a usar"
  type        = number
  default     = 2
}

variable "health_check_path" {
  description = <<-EOT
    Path del health check en el contenedor (debe devolver 2xx).
    Usamos /actuator/health/liveness en vez de /actuator/health para que el
    ALB no derribe la task cuando un HealthIndicator externo (Mongo, etc.)
    esté DOWN. Liveness solo verifica que la JVM/Spring está viva.
  EOT
  type        = string
  default     = "/actuator/health/liveness"
}

variable "health_check_grace_period_seconds" {
  description = "Segundos de gracia antes de que ECS evalúe el health check"
  type        = number
  default     = 60
}

variable "log_retention_days" {
  description = "Días de retención de logs en CloudWatch"
  type        = number
  default     = 7
}

# --- Secretos / configuración sensible ---
variable "mongodb_uri" {
  description = <<-EOT
    URI de conexión a MongoDB Atlas.

    IMPORTANTE — Parámetros mínimos requeridos:
      - authSource=admin     -> obliga al driver a autenticar contra la base 'admin'
                                (única base donde Atlas almacena los usuarios). Evita el
                                error: "not authorized on local to execute command hello".
      - retryWrites=true     -> reintentos automáticos de escrituras idempotentes.
      - w=majority           -> consistencia / durabilidad recomendada por Atlas.

    El usuario de Atlas debe tener, además de readWrite sobre la BD de la app,
    el privilegio 'clusterMonitor' (incluido en el built-in role
    "Read and write to any database"). Sin él, los heartbeats SDAM del driver
    fallan con AtlasError 8000 (Unauthorized) y las tasks de ECS entran en
    bucle de reinicios al fallar el health check.
  EOT
  type        = string
  sensitive   = true
  default     = "mongodb+srv://juanacosta_db_user:pragma2026@franchise-db.fx23h97.mongodb.net/test?authSource=admin&retryWrites=true&w=majority&appName=franchise-db"
}

