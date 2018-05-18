# Monitor-Engine
> act as a stateless service to interact with the front-end  service and provide necessary files for docker daemon to build image and finally to generate run-time service.
## Getting started

### Using docker to deploy this service

> recommend using docker 

- first, install [docker](https://www.docker.com/) ,version from 17.06.

- Go to parent directory of the project and run the command below

  ```shell
  # before running this, set the right path of nfs shared data
  docker build .
  # after build the image successfully (u can see the image by docker images)
  docker run --privileged=true -p port:8102 -name $CONTAINER_NAME $IMAGE_ID 
  ```

- finally, you can access this service via host:port/monitor/blocks 

### Deploy without docker

- jdk1.8 ,maven 3 required.
- see more details via [Dockerfile](https://github.com/ISS-Boy/Monitor-Engine/blob/master/Dockerfile) and [shell-script](https://github.com/ISS-Boy/Monitor-Engine/blob/master/docker-entrypoint.sh)
- you should mount NFS file-system first (better to use *nix OS)and do remember to modify the application.properties file.

## Architecture

### Core

> transform service provider

- we have a json parser to parse the json derived by front-end service ,this is treated as an abstract syntax tree(AST) to build logical plan
- using AST to build logical plan which is basically a operation tree consist of different operation nodes such as join,aggregate,filter,select e.t.c. The visitor pattern is used here.
- using logical plan to generate real kafka stream code using some predefined [**Apache Velocity**](https://velocity.apache.org/) template ,and compile all the code to class stored in the memory at run-time.
- Finally ,we package all the class files into kafka-stream-application.jar and upload it along with the Dockerfile to network file system.

### Stream-App-template
>  cover all dependencies a kafka stream app needed.

- we define the basic abstraction here, we treat the data in kafka as a structured and flattened data map.
- all the aggregation functions are defined here.
- future : move the join and filter Node's code generation part to this module.

### Server

> using spring-boot framework to expose REST interface

- this is a simple spring-boot application ,exposing REST interface to be accessed by other services.
- some detailed implement should be improved, such as 
  - exception type design 
  - schema fetcher
  - adding topics GC-Worker thread
  - performance tuning 
    - using asynchronize Post
    - do the analyzing work for each monitor in the monitor group or even each step of the analyze in parallel.
