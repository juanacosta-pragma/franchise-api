output "vpc_id" {
  description = "ID de la VPC por defecto"
  value       = data.aws_vpc.default.id
}

output "subnet_ids" {
  description = "IDs de las subnets default"
  value       = data.aws_subnets.default.ids
}

output "security_group_id" {
  description = "ID del Security Group creado para la API"
  value       = aws_security_group.api.id
}

