
# Latest Changes: 

## [1] In /login route, made some changes

#### a) on failure: 

`HTTP UNAUTHORIZED: `

![[file-20250619202814193.png]]



`HTTP OK: `
#### b)  on success: 

![[file-20250619202939185.png]]


> [!note]
> 
> Frontend should tried to determine whether the login is successful or not with HTTP Status code, not by comparing the 'message' field which is subject to change in future


## [2] /logout has been implemented

#### a) when a logged in user invokes /logout, response will be: 

`HTTP OK`
![[file-20250619203122067.png]]

#### b) when an anonymous user invokes /logout, response will be: 

`HTTP UNAUTHORIZED`

![[file-20250619203149101.png]]

>[!note]
> This is because /logout is a protected route and to invoke it, the users must be logged in



## [3] /isAuthenticated has been implemented


#### a) when a logged in user invokes /isAuthenticated, the response will be: 

`HTTP OK`

![[file-20250619203618659.png]]

#### b) when an anonymous user invoked '/isAuthenticated', the response will be: 

`HTTP UNAUTHORIZED`

![[file-20250619203255487.png]]


## [4] Moved to H2 Database

- H2 is a in memory database and the project no longer uses mysql(during development)

1) To view the database in the H2 database, first start the server, then go to: `localhost:8080/h2-console` and following page will appear

![[file-20250619204055155.png]]

2) Don't write any password, just press `connect` button (because password is empty string) and the following page will appear

![[file-20250619204140165.png]]

3)  All the databse tables can be viewed, queried from here. (click on `APP_USERS`) which is a database table. A query will appear, and then click `run`

> [!important] 
> Database is populated when the server is started with a `sql script` and new data won't be persisted when the server is turned off
