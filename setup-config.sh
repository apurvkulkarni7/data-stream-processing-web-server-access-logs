#!/bin/bash

source env.sh

# Setup Apache Flink parallelism and slots
sed -i 's#^\(taskmanager.numberOfTaskSlots:\s*\).*$#\1'$FLINK_SLOTS_PER_TASKMANAGER'#' "${FLINK_HOME}/conf/flink-conf.yaml"
sed -i 's#^\(parallelism.default:\s*\).*$#\1'$FLINK_PARALLELISM'#' "${FLINK_HOME}/conf/flink-conf.yaml"