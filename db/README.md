Instalar [docker compose](https://docs.docker.com/compose/install/).

En este directorio, ejecutar:

```bash
docker compose up -d
```

La base de datos ya debería estar corriendo.

Para conectase:

```bash
docker compose exec mysql mysql -u root -pWin2002Racedb$
```

Para detener la base de datos:

```bash
docker compose stop
```
