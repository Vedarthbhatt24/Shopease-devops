# ShopEase - DevOps Internship Project
### Vedarth Bhatt | Enrollment: 2201201138 | KSET, KPGU | 2024-25

---

## What is this?

**ShopEase** is a fully functional e-commerce web application deployed through a complete DevOps pipeline. Every tool covered in the internship training is used:

| Tool | How it's used |
|------|--------------|
| **Git + Git Flow** | Source control, `feature/*` branches, merge to `main` triggers pipeline |
| **Maven** | Build, compile, test, package WAR, deploy to Nexus |
| **SonarQube** | Static code analysis, quality gate (blocks deploy if code quality fails) |
| **Nexus** | Stores versioned WAR artifacts after every successful build |
| **Jenkins** | Orchestrates the full 8-stage CI/CD pipeline via Jenkinsfile |
| **Ansible** | Deploys the WAR to Tomcat on the Linux VM automatically |
| **IIS / Tomcat** | Hosts the running web application |

## Quick Start

1. Read **`docs/SETUP_GUIDE.md`** for full step-by-step instructions
2. The app is a Spring Boot Java application — needs Java 17 + Maven 3.9
3. To run locally (no DevOps pipeline): `cd app && mvn spring-boot:run`
4. Open browser: `http://localhost:8080`

## App Features

- 🛍️ Product catalog with categories and search
- 🛒 Shopping cart (session-based)
- 💳 Checkout and order placement
- 📦 Order tracking by email
- 🔧 Admin panel (product + order management)

**Admin login:** `http://localhost:8080/login` → admin / admin123

## Pipeline Flow

```
git push → Jenkins webhook triggered
         → mvn compile
         → mvn test (JUnit)
         → SonarQube analysis + quality gate
         → mvn package (produces shopease.war)
         → Upload WAR to Nexus
         → Ansible deploys to Tomcat VM
         → App available at http://VM-IP:8080/shopease
```
