# Stragram Backend

A social media backend API built with Spring Boot following Hexagonal Architecture (Ports and Adapters).

## Architecture

This project follows the Hexagonal Architecture pattern (also known as Ports and Adapters):

- **Domain**: The core business logic, free from framework dependencies.
- **Application**: Use cases that orchestrate the domain model.
- **Adapters**: Implementation of interfaces defined by the application.
  - **In**: Controllers, DTOs, and mappers for handling incoming requests.
  - **Out**: Repositories, security, and storage implementations.

## Technologies

- Java 17
- Spring Boot 3.5.6
- Spring Security with JWT authentication
- Spring Data JPA
- H2 Database (for development)
- PostgreSQL (for production)
- Flyway for database migrations

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
./gradlew bootRun
```

The application will start on port 8080.

### API Endpoints

#### Authentication

- `POST /auth/register` - Register a new user
- `POST /auth/login` - Login and get JWT token

#### Users

- `GET /users/{id}` - Get user profile
- `PUT /users/{id}` - Update user profile
- `DELETE /users/{id}` - Delete user
- `GET /users/search` - Search users
- `POST /users/{id}/follow` - Follow user
- `DELETE /users/{id}/follow` - Unfollow user

#### Posts

- `POST /posts` - Create a new post (multipart form with image)
- `GET /posts/{id}` - Get post by ID
- `GET /posts/user/{userId}` - Get posts by user
- `PUT /posts/{id}` - Update post
- `DELETE /posts/{id}` - Delete post

#### Likes and Favorites

- `POST /posts/{postId}/like` - Like a post
- `DELETE /posts/{postId}/like` - Unlike a post
- `POST /posts/{postId}/favorite` - Favorite a post
- `DELETE /posts/{postId}/favorite` - Unfavorite a post

#### Comments

- `POST /posts/{postId}/comments` - Add comment to post
- `GET /posts/{postId}/comments` - Get comments for post
- `PUT /comments/{id}` - Edit comment
- `DELETE /comments/{id}` - Delete comment

#### Notifications

- `GET /notifications` - Get user notifications
- `PUT /notifications/{id}/read` - Mark notification as read
- `PUT /notifications/read-all` - Mark all notifications as read

#### Admin

- `GET /admin/users` - Get all users (admin only)
- `PUT /admin/users/{id}` - Update any user (admin only)
- `DELETE /admin/users/{id}` - Delete any user (admin only)
- `PUT /admin/posts/{id}` - Update any post (admin only)
- `DELETE /admin/posts/{id}` - Delete any post (admin only)
- `DELETE /admin/comments/{id}` - Delete any comment (admin only)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── be/
│   │       └── heh/
│   │           └── stragram/
│   │               ├── StragramApplication.java
│   │               ├── adapter/
│   │               │   ├── in/
│   │               │   │   ├── web/
│   │               │   │   │   ├── controllers
│   │               │   │   │   ├── dto/
│   │               │   │   │   └── mapper/
│   │               │   │   └── security/
│   │               │   └── out/
│   │               │       ├── persistence/jpa/
│   │               │       │   ├── entity/
│   │               │       │   ├── repositories
│   │               │       │   ├── mapper/
│   │               │       │   └── adapter/
│   │               │       ├── storage/
│   │               │       └── crypto/
│   │               └── application/
│   │                   ├── domain/
│   │                   │   ├── model/
│   │                   │   ├── value/
│   │                   │   ├── service/
│   │                   │   └── exception/
│   │                   ├── port/
│   │                   │   ├── in/
│   │                   │   └── out/
│   │                   └── service/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
    └── java/
        └── be/
            └── heh/
                └── stragram/
```

## License

This project is licensed under the MIT License.
