# 📡 Stragram Backend - Routes API Messaging

## Routes REST (HTTP)

### Base URL: `http://localhost:8080/api/messages`

| Méthode | Route | Description | Auth Required |
|---------|-------|-------------|---------------|
| GET | `/conversations` | Liste toutes les conversations de l'utilisateur | ✅ JWT |
| GET | `/conversations/with/{otherUserId}` | Récupère ou crée une conversation avec un utilisateur | ✅ JWT |
| GET | `/conversations/{conversationId}/messages` | Récupère tous les messages d'une conversation | ✅ JWT |
| PUT | `/conversations/{conversationId}/read` | Marque tous les messages d'une conversation comme lus | ✅ JWT |
| GET | `/unread-count` | Récupère le nombre total de messages non lus | ✅ JWT |

---

## WebSocket Endpoints

### Connexion WebSocket
- **URL de connexion**: `ws://localhost:8080/ws`
- **Protocol**: STOMP over SockJS
- **Header d'authentification**: `Authorization: Bearer <jwt-token>`

### Destinations pour ENVOYER (Publish)

| Destination | Description | Payload |
|-------------|-------------|---------|
| `/app/chat.send` | Envoyer un message | `{ "receiverId": "uuid", "content": "text" }` |
| `/app/chat.typing` | Notifier qu'on est en train d'écrire | `"receiver-uuid"` |

### Destinations pour RECEVOIR (Subscribe)

| Destination | Description | Format reçu |
|-------------|-------------|-------------|
| `/user/queue/messages` | Recevoir les nouveaux messages | `MessageDto` (voir structure ci-dessous) |
| `/user/queue/typing` | Recevoir les notifications "en train d'écrire" | `"sender-uuid"` |

---

## 📦 Structures de données

### MessageDto
```json
{
  "id": "uuid",
  "conversationId": "uuid",
  "senderId": "uuid",
  "senderUsername": "string",
  "senderAvatarUrl": "string",
  "content": "string",
  "sentAt": "ISO8601 datetime",
  "isRead": boolean,
  "readAt": "ISO8601 datetime or null"
}
```

### ConversationDto
```json
{
  "id": "uuid",
  "otherUserId": "uuid",
  "otherUserUsername": "string",
  "otherUserAvatarUrl": "string",
  "lastMessage": MessageDto,
  "unreadCount": number,
  "createdAt": "ISO8601 datetime",
  "updatedAt": "ISO8601 datetime"
}
```

### SendMessageRequest
```json
{
  "receiverId": "uuid",
  "content": "string (max 1000 chars)"
}
```

---

## 🔄 Flux de communication

### 1. Initialisation
```
1. Utilisateur se connecte → Obtient JWT token
2. Frontend établit connexion WebSocket avec le token
3. Frontend s'abonne à /user/queue/messages
4. Frontend s'abonne à /user/queue/typing
```

### 2. Envoi de message
```
Frontend → /app/chat.send → Backend
Backend → Sauvegarde en DB
Backend → /user/queue/messages → Destinataire (WebSocket)
Backend → /user/queue/messages → Expéditeur (confirmation)
```

### 3. Réception de message
```
Backend → /user/queue/messages → Frontend
Frontend → Affiche le message
Frontend → Appelle PUT /conversations/{id}/read (si conversation ouverte)
```

### 4. Indicateur de saisie
```
Frontend → /app/chat.typing → Backend
Backend → /user/queue/typing → Destinataire
Frontend destinataire → Affiche "En train d'écrire..."
```

---

## 🚀 Utilisation dans React

### Configuration minimale

```javascript
// 1. Installation
npm install sockjs-client @stomp/stompjs axios

// 2. Connexion WebSocket
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:8080/ws');
const client = new Client({
  webSocketFactory: () => socket,
  connectHeaders: {
    Authorization: `Bearer ${yourJwtToken}`
  },
  onConnect: () => {
    // S'abonner aux messages
    client.subscribe('/user/queue/messages', (message) => {
      const data = JSON.parse(message.body);
      console.log('New message:', data);
    });
  }
});
client.activate();

// 3. Envoyer un message
client.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    receiverId: 'uuid-destinataire',
    content: 'Hello!'
  })
});

// 4. Récupérer les conversations (REST)
const response = await axios.get('http://localhost:8080/api/messages/conversations', {
  headers: { Authorization: `Bearer ${yourJwtToken}` }
});
```

---

## ⚙️ Configuration Backend

### application.properties
```properties
# JWT Configuration (déjà existant)
jwt.secret=your-secret-key
jwt.expiration=86400000

# Database (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/stragram
spring.datasource.username=your-username
spring.datasource.password=your-password

# Flyway
spring.flyway.enabled=true

# WebSocket (pas de config spéciale nécessaire)
```

---

## 🧪 Test avec Postman/Thunder Client

### 1. Tester REST API

```bash
# Récupérer les conversations
GET http://localhost:8080/api/messages/conversations
Headers:
  Authorization: Bearer <your-jwt-token>

# Créer/récupérer conversation
GET http://localhost:8080/api/messages/conversations/with/e4b5c6d7-8901-4567-89ab-cdef01234567
Headers:
  Authorization: Bearer <your-jwt-token>

# Récupérer messages
GET http://localhost:8080/api/messages/conversations/{conversationId}/messages
Headers:
  Authorization: Bearer <your-jwt-token>
```

### 2. Tester WebSocket (Utiliser un client WebSocket)

```javascript
// Dans la console du navigateur ou un outil comme wscat
const socket = new WebSocket('ws://localhost:8080/ws');
// Puis établir connexion STOMP
```

---

## 📊 Base de données

### Tables créées
1. **conversations**: Stocke les conversations entre utilisateurs
2. **conversation_participants**: Table de liaison (conversation ↔ utilisateurs)
3. **messages**: Stocke tous les messages

### Index créés pour performance
- `idx_messages_conversation_id`
- `idx_messages_sender_id`
- `idx_messages_sent_at`
- `idx_messages_is_read`
- `idx_conversation_participants_participant_id`
- `idx_conversations_updated_at`

---

## 🎨 Fonctionnalités implémentées

✅ Envoi/réception de messages en temps réel  
✅ Historique des conversations  
✅ Messages non lus avec compteur  
✅ Marquer conversations comme lues  
✅ Indicateur "en train d'écrire"  
✅ Authentification JWT sur WebSocket  
✅ Reconnexion automatique  
✅ Support SockJS (fallback si WebSocket indisponible)  
✅ Messages persistés en base de données  
✅ Horodatage des messages (envoi + lecture)  

---

## 🔐 Sécurité

- ✅ Authentification JWT obligatoire
- ✅ Vérification que l'utilisateur fait partie de la conversation
- ✅ Validation des entrées (taille des messages, etc.)
- ✅ Protection contre l'accès non autorisé aux messages d'autrui
- ✅ CORS configuré pour WebSocket

---

## 📝 Notes importantes

1. **Une conversation = 2 utilisateurs** : Le système est conçu pour des conversations 1-to-1
2. **Messages confirmés** : L'expéditeur reçoit aussi le message via WebSocket (confirmation d'envoi)
3. **Persistance** : Tous les messages sont sauvegardés en BDD même si le destinataire est hors ligne
4. **Ordre chronologique** : Les messages sont toujours retournés triés par date d'envoi
5. **Performance** : Index optimisés pour les requêtes fréquentes
