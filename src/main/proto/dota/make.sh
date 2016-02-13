#!/usr/bin/env bash
protoc --proto_path=/usr/include --proto_path=. --java_out=../../java *.proto