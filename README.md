# File Sharing Application

A simple, no-login file sharing web application built with Spring Boot backend and HTML/JavaScript frontend.

## Features

- **No Authentication Required** - Upload and share files instantly without creating an account
- **Drag & Drop Upload** - Modern file upload interface with drag and drop support
- **Multiple File Upload** - Upload multiple files at once
- **File Management** - View, download, and delete uploaded files
- **Real-time Dashboard** - Live dashboard showing all uploaded files
- **File Type Icons** - Visual file type identification
- **Responsive Design** - Works on desktop and mobile devices

## Screenshots

### File Upload Page
![Upload Page](https://github.com/user-attachments/assets/54844427-8a36-48cd-a8fd-15379768a492)

### File Dashboard
![Dashboard](https://github.com/user-attachments/assets/10afb2ce-118d-4a21-bec3-12f552da78cd)

## Technology Stack

### Backend
- **Spring Boot 3.2.0** - Java web framework
- **Maven** - Build tool and dependency management
- **Java 17** - Programming language
- **Embedded Tomcat** - Web server

### Frontend  
- **HTML5** - Markup
- **Bootstrap 5.3.0** - CSS framework
- **Vanilla JavaScript** - Interactive functionality
- **Fetch API** - HTTP requests

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/files/health` | Service health check |
| POST | `/api/files/upload` | Upload file (multipart form data) |
| GET | `/api/files/list` | List all uploaded files |
| GET | `/api/files/download/{fileName}` | Download specific file |
| DELETE | `/api/files/delete/{fileName}` | Delete specific file |

## Setup and Installation

### Prerequisites
- Java 17 or later
- Maven 3.6 or later

### Running the Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/GauravGupta899/UserAuthentication.git
   cd UserAuthentication
   ```

2. **Start the Spring Boot backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8000`

3. **Serve the frontend** (in a new terminal)
   ```bash
   cd frontend
   python3 -m http.server 8080
   ```
   The frontend will be available at `http://localhost:8080`

4. **Access the application**
   - Upload files: `http://localhost:8080/login.html`
   - View dashboard: `http://localhost:8080/dashboard.html`

## Configuration

The application can be configured by editing `backend/src/main/resources/application.properties`:

```properties
# Server port
server.port=8000

# File upload limits
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# File storage location
file.upload-dir=/tmp/uploads
```

## File Storage

- Files are stored in `/tmp/uploads` by default
- Files are prefixed with timestamp to prevent naming conflicts
- Files may be removed when the server restarts
- Maximum file size: 50MB per file

## Development

### Building the backend
```bash
cd backend
mvn clean compile
```

### Running tests
```bash
cd backend
mvn test
```

## Security Notes

- This application is designed for temporary file sharing
- No authentication or authorization is implemented
- Files are accessible to anyone with the URL
- Consider implementing authentication for production use
- Files are not encrypted at rest

## Browser Support

- Chrome 60+
- Firefox 60+  
- Safari 12+
- Edge 79+

## License

This project is open source and available under the [MIT License](LICENSE).
