# 💱 MoneyConvertisseur

Bienvenue sur **MoneyConvertisseur**, une application Android développée en **Kotlin** avec **Jetpack Compose** et **Firebase**, permettant aux utilisateurs de :

- Créer un compte ou se connecter via email/mot de passe
- Convertir des devises (EUR, USD, GBP)
- Sauvegarder leur dernier montant converti dans **Firebase Realtime Database**

---

## 🚀 Fonctionnalités

- 🔐 **Authentification** : Connexion et inscription avec Firebase Authentication  
- 💱 **Convertisseur de devises** : Conversion simple entre EUR, USD et GBP avec des taux fixes  
- ☁️ **Sauvegarde cloud** : Le dernier montant saisi est stocké en ligne via Firebase Realtime Database  
- 🔁 **Lecture en temps réel** : Les données sont automatiquement mises à jour à chaque modification  

---

## 🛠 Technologies utilisées

- Kotlin avec Jetpack Compose  
- Firebase Authentication  
- Firebase Realtime Database  
- Material3 (Compose UI)

---

## 📂 Structure du projet

- `AuthNavigator.kt` : Gère l'interface de connexion et d'inscription  
- `HomeScreen.kt` : Affiche les informations utilisateur, la section convertisseur et les boutons d’action  
- `RealtimeDatabaseSection.kt` : Composant dédié à l’affichage du message sauvegardé depuis Firebase  


---

## 🔧 Taux de conversion utilisés

| De  | À   | Taux |
|-----|-----|------|
| EUR | USD | 1.08 |
| USD | EUR | 0.93 |
| EUR | GBP | 0.86 |
| GBP | EUR | 1.16 |

---

## 🔒 Authentification

Les comptes sont gérés via **FirebaseAuth**.  
Un utilisateur peut :

- S’inscrire avec un email et un mot de passe  
- Se connecter et rester connecté  
- Se déconnecter manuellement via l’interface  

---

## 🧠 Remarques techniques

- L’authentification et les appels à Firebase sont gérés de manière **asynchrone avec coroutines**  
- La conversion est **locale et instantanée** (pas de requêtes API externes)  
- Les taux sont **statiques** et définis dans le code  

---

## ✅ À faire (éventuellement)

- Mettre à jour les taux de change dynamiquement via une API  
- Ajouter d'autres devises  
- Améliorer la gestion des erreurs  
- Implémenter un historique des conversions  

---

## 👥 Auteurs

Projet réalisé par :

- **Maxime Desclaux**  
- **Cédric Hu**  
- **Wilhem Ho**  
- **Florine Saidi**
