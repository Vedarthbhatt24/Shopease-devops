# Windows Server 2022 Setup Guide for ShopEase
## Run these steps ONCE after installing Windows Server 2022 VM

---

## STEP A — Download the Right ISO

**Windows Server 2022 Evaluation (Desktop Experience)**
URL: https://www.microsoft.com/en-us/evalcenter/evaluate-windows-server-2022
- Click: "Download the ISO"
- Fill the registration form (use any details)
- Choose: ISO, English, 64-bit
- File size: ~5.4 GB
- FREE for 180 days — no product key needed at install
- During install → choose "Windows Server 2022 Standard (Desktop Experience)"
  ← IMPORTANT: Pick Desktop Experience, NOT Server Core

---

## STEP B — VirtualBox VM Settings for Windows Server 2022

| Setting       | Value                          |
|---------------|-------------------------------|
| RAM           | 2500 MB (2.5 GB)              |
| CPU           | 2 cores                       |
| Storage       | 40 GB (dynamic)               |
| Network 1     | NAT (for internet)            |
| Network 2     | Host-Only (192.168.56.x)      |
| Video Memory  | 128 MB                        |

**VirtualBox Guest Additions:**
After Windows install → Devices menu → Insert Guest Additions CD
→ Run VBoxWindowsAdditions.exe inside the VM
→ This gives you better resolution and shared clipboard

---

## STEP C — After Windows Server 2022 Installs

### C1. Set a static IP on the Host-Only adapter
1. Open Network Connections (ncpa.cpl)
2. Right-click the Host-Only adapter → Properties
3. IPv4 → Use the following IP:
   - IP: 192.168.56.102
   - Subnet: 255.255.255.0
   - Gateway: (leave blank)

### C2. Install Java 17 on Windows Server
1. Download: https://adoptium.net/temurin/releases/?version=17
   → Windows x64 .msi installer
2. Install → check "Set JAVA_HOME variable" and "Add to PATH"
3. Open PowerShell → verify: `java -version`

### C3. Enable WinRM (lets Ansible connect to Windows)
Open PowerShell as Administrator and run:

```powershell
# Enable WinRM service
Enable-PSRemoting -Force

# Allow basic authentication
winrm set winrm/config/service/Auth '@{Basic="true"}'
winrm set winrm/config/service '@{AllowUnencrypted="true"}'
winrm set winrm/config/client '@{AllowUnencrypted="true"}'

# Open firewall for WinRM
netsh advfirewall firewall add rule name="WinRM-HTTP" `
  dir=in action=allow protocol=TCP localport=5985

# Test WinRM is working
winrm enumerate winrm/config/listener
```

### C4. Install IIS manually (optional - Ansible does this too)
If you want to verify IIS works before running Ansible:
1. Server Manager → Add Roles and Features
2. Role: Web Server (IIS)
3. Features: Keep defaults + add "URL Rewrite" if available
4. Install → Finish
5. Open browser inside VM: http://localhost → should show IIS default page

---

## STEP D — Enable Ansible to connect from Ubuntu VM

On your Ubuntu VM (VM1 - Jenkins server), install Windows modules:
```bash
pip3 install pywinrm
ansible-galaxy collection install ansible.windows
```

Test connection from Ubuntu to Windows:
```bash
ansible -i ansible/inventory/hosts-windows.ini windows -m win_ping
```
Expected: `winserver | SUCCESS => {"ping": "pong"}`

---

## STEP E — Run the Deployment

From your Ubuntu VM (after a successful Jenkins build):
```bash
# First time only - set up IIS and configure server:
ansible-playbook ansible/deploy-iis.yml \
  -i ansible/inventory/hosts-windows.ini -v

# Update Jenkinsfile to call this instead of deploy.yml
```

---

## STEP F — Access Your App

After deployment:
| What | URL |
|------|-----|
| ShopEase via IIS (port 80) | http://192.168.56.102/ |
| Admin Panel | http://192.168.56.102/admin |
| Direct Spring Boot | http://192.168.56.102:8080/ |
| IIS Manager | Open on Windows Server VM |
| Windows Services | services.msc → find "ShopEaseApp" |

---

## STEP G — Updating Jenkinsfile for IIS Deploy

Change Stage 8 in your Jenkinsfile from:
```groovy
sh 'ansible-playbook ansible/deploy.yml -i ansible/inventory/hosts.ini'
```
To:
```groovy
sh 'ansible-playbook ansible/deploy-iis.yml -i ansible/inventory/hosts-windows.ini'
```

---

## RAM Management Tips for i3 + 8GB

Since RAM is limited, follow this workflow:

1. Start VM1 (Ubuntu - Jenkins) → RAM: 3GB
2. Run the Jenkins pipeline (build + test + sonar + nexus)
3. Pipeline auto-triggers Ansible which deploys to VM2
4. VM2 (Windows Server) only needs to be ON — it doesn't need much RAM
   during the pipeline, only during the actual deploy stage

**Rule:** Never run SonarQube analysis while doing anything heavy on VM2.
SonarQube alone uses 1.5GB — do one thing at a time.

---

## Quick Troubleshooting

| Problem | Fix |
|---------|-----|
| Ansible can't connect to Windows | Re-run WinRM setup commands in PowerShell |
| IIS shows 502 Bad Gateway | Spring Boot not started yet, wait 30s more |
| java not found on Windows Server | Re-check JAVA_HOME in System Variables |
| Port 80 blocked | Run: `netsh advfirewall firewall add rule name="HTTP" dir=in action=allow protocol=TCP localport=80` |
| ShopEase service not starting | Check: `Get-EventLog -LogName Application -Source "ShopEaseApp" -Newest 10` |
