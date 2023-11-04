# Random-Walker-Attacker

### AUTHOR : Aliasgar Zakir Merchant
### EMAIL : [amerch23@uic.edu]()

## Overview
This repository and code was built on Ubuntu 22.04 and hence the **ReadMe** is created considering that you would run
the program on an Ubuntu system.

The program replicates an Insider Attacker trying to gain access to company's secure computers in order to steal valuable data and returns the result of the attack.

This program is performed in a distributed environment using Spark and can act as a toolkit to test a company's defense mechanism.

## Program Design
- The design is based on the idea of performing functions in parallel across [RDD](https://spark.apache.org/docs/latest/rdd-programming-guide.html)s of Edge Triplets 
obtained from a [GraphX Model](https://spark.apache.org/docs/latest/graphx-programming-guide.html).
- On each partition, random walks for a fixed number of iterations are performed. Before saving a random walk from each computing node to a global
[accumulator](https://spark.apache.org/docs/latest/rdd-programming-guide.html#accumulators) variable, a check is performed to see if it has already been walked
to avoid redundancy.
- After walking, matches on each partition of the resultant RDD containing a List of walked nodes of the type (VertexID, Node) is performed.
Every walked node is matched with the most similar important node, using the similarity formula from [Project 1](https://github.com/aliasgar-m/CS441_Fall2023_HW1), of the original graph. 
- An important node of the original graph is a node that contain valuable information. A match is collected when the similarity measure is greater than the matching threshold.
- The results are then collected across every computing node and a random node is selected from the list based on the attacking
threshold. This random node is then attacked thereby resulting in a string value of **Success**, **Failure**, and **Not Found**.
- The above steps are performed for a fixed number of trials.
- The results are then collected and stored in a yaml file in the [outputs](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/096930b7a7f12e355089ba55bd4c3962ee28e6fe/outputs) folder.

## Necessary Prerequisites
In order to run this repository on one's system, you must install the following dependencies:
- [Scala](https://www.scala-lang.org/download/) ver 2.13.8
- [SBT](https://www.scala-sbt.org/release/docs/Setup.html) ver 1.9.2
- [JDK/JVM](https://docs.oracle.com/en/java/javase/) ver 1.8.0_382
- [Apache Spark](https://spark.apache.org/downloads.html) ver 3.5.0 (built specifically for scala 2.13)

## SetUp Repository
Once the prerequisites are met, clone this repository using the command `git clone`.

## SetUp Spark
### Initial System Setup
- First, you need to run the command `java --version` in order to determine if java has been installed on your PC.
- If not installed then you can install JAVA using `sudo apt install openjdk-8-jdk -y`
- Next, install Apache Spark for the above dependency and add the folder to `/usr/local/` directory so that it is available everywhere.
- Finally, in order to run the `spark-shell` command from any file location in the terminal, add the following code to your `~/.bashrc` file:
```
  export SPARK_HOME=/usr/local/Spark
  export PATH=$PATH:$SPARK_HOME/bin
```

## Configuration Management
- All configurations to the project can be managed from the [application.conf](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/src/main/resources/application.conf) file.
- In order to change the input NetGraphs on which the calculations will be performed, you will need to change the **inputDir**,  **orgInputFile**, and **perInputFile** parameters.
- Based on your preference, you can change the file paths where the result will be stored by changing **outputDir**, **orgGraphXFile**, **perGraphXFile**, **resultFile** parameters.
- To improve the performance of the computer, you can change the following parameters:
  - **noOfTrials** : change the number of trials or which the program is run.
  - **master** : change the value mentioned in the `[]` brackets to vary the number of threads on which the computation is performed.
  - **noOfSteps** : change this parameter to increase or decrease the number of steps performed in each random walk.
  - **noOfWalksPerPartition** : change this parameter to increase or decrease the number of walks performed per partition. 
  This was used as a substitute for when the match is not found.

## Input-Output
- The NetGraph input to the project is located in [inputs](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs/50_nodes/result.yaml) folder. You can load variations of the graph from
  this folder in the [application.conf](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/src/main/resources/application.conf) file.
- When you load and run the project, the output is stored in the [outputs](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs) folder.
- The output consists of 3 files. These are:
  - [orgGraphX.rwa](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs/50_nodes/orgGraphX.rwa) : The original GraphX model
  - [perGraphX.rwa](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs/50_nodes/perGraphX.rwa) : The perturbed GraphX model
  - [result.yaml](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs/50_nodes/result.yaml) : The YAML file containing the result of the program.

## Running the program
- In order to compile and run the project, you will need to load the project in [IntelliJ](https://www.jetbrains.com/idea/) so
  that the dependencies can be correctly loaded into the project.
- Once the project is loaded, you will need to run the command `sbt clean compile assembly` in order to generate a jar file
that will be located in the folder `\target\scala-2.13\`.
- Next, you need to run the following command in the terminal:
```
  spark-submit --class com.lsc.Main --jars "target/scala-2.13/rwa.jar" --driver-class-path "target/scala-2.13/rwa.jar" "target/scala-2.13/rwa.jar"
```

## Results
The outcome of the project is stored in the yaml file found in the [outputs](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/0d241dd159b8db39cc0a34ca3f6789bf962f1156/outputs) folder.
The outcome has the following information:
- **Number of Trials**: Number of trials the program was run for.
- **Number of Walks per Partition**: Number of walks performed per partition.
- **Steps per walk**: Number of steps in each walk.
- **Matching Pairs Threshold**: Similarity of the matched pairs must be greater than this threshold.
- **Attacking Pairs Threshold**: Similarity of the filtered attacked pairs must be greater than this threshold.
- **Total Successful Trials**: Number of trials where the attack was successful.
- **Total Failed Trials**: Number of trials where the attack failed.
- **Total DeadEnds Trials**: Number of trials where the attack reached a dead end i.e., to say there were no matched pairs whose similarity was greater than the attacking threshold.
- **Success Probability Percentage**: Percentage of success calculated as `Total Successful Trials / Number of Trials` 

An example is given below:

![images/result.png](https://github.com/aliasgar-m/Random-Walker-Attacker/blob/a56aaa4eae2c5b3b57588788fcceb44ecc6ba4e2/images/result.png)

## References
- https://spark.apache.org/docs/latest/quick-start.html
- https://spark.apache.org/docs/latest/rdd-programming-guide.html
- https://spark.apache.org/docs/latest/graphx-programming-guide.html
