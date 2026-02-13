# SWP Codebase AI Instructions

This is a **Jakarta EE 6.0 web application** with user authentication and role-based access control (admin/member). The project is built with **Ant** and deployed as a WAR file.

## Architecture Overview

### Layered Structure
- **Controllers** (`control/`): Jakarta Servlets handling HTTP routing
  - `AuthController`: Handles `/auth/login` and `/auth/register` (POST/GET)
  - `HomeController`, `DashboardController`: Route to JSP views
- **Data Access** (`dal/`): `UserDAO` manages all database operations using direct JDBC
- **DTOs** (`dto/`): `UserDTO` transfers user data between layers (userId, username, email, role)
- **Config** (`config/DBContext.java`): JDBC connection pool for SQL Server
- **Filters** (`filter/`): `AuthFilter` enforces session-based authentication on `/home` and `/admin/*`
- **Utilities** (`util/`): `PasswordUtil` hashes passwords with SHA-256

### Critical Data Flow
1. **Login/Register**: `AuthController` → validates input → `UserDAO.login()/register()` → stores/retrieves `UserDTO` in session
2. **Protected Routes**: Request → `AuthFilter` checks session for `USER` attribute → redirects to `/auth/login` if missing
3. **Admin Access**: `AuthFilter` validates `user.getRole() == "admin"` before allowing `/dashboard`

## Database & Configuration

- **Database**: SQL Server (localhost:1433, database: `devquery`)
- **Connection**: `DBContext.getConnection()` uses `com.microsoft.sqlserver.jdbc.SQLServerDriver`
- **Credentials**: Hard-coded in `src/java/config/DBContext.java` (user: `huylq`, pass: `123`)
  - ⚠️ Change these for any production use
- **Default Role**: New users registered as `"member"` role in `UserDAO.register()`

## Key Conventions

### Session Management
- Authenticated user stored as: `session.setAttribute("USER", userDTO)`
- Authentication checked in `AuthFilter` via: `(UserDTO) session.getAttribute("USER")`
- Session timeout: 60 minutes (configured in `web.xml`)

### Password Security
- All passwords hashed with `PasswordUtil.sha256()` before DB insert
- Login compares hashed password: `PasswordUtil.sha256(rawPassword)` against `password_hash` column

### Request Routing
- Servlet path extracted via `request.getServletPath()` (not query params)
- Form parameters use `request.getParameter("fieldName")`
- Views routed via `request.getRequestDispatcher().forward()` or `response.sendRedirect()`

### Error Handling
- Validation errors set as request attribute: `request.setAttribute("error", message)`
- JSP pages display errors via `${error}`
- Controller catches all exceptions, sets error message, and forwards back to originating form

## Build & Deployment

- **Build Tool**: Apache Ant
- **Build Command**: `ant` (compiles to `build/classes/`, packages to `build/web/`)
- **Project Structure**: NetBeans project (`nbproject/`) with `build-impl.xml` for compilation
- **WAR Output**: Deploy to Tomcat 10+ (requires Jakarta EE 6.0)

## Testing & Debugging

- Test accounts: Use `/auth/register` to create test users
- Database schema: See `database/script.sql` for table definitions
- Debug DB issues: Check `DBContext.getConnection()` driver loading and connection string
- Session issues: Verify `request.getSession(false)` in `AuthFilter` (returns null if no session exists)

## Common Patterns to Follow

1. **Adding a new controller**: Extend `HttpServlet`, use `@WebServlet` annotation with URL pattern
2. **Database operations**: Instantiate `UserDAO` in servlet, use try-with-resources for connections
3. **Form validation**: Check null/empty before using parameters, set error attribute if invalid
4. **Protecting routes**: Add to `web.xml` filter-mapping or check role in filter
5. **Redirecting**: Use `response.sendRedirect(request.getContextPath() + "/path")` for external redirects
