variable "name_prefix" {
  description = "Prefijo para nombrar recursos"
  type        = string
}

variable "container_port" {
  description = "Puerto que se abrirá en el security group de las tasks"
  type        = number
}

variable "az_count" {
  description = "Numero de Availability Zones a usar"
  type        = number
  default     = 2
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}
