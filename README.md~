This program implements Lamport Clock simulation as per this excercise http://www.cs.helsinki.fi/webfm_send/1746

To run the program, compile java sources in /src/lamport/, namely Lamport.java, LamportNode.java and NodeStruct.java. Then run them.
For your convenience a pre-built JAR package is also provided and can be found at /dist. Run with 'java -jar Lamport.jar <arguments>'.

As per the excercise, two arguments have to be provided. A configuration file of all the nodes in the simulation and the node ID of the running node.
Two configuration files are provided at / : Config provides a three node setup on localhost and UkkoConfig provides a three node setup for University of Helsinki's Ukko cluster's machines 107-109.
Program has been run without problems with such setups both locally and on the aforementioned Ukko cluster nodes.

Error checking is not comprehensive:
	
	A running node accepts messages from all sources and does not check their format. An attacker from outside the network could tamper with the clock.
	In general, message format is not checked as the nodes send correct messages.
	A node acts as if the nodes provided in the configuration file are always present, even if that wasn't the case.
	
More detailed description of how the program works is provided in the source code which is documented extensively.
