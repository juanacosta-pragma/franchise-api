output "vpc_id" {
  description = "ID de la VPC por defecto"
  value       = data.aws_vpc.default.id
}

output "subnet_ids" {
  description = "Subnets seleccionadas (una por AZ)"
  value       = local.selected_subnet_ids
}

output "availability_zones" {
  description = "AZs en uso"
  value       = local.selected_azs
}

output "security_group_id" {
  description = "ID del Security Group de las tasks (solo recibe trafico del ALB)"
  value       = aws_security_group.api.id
}

output "alb_security_group_id" {
  description = "ID del Security Group del ALB"
  value       = aws_security_group.alb.id
}
