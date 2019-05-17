# Architector

## Локальный запуск проекта для разработки

Запустить ``postgresql``:

```bash
docker-compose -f docker-compose-db.yml up -d
```

Собрать проект:
```bash
./gradlew clean build
```

После, запустить отдельно бекенд и фронтенд:

```bash
./gradlew :backend:bootRun
```

```bash
cd webclient
yarn start
```

## Запуск сервиса в production

Осуществить сборку проекта:
```bash
./build.sh
```

Затем запустить полученный `jar` файл:
```bash
java -jar ./architector.jar
```

