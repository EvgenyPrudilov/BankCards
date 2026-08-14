# Система Управления Банковскими Картами

## Возможности:

### Администратор:
- Создаёт, блокирует, активирует, удаляет карты
- Управляет пользователями
- Видит все карты

### Пользователь:
- Просматривает свои карты (поиск + пагинация)
- Запрашивает блокировку карты
- Делает переводы между своими картами
- Смотрит баланс
  
## Требования:
### Аутентификация и авторизация:
- Spring Security + JWT
- Роли: ADMIN и USER

### Безопасность:
- Шифрование данных
- Ролевой доступ
- Маскирование номеров карт

## Атрибуты карты
- Номер карты (зашифрован, отображается маской: `**** **** **** 1234`)
- Владелец
- Срок действия
- Статус: Активна, Заблокирована, Истек срок
- Баланс

## Технологический стек
- **Backend:** Java 17, Spring Boot 4.x, Spring Security
- **Data Access:** Spring Data JPA (Hibernate), PostgreSQL, Liquibase
- **Infrastructure:** Docker, Docker Compose
- **Utilities:** Lombok, Mapper, Swagger UI / OpenAPI

## Быстрый запуск (Docker Compose)

### 1. Генерация Keystore
Для работы сервиса авторизации (подпись JWT) необходимо локально сгенерировать файл `keystore.p12`.

1. Выполните команду в каталоге ресурсов проекта (замените `YOUR_SECRET_PASSWORD` на ваш пароль и `AUTHENTICATION_SERVICE_JWT_KEYSTORE_ALIAS` на имя алиаса):
```bash
keytool -genkeypair \
  -alias AUTHENTICATION_SERVICE_JWT_KEYSTORE_ALIAS \
  -keyalg EC \
  -groupname secp256r1 \
  -validity 365 \
  -keystore keystore.p12 \
  -storetype PKCS12 \
  -storepass AUTHENTICATION_SERVICE_JWT_KEYSTORE_PASSWORD \
  -dname "CN=auth-server, OU=Development, O=Cohenrol, C=NL" \
  -noprompt
```

### 2. Переменные окружения
В корне проекта должен быть `.env` файл, содержащий различные пароли и значения портов, например:
```env
DB_NAME=bank_cards
DB_USERNAME=bank_admin
DB_PASSWORD=bank_secure_password123

DB_INNER_PORT=5432
DB_EXTERNAL_PORT=5555

SERVICE_INNER_PORT=8080
SERVICE_EXTERNAL_PORT=8080

SERVICE_JWT_KEYSTORE_PASSWORD=supersecurepassword
SERVICE_JWT_KEYSTORE_ALIAS=auth-server-ec
SERVICE_JWT_KEYSTORE_LOCATION=classpath:keystore.p12

SERVICE_INIT_ADMIN_USERNAME=super_admin
SERVICE_INIT_ADMIN_PASSWORD=password
SERVICE_INIT_ADMIN_EMAIL=root@bank.com
```

### 3. Запуск инфраструктуры
Запустите сборку:
```bash
docker compose --env-file .env up --build
```
