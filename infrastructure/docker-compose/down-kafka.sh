#!/bin/bash

docker compose -f common.yml -f zookeeper.yml -f kafka_cluster.yml down

