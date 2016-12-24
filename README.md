# Summary
=================================
Example Todo Application with MongoDB

# Installation
=================================
Platform: Ubuntu 14.04

require user to be a passwordless sudoers user

### install ansible
$ sudo apt-get install software-properties-common
$ sudo apt-add-repository ppa:ansible/ansible
$ sudo apt-get update
$ sudo apt-get install ansible

### install application on localhost
$ cd [ROOT_PROJECT]/ansible
$ sudo ansible-playbook main.yml

### run test cases 
for first run it will download all dependencies a bit longer please waiting

$ cd [ROOT_PROJECT]
$ sbt -v compile
$ sbt test

### run application
$ cd [ROOT_PROJECT]
$ sbt run

application listen on 0.0.0.0:9000

# API design
=================================
Any validation error will return 400 Bad request with Messages as Example
```json
{
	"error" : {
		"subject" : ["This field is required", ...],
		"detail" : ["This field is required", ...],
	}
}
```
Any specified Task'id operations (update and delete) will return 404 Not found with unavailable task

### Get specify task by id

GET /api/tasks/:id	 
Response Example:
```json
{"id" :"id", "subject":"subject", "detail":"detail", "isDone":"true|false"}
```

###  Get pagination result of tasks

GET /api/tasks?pageNo=1&pageSize=10
Response Example:
```json
{
	"pageNo": 1,
	"pageSize": 10,
	"totalRecords": 100,
	"items": [
		{id:"id", subject:"subject", detail:"detail", isDone: true|false },
		..
		..
	]
}
```

###  Create new task

POST  /api/tasks
Request Body Example:
```json
{
	"subject": "test",
	"detail": "test"
}
```
Response Exmaple:
```json
{ "id": "new-generated-id" }
```

###  Update task by id

PUT	/api/tasks/:id
Request Body Example:
```json
{
	"subject": "test",
	"detail": "test",
	"isDone": true
}
```

###  Delete task by id
DELETE	/api/tasks/:id
return 200 OK
	
