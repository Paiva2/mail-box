# E-mail Box #In-Progress

## Technologies

- Java 17
- Spring Boot
- Spring Security
- Postgres
- Docker

## Functionalities

#### User

- [x] RBAC
- [x] Register
- [x] Login
- [ ] Forgot Password
- [x] Update profile
- [x] Fetch Profile infos

#### Contact

- [x] Create a new contact
- [x] List all my contacts
- [x] Filter a single contact
- [ ] Delete a contact
- [ ] Update a contact

#### E-mail

- [x] Send a new e-mail to other users on app
- [ ] Send a new e-mail with opening orders (Only when the sender in order opens the email will the next one receive the
  email) ps: e-mail cannot contain cc's
- [x] Get inbox with possible filters
- [x] Get all e-mails sent by me with possible filters
- [x] Get all SPAM e-mails
- [ ] Mark an e-mail sent to me as SPAM
- [x] Filter a single e-mail sent to me
- [ ] Filter a single e-mail sent by me
- [x] Mark e-mail sent to me as opened when opened
- [ ] List all non-opened e-mails sent to me
- [ ] List all opened e-mails sent to me
- [ ] Sent e-mails to trash
- [ ] Permanently delete an e-mail (must be on trash before)
- [ ] Mark an e-mail as draft
- [ ] List all my draft e-mails
- [ ] Remove a e-mail from draft's
- [ ] Send a draft e-mail

#### E-mail cc's

- [x] Insert user's as copied on an e-mail sent
- [ ] Filter all e-mails sent to me as a copy
- [ ] Filter a single e-mail sent to me as a copy
- [ ] Delete an e-mail sent to me as a copy
- [ ] Mark e-mail sent to me as copy as opened when opened

#### Folder

- [ ] Create a new folder
- [ ] Insert e-mails on this folder
- [ ] List all e-mails on this folder
- [ ] Remove e-mails from this folder