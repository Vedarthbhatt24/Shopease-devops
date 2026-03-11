# ShopEase DevOps Project - Complete Setup Guide
## Vedarth Bhatt | KSET, Dr. KP Patel Global University | 2024-25

---

## 🗂️ PROJECT OVERVIEW

**Project:** ShopEase - E-Commerce Platform  
**DevOps Pipeline:** Git → Jenkins → Maven → SonarQube → Nexus → Ansible → Tomcat (Linux VM)

```
Developer pushes code to Git
        ↓
Jenkins detects change (webhook)
        ↓
Maven: Build + Unit Tests
        ↓
SonarQube: Code Quality Analysis
        ↓
Maven: Package WAR artifact
        ↓
Nexus: Store artifact
        ↓
Ansible: Deploy to Linux VM (Tomcat)
        ↓
App live at http://VM-IP:8080/shopease
```

---

## 💻 WHAT YOU NEED

| Item | Requirement |
|------|------------|
| Windows PC RAM | 8 GB (6 GB free while running VMs) |
| VirtualBox | 7.0+ (free download) |
| Internet | For downloads only |

---

## STEP 1 — INSTALL TOOLS ON WINDOWS (HOST MACHINE)

### 1.1 Install Java 17
1. Download from: https://adoptium.net/temurin/releases/?version=17
2. Install → choose "Add to PATH" and "Set JAVA_HOME"
3. Verify: Open CMD → `java -version` → should show 17.x

### 1.2 Install Maven 3.9
1. Download Binary ZIP from: https://maven.apache.org/download.cgi
2. Extract to: `C:\maven`
3. Add to PATH: System Properties → Environment Variables → PATH → add `C:\maven\bin`
4. Verify: CMD → `mvn -version`

### 1.3 Install Git
1. Download from: https://git-scm.com/download/win
2. Install with default options
3. Verify: CMD → `git --version`

### 1.4 Copy Maven settings.xml
1. Copy `nexus-config/settings.xml` from this project
2. Paste to: `C:\Users\YOUR_USERNAME\.m2\settings.xml`
   (Create `.m2` folder if it doesn't exist)

---

## STEP 2 — SET UP VirtualBox VMs

You need **2 VMs**: one for the DevOps tools, one for deployment.

### VM 1 — DevOps Server (Jenkins + SonarQube + Nexus)
| Setting | Value |
|---------|-------|
| OS | Ubuntu 22.04 LTS |
| RAM | 3 GB |
| CPU | 2 cores |
| Storage | 30 GB |
| Network | NAT + Host-Only Adapter |

### VM 2 — Deploy Server (Tomcat — where app runs)
| Setting | Value |
|---------|-------|
| OS | Ubuntu 22.04 LTS |
| RAM | 1.5 GB |
| CPU | 1 core |
| Storage | 20 GB |
| Network | NAT + Host-Only Adapter |

### Download Ubuntu 22.04:
https://releases.ubuntu.com/jammy/ubuntu-22.04.6-live-server-amd64.iso

### Host-Only Network Setup (VirtualBox):
1. VirtualBox → File → Host Network Manager
2. Create network: `192.168.56.0/24`
3. Assign Host-Only adapter to both VMs
   - VM1 will get IP: `192.168.56.100`
   - VM2 will get IP: `192.168.56.101`

### After Ubuntu install on each VM:
```bash
# Set static IP (do this on each VM)
sudo nano /etc/netplan/00-installer-config.yaml

# Add:
network:
  version: 2
  ethernets:
    enp0s8:           # Host-Only interface name (check with: ip a)
      dhcp4: no
      addresses: [192.168.56.100/24]   # Use .101 for VM2

sudo netplan apply
```

---

## STEP 3 — SET UP JENKINS (on VM1: 192.168.56.100)

```bash
# SSH into VM1 from Windows:
# Open CMD: ssh devops@192.168.56.100

# Install Java 17
sudo apt update
sudo apt install -y openjdk-17-jdk

# Install Jenkins
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update
sudo apt install -y jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

**Access Jenkins:** Open browser on Windows → `http://192.168.56.100:8080`

**Jenkins Initial Setup:**
1. Enter the admin password from above
2. Click "Install suggested plugins"
3. Create admin user (e.g., admin / admin123)
4. Save and Finish

**Install Additional Jenkins Plugins:**
- Go to: Manage Jenkins → Plugins → Available
- Search and install: `SonarQube Scanner`, `Nexus Artifact Uploader`, `Pipeline`

**Configure Maven in Jenkins:**
- Manage Jenkins → Tools → Maven → Add Maven
- Name: `Maven-3.9` → Install automatically → Version 3.9.6

**Configure JDK in Jenkins:**
- Manage Jenkins → Tools → JDK → Add JDK
- Name: `JDK-17` → JAVA_HOME: `/usr/lib/jvm/java-17-openjdk-amd64`

---

## STEP 4 — SET UP SONARQUBE (on VM1)

```bash
# Install Docker (easiest way to run SonarQube)
sudo apt install -y docker.io
sudo usermod -aG docker $USER
newgrp docker

# Run SonarQube (Community Edition - free)
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:community

# Wait 2-3 minutes for startup
docker logs -f sonarqube   # Watch logs until you see "SonarQube is operational"
```

**Access SonarQube:** `http://192.168.56.100:9000`  
Default login: `admin` / `admin` → change password on first login

**Create SonarQube Token for Jenkins:**
1. SonarQube → My Account → Security → Generate Token
2. Name: `jenkins-token` → Generate → **COPY THE TOKEN**

**Configure SonarQube in Jenkins:**
1. Manage Jenkins → System → SonarQube servers
2. Add: Name=`SonarQube`, URL=`http://192.168.56.100:9000`
3. Add token as Secret Text credential

---

## STEP 5 — SET UP NEXUS (on VM1)

```bash
# Run Nexus with Docker
docker run -d \
  --name nexus \
  -p 8081:8081 \
  -v nexus-data:/nexus-data \
  sonatype/nexus3

# Wait ~3 minutes for startup
docker logs -f nexus   # Wait for "Started Sonatype Nexus"
```

**Access Nexus:** `http://192.168.56.100:8081`

**Get initial Nexus password:**
```bash
docker exec nexus cat /nexus-data/admin.password
```

Login: `admin` / [password from above] → Set new password: `admin123`

**Create Nexus Repositories:**
1. Settings → Repositories → Create repository
2. Create: `maven-releases` (hosted) and `maven-snapshots` (hosted)
3. Create: `maven-proxy` (proxy) → Remote URL: `https://repo1.maven.org/maven2/`
4. Create: `maven-public` (group) → add all three above

**Add Nexus credentials to Jenkins:**
- Manage Jenkins → Credentials → Add
- Username: `admin`, Password: `admin123`, ID: `nexus-credentials`

---

## STEP 6 — SET UP DEPLOY SERVER WITH ANSIBLE (VM2: 192.168.56.101)

### 6.1 Install Ansible on VM1 (controller)
```bash
sudo apt install -y ansible
ansible --version
```

### 6.2 Set up SSH key authentication (VM1 → VM2)
```bash
# On VM1: Generate SSH key
ssh-keygen -t rsa -b 4096 -C "jenkins@devops" -f ~/.ssh/id_rsa -N ""

# Copy key to VM2
ssh-copy-id devops@192.168.56.101

# Test connection
ansible -i ansible/inventory/hosts.ini webservers -m ping
```
Expected output: `deploy-vm | SUCCESS => {"ping": "pong"}`

### 6.3 Run server setup playbook (installs Java + Tomcat on VM2)
```bash
cd /path/to/project
ansible-playbook ansible/setup.yml -i ansible/inventory/hosts.ini -v
```
This automatically:
- Installs OpenJDK 17
- Downloads and installs Apache Tomcat 10
- Creates tomcat systemd service
- Opens port 8080

---

## STEP 7 — PUSH CODE TO GITHUB

```bash
# On your Windows machine, in the project directory:
git init
git add .
git commit -m "feat: initial ShopEase e-commerce application"
git branch -M main

# Create repo on github.com, then:
git remote add origin https://github.com/YOUR_USERNAME/shopease-devops.git
git push -u origin main

# Create develop branch (Git Flow)
git checkout -b develop
git push -u origin develop
```

**Update Jenkinsfile:** Edit `jenkins/Jenkinsfile` → change the git URL to your repo

---

## STEP 8 — CREATE JENKINS PIPELINE

1. Jenkins → New Item → Name: `ShopEase-Pipeline` → Pipeline → OK
2. Under Pipeline: Source: `Pipeline script from SCM`
3. SCM: Git → Repository URL: your GitHub URL
4. Branch: `*/main`
5. Script Path: `jenkins/Jenkinsfile`
6. Save

### Set up GitHub Webhook (auto-trigger on push):
1. GitHub repo → Settings → Webhooks → Add webhook
2. URL: `http://192.168.56.100:8080/github-webhook/`
3. Content type: `application/json` → Just the push event → Add

---

## STEP 9 — RUN THE FULL PIPELINE

```bash
# Make a code change to trigger the pipeline:
git checkout -b feature/test-pipeline
echo "# Pipeline test" >> README.md
git add . && git commit -m "test: trigger CI pipeline"
git push origin feature/test-pipeline

# Create PR and merge to main, OR:
git checkout main && git merge feature/test-pipeline && git push
```

**Watch the pipeline run** in Jenkins Blue Ocean:
`http://192.168.56.100:8080/blue`

Each stage will show:
- ✅ Checkout → Build → Tests → SonarQube → Quality Gate → Package → Nexus → Deploy

---

## STEP 10 — VERIFY EVERYTHING WORKS

| What | URL |
|------|-----|
| 🛒 ShopEase Store | http://192.168.56.101:8080/shopease/ |
| 🔧 Admin Panel | http://192.168.56.101:8080/shopease/admin (admin/admin123) |
| ⚙️ Jenkins | http://192.168.56.100:8080 |
| 📊 SonarQube | http://192.168.56.100:9000 |
| 📦 Nexus | http://192.168.56.100:8081 |

---

## 🎓 PROJECT STRUCTURE (for your report)

```
shopease-devops/
├── app/                          ← Spring Boot E-Commerce App
│   ├── pom.xml                   ← Maven build config + Nexus/SonarQube
│   └── src/
│       ├── main/java/com/vedu/shop/
│       │   ├── controller/       ← ShopController, AdminController
│       │   ├── model/            ← Product, Order, OrderItem
│       │   ├── service/          ← ProductService, OrderService
│       │   └── repository/       ← Spring Data JPA repos
│       ├── main/resources/
│       │   └── templates/        ← Thymeleaf HTML pages
│       └── test/                 ← JUnit unit tests (for SonarQube coverage)
├── jenkins/
│   └── Jenkinsfile               ← 8-stage CI/CD pipeline definition
├── ansible/
│   ├── setup.yml                 ← Server setup (Java + Tomcat)
│   ├── deploy.yml                ← App deployment playbook
│   └── inventory/hosts.ini       ← Target servers
├── sonarqube/
│   └── sonar-project.properties  ← SonarQube config
└── nexus-config/
    └── settings.xml              ← Maven → Nexus integration
```

---

## ⚡ QUICK TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| Jenkins can't connect to SonarQube | Check `docker ps` - is SonarQube running? |
| Ansible ping fails | Check SSH key: `ssh devops@192.168.56.101` |
| WAR not deploying | Check Tomcat logs: `sudo journalctl -u tomcat -f` |
| Nexus upload fails | Check credentials ID matches `nexus-credentials` in Jenkins |
| VM IP not reachable | Check Host-Only adapter in VirtualBox network settings |
| Maven build fails | Run `mvn clean install` locally first to check |

---

*This project demonstrates a complete DevOps pipeline as part of internship training at KSET, Dr. KP Patel Global University, 2024-25.*
