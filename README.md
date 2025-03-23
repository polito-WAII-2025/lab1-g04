[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)

# lab1-wa2025

## Docker build and run

### Building the image

On a terminal:

- <code>cd RouteAnalyzer </code>
- <code>docker build -t route-analyzer .</code>

### Running the container

On a terminal:

- <code>cd RouteAnalyzer</code>
- <code>docker run -d -p 3003:80 -v \<Path to waypoints.csv\>:/app/inputFiles/waypoints.csv -v \<Path to
  custom-parameters.yml\>:/app/inputFiles/custom-parameters.yml route-analyzer</code>
