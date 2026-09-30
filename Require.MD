# Title: Login & Registration System

## AI Test ( After completing this task, please notify the interviewer → the interviewer will switch your access to View)

- Estimated time to complete with AI assistance: 30 minutes
- You are free to use any **AI tools** (such as ChatGPT, Claude, Cursor, etc.) during the task.

---

### Description:

Please use **Java 17 and Spring Boot** to develop a **user authentication system (login & registration)** for a website.

The system must support:

- Login using **Email + Password**

---

### Email + Password Login:

1. Use `email` and `password` to **register an account**
2. Store account data in **memory (in-memory storage)**
3. Passwords must be **securely encrypted**
4. Validate the email and password during login

### Example Data Format:

Format: (`application/x-www-form-urlencoded`)

**Request:**

```json
{
  "email": "test@example.com",
  "password": "mypassword"
}
```

Response:

Register User (POST /register)

```json
User registered successfully.
```

---

### Login (POST /login)

**Request:**

```json
{
  "email": "test@example.com",
  "password": "mypassword"
}
```

Response:

```
Login successful
```

---

### Evaluation Criteria  :

#### Code Structure (30%)

- Proper Spring Boot structure (Controller, Service, etc.)
- Clear separation of concerns
- Clean and readable code
- Proper use of annotations and project organization

#### Functionality & Problem Solving (30%)

- Registration and login work correctly
- Secure password handling (e.g., BCrypt)
- Handles basic edge cases (invalid login, duplicate email)

#### AI Prompting & Usage (30%)

- Writes clear and effective prompts
- Iterates/improves prompts when needed
- Uses AI output critically (not blind copy-paste)

#### Validation & Error Handling (10%)

- Basic input validation (email/password)
- Clear and meaningful error responses