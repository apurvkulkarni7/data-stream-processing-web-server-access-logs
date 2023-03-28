#!/bin/bash

# General settings
#-----------------------------------------------------------------------------
export BASE_DIR=$(pwd -P .)
# Location of frameworks to be set up
export FRAMEWORK_DIR="${BASE_DIR}/frameworks"

# Flink settings
#-----------------------------------------------------------------------------
export FLINK_HOME="${FRAMEWORK_DIR}/apache_flink"
# Increase the following value for higher load frequency
export FLINK_SLOTS_PER_TASKMANAGER="1"
export FLINK_PARALLELISM="$FLINK_SLOTS_PER_TASKMANAGER" # For multi-node cluster the above value will be different
# Sink type: 1- kafka, 2-filesystem, 3- stdout
# filesystem - the output will be generated in file inside directory "FILESYSTEM_OUTPUT_DIR"
export SINK_TYPE="filesystem"
export FILESYSTEM_OUTPUT_DIR="$BASE_DIR/out"
# Processing window length in seconds (how often the averaging should be produced)
export WINDOW_LENGTH_SEC=5

# Apache Kafka settings
#-----------------------------------------------------------------------------
export KAFKA_HOME="$FRAMEWORK_DIR/apache_kafka"
# Zookeeper configuration file
export ZOOKEEPER_CONFIG="${KAFKA_HOME}/config/zookeeper.properties"
# Apache Kafka configuration
export KAFKA_CONFIG="${KAFKA_HOME}/config/server.properties"
# Hostname and port for source Kafka topic
export SOURCE_BOOTSTRAP_SERVER="localhost:9092"
# Source Kafka topic name
export SOURCE_TOPIC="events-source"
# Source Kafka topic partition number
export KAFKA_SOURCE_TOPIC_PARTITION_NUM=$FLINK_PARALLELISM
# Hostname and port for sink Kafka topic. Used only if SINK_TYPE="kafka"
export SINK_BOOTSTRAP_SERVER="localhost:9092"

# Maven settings
#-----------------------------------------------------------------------------
export MVN_HOME="${FRAMEWORK_DIR}/apache_maven"
export MVN="${MVN_HOME}/bin/mvn"

# Java settings
#-----------------------------------------------------------------------------
export JAVA_HOME="${FRAMEWORK_DIR}/java"
export JAVA="${JAVA_HOME}/bin/java"

# Simulation setting
#-----------------------------------------------------------------------------
# Total duration of the demo/simulation
export SIMULATION_DURATION_SEC=1200
# Generator type (constant/random)
# constant - to generate events at constant rate at LOAD_HZ frequency
# random - to generate events at random rate
export GENERATOR_TYPE="random"
# Log events to be generated per second (used if 'GENERATOR_TYPE="constant"' )
export LOAD_HZ=10

