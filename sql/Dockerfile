FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=root_password
ENV MYSQL_DATABASE=RaPizz 
ENV MYSQL_USER=rapizz
ENV MYSQL_PASSWORD=rapizz

COPY create.sql /docker-entrypoint-initdb.d/01_create.sql
COPY insert.sql /docker-entrypoint-initdb.d/02_insert.sql