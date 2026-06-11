variable "name_prefix" {
  description = "Prefijo para nombrar los recursos del ALB"
  type        = string
}

variable "vpc_id" {
  description = "ID de la VPC"
  type        = string
}

variable "subnet_ids" {
  description = "Subnets públicas en distintas AZs para el ALB"
  type        = list(string)
}

variable "alb_security_group_id" {
  description = "Security Group del ALB (permite 80 desde internet)"
  type        = string
}

variable "container_port" {
  description = "Puerto de los contenedores backend"
  type        = number
}

variable "health_check_path" {
  description = "Path del health check (debe devolver 2xx)"
  type        = string
  default     = "/actuator/health"
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

