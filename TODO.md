# Disable Spring Security - Progress Tracking

## Steps from Plan:
- [x] Edit backend/pom.xml: Remove spring-boot-starter-security and spring-security-test dependencies ✅ (done, pom updated)
- [ ] Fix compile errors in security files
  - Remove security package files or comment out
  - Remove @PreAuthorize from controllers
  - Remove security exception handling
- [ ] Run mvn clean compile in backend
- [ ] Restart server: cd backend; mvn spring-boot:run
- [ ] Test frontend API calls (no auth)

Current status: POM updated, server failed to restart due to compile errors in SecurityConfig.java etc. Next: fix code.

