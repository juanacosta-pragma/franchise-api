variable "name_prefix" {
  description = "Prefijo para nombrar los recursos"
  type        = string
}

variable "aws_region" {
  description = "Región AWS (usada para configurar logs)"
  type        = string
}

variable "container_image" {
  description = "Imagen Docker a desplegar"
  type        = string
}

variable "container_port" {
  description = "Puerto expuesto por el contenedor"
  type        = number
}

variable "task_cpu" {
  description = "CPU asignada a la task"
  type        = string
}

variable "task_memory" {
  description = "Memoria asignada a la task (MB)"
  type        = string
}

variable "desired_count" {
  description = "Número de instancias del servicio"
  type        = number
  default     = 1
}

variable "log_retention_days" {
  description = "Retención de logs en días"
  type        = number
  default     = 7
}

variable "execution_role_arn" {
  description = "ARN del rol de ejecución de ECS"
  type        = string
}

variable "task_role_arn" {
  description = "ARN del rol de la task"
  type        = string
}

variable "subnet_ids" {
  description = "Subnets donde se desplegará el servicio"
  type        = list(string)
}

variable "security_group_ids" {
  description = "Security Groups a aplicar al servicio"
  type        = list(string)
}

variable "mongodb_uri" {
  description = "URI de MongoDB"
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

variable "target_group_arn" {
  description = "ARN del Target Group del ALB al que se registra el servicio"
  type        = string
}

variable "health_check_grace_period_seconds" {
  description = "Segundos que ECS espera antes de comenzar los health checks (útil para apps lentas en arrancar)"
  type        = number
  default     = 60
}
