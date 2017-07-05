# Microservices-App

A java application made for an internship for the company Artexe SPA, Milano


--------------

##Sprint 2:

Here we send messages to a service (a rootingKey):
				


			       -> Client1 {ServiceA}
			      /
			     /
Publisher ---#ServiceA---> MessageBroker ---> Client2 {ServiceA, ServiceB}
				

				  Client3{ServiceB}



##Sprint 1:

Using RabbitMQ & Sprint Boot to make a simple infrastructure 


Publisher ---> MessageBroker ---> Receiver

each of them in a different container.


