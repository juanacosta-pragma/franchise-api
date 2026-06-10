output "cluster_id" {
  description = "ID del clúster ECS"
  value       = aws_ecs_cluster.this.id
}

output "cluster_name" {
  description = "Nombre del clúster ECS"
  value       = aws_ecs_cluster.this.name
}

output "service_name" {
  description = "Nombre del servicio ECS"
  value       = aws_ecs_service.this.name
}

output "task_definition_arn" {
  description = "ARN de la task definition"
  value       = aws_ecs_task_definition.this.arn
}

output "log_group_name" {
  description = "Nombre del CloudWatch Log Group"
  value       = aws_cloudwatch_log_group.api.name
}

