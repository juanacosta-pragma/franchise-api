variable "name_prefix" {
  description = "Prefijo para nombrar los roles"
  type        = string
}

variable "tags" {
  description = "Tags comunes"
  type        = map(string)
  default     = {}
}

