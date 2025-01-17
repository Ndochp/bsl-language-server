# Отсутствие проверки на NULL для полей из присоединяемых таблиц (FieldsFromJoinsWithoutIsNull)

|   Тип    |    Поддерживаются<br>языки    |  Важность   |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |                         Теги                         |
|:--------:|:-----------------------------:|:-----------:|:------------------------------:|:-----------------------------------:|:----------------------------------------------------:|
| `Ошибка` |         `BSL`<br>`OS`         | `Критичный` |              `Да`              |                 `2`                 |       `sql`<br>`suspicious`<br>`unpredictable`       |

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики
<!-- Описание диагностики заполняется вручную. Необходимо понятным языком описать смысл и схему работу -->
Диагностика проверяет поля из левых, правых, полных соединений, для которых не выполняется проверка с помощью `ЕСТЬNULL()` или `НЕ ЕСТЬ NULL` или или `ЕСТЬ НЕ NULL`.

В запросах нельзя использовать реквизиты из присоединяемых слева или справа таблиц без проверки значений на `NULL`. 
Указанное обращение может приводить к ошибкам, если условие соединения не выполнено и нет подходящих записей в левой или правой таблице.
В итоге в результате запроса можно получить неожиданные данные и система может повести себя неверным образом.

Важно помнить, что любое сравнение значения `NULL` с любым другими выражением всегда ложно, даже сравнение `NULL` и `NULL` всегда ложно. 
Смотрите ниже пример подобных неверных сравнений.
Поэтому нужно правильно выполнять сравнение с `NULL` - или через оператор `ЕСТЬ NULL` или через функцию `ЕСТЬNULL()`.

Также достаточно часто используются левые\правые соединения, хотя данные позволяют использовать внутреннее соединение, в этом случае не нужны проверки на `NULL`.

Или дополнительные проверки реквизитов выполняются в коде 1С, а не в тексте запроса. Подобные обращения затрудняют чтение кода и рефакторинг кода, т.к. контекст обращения к реквизиту приходится учитывать в нескольких местах. 
В дополнение нужно учитывать, что простые проверки в запросе выполняются чуть быстрее и проще, чем в интерпретируемом коде 1С.

Указанные проблемы являются одними из самых частых ошибок разработчиков 1С самого разного уровня компетенций.

## Примеры
<!-- В данном разделе приводятся примеры, на которые диагностика срабатывает, а также можно привести пример, как можно исправить ситуацию -->
Пример, показывающий проблемы сравнения с NULL - в примере 2 таблицы соединяются заведомо неверно и приведены разные способы сравнения
```sdbl
ВЫБРАТЬ
  ВЫБОР
    КОГДА Левая.Поле2 = 0 ТОГДА "Равно 0 - не работает"
    КОГДА Левая.Поле2 <> 0 ТОГДА "НЕ Равно 0 - не работает"
    КОГДА Левая.Поле2 = NULL ТОГДА "Равно NULL - не работает"
    КОГДА Левая.Поле2 ЕСТЬ NULL ТОГДА "ЕСТЬ NULL - этот вариант работает"
    КОГДА ЕСТЬNULL(Левая.Поле2, 0) = 0  ТОГДА "ЕСТЬNULL() - этот вариант также работает"
    ИНАЧЕ "Иначе"
  КОНЕЦ
ИЗ
  Первая КАК Первая
  ЛЕВОЕ СОЕДИНЕНИЕ Левая КАК Левая
  ПО Ложь // чтобы не было соединения
```

Подозрительный код обращения к реквизиту присоединенной таблицы
```sdbl
ВЫБРАТЬ 
  ДокументыПродажи.Ссылка КАК ДокПродажи,
  РегистрПродажи.Сумма КАК Сумма // здесь ошибка
ИЗ Документ.РеализацияТоваровУслуг КАК ДокументыПродажи
ЛЕВОЕ СОЕДИНЕНИЕ  РегистрНакопления.Продажи КАК РегистрПродажи
ПО ДокументыПродажи.Ссылка = РегистрПродажи.Документ
```
Правильно
```sdbl
ВЫБРАТЬ 
  ДокументыПродажи.Ссылка КАК ДокПродажи,
  ЕстьNULL(РегистрПродажи.Сумма, 0) КАК Сумма
ИЗ Документ.РеализацияТоваровУслуг КАК ДокументыПродажи
ЛЕВОЕ СОЕДИНЕНИЕ  РегистрНакопления.Продажи КАК РегистрПродажи
ПО ДокументыПродажи.Ссылка = РегистрПродажи.Документ
```
Также правильно
```sdbl
ВЫБРАТЬ 
  ДокументыПродажи.Ссылка КАК ДокПродажи,
  ВЫБОР КОГДА РегистрПродажи.Сумма Есть NULL ТОГДА 0
  ИНАЧЕ  РегистрПродажи.Сумма 
  КОНЕЦ КАК Сумма
ИЗ Документ.РеализацияТоваровУслуг КАК ДокументыПродажи
ЛЕВОЕ СОЕДИНЕНИЕ  РегистрНакопления.Продажи КАК РегистрПродажи
ПО ДокументыПродажи.Ссылка = РегистрПродажи.Документ
```
И еще возможный вариант
```sdbl
ВЫБРАТЬ 
  ДокументыПродажи.Ссылка КАК ДокПродажи,
  РегистрПродажи.Сумма КАК Сумма
ИЗ Документ.РеализацияТоваровУслуг КАК ДокументыПродажи
ЛЕВОЕ СОЕДИНЕНИЕ  РегистрНакопления.Продажи КАК РегистрПродажи
ПО ДокументыПродажи.Ссылка = РегистрПродажи.Документ
ГДЕ
    РегистрПродажи.Документ ЕСТЬ НЕ NULL
    //или НЕ РегистрПродажи.Документ ЕСТЬ NULL
```
Последний вариант - не самый лучший, т.к. в нем фактически эмулируется внутреннее соединение. 
И проще явно указать `ВНУТРЕННЕЕ СОЕДИНЕНИЕ` вместо использования левого соединения с проверкой `ЕСТЬ НЕ NULL` или `НЕ ЕСТЬ NULL`

## Источники
<!-- Необходимо указывать ссылки на все источники, из которых почерпнута информация для создания диагностики -->
<!-- Примеры источников

* Источник: [Стандарт: Тексты модулей](https://its.1c.ru/db/v8std#content:456:hdoc)
* Полезная информация: [Отказ от использования модальных окон](https://its.1c.ru/db/metod8dev#content:5272:hdoc)
* Источник: [Cognitive complexity, ver. 1.4](https://www.sonarsource.com/docs/CognitiveComplexity.pdf) -->
* [Использование функции ЕСТЬNULL() - Стандарт](https://its.1c.ru/db/metod8dev/content/2653/hdoc)
* [Понятие "пустых" значений - Методические рекомендации 1С](https://its.1c.ru/db/metod8dev/content/2614/hdoc/_top/%D0%B5%D1%81%D1%82%D1%8C%20null)
    * [Чем отличается значение типа Неопределено и значение типа Null? - Методические рекомендации 1С](https://its.1c.ru/db/metod8dev#content:2516:hdoc)
* [Особенности связи с виртуальной таблицей остатков - Методические рекомендации 1С](https://its.1c.ru/db/metod8dev/content/2657/hdoc/_top/%D0%B5%D1%81%D1%82%D1%8C%20null)
* [Сортировка по полю запроса, которое может потенциально содержать NULL - статья "Упорядочивание результатов запроса" - Стандарт](https://its.1c.ru/db/v8std/content/412/hdoc/_top/%D0%B5%D1%81%D1%82%D1%8C%20null)
* [Поля иерархического справочника могут содержать NULL - Методические рекомендации 1С](https://its.1c.ru/db/metod8dev/content/2649/hdoc/_top/%D0%B5%D1%81%D1%82%D1%8C%20null)
    * [Как получить записи иерархической таблицы и расположить их в порядке иерархии - Методические рекомендации 1С](https://its.1c.ru/db/pubqlang/content/27/hdoc/_top/%D0%B5%D1%81%D1%82%D1%8C%20null)
* [Как получить данные из разных таблиц для одного и того же поля - онлайн-книга "Язык запросов 1С:Предприятия"](https://its.1c.ru/db/pubqlang#content:43:hdoc)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:FieldsFromJoinsWithoutIsNull-off
// BSLLS:FieldsFromJoinsWithoutIsNull-on
```

### Параметр конфигурационного файла

```json
"FieldsFromJoinsWithoutIsNull": false
```
