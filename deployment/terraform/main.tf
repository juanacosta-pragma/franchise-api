locals {
  name_prefix = "${var.project_name}-${var.environment}"
}

# Red: descubre VPC/subnets default y crea security group
module "networking" {
  source = "./modules/networking"

  name_prefix    = local.name_prefix
  container_port = var.container_port
  tags           = var.tags
}

# IAM: roles de ejecución y de la task
module "iam" {
  source = "./modules/iam"

  name_prefix = local.name_prefix
  tags        = var.tags
}

# ECS: cluster, task definition, service y log group
module "ecs" {
  source = "./modules/ecs"

  name_prefix        = local.name_prefix
  container_image    = var.container_image
  container_port     = var.container_port
  task_cpu           = var.task_cpu
  task_memory        = var.task_memory
  desired_count      = var.desired_count
  log_retention_days = var.log_retention_days
  mongodb_uri        = var.mongodb_uri
  aws_region         = var.aws_region

  execution_role_arn = module.iam.execution_role_arn
  task_role_arn      = module.iam.task_role_arn
  subnet_ids         = module.networking.subnet_ids
  security_group_ids = [module.networking.security_group_id]

  tags = var.tags
}
