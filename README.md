[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)

# lab1-wa2025

## Docker build and run

### Building the image

On a terminal:

```bash
cd RouteAnalyzer
docker build -t route-analyzer .
```

### Running the container

`<Input path>`: The path to the directory that contains both waypoints.csv and custom-parameters.yml.

`<Output path>`: The path to the directory were you want to save the results.

On a terminal:

```bash
docker run -d -p 8080:80 -v <Input path>:/app/inputFiles/ -v <Output path>:/app/outputFiles route-analyzer
```
