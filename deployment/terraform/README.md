# Despliegue Terraform — Franchise API

Infraestructura AWS modularizada para desplegar la API en **ECS Fargate**.

## Estructura

```
terraform/
├── versions.tf              # Providers y versiones
├── main.tf                  # Composición de módulos
├── variables.tf             # Variables de entrada
├── outputs.tf               # Outputs raíz
├── terraform.tfvars.example # Plantilla de valores (copiar a terraform.tfvars)
└── modules/
    ├── networking/   # VPC default, subnets, security group
    ├── iam/          # Roles de ejecución y de task
    └── ecs/          # Cluster + task definition + service + log group
```

## Uso

1. Copia el archivo de variables y rellena los valores reales:

   ```powershell
   Copy-Item terraform.tfvars.example terraform.tfvars
   notepad terraform.tfvars
   ```

   > ⚠️ **`terraform.tfvars` NO debe commitearse.** Contiene el URI de MongoDB con credenciales.

2. Inicializa, planifica y aplica:

   ```powershell
   terraform init
   terraform plan
   terraform apply
   ```

3. Destruir todo:

   ```powershell
   terraform destroy
   ```

## Variables sensibles

`mongodb_uri` está marcada como `sensitive = true`. Opciones recomendadas en orden de seguridad:

1. **AWS Secrets Manager** (ideal en producción):
   - Crear el secreto en AWS.
   - Usar `secrets` en el container definition en lugar de `environment`.
2. **Variable de entorno local**:
   ```powershell
   $env:TF_VAR_mongodb_uri = "mongodb+srv://..."
   terraform apply
   ```
3. **`terraform.tfvars` local** (gitignored).

## Backend remoto (recomendado para equipos)

Actualmente el estado vive en `terraform.tfstate` local. Para trabajar en equipo, añadir un backend S3 + DynamoDB para state locking en `versions.tf`:

```hcl
terraform {
  backend "s3" {
    bucket         = "mi-bucket-tfstate"
    key            = "franchise-api/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
```

## Outputs

Tras `apply`:

- `cluster_name` — nombre del cluster ECS.
- `service_name` — nombre del servicio.
- `task_definition_arn` — ARN de la task definition.
- `log_group_name` — Log Group de CloudWatch.
- `security_group_id` — Security Group del servicio.

## Próximas mejoras sugeridas

- Añadir **Application Load Balancer** delante del servicio para tener un DNS estable y soportar HTTPS.
- Migrar secretos a **AWS Secrets Manager** / **SSM Parameter Store**.
- Crear módulo separado para **ECR**.
- Añadir **autoscaling** del servicio basado en CPU/memoria.
- Configurar **backend remoto** (S3 + DynamoDB).

