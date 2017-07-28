# Microservices-App

A java application made for an internship for the company Artexe SPA, Milano

To see the visual support of presentation, links are here:

* [Introduction to microservices](https://docs.google.com/presentation/d/1pn9b_nvikDYgB3SHKhdst1AaawZzB8uPamtuvcJ8uGw/edit?usp=sharing)
* [Microservices: a more advanced design](https://docs.google.com/presentation/d/1pt_wSeMzd1c5xuuobuICx7n38llLRvQWh5K69NPmVic/edit?usp=sharing)

--------------

## How to install:

In the directory of an app, type the command:

> $ mvn package spring-boot:repackage

Or for the app which does not need Spring boot:

> $ mvn package

The the .jar file is in the directory ./target

#### Tips:
All the docker images I have used are in the dedicated archive, you can download it [here](https://drive.google.com/open?id=0B0yOz4FKw4qVM1Y1WllKMmZwX0k). Just run:

> $ zcat myDockerImages.tar.gz | docker load


--------------
## Logs infrastructure:

The java applications are now implementing log4j in order to have standard logs format.

This step incorporates the setting up of a ELK stack in Docker containers. The RabbitMQ server has Logstah pipeline directly to Elasticsearch. The other Docker logs are catch by a FileBeats proxy (in its own docker container) via the Docker API. Then, there is a Logstash pipeline which links FileBeats to Elasticsearch. At the end, an instance of Kinana is running to look at all the logs.

This is mainly configration of the ELK stack (especially the Logstash filters).


## Step 5:

In this step, the EmitterApp & TcallApp are appeared. They communicate with the BoardManager (rename ServiceManager to avoid confusion) by RPC using the AMQP of the RabbitMQ server.
* The EmitterApp is a web server which is a proxy for the Kiosk browser to ask a new ticket for a service.  

* The TcallApp  is a web server which is a proxy for the Terminal call to asks which ticket is the next.  

* The CountingManager (rename WebCounterApp) has a new policy with the calls. It implements a queue of tickets: when a Emitter (a kiosk) asks a ticket for a service,
it send him a awmser and adds a ticket to the queue.
* When the WebCounterApp receives a event from a Tcall, it check if there is a ticket in the witing queue, then it removes the ticket form the queue.   

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



 	Publisher                       ----> Board1
            \                      /
          -----> BoardManager --------> Board2         (RabbitMQ server)
		     /     |           \
 	Publisher          v            ----> Board3
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
