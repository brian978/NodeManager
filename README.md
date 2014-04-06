NodeManager
===========

NodeJS server manager that is able to take different actions depending on the node.js server state.

What it can do so far:

* log output from the NodeJS process (standard output & error output)
* restart the NodeJS process if it crashes due to an error


Usage:
---------

Basically all you have to do is create a nodes.xml file next to the JAR file that has the structure provided in the
examples folder

***Note:*** The <log /> configuration in the XML is not yet used.
