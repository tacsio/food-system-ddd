#!/bin/bash

docker compose -f common.yml -f zookeeper.yml up &

sleep 10

docker compose -f common.yml -f kafka_cluster.yml up &

docker compose -f common.yml -f postgres.yml up &