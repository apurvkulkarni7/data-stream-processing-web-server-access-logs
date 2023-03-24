#!/bin/bash

logger_info () {
  echo "[$(date)] [INFO] $1" >&2
}

logger_error () {
  echo ""
  echo "[$(date)] [ERROR] $1"
  exit 1
}

kill_process () {
  PID="$(jps | grep "$1" | awk '{print $1}')"
  if [[ "$PID" != "" ]]; then
    logger_info "$2"
    kill "$PID"
  fi
}

select_framework () {
  local message
  message="
  Which frameworks would you like to select for the operation.
  Write one or more options (seperated by whitespace):
  1 : Apache Flink
  2 : Apache Kafka
  3 : Apache Maven
  4 : Java 11.0.2-openjdk
  5 : All
  Input: "

  local OPT
  read -p "$message" OPT
  echo $OPT
}

download_frameworks () {
  local SETUP_OPT
  SETUP_OPT=$1

  if [[ "$SETUP_OPT" != "" ]]; then
    local FRAMEWORK_DIR="${2:-"./frameworks"}"
    mkdir -p "$FRAMEWORK_DIR/tmp"
  fi

  for i in $SETUP_OPT; do

    if [[ "$SETUP_OPT" == "1" ]] || [[ "$SETUP_OPT" == "5" ]]; then
      if [[ ! -f "$FRAMEWORK_DIR/tmp/flink.tgz" ]]; then
          logger_info "Getting Apache Flink-1.16.1 file archive"
          wget -O "$FRAMEWORK_DIR/tmp/flink.tgz" "https://dlcdn.apache.org/flink/flink-1.16.1/flink-1.16.1-bin-scala_2.12.tgz"
      fi
    fi
    if [[ "$SETUP_OPT" == "2" ]] || [[ "$SETUP_OPT" == "5" ]]; then
      if [[ ! -f "$FRAMEWORK_DIR/tmp/kafka.tgz" ]]; then
        logger_info "Getting Apache Kafka-3.4.0 file archive"
        wget -O "$FRAMEWORK_DIR/tmp/kafka.tgz" "https://dlcdn.apache.org/kafka/3.4.0/kafka_2.13-3.4.0.tgz"
      fi
    fi
    if [[ "$SETUP_OPT" == "3" ]] || [[ "$SETUP_OPT" == "5" ]]; then
      if [[ ! -f "$FRAMEWORK_DIR/tmp/mvn.tar.gz" ]]; then
        logger_info "Getting Apache Maven-3.9.1 file archive"
        wget -O "$FRAMEWORK_DIR/tmp/mvn.tar.gz" "https://dlcdn.apache.org/maven/maven-3/3.9.1/binaries/apache-maven-3.9.1-bin.tar.gz"
      fi
    fi
    if [[ "$SETUP_OPT" == "4" ]] || [[ "$SETUP_OPT" == "5" ]]; then
      if [[ ! -f "$FRAMEWORK_DIR/tmp/java.tar.gz" ]]; then
        logger_info "Getting OpenJDK-11.0.2, file archive"
        wget -O "$FRAMEWORK_DIR/tmp/java.tar.gz" "https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz"
      fi
    fi
  done
}

setup_frameworks () {
  local SETUP_OPT="$1"
  local FRAMEWORK_DIR="$2"

  if [[ "$SETUP_OPT" == "1" ]] || [[ "$SETUP_OPT" == "5" ]]; then
    if [[ ! -d "$FRAMEWORK_DIR/apache_flink" ]]; then
      logger_info "Setting Apache Flink files"
      tar -xzf "$FRAMEWORK_DIR/tmp/flink.tgz" --directory "$FRAMEWORK_DIR"
      mv "$(find ./frameworks/ -maxdepth 1 -type d | grep '/flink')/" "$FRAMEWORK_DIR/apache_flink"
    fi
  fi
  if [[ "$SETUP_OPT" == "2" ]] || [[ "$SETUP_OPT" == "5" ]]; then
    if [[ ! -d "$FRAMEWORK_DIR/apache_kafka" ]]; then
      logger_info "Extracting Apache Kafka files"
      tar -xzf "$FRAMEWORK_DIR/tmp/kafka.tgz" --directory "$FRAMEWORK_DIR/"
      mv "$(find ./frameworks/ -maxdepth 1 -type d | grep '/kafka')/" "$FRAMEWORK_DIR/apache_kafka"
    fi
  fi
  if [[ "$SETUP_OPT" == "3" ]] || [[ "$SETUP_OPT" == "5" ]]; then
    if [[ ! -d "$FRAMEWORK_DIR/apache_maven" ]]; then
      logger_info "Extracting Apache Maven files"
      tar -xzf "$FRAMEWORK_DIR/tmp/mvn.tar.gz" --directory "$FRAMEWORK_DIR/"
      mv "$(find ./frameworks/ -maxdepth 1 -type d | grep '/apache-maven')/" "$FRAMEWORK_DIR/apache_maven"
    fi
  fi
  if [[ "$SETUP_OPT" == "4" ]] || [[ "$SETUP_OPT" == "5" ]]; then
    if [[ ! -d "$FRAMEWORK_DIR/java" ]]; then
      logger_info "Extracting java-openjdk-11.0.2 files"
      tar -xzf "$FRAMEWORK_DIR/tmp/java.tar.gz" --directory "$FRAMEWORK_DIR/"
      mv "$(find ./frameworks/ -maxdepth 1 -type d | grep '/jdk')/" "$FRAMEWORK_DIR/java"
      logger_info "Extraction complete"
    fi
  fi
}

kafka_topic() {
  local count_source
  count_source=$("$KAFKA_HOME"/bin/kafka-topics.sh --list --bootstrap-server "$SOURCE_BOOTSTRAP_SERVER" --topic "${SOURCE_TOPIC}" 2>/dev/null | grep -Pc "${SOURCE_TOPIC}")

  if [[ "$1" == "CREATE" ]]; then
    out=$("$KAFKA_HOME"/bin/kafka-topics.sh --create --topic $SOURCE_TOPIC --bootstrap-server $SOURCE_BOOTSTRAP_SERVER --replication-factor 1 --partitions $KAFKA_SOURCE_TOPIC_PARTITION_NUM)
    sleep 5s
    logger_info "$out"
  elif [[ "$1" == "DELETE" ]]; then
    if [[ "$count_source" != "0" ]]; then
      "${KAFKA_HOME}"/bin/kafka-topics.sh --delete --bootstrap-server "$SOURCE_BOOTSTRAP_SERVER" --topic "${SOURCE_TOPIC}" #2>/dev/null
      out="Kafka topic \"${SOURCE_TOPIC}\" deleted."
    else
      out="Kafka topic '$SOURCE_TOPIC' does not exist"
    fi
    logger_info "$out"
  elif [[ "$1" == "DELETE_ALL" ]]; then
    ALL_TOPICS_LIST=$("${KAFKA_HOME}"/bin/kafka-topics.sh --list --bootstrap-server localhost:9092)
    if [[ "${ALL_TOPICS_LIST}" != "" ]]; then
      for topic in $ALL_TOPICS_LIST ; do
        "${KAFKA_HOME}"/bin/kafka-topics.sh --delete --topic "$topic" --bootstrap-server localhost:9092
      done
    fi
  fi
}

run () {
  local OPERATION="$1"

  if [[ "${OPERATION}" == "SETUP_FRAMEWORKS" ]];then

    local SETUP_OPT
    SETUP_OPT=$(select_framework)

    for SETUP_OPT_i in $SETUP_OPT; do
      download_frameworks "$SETUP_OPT_i" "${FRAMEWORK_DIR}"
      setup_frameworks "$SETUP_OPT_i" "${FRAMEWORK_DIR}"
    done

  elif [[ "${OPERATION}" == "RESET_FRAMEWORKS" ]]; then

    logger_info "Re-setting frameworks"
    rm -rf "${FRAMEWORK_DIR}"/{apache_flink,apache_kafka,apache_maven,java}
    SETUP_OPT=$(select_framework)
    setup_frameworks "$SETUP_OPT" "$FRAMEWORK_DIR"

  elif [[ "${OPERATION}" == "COMPILE_FILES" ]]; then

    $MVN clean package

  elif [[ "${OPERATION}" == "CLEAN" ]]; then

      $MVN clean
      rm -rf log/*
      rm -rf out/*

  elif [[ "${OPERATION}" == "DEEP_CLEAN" ]]; then

    $MVN clean
    rm -rf "${FRAMEWORK_DIR}"/*

  elif [[ "${OPERATION}" == "SETUP" ]]; then

    run "SETUP_FRAMEWORKS"
    run "COMPILE_FILES"

  elif [[ "${OPERATION}" == "START_KAFKA" ]]; then

    # Starting Zookeeper and Kafka
    logger_info "Starting Zookeeper server"
    i=1
    while [[ "$(jps| grep Quorum | awk '{print $1}')" == "" ]]; do
      logger_info "    attempt $i"
      "${KAFKA_HOME}"/bin/zookeeper-server-start.sh -daemon "${ZOOKEEPER_CONFIG}"
      ((i++))
      sleep 10
    done
    # Starting kafka broker
    logger_info "Starting Kafka broker"
    i=1
    while [[ "$(jps| grep Kafka | awk '{print $1}')" == "" ]]; do
      logger_info "    attempt $i"
      "${KAFKA_HOME}"/bin/kafka-server-start.sh -daemon "${KAFKA_CONFIG}"
      ((i++))
      sleep 10
    done
    kafka_topic "DELETE_ALL"
    kafka_topic "CREATE"

  elif [[ "${OPERATION}" == "STOP_KAFKA" ]]; then

    logger_info "Stopping Kafka broker"
    "${KAFKA_HOME}"/bin/kafka-server-stop.sh "$KAFKA_CONFIG"

    logger_info "Stopping Zookeeper"
    "${KAFKA_HOME}"/bin/zookeeper-server-stop.sh "$ZOOKEEPER_CONFIG"

  elif [[ "$OPERATION" == "START_FLINK" ]]; then

    logger_info "Starting Apache Flink cluster"
    "${FLINK_HOME}/bin/start-cluster.sh"

  elif [[ "$OPERATION" == "STOP_FLINK" ]]; then

    "${FLINK_HOME}/bin/stop-cluster.sh"

  elif [[ "$OPERATION" == "START_FLINK_PROCESSING" ]]; then

    logger_info "Starting Apache Flink processing"
    FLINK_OPTS="--parallelism ${FLINK_PARALLELISM} --source-type kafka --source-kafka-topic ${SOURCE_TOPIC} --source-bootstrap-server ${SOURCE_BOOTSTRAP_SERVER} --sink-type ${SINK_TYPE} --runtime ${SIMULATION_DURATION_SEC} --window-length ${WINDOW_LENGTH_SEC}"
    "${FLINK_HOME}/bin/flink" run ./stream-processor/target/stream-processor-1.0.0.jar $FLINK_OPTS > ./logs/flink.out 2>&1 &

  elif [[ "$OPERATION" == "STOP_FLINK_PROCESSING" ]]; then

    logger_info "STOP_FLINK_PROCESSING not implemented yet"

  elif [[ "$OPERATION" == "START_GENERATOR" ]]; then

    logger_info "Starting log generator"
    GENERATOR_OPTS="--generator-type ${GENERATOR_TYPE} --loadHz ${LOAD_HZ} --run-time ${SIMULATION_DURATION_SEC} --kafka-topic ${SOURCE_TOPIC} --bootstrap-server ${SOURCE_BOOTSTRAP_SERVER}"

    if [[ "$GENERATOR_TYPE" == "constant" ]]; then
      GENERATOR_OPTS="$GENERATOR_OPTS --loadHz ${LOAD_HZ}"
    fi

    $JAVA -Dlogfile.name=./logs/generator.log -cp ./generator/target/generator-1.0.0.jar org.example.generator.Simulator $GENERATOR_OPTS > ./logs/generator.out 2>&1 &

  elif [[ "$OPERATION" == "STOP_GENERATOR" ]]; then

    kill_process "Simulator" "Stopping generator"

  elif [[ "$OPERATION" == "START_SIMULATION" ]]; then

    logger_info "Initializing frameworks"
    bash setup-config.sh
    run "START_KAFKA"
    run "START_FLINK"
    run "START_GENERATOR"
    run "START_FLINK_PROCESSING"
    logger_info "Running the simulation for $(( SIMULATION_DURATION_SEC/60 )) minutes"
    sleep "${SIMULATION_DURATION_SEC}s"
    run "STOP_SIMULATION"

  elif [[ "$OPERATION" == "STOP_SIMULATION" ]]; then

    run "STOP_GENERATOR"
    run "STOP_FLINK"
    run "STOP_KAFKA"

  else

    logger_error "Incorrect input. For more information, try './run.sh HELP'"
    logger_error ""

  fi
}

trap_ctrlc () {
  logger_error "Program interrupted"
  run "STOP_SIMULATION"
  exit 1
}

trap trap_ctrlc SIGINT

# Abort if not super user
if [[ ! `whoami` = "root" ]]; then
    echo "You must have administrative privileges to run this script"
    echo "Try 'sudo $0 <OPTIONS>'"
    exit 1
fi

if [[ "$1" == "HELP" ]] ; then
  cat << HELPTXT

usage: $0 <OPTIONS>

OPTIONS:
  HELP                    : Print this message
  SETUP_FRAMEWORKS        : Downloading and setting-up frameworks
  RESET_FRAMEWORKS        : Resetting frameworks
  COMPILE_FILES           : Compiles the java source code to jar files
  SETUP                   : SETUP_FRAMEWORKS + COMPILE_FILES
  START_SIMULATION        : Starting simulation including all frameworks
  STOP_SIMULATION         : Stopping simulation including all frameworks
  START_KAFKA             : Starting Kafka
  STOP_KAFKA              : Stopping Kafka
  START_FLINK             : Starting Flink cluster
  STOP_FLINK              : Stopping Flink cluster
  START_GENERATOR         : Starting generator
  STOP_GENERATOR          : Stopping generator
  START_FLINK_PROCESSING  : Starting Flink(Stream) processing
  STOP_FLINK_PROCESSING   : Stopping Flink(Stream) processing
  CLEAN                   : Remove compiled files + contents of "logs" and "out" directory, if exist
  DEEP_CLEAN              : CLEAN + remove framework directory

HELPTXT
elif [[ "$#" -lt "1" ]]; then
  logger_error "Atleast one option needs to be specified"
  logger_error "For more information: ./run.sh --help"
else
  OPERATION="$1"
  source env.sh
  run "$OPERATION"
fi
