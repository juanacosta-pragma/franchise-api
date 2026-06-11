output "alb_dns_name" {
  description = "DNS público del Application Load Balancer"
  value       = aws_lb.this.dns_name
}

output "alb_arn" {
  description = "ARN del ALB"
  value       = aws_lb.this.arn
}

output "target_group_arn" {
  description = "ARN del Target Group al que se registran las tasks"
  value       = aws_lb_target_group.this.arn
}

output "listener_arn" {
  description = "ARN del Listener HTTP del ALB"
  value       = aws_lb_listener.http.arn
}

