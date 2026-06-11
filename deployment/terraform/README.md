# Despliegue Terraform — Franchise API

Infraestructura AWS modularizada para desplegar la API en **ECS Fargate** con alta disponibilidad en **2 Availability Zones** detrás de un **Application Load Balancer**.

## Arquitectura

```
Internet
   │
   ▼  puerto 80
┌──────────────────────────────────────┐
│   Application Load Balancer (ALB)    │
│   SG: acepta 0.0.0.0/0:80            │
└────────────┬─────────────────────────┘
             │  round-robin
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
  AZ-1              AZ-2
Fargate task      Fargate task    ← 2 tasks (desired_count=2)
port 8080         port 8080
SG: solo ALB      SG: solo ALB
    │                 │
    └────────┬────────┘
             │
             ▼
         MongoDB Atlas
```

## Módulos

```
terraform/
├── versions.tf          # Providers y versiones
├── main.tf              # Composición de módulos
├── variables.tf         # Variables de entrada
├── outputs.tf           # Outputs raíz
└── modules/
    ├── networking/      # VPC default, subnets multi-AZ, SG del ALB y tasks
    ├── alb/             # Application Load Balancer, Target Group, Listener HTTP:80
    ├── iam/             # Roles de ejecución y de task
    └── ecs/             # Cluster, Task Definition, Servicio con 2 tareas
```

## Uso

1. Copia el archivo de variables:

   ```powershell
   Copy-Item terraform.tfvars.example terraform.tfvars
   ```

2. Inicializa (necesario después de añadir módulo `alb`):

   ```powershell
   terraform init
   ```

3. Planifica y aplica:

   ```powershell
   terraform plan
   terraform apply
   ```

4. Tras el apply verás el DNS del ALB en los outputs:

   ```
   api_base_url = "http://franchise-pragma-dev-alb-XXXX.us-east-1.elb.amazonaws.com"
   ```

## Outputs principales

| Output | Descripción |
|---|---|
| `alb_dns_name` | DNS del ALB |
| `api_base_url` | URL base completa `http://...` |
| `cluster_name` | Nombre del clúster ECS |
| `service_name` | Nombre del servicio ECS |
| `availability_zones` | AZs en uso |
| `log_group_name` | CloudWatch Log Group |

## Variables clave para alta disponibilidad

| Variable | Default | Descripción |
|---|---|---|
| `desired_count` | `2` | Número de tasks Fargate |
| `az_count` | `2` | Número de Availability Zones |
| `health_check_path` | `/actuator/health` | Endpoint de health check |
| `health_check_grace_period_seconds` | `60` | Espera antes de evaluar el health |

## Migración desde el estado anterior

Si ya tenías recursos en el state sin el ALB, ejecuta los `state mv` correspondientes antes del `apply` para evitar destroy/recreate de recursos existentes. Los nuevos recursos (`alb`, `networking.alb_security_group_id`) serán creados desde cero.

## Próximas mejoras sugeridas

- HTTPS con ACM certificate + listener en 443 + redirect 80→443
- Auto Scaling basado en CPU/memoria del servicio
- Backend S3 + DynamoDB para state remoto en equipo


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

## Troubleshooting

### ❗ Las tasks entran en bucle de reinicios — `MongoCommandException 8000 (AtlasError): not authorized on local to execute command { hello: 1, $db: "local" }`

**Causa:** el driver de MongoDB ejecuta `hello` contra la base `local` como parte del monitor SDAM (descubrimiento del replica set / heartbeat). Atlas devuelve `Unauthorized` porque el usuario de Atlas configurado en `mongodb_uri` **no tiene el privilegio `clusterMonitor`**. El health check `/actuator/health` se vuelve `DOWN`, el ALB marca la task como `unhealthy` y ECS la mata en bucle.

**Solución (orden recomendado):**

1. **Atlas → Security → Database Access** → editar el usuario.
   - Asignar el built-in **“Read and write to any database”** (incluye `clusterMonitor`), **o**
   - Añadir manualmente: `readWrite @ <bd_app>` **+** `clusterMonitor @ admin`.
2. Asegurar que la URI use **`mongodb+srv://...`** y contenga **`authSource=admin`**:
   ```
   mongodb+srv://USER:PASS@HOST/test?authSource=admin&retryWrites=true&w=majority&appName=franchise-db
   ```
3. **Atlas → Network Access**: añadir las IPs públicas salientes de las tasks Fargate (o `0.0.0.0/0` solo en dev).
4. Si la contraseña contiene `@ : / ? # [ ] %`, **URL-encode** esos caracteres en la URI.
5. Verificar manualmente desde tu equipo:
   ```bash
   mongosh "mongodb+srv://USER:PASS@HOST/test?authSource=admin"
   # Dentro:
   db.hello()           # debe devolver { ok: 1, isWritablePrimary: true, ... }
   db.runCommand({ ping: 1 })
   ```
6. Forzar un nuevo deployment de ECS para que las tasks tomen la URI corregida:
   ```bash
   aws ecs update-service \
     --cluster franchise-pragma-dev-cluster \
     --service franchise-pragma-dev-service \
     --force-new-deployment
   ```

> 🔒 Una vez estabilizado, migrar la URI desde la variable de entorno plana hacia **AWS Secrets Manager** y referenciarla en la task definition con `secrets` (en lugar de `environment`) para no exponer credenciales en logs ni en el plan de Terraform.

