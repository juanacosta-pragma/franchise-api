# 1. Definir el proveedor de AWS
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# 2. Detectar automáticamente la VPC y Subnets por defecto de tu cuenta AWS
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# 3. Crear el Clúster de ECS
resource "aws_ecs_cluster" "api_cluster" {
  name = "franchise-pragma-cluster"
}

# 4. Crear el Rol de Ejecución para que ECS pueda leer de ECR y crear logs
resource "aws_iam_role" "ecs_execution_role" {
  name = "franchise_ecs_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# En caso de que uses Spring Cloud AWS o Parameter Store en el futuro, este rol te servirá
resource "aws_iam_role" "ecs_task_role" {
  name = "franchise_ecs_task_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

# 5. Definición de la Tarea (Task Definition)
resource "aws_ecs_task_definition" "api_task" {
  family                   = "franchise-pragma-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"  # 0.5 vCPU
  memory                   = "1024" # 1 GB RAM
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "franchise-pragma-api"
      image     = "437952802980.dkr.ecr.us-east-1.amazonaws.com/franchise-pragma-api:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      environment = [
        {
          name  = "SPRING_DATA_MONGODB_URI"
          value = "mongodb+srv://juanacosta_db_user:pragma2026@franchise-db.fx23h97.mongodb.net/test?appName=franchise-db"
        }
      ]
    }
  ])
}

# 6. Grupo de Seguridad (Abrir puerto 8080 al mundo)
resource "aws_security_group" "api_sg" {
  name        = "franchise-api-sg"
  description = "Permitir acceso al puerto 8080"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 7. Crear el Servicio ECS Fargate
resource "aws_ecs_service" "api_service" {
  name            = "franchise-pragma-service"
  cluster         = aws_ecs_cluster.api_cluster.id
  task_definition = aws_ecs_task_definition.api_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.api_sg.id]
    assign_public_ip = true
  }
}

resource "aws_cloudwatch_log_group" "api_logs" {
  name              = "/ecs/franchise-pragma-api"
  retention_in_days = 7 # Borra los logs viejos para no cobrarte de más
}