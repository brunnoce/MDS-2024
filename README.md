# Ktor Project - Docker Setup

Este proyecto utiliza Docker para facilitar la configuración y ejecución del entorno de desarrollo. Todo lo que necesitas está definido en el archivo `docker-compose.yml`.

## Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

- [Docker Desktop](https://www.docker.com/products/docker-desktop) (Asegúrate de que Docker esté en ejecución, sino funciona o tira error, ejecutar como ADMINISTRADOR)
- [Mongo Compass](https://www.mongodb.com/products/tools/compass) (Permite ver la base de datos)
- [Postman](https://www.postman.com/downloads/) (Manejo de peticiones)

INSTRUCCIONES:

1. Clonar el Repositorio:
Clona el repositorio en tu máquina local:
- `git clone https://github.com/brunnoce/MDS-2024.git`

2. Descargar las Imágenes de MongoDB y Mongo Express
- Antes de iniciar los servicios, descarga las imágenes necesarias ejecutando en la terminal del IDE:
```bash
docker pull mongo
docker pull mongo-express
```
3. Iniciar los Servicios con Docker Compose
- Con las imágenes descargadas, ahora puedes iniciar todos los servicios definidos en el archivo docker-compose.yml con el siguiente comando:
```bash
docker-compose up
```
Este comando levantará los contenedores para MongoDB, Mongo Express, y la aplicación Ktor.

4. Verificar la Aplicación
Una vez que los servicios estén corriendo, puedes verificar el estado de la aplicación accediendo a:
- AplicacttionKt (http://127.0.0.1:8080/)
  
6. Detener los Servicios
Para detener los servicios ejecuta:
```bash
docker-compose down
```
Esto detendrá y eliminará todos los contenedores creados por `docker-compose up`.

