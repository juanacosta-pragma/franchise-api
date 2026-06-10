variable "name_prefix" {
  description = "Prefijo para nombrar recursos"
  type        = string
}

variable "container_port" {
  description = "Puerto que se abrirá en el security group"
  type        = number
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

