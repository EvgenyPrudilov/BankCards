<h1>🚀 Разработка Системы Управления Банковскими Картами</h1>

<h2>📁 Стартовая структура</h2>
  <p>
    Проектная структура с директориями и описательными файлами (<code>README Controller.md</code>, <code>README Service.md</code> и т.д.) уже подготовлена.<br />
    Все реализации нужно добавлять <strong>в соответствующие директории</strong>.
  </p>
  <p>
    После завершения разработки <strong>временные README-файлы нужно удалить</strong>, чтобы они не попадали в итоговую сборку.
  </p>
  
<h2>📝 Описание задачи</h2>
  <p>Разработать backend-приложение на Java (Spring Boot) для управления банковскими картами:</p>
  <ul>
    <li>Создание и управление картами</li>
    <li>Просмотр карт</li>
    <li>Переводы между своими картами</li>
  </ul>

<h2>💳 Атрибуты карты</h2>
  <ul>
    <li>Номер карты (зашифрован, отображается маской: <code>**** **** **** 1234</code>)</li>
    <li>Владелец</li>
    <li>Срок действия</li>
    <li>Статус: Активна, Заблокирована, Истек срок</li>
    <li>Баланс</li>
  </ul>

## Быстрый запуск (Docker Compose)

### 1. Генерация Keystore
Для работы сервиса авторизации (подпись JWT) необходимо локально сгенерировать файл `keystore.p12`.

1. Выполните команду в каталоге ресурсов проекта (замените `YOUR_SECRET_PASSWORD` на ваш пароль и AUTHENTICATION_SERVICE_JWT_KEYSTORE_ALIAS на имя алиаса):
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
