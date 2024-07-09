# Клиент для почты
Существует веб версия почты моего вуза и для неё нет открытого API...</br>

## Решение 
Написать клиент, который будет парсить нужные странички, переходить по всем необходимым ссылкам для получения писем с почты и отправки их в тг боте 🗨️

## Quick start
Для запуска бота необходимо указать необходимые данные в application.properties (на основе данных в application.properties.origin)

Поля default.user.* сделаны для того, чтобы после старта в "БД" уже был добавлен нужный пользователь

Настройка default.user.only=true дает возможность пользоваться ботом и получать сообщения только default-ному пользователю


