<!-- <p align="center">
  <img src="https://imgur.com/aVS7qno.png" alt="iPub Banner" width="100%" />
</p> -->

<h1 align="center">📱 iPub App</h1>
<p align="center">Gerenciador moderno de membros de ministérios e departamentos da igreja</p>

<!-- <p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1EB980?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/MVVM-673AB7?style=for-the-badge"/>
</p> -->


<!-- <h1 align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://imgur.com/3q2pp6V.png">
    <img src="https://imgur.com/aVS7qno.png" width="100%" alt="iPub App Banner">
  </picture>
</h1> -->



# 📱 iPub App — Gestão de Membros da Igreja

Um aplicativo Android desenvolvido em **Kotlin + Jetpack Compose** para organização de membros, departamentos e aniversários.  
Desenvolvido com arquitetura **MVVM**, banco de dados local e interface moderna utilizando **Material 3**.

---

<p align="center">

  <!-- Linguagem -->
  <img src="https://img.shields.io/badge/Kotlin-v1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>

  <!-- Android -->
  <img src="https://img.shields.io/badge/Android-API%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>

  <!-- Jetpack Compose -->
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>

  <!-- Architecture -->
  <img src="https://img.shields.io/badge/Architecture-MVVM-673AB7?style=for-the-badge"/>

  <!-- Build tools -->
  <img src="https://img.shields.io/badge/Gradle-8.2-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>

  <!-- Status -->
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-F7B500?style=for-the-badge"/>

</p>


## ✨ Funcionalidades

- ✔ Cadastro de membros  
- ✔ Listagem de todos os membros  
- ✔ Deleção de membros  
- ✔ Seleção de departamento por lista  
- ✔ Seleção de data com DatePicker  
- ✔ UI moderna com Material 3  
- ✔ Armazenamento em banco de dados (Room ou Firestore – conforme implementação)  

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Material Design 3**
- **ViewModel + StateFlow**
- **Room Database** (ou Firestore se estiver usando)
- **MVVM Pattern**
- **Android Studio**

---

## 📸 Telas (Screenshots)

> *Apresentação da interface*   
>  
> ![Tela inicial](./screenshots/screen1.png)  
> ![Cadastro](./screenshots/screen2.png)

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM**:

/data

├─ Member.kt
├─ MemberDao.kt
├─ MemberDatabase.kt
└─ MemberRepository.kt

/viewmodel
└─ MemberViewModel.kt

/ui
├─ MembersScreen.kt
├─ AddMemberScreen.kt
└─ components/
└─ MemberItem.kt


---

## 🚀 Como executar o projeto

1. Clone o repositório:
```bash
git clone https://github.com/SEU_USUARIO/ipub_app.git


data class Member(
    val id: String = "",
    val name: String,
    val role: String,
    val department: String = "",
    val birthday: String = ""
)

▶ Próximos Passos (Roadmap)

 Tela de detalhes do membro

 Edição de membros

 Filtro por departamento

 Pesquisa por nome

 Agrupamento por mês de aniversário

 Tema dark/light automático

 Sincronização com Firebase

🤝 Contribuições

Sinta-se à vontade para abrir Issues ou enviar Pull Requests.

📄 Licença

Este projeto está sob a licença MIT, permitindo uso livre e modificações.

👤 Autor

Isaque Prates
📍 Vitória da Conquista – BA
💼 Desenvolvedor Mobile & Fullstack


---

# 🎁 Quer que eu gere também:

📌 O banner do projeto (aquele topo bonito do GitHub)  
📌 Badges personalizadas (Kotlin, Android, Compose)  
📌 Um `.gitignore` perfeito para Android  
📌 README com tema dark/light automático (para GitHub)

É só pedir!
