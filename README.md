
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
