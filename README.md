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

## Запустить сервис на удаленной машине

Зайти на машину и установить docker и docker-compose по следующей ссылке:
``` http request
https://acloudxpert.com/how-to-install-docker-compose-on-amazon-linux-ami/
```

Скопировать docker-compose файл:
```bash
scp -i ./deploy/architector_ssh_key.pem  ./docker-compose.yml ec2-user@ip_address:/home/ec2-user/
```
 Подключиться к машине:
 ```bash
ssh -i ./deploy/architector_ssh_key.pem  ec2-user@ip_address
```

