# Data Stream Processing of Web Server Access Logs
## About
This repository contains demo for implementing data stream processing pipeline. In this pipeline a stream of where stream of web server access logs are processed.

### Architecture
```mermaid  
flowchart LR  
id0(Web server log generator)
id1(Message Broker: Apache Kafka)
id2(Stream Processor: Apache Flink)
id3[(File System)]

id0 --1--> id1 --1-->id2 --2--> id3  
```
1 = A web server access log line like
```
92.247.94.198 - - [23/Mar/2023:10:02:08 +0100] "GET /contact.html?id=3 HTTP/1.1" 410 6502 "http://www.google.com" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like G
ecko) Chrome/93.0.4577.63 Safari/537.36"
```
2 = Average visitors per second  

## Prerequisites
This setup requires following libraries/frameworks:
- Java (openjdk 11.0.2)
- Apache Maven (3.9.1)
- Apache Kafka (3.4.0)
- Apache Flink (1.16.1)

These frameworks can be either downloaded and set up manually or using the provided script. Once these frameworks are set up, the provided source code should be compiled.

### Manual setup
- If one or more of these frameworks are already present on the system or,
- If these frameworks are manually downloaded and setup,

please change the following variables, in the environment file ([env.sh](./env.sh)), accordingly:
```
FLINK_HOME
KAFKA_HOME
MVN_HOME
JAVA_HOME
```
 
### Setup using the script
To download and setup these frameworks using script:
1. Change the `FRAMEWORK_DIR` option in environment file ([env.sh](./env.sh)). The default option is set to `<current-directory>/frameworks`. Do not change this variable if the default option is to be used.
2. Run the main script ([run.sh](./run.sh)) with root privileges: 
   ```
   # To (download and setup frameworks) + (compile source code)
   sudo ./run.sh SETUP
      
   # For more info
   sudo ./run.sh HELP
   ```

## Usage

To start the demo the main script ([run.sh](./run.sh)) should be used.
First please set following variable values in the environment file ([env.sh](./env.sh)).
The default values could also be used to run the demo.
```
FLINK_SLOTS_PER_TASKMANAGER
SOURCE_BOOTSTRAP_SERVER
SOURCE_TOPIC
SINK_TYPE
FILESYSTEM_OUTPUT_DIR (only if SINK_TYPE="filesystem")
WINDOW_LENGTH
SIMULATION_DURATION_SEC
GENERATOR_TYPE
LOAD_HZ
```
Once these values are set, the simulation can be started using,
```bash
sudo ./run START_SIMULATION
```
To stop the simulation at anytime before the SIMULATION_DURATION_SEC, press `CTRL+C` keys. Then run following,
```bash
sudo ./run STOP_SIMULATION
```

For more information
```bash
sudo ./run.sh HELP
```

## License
To be distributed and used under [GNU GENERAL PUBLIC LICENSE V3](./LICENSE)