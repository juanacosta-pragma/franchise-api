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
  description = "Número de instancias deseadas del servicio"
  type        = number
  default     = 1
}

variable "log_retention_days" {
  description = "Días de retención de logs en CloudWatch"
  type        = number
  default     = 7
}

# --- Secretos / configuración sensible ---
variable "mongodb_uri" {
  description = "URI de conexión a MongoDB."
  type        = string
  sensitive   = true
  default     = "mongodb+srv://juanacosta_db_user:pragma2026@franchise-db.fx23h97.mongodb.net/test?appName=franchise-db"
}

