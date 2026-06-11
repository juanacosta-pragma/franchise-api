# Diagrama de Arquitectura AWS — Franchise Pragma API

> **Cómo visualizar en draw.io:**
> 1. Abre [draw.io](https://app.diagrams.net/) (o la app de escritorio).
> 2. Menú **Extras → Edit Diagram**.
> 3. Cambia el formato a **Mermaid** (pestaña en la parte superior del editor).
> 4. Pega el código Mermaid de abajo y haz clic en **OK**.

---

```mermaid
graph TB
    %% ─── Estilos ─────────────────────────────────────────────────────────
    classDef internet  fill:#f5f5f5,stroke:#999,color:#333,font-weight:bold
    classDef alb       fill:#FF9900,stroke:#c47400,color:#fff,font-weight:bold
    classDef ecs       fill:#FF4F8B,stroke:#c73970,color:#fff,font-weight:bold
    classDef ecr       fill:#FF4F8B,stroke:#c73970,color:#fff
    classDef cw        fill:#FF4F8B,stroke:#c73970,color:#fff
    classDef iam       fill:#DD344C,stroke:#aa1f33,color:#fff
    classDef mongo     fill:#00ED64,stroke:#009e42,color:#fff,font-weight:bold
    classDef sg        fill:#E8F4FD,stroke:#4A90D9,color:#333,stroke-dasharray:5 5
    classDef tg        fill:#FFF3CD,stroke:#FFC107,color:#333

    %% ─── Internet ────────────────────────────────────────────────────────
    Internet(["🌐 Internet\n0.0.0.0/0"]):::internet

    %% ─── AWS Region ──────────────────────────────────────────────────────
    subgraph AWS["☁️  AWS  —  us-east-1  (account: 437952802980)"]

        %% ── Networking ────────────────────────────────────────────────
        subgraph VPC["🔷 VPC Default"]

            subgraph SG_ALB["🛡️ Security Group — ALB\nIngress: 0.0.0.0/0 → TCP :80\nEgress: all"]
                ALB["⚖️ Application Load Balancer\nfranchise-pragma-dev-alb\nInternet-facing · HTTP :80\nListener → forward to TG"]:::alb
            end

            TG["🎯 Target Group\nfranchise-pragma-dev-tg\nProtocol: HTTP · Port: 8080\nTarget type: ip (Fargate)\nHealth check: GET /actuator/health"]:::tg

            subgraph AZ1["📍 Availability Zone — us-east-1a"]
                subgraph SG_T1["🛡️ SG API — Ingress: ALB SG → TCP :8080"]
                    Task1["📦 Fargate Task 1\nfranchise-pragma-dev-api\nImage: franchise-pragma-api:latest\n512 CPU  /  1024 MB RAM\nIP pública · Puerto :8080"]:::ecs
                end
            end

            subgraph AZ2["📍 Availability Zone — us-east-1b"]
                subgraph SG_T2["🛡️ SG API — Ingress: ALB SG → TCP :8080"]
                    Task2["📦 Fargate Task 2\nfranchise-pragma-dev-api\nImage: franchise-pragma-api:latest\n512 CPU  /  1024 MB RAM\nIP pública · Puerto :8080"]:::ecs
                end
            end
        end

        %% ── ECS Cluster ──────────────────────────────────────────────
        subgraph CLUSTER["🚀 ECS Fargate Cluster\nfranchise-pragma-dev-cluster\nECS Service: desired_count = 2"]
            direction TB
            Task1
            Task2
        end

        %% ── ECR ──────────────────────────────────────────────────────
        ECR["🐳 Amazon ECR\n437952802980.dkr.ecr.us-east-1.amazonaws.com\n/franchise-pragma-api:latest"]:::ecr

        %% ── CloudWatch ───────────────────────────────────────────────
        CW["📊 CloudWatch Logs\nLog Group: /ecs/franchise-pragma-dev-api\nRetención: 7 días\nDriver: awslogs"]:::cw

        %% ── IAM ──────────────────────────────────────────────────────
        subgraph IAM["🔐 IAM Roles"]
            ExecRole["Execution Role\nfranchise-pragma-dev-ecs-execution-role\nPolicy: AmazonECSTaskExecutionRolePolicy\n(pull ECR + escribir logs)"]:::iam
            TaskRole["Task Role\nfranchise-pragma-dev-ecs-task-role\n(permisos de la app en runtime)"]:::iam
        end
    end

    %% ─── MongoDB Atlas (externo) ─────────────────────────────────────────
    MongoDB[("🍃 MongoDB Atlas\nfranchise-db.fx23h97.mongodb.net\nBase: test\nVar: SPRING_DATA_MONGODB_URI")]:::mongo

    %% ─── Flujo de tráfico ────────────────────────────────────────────────
    Internet -- "HTTP :80" --> ALB
    ALB -- "forward" --> TG
    TG -- "HTTP :8080" --> Task1
    TG -- "HTTP :8080" --> Task2

    %% ─── ECR pull ────────────────────────────────────────────────────────
    Task1 -. "docker pull" .-> ECR
    Task2 -. "docker pull" .-> ECR

    %% ─── Logs ────────────────────────────────────────────────────────────
    Task1 -- "awslogs" --> CW
    Task2 -- "awslogs" --> CW

    %% ─── MongoDB ─────────────────────────────────────────────────────────
    Task1 -- "MongoDB URI\n(env var)" --> MongoDB
    Task2 -- "MongoDB URI\n(env var)" --> MongoDB

    %% ─── IAM assume ──────────────────────────────────────────────────────
    ExecRole -. "sts:AssumeRole\n(startup)" .-> Task1
    ExecRole -. "sts:AssumeRole\n(startup)" .-> Task2
    TaskRole -. "sts:AssumeRole\n(runtime)" .-> Task1
    TaskRole -. "sts:AssumeRole\n(runtime)" .-> Task2
```

---

## Resumen de recursos desplegados

| Módulo | Recurso AWS | Nombre / Valor |
|--------|-------------|----------------|
| **networking** | VPC | Default VPC |
| **networking** | Subnets | 1 por AZ (us-east-1a, us-east-1b) |
| **networking** | Security Group ALB | Ingress TCP :80 from 0.0.0.0/0 |
| **networking** | Security Group API | Ingress TCP :8080 from SG ALB |
| **alb** | Application Load Balancer | `franchise-pragma-dev-alb` (internet-facing) |
| **alb** | Listener | HTTP :80 → forward |
| **alb** | Target Group | `franchise-pragma-dev-tg` — health check: `/actuator/health` |
| **ecs** | ECS Cluster | `franchise-pragma-dev-cluster` |
| **ecs** | Task Definition | Fargate · 512 CPU · 1024 MB · awsvpc |
| **ecs** | ECS Service | `franchise-pragma-dev-service` · desired_count=2 |
| **ecs** | CloudWatch Log Group | `/ecs/franchise-pragma-dev-api` · 7 días |
| **iam** | Execution Role | `franchise-pragma-dev-ecs-execution-role` |
| **iam** | Task Role | `franchise-pragma-dev-ecs-task-role` |
| **externo** | ECR | `437952802980.dkr.ecr.us-east-1.amazonaws.com/franchise-pragma-api:latest` |
| **externo** | MongoDB Atlas | `franchise-db.fx23h97.mongodb.net` |

