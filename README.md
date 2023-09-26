# java-filmorate
![](https://github.com/FantasticNone/java-filmorate/blob/add-database/ERD.jpeg)

**Примеры запросов для основных операций**

:heavy_check_mark: Получение всех пользователей
```
SELECT *
FROM User u
```
:heavy_check_mark: Получение списка всех друзей пользователя
```
SELECT u.*
FROM User u
JOIN Friends f ON u.user_id = f.user2Id
WHERE f.status = true AND f.user1Id = {userId};
```
:heavy_check_mark: Получение списка общих друзей пользователей
```
SELECT u.*
FROM User u
JOIN Friends f1 ON u.user_id = f1.user2Id
JOIN Friends f2 ON f1.user1Id = f2.user1Id AND f2.user2Id = {otherId}
WHERE f.status = true AND f1.user1Id = {userId};
```
:ballot_box_with_check: Получение всех фильмов
```
SELECT *
FROM Film f
```
:ballot_box_with_check: Получение топ-10 фильмов
```
SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count
FROM Film AS f
LEFT JOIN Likes AS l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes_count DESC
LIMIT 10;
```
