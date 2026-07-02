Usage

This small tool helps test MySQL connectivity from Render.

1. In Render create a new Web Service:
   - Environment: Docker
   - Docker context / Root Directory: tools/render-db-check
   - Dockerfile path: Dockerfile
   - (Optional) branch: render

2. In Render Service -> Environment variables, set:
   - DB_HOST (e.g. mysql-host.o2switch.net)
   - DB_PORT (default 3306)
   - DB_USER
   - DB_PASSWORD
   - DB_NAME

3. Deploy the service. Check the logs: the container will run the check and print either "Connection OK" or "Connection FAILED" plus verbose mysql output.

Notes:
- This service is intended temporary: after the test you can delete the service.
- Do NOT commit real secrets in repo; use Render env vars or Render Secrets.
