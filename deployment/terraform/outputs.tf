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

output "security_group_id" {
  description = "ID del Security Group del servicio"
  value       = module.networking.security_group_id
}

