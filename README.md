# Architector

## Запуск проекта

Запустить ``postgresql``:

```bash
docker-compose up -d
```

Собрать проект:
```bash
./gradlew clean build
```

После, запустить приложение следующим способом:

```bash
./gradlew :backend:bootRun
```