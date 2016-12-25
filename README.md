# Summary
=================================
Example Todo Application with MongoDB and Angular2 singlepage style front end ui

# Installation
=================================
Platform: Ubuntu 14.04

require user to be a passwordless sudoers user

### install ansible
```
$ sudo apt-get install software-properties-common
$ sudo apt-add-repository ppa:ansible/ansible
$ sudo apt-get update
$ sudo apt-get install ansible
```

### install application on localhost
```
$ cd [ROOT_PROJECT]/ansible
$ sudo ansible-playbook main.yml
```

### run test cases 
for first run it will download all dependencies a bit longer please waiting
```
$ cd [ROOT_PROJECT]
$ sbt -v compile
$ sbt test
```
if run test failed with jvm cannot allocate memory just try it again (cause the last process consume a lot of memory and did not free the cache, I will try to solve this later)


### run application
```
$ cd [ROOT_PROJECT]
$ sbt run
```
run appliation will also create 200 test data records if the database is empty 

application listen on 0.0.0.0:9000

interaction with Angular ui at root path

# API design
=================================
Any validation error will return 400 Bad request with Messages as Example
```json
{
	"error" : {
		"subject" : ["This field is required"],
		"detail" : ["This field is required"]
	}
}
```
Any specified Task'id operations (update and delete) will return 404 Not found with unavailable task

### Get specify task by id

GET /api/tasks/:id	 
Response Example:
```json
{"id" :"id", "subject":"subject", "detail":"detail", "isDone":false}
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
		{"id": "id", "subject": "subject", "detail": "detail", "isDone": false }
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

### Create upstart service
create upstart service name todo listen all interfaces on port 9000
#### service home /usr/share/todo

```
$ cd [ROOT_PROJECT]
$ sudo apt-get install fakeroot
$ sbt debian:packageBin
$ sudo dpkg -i target/Todo_1.0-SNAPSHOT_all.deb
```
	
