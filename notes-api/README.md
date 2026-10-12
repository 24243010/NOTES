# Notes API

A small Spring Boot REST API for creating, listing, reading, updating, and deleting notes. Notes are held in memory, so they are lost when the container is removed or restarted.

## Requirements

- Docker Desktop installed and running
- PowerShell

The Docker build compiles the application and runs its automated tests before creating the runtime image. Java and Maven do not need to be installed on the host.

## Build and run

Open PowerShell in this folder (the folder containing this `README.md`) and run:

```powershell
docker build -t notes-api:1.0 .
docker run --name notes-api -d -p 8080:8080 notes-api:1.0
```

Wait for Spring Boot to start, then confirm that the container is running and inspect its startup log:

```powershell
docker ps --filter "name=notes-api"
docker logs notes-api
```

The API listens at `http://localhost:8080`. An empty notes list is `[]`:

```powershell
Invoke-RestMethod http://localhost:8080/api/notes
```

## Verify the notes API

Run this PowerShell block to create a note, read it, update it, list notes, and delete it. Each step checks the returned data and stops with an error if a request fails.

```powershell
$baseUrl = "http://localhost:8080/api/notes"
$createBody = @{
    title = "Docker smoke test"
    content = "Created inside the running container"
} | ConvertTo-Json

$note = Invoke-RestMethod -Method Post -Uri $baseUrl -ContentType "application/json" -Body $createBody
if ($note.title -ne "Docker smoke test" -or -not $note.id) { throw "Create failed" }
Write-Host "PASS: created note $($note.id)"

$readNote = Invoke-RestMethod -Uri "$baseUrl/$($note.id)"
if ($readNote.content -ne "Created inside the running container") { throw "Read failed" }
Write-Host "PASS: read note"

$updateBody = @{
    title = "Updated smoke test"
    content = "PUT request succeeded"
} | ConvertTo-Json
$updatedNote = Invoke-RestMethod -Method Put -Uri "$baseUrl/$($note.id)" -ContentType "application/json" -Body $updateBody
if ($updatedNote.title -ne "Updated smoke test") { throw "Update failed" }
Write-Host "PASS: updated note"

$allNotes = Invoke-RestMethod -Uri $baseUrl
if (-not ($allNotes | Where-Object { $_.id -eq $note.id })) { throw "List failed" }
Write-Host "PASS: note appears in list"

Invoke-RestMethod -Method Delete -Uri "$baseUrl/$($note.id)" | Out-Null
Write-Host "PASS: deleted note (HTTP 204)"
```

The following requests should return HTTP `400 Bad Request` for an invalid note and HTTP `404 Not Found` for an unknown ID:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/notes" -ContentType "application/json" -Body '{"title":" ","content":"invalid"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/notes/999999"
```

PowerShell displays an error for these expected non-2xx responses. The automated test suite also verifies blank-title rejection and the `404` behavior after deleting a note.

## API reference

| Method | Path | Result |
| --- | --- | --- |
| `GET` | `/api/notes` | List all notes |
| `POST` | `/api/notes` | Create a note (`201 Created`) |
| `GET` | `/api/notes/{id}` | Read one note |
| `PUT` | `/api/notes/{id}` | Replace a note's title and content |
| `DELETE` | `/api/notes/{id}` | Delete a note (`204 No Content`) |

Create and update requests use JSON with a non-blank `title` of at most 120 characters and a `content` value of at most 5,000 characters:

```json
{
  "title": "Shopping list",
  "content": "Coffee, bread, and apples"
}
```

## Stop and remove

```powershell
docker stop notes-api
docker rm notes-api
```

To remove the built image as well:

```powershell
docker rmi notes-api:1.0
```