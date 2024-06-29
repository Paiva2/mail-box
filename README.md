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
- [x] Delete a contact
- [x] Update a contact

#### E-mail

- [x] Send a new e-mail to other users on app
- [x] Send a new e-mail with opening orders (Only when the sender in order opens the email will the next one receive the
  email) ps: e-mail cannot contain cc's
- [x] Get inbox with possible filters
- [ ] Websocket on e-mails listing
- [x] Get all e-mails sent by me with possible filters
- [x] Get all SPAM e-mails
- [x] Mark or Unmark an e-mail sent to me as SPAM
- [x] Filter a single e-mail sent to me
- [x] Filter a single e-mail sent by me
- [x] Mark e-mail sent to me as opened when opened
- [x] Mark an opened e-mail as un-opened and handle opening order
- [x] List all non-opened e-mails sent to me
- [x] List all opened e-mails sent to me
- [x] Sent e-mails to trash and handle any e-mail ordering having
- [x] List all e-mails on trash with filters
- [x] Recover an e-mail from trash
- [x] Permanently delete an e-mail (must be on trash before)
- [x] Mark an e-mail as draft
- [x] List all my draft e-mails
- [x] Delete a e-mail from draft's
- [x] Send a draft e-mail
- [x] Mark e-mail with flags (important, favourite, etc...)
- [ ] List all e-mail with flags (important, favourite, etc...)
- [x] Remove e-mail flags
- [x] Update e-mail flags

#### E-mail cc's

- [x] Insert user's as copied on an e-mail sent
- [x] Filter all e-mails sent to me as a copy
- [x] Filter a single e-mail sent to me as a copy
- [x] Delete an e-mail sent to me as a copy
- [x] Mark e-mail sent to me as copy as opened when opened

#### Folder

- [x] Create a new folder
- [x] List all Folders
- [x] List all folder children
- [x] Insert e-mails folder
- [x] List all e-mails on folder
- [x] Change e-mails folder
- [x] Remove e-mails from folder
- [x] Change folder parent or remove

#### In progess ATM