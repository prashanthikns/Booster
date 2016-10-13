# [FuelUp](http://fuelup.us/map/trucks/all)
## Table of contents
1. [Introduction](README.md#introduction)
2. [AWS Clusters](README.md#aws-clusters) 
3. [Data Pipeline](README.md#data-pipeline)
4. [Performance](README.md#performance)
5. [Presentation](README.md#presentation)


## Introduction 
[Back to Table of contents](README.md#table-of-contents)

[FuelUp](http://fuelup.us/map/trucks/all) is a consulting project for Booster
Fuels Inc. 

Booster Fuels have trucks operating in the field that are streaming their vehicle
telemetry data in real-time. 

The project was to build a real-time, scalable platform for reading and
archiving the raw, real-time telemetry streams from each of their trucks. Use the
data to calculate core metrics related to delivery efficiency on a per
truckbasis, develop a system for persisting these data streams to a
datastore in a scalable fashion as well as a mechanism for fusing them with
other data to calculate crucial operational metrics in real-time. 

## AWS Clusters
[Back to Table of contents](README.md#table-of-contents)

[FuelUp](http://fuelup.us/map/trucks/all) runs on four clusters on AWS:
<ul>
<li>3 m3.large nodes for Kafka and Kafka Connect</li>
<li>1 m3.large nodes for PostGres DB and Flask </li>
<li>1 m3.large node for Kafka Producer Java script</li>
</ul>

## Data Pipeline
[Back to Table of contents](README.md#table-of-contents)

The image below depicts the underlying data pipeline.

![Alt text](/pipeline.png?raw=true "Pipeline")

### Data source
Two streams of data in JSON format are collected: real-time telemetry data(from
trucks) and user fuel request.
The Vinli Web API provides access over HTTP/SSL to the data collected from the
Vinli devices. Telemetry Service - provides access to time-series data for all
vehicle telemetry gathered on a device-by-device basis.

### Kafka and Kafka Connect
The data is ingested into 2 separate topics, one for truck data and one for
fuel request.
Kafka Connect is uses to consume the messages from both the kafka topics, parse
the data and store into the database. Kafka connect leverages Kafka’s
parallelism for horizontally scaling high volume data streams, and has inbuilt
offset management.

### Databases
The data is stored in PostGres DB with PostGIS extension. Its geospatial
capabilities are used to find the closest trucks based on user fuel requests
lat, long.

There are three tables: 
deviceDataTable:  Store the data related to the truck, and also the history of
every data sample for the truck.
deviceTimeTable : This table holds the latest sample for a truck at any time,
only one entry per truck.
fuelTable: Details of the user fuel request.

## Performance
[Back to Table of contents](README.md#table-of-contents)
The current system can process upto 2000 events per sec.

## Presentation
[Presentation](http://bit.do/fuelup) and demo [video](https://youtu.be/Ld0NZB_4xOQ) for FuelUp.
