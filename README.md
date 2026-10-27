<!-- Banners comentados caso queira ativar no futuro -->
<!-- <p align="center">
  <img src="https://imgur.com/aVS7qno.png" alt="iPub Banner" width="100%" />
</p> -->

<h1 align="center">📱 iPub App</h1>
<p align="center">Gerenciador moderno de membros, aniversariantes e departamentos da igreja</p>

---

<p align="center">

  <!-- Linguagem -->
  <img src="https://img.shields.io/badge/Kotlin-v1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>

  <!-- Android -->
  <img src="https://img.shields.io/badge/Android-API%2026+-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>

  <!-- Jetpack Compose -->
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>

  <!-- Firebase -->
  <img src="https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>

  <!-- Architecture -->
  <img src="https://img.shields.io/badge/Architecture-MVVM-673AB7?style=for-the-badge"/>

  <!-- Build tools -->
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>

  <!-- Status -->
  <img src="https://img.shields.io/badge/Status-Concluído-3DDC84?style=for-the-badge"/>

</p>

---

## ✨ Funcionalidades

- ✔ **Gestão Completa de Membros:** Cadastro, edição, busca e exclusão de membros.
- ✔ **Autenticação Segura:** Autenticação via Firebase Auth (Login e Logout).
- ✔ **Nuvem & Sincronização:** Banco de dados em tempo real com Firebase Firestore.
- ✔ **Filtro por Departamentos:** Organização visual de membros agrupados por ministérios e cargos.
- ✔ **Calendário de Aniversários:** Visualização de aniversariantes do dia e navegação interativa.
- ✔ **Notificações Diárias Inteligentes:** Verificação automática em segundo plano via **WorkManager**, com barreira idempotente (`SharedPreferences`) para evitar alertas duplicados no mesmo dia.
- ✔ **Navegação via Notificação (Deep Linking):** Clique direto na notificação que abre a tela de detalhes dos aniversariantes (`BirthdayDetailScreen`).
- ✔ **Navegação Fluida:** `NavigationSuiteScaffold` com reset instantâneo de pilha ao tocar no ícone Home.
- ✔ **UI/UX Moderna:** Interface responsiva desenvolvida 100% com **Jetpack Compose** e **Material 3**.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

- **Linguagem:** Kotlin
- **Interface UI:** Jetpack Compose + Material Design 3
- **Arquitetura:** MVVM (Model-View-ViewModel) + StateFlow
- **Backend & Cloud:** Firebase Firestore (Database) & Firebase Auth
- **Tarefas em Segundo Plano:** Android WorkManager
- **Navegação & Scaffolding:** Navigation Compose & Adaptive Navigation Suite
- **Notificações:** Android Notification Manager + PendingIntents com extras
- **Injeção de Plugins:** Gradle Version Catalog (`libs.plugins`) + Google Services Plugin

---

## 🏗️ Estrutura do Projeto (Arquitetura MVVM)

| Pacote / Arquivo | Descrição |
| :--- | :--- |
| **`data/Member.kt`** | Modelo de dados do membro |
| **`viewmodel/MemberViewModel.kt`** | Gerenciamento de estado (`StateFlow`) e integração Firestore |
| **`notification/BirthdayNotificationHelper.kt`** | Canal de notificação e construção de mensagens com Deep Link |
| **`notification/BirthdayNotificationWorker.kt`** | Worker executado via WorkManager com idempotência via `SharedPreferences` |
| **`notification/BirthdayScheduler.kt`** | Agendamento do ciclo de verificação diária |
| **`ui/HomeScreen.kt`** | Calendário principal e navegação |
| **`ui/BirthdayDetailScreen.kt`** | Tela de detalhes com aniversariantes do dia selecionado |
| **`ui/MembersScreen.kt`** | Listagem, busca, edição e deleção de membros |
| **`ui/DepartmentMembersScreen.kt`** | Exibição de membros agrupados por ministério/departamento |
| **`MainActivity.kt`** | Ponto de entrada com `NavigationSuiteScaffold`, tratamento de `Intent` e navegação |

---

## 🚀 Como Executar o Projeto

1. **Clone o repositório:**

git clone https://github.com/isaqueprates/ipub_app.git

2. **Configuração do Firebase:**
- Adicione o seu arquivo `google-services.json` dentro da pasta `app/`.
- Certifique-se de que o Firebase Auth (Email/Senha) e o Cloud Firestore estão ativos no console do Firebase.

3. **Compilação:**
- Abra o projeto no **Android Studio**.
- Sincronize as dependências do Gradle (**Sync Project with Gradle Files**).
- Execute no emulador ou dispositivo físico com Android 8.0 (API 26) ou superior.

---

## 📦 Assinatura e Distribuição (Release)

O projeto está configurado com um fluxo oficial de *Release*:
- Chave Keystore (`.jks`) configurada no `build.gradle.kts` (`signingConfigs`).
- Versionamento automático preparado para atualizações externas (`versionCode` e `versionName`).

---

## 👤 Autor

**Isaque Prates Silva**  
📍 Vitória da Conquista – BA  
💼 Desenvolvedor Mobile & Fullstack

---

## 📄 Licença

Este projeto está sob a licença MIT. Sinta-se livre para usar e colaborar!