output "execution_role_arn" {
  description = "ARN del rol de ejecución de ECS"
  value       = aws_iam_role.execution.arn
}

output "task_role_arn" {
  description = "ARN del rol asumido por la task"
  value       = aws_iam_role.task.arn
}

