
### Pre-requisites

These are the tools you would need to follow this tutorial

1. Install docker and verify that docker is working
	
	https://docs.docker.com/engine/installation/

	`docker run hello-world`

	After installation verify the docker daemon is listening on tcp port. 

	Ubuntu follow these steps as the default service daemon does not listen on tcp port,
	~~~
	sudo service stop docker
	sudo docker daemon -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
	~~~
2. Install docker-compose, which we will use to bring up multiple docker containers easily.

	https://docs.docker.com/compose/install/

### Example

1. Clone this repository or download the archive from Github.
2. Run the following command, this pulls 2 images and runs a network of 2 peers
 * fabric-peer which hosts the hyperledger fabric backend
 * fabric-javaenv which is the runtime image for java chaincodes
	~~~
		cd docker-compose
		docker-compose up
	~~~

3. Open another terminal in the same directory and start the fabric-peer container to issue deploy and invoke transactions

	`docker-compose run vp1 bash`
4. Once you are inside the container deploy the given example,
	
	`peer chaincode deploy --logging-level debug -l java -p examples/chaincode/java/SimpleSample -c '{"Function": "init", "Args": ["alice","100", "bob", "200"]}'`

	~~~
	17:23:38.936 [logging] LoggingInit -> DEBU 001 Setting default logging level to DEBUG for command 'chaincode'
	17:23:39.131 [chaincodeCmd] chaincodeDeploy -> INFO 002 Deploy result: type:JAVA chaincodeID:<path:"examples/chaincode/java/SimpleSample" name:"771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0" > ctorMsg:<args:"init" args:"alice" args:"100" args:"bob" args:"200" > 
	17:23:39.131 [main] main -> INFO 003 Exiting.....
	~~~

	Note the value for 'name' from the response. This is like a unique identifier for the chaincode that was just deployed. This will be reused as a parameter in the following commands.

5. Query the initial state of the accounts,

	~~~
	peer chaincode query -l java \
	-n 771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0 \
	-c '{ "Function": "query", "Args": ["alice"]}'
	~~~
	~~~
	peer chaincode query -l java \
	-n 771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0 \
	-c '{ "Function": "query", "Args": [ "bob"]}'
	~~~

6. Invoke a transaction on the example,

	~~~
	peer chaincode invoke -l java \
		-n 771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0 \
		-c '{ "Function": "transfer",  "Args": [ "alice", "bob", "10"]}'
	~~~

7. Query the state of the accounts again,

	~~~
	peer chaincode query -l java \
		-n 771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0 \
		-c '{ "Args": ["query", "alice"]}'
	~~~

	~~~
	peer chaincode query -l java \
		-n 771df7c1cfa4dd9607c72ec190c8e7f1954c1d431c08017496097c3007cd06e4902aa49e7385a1b8ab75111fa66a17cd7094d8b8a325de62d33bf8014e62dcf0 \
		-c '{ "Args": ["query", "bob"]}'
	~~~

### Repo Contract example

This example is located under docker-compose/project/repoChaincode. This project folder is mapped to the peer docker container and made available as /root/ volume. So any changes made to this folder will get reflected in the docker container.

1. Deploy the Repo contract chaincode using the following command

	~~~
	peer chaincode deploy --logging-level debug -l java -p /root/project/repoChaincode -c '{"Function": "init", "Args": []}'
	~~~

2. Dealer X  with 2 trades,  Trade-1 between ML 9510 and JPMorgan 9512 
	~~~
	peer chaincode invoke -l java \
	-n fb14710b9ca328fcff5784e7f99ff94277b369f05f39ee869c112821e08c7437add98f4ab3d54c702aabfd018ff0585a195d3c76e83a73de20002b9585d18855 \
	-c '{"Function": "instruct", "Args": ["XREF-MLJPM1", "TID1001", "9510", "9512", "0.34", "8000000", "100000", "371487AE9"]}'
	~~~
3. And a Trade-2 between ML 9510 and HSBC 9513 
	~~~
	peer chaincode invoke -l java \
	-n fb14710b9ca328fcff5784e7f99ff94277b369f05f39ee869c112821e08c7437add98f4ab3d54c702aabfd018ff0585a195d3c76e83a73de20002b9585d18855 \
	-c '{"Function": "instruct", "Args": ["XREF-MLHSBC", "TID1002", "9510", "9513", "0.36", "5000000", "20000", "371487AE9"]}'
	~~~
4. Now Dealer Y comes with a matching trade for XREFMLJPM1
	~~~
	peer chaincode invoke -l java \
	-n fb14710b9ca328fcff5784e7f99ff94277b369f05f39ee869c112821e08c7437add98f4ab3d54c702aabfd018ff0585a195d3c76e83a73de20002b9585d18855 \
	-c '{"Function": "instruct", "Args": ["XREF-BRK1MLJPM", "TIDX002", "9512", "9510", "0.34", "8000000", "100000", "371487AE9"]}'
	~~~
5. Once this transaction is made, it is matched with existing trade and the trades gets updated accordingly.
	~~~
	peer chaincode query -l java \
	-n fb14710b9ca328fcff5784e7f99ff94277b369f05f39ee869c112821e08c7437add98f4ab3d54c702aabfd018ff0585a195d3c76e83a73de20002b9585d18855 \
	-c '{"Function": "trades", "Args": ["", ""]}'
	~~~

#### Development of chaincodes

Please follow the repoChaincode example in the projects folder and modify build.gradle for your needs,

* Make your main class extend ChaincodeBase class and implement the following methods from base class.
~~~
    public String run(ChaincodeStub stub, String function, String[] args)
    public String query(ChaincodeStub stub, String function, String[] args)
    public String getChaincodeID()
~~~
* Implement the main method to instantiate and run the start() method.
* Modify the mainClassName in build.gradle to point to your new class.
