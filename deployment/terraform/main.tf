locals {
  name_prefix = "${var.project_name}-${var.environment}"
}

# Red: VPC default, subnets en 2 AZs, SG del ALB y SG de las tasks
module "networking" {
  source = "./modules/networking"

  name_prefix    = local.name_prefix
  container_port = var.container_port
  az_count       = var.az_count
  tags           = var.tags
}

# IAM: roles de ejecución y de la task
module "iam" {
  source = "./modules/iam"

  name_prefix = local.name_prefix
  tags        = var.tags
}

# ALB: balanceador público, target group y listener HTTP:80
module "alb" {
  source = "./modules/alb"

  name_prefix           = local.name_prefix
  vpc_id                = module.networking.vpc_id
  subnet_ids            = module.networking.subnet_ids
  alb_security_group_id = module.networking.alb_security_group_id
  container_port        = var.container_port
  health_check_path     = var.health_check_path
  tags                  = var.tags
}

# ECS: cluster, task definition, 2 tasks Fargate distribuidas en 2 AZs
module "ecs" {
  source = "./modules/ecs"

  name_prefix                       = local.name_prefix
  container_image                   = var.container_image
  container_port                    = var.container_port
  task_cpu                          = var.task_cpu
  task_memory                       = var.task_memory
  desired_count                     = var.desired_count
  log_retention_days                = var.log_retention_days
  mongodb_uri                       = var.mongodb_uri
  aws_region                        = var.aws_region
  target_group_arn                  = module.alb.target_group_arn
  health_check_grace_period_seconds = var.health_check_grace_period_seconds

  execution_role_arn = module.iam.execution_role_arn
  task_role_arn      = module.iam.task_role_arn
  subnet_ids         = module.networking.subnet_ids
  security_group_ids = [module.networking.security_group_id]

  tags = var.tags

  # Dependencia explícita: garantiza que en terraform destroy
  # el servicio ECS (y sus tasks/ENIs) se eliminen ANTES que los SGs de networking
  depends_on = [module.networking, module.alb]
}
