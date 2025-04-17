# Start with a base image that has your app built with bootBuildImage
FROM invdb-ms-base:latest as base

USER root
# Install additional packages
RUN apt-get update && apt-get install -y fontconfig
RUN mkdir -p /tmp/qc-data/s3Files \
    /tmp/qc-data/archive/filtered 
RUN chown -R 1001:cnb /tmp/qc-data
USER 1001
