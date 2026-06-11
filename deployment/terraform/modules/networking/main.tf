data "aws_vpc" "default" {
  default = true
}

# Selecciona las AZs disponibles en la región
data "aws_availability_zones" "available" {
  state = "available"
}

# Trae todas las subnets de la VPC default
data "aws_subnets" "all" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Datos de cada subnet (para conocer su AZ)
data "aws_subnet" "details" {
  for_each = toset(data.aws_subnets.all.ids)
  id       = each.value
}

locals {
  # Toma las primeras N AZs configuradas (default: 2)
  selected_azs = slice(data.aws_availability_zones.available.names, 0, var.az_count)

  # Mapa AZ -> primera subnet encontrada en esa AZ
  subnets_by_az = {
    for az in local.selected_azs : az => [
      for s in data.aws_subnet.details : s.id if s.availability_zone == az
    ][0]
  }

  # Lista final de subnets, una por AZ
  selected_subnet_ids = values(local.subnets_by_az)
}

# ---------- Security groups ----------

# SG del ALB: público en 80 (HTTP) — y opcionalmente 443 si se configura HTTPS más adelante
resource "aws_security_group" "alb" {
  name_prefix = "${var.name_prefix}-alb-sg-"
  description = "Permite trafico publico HTTP hacia el ALB"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "HTTP from internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Egress all"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags

  lifecycle {
    create_before_destroy = true
  }
}

# SG de las tasks: solo acepta trafico del ALB en el puerto del contenedor
resource "aws_security_group" "api" {
  name_prefix = "${var.name_prefix}-sg-"
  description = "Permite trafico desde el ALB hacia las tasks"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "App port from ALB"
    from_port       = var.container_port
    to_port         = var.container_port
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    description = "Egress all"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags

  lifecycle {
    create_before_destroy = true
  }
}
