output "alb_dns_name" {
  description = "DNS público del ALB — usa este URL para consumir la API"
  value       = module.alb.alb_dns_name
}

output "api_base_url" {
  description = "URL base de la API a través del ALB"
  value       = "http://${module.alb.alb_dns_name}"
}

output "cluster_name" {
  description = "Nombre del clúster ECS"
  value       = module.ecs.cluster_name
}

output "service_name" {
  description = "Nombre del servicio ECS"
  value       = module.ecs.service_name
}

output "task_definition_arn" {
  description = "ARN de la task definition"
  value       = module.ecs.task_definition_arn
}

output "log_group_name" {
  description = "Nombre del Log Group en CloudWatch"
  value       = module.ecs.log_group_name
}

output "availability_zones" {
  description = "AZs donde corren las tasks"
  value       = module.networking.availability_zones
}

output "security_group_id" {
  description = "ID del Security Group del servicio"
  value       = module.networking.security_group_id
}
