# Microservices-App

A java application made for an internship for the company Artexe SPA, Milano

--------------

## How to install:

In the directory of an app, type the command:

> $ mvn package spring-boot:repackage

Or for the app which doenot need Spring boot:

> $ mvn package

The the .jar file is in the directory ./target

--------------


## Step 4:

The use of the Spring-boot framework is too heavy for some app which do a simple thing
with AMQP library. So the Publisher is remplaced by a ligther one without Spring-Boot.
In addition, instead of having a web server on every Board which is quiet heavy too,
there is one new app, the Board App, with one web server for all the board. So on the
board, there is only a Web Browser to establish a STOMP connection with the right
queue.

  				REST             Http
	BoardManager -----> BoardApp <------ Boards  
			     <-----




## Step 3:

A more complex architecture with a server Web (counting Manager) and a Board Manager.
Every App is deployed in a Docker container in the same network.



 	Publisher                  ----> Board1
		\ 					 /
   	  -----> BoardManager --------> Board2         (RabbitMQ server)
		/               |      \
 	Publisher          v       ----> Board3
            	CountingManager


1) The communication between the publishers and the BoardManager are managed by the
RabbitMQ server by AMQP.
2) They send an event to the Board Manager which send an Http request to the Counting Manager.  
3) It anwsers with a Json which contains the name (the routing key) of the future
service to contact.  
4) The Boards are connected to the Board Manager by a web STOMP connection (plugin of RabbitMQ): They receive a prefix with the number of their task.




## Step 2:

Here we send messages to a service (a rootingKey):



                                   	-> Client1 {ServiceA}
                                  	/
                                 	/
 	Publisher ---#ServiceA---> MessageBroker ---> Client {ServiceA, ServiceB}
                                 	\
                                  	\
                                   	-> Client3{ServiceB}



## Step 1:

Using RabbitMQ & Sprint Boot to make a simple infrastructure


  	Publisher ---> MessageBroker ---> Receiver

each of them in a different container.
