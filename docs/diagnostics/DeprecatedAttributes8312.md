# Устаревшие объекты платформы 8.3.12 (DeprecatedAttributes8312)

|      Тип      |    Поддерживаются<br>языки    |     Важность     |    Включена<br>по умолчанию    |    Время на<br>исправление (мин)    |     Теги     |
|:-------------:|:-----------------------------:|:----------------:|:------------------------------:|:-----------------------------------:|:------------:|
| `Дефект кода` |             `BSL`             | `Информационный` |              `Да`              |                 `1`                 | `deprecated` |

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики
<!-- Описание диагностики заполняется вручную. Необходимо понятным языком описать смысл и схему работу -->
В платформе 8.3.12 следующие элементы стали устаревшими:

* Для системного перечисления `ГруппировкаПодчиненныхЭлементовФормы` реализовано значение `ГоризонтальнаяВсегда`, значение `ГруппировкаПодчиненныхЭлементовФормы.Горизонтальная` считается устаревшим
* Системное перечисление `ОриентацияМетокДиаграммы` более не доступно. Актуальный вариант `ОриентацияПодписейДиаграммы`
* Свойства и методы объекта Диаграмма устарели и не рекомендуются к использованию:
   * `ПалитраЦветов`;
   * `ЦветНачалаГрадиентнойПалитры`;
   * `ЦветКонцаГрадиентнойПалитры`; 
   * `МаксимальноеКоличествоЦветовГрадиентнойПалитры`; 
   * `ПолучитьПалитру()`;
   * `УстановитьПалитру()`.
  
* Названия свойств объекта `ОбластьПостроенияДиаграммы`:
   * `ОтображатьШкалу`
   * `ЛинииШкалы`
   * `ЦветШкалы`
  
* Следующие свойства объекта `ОбластьПостроенияДиаграммы` являются устаревшими, не рекомендуются для использования и поддерживаются для совместимости:  
   * `ОтображатьПодписиШкалыСерии` - рекомендуется использовать `ШкалаСерий.ПоложениеПодписейШкалы`
   * `ОтображатьПодписиШкалыТочек` - рекомендуется использовать `ШкалаТочек.ПоложениеПодписейШкалы`
   * `ОтображатьПодписиШкалыЗначений` - рекомендуется использовать `ШкалаЗначений.ПоложениеПодписейШкалы`
   * `ОтображатьЛинииЗначенийШкалы` - рекомендуется использовать `ШкалаЗначений.ОтображениеЛинийСетки`
   * `ФорматШкалыЗначений` - рекомендуется использовать `ШкалаЗначений.ФорматПодписей`
   * `ОриентацияМеток` - доступа рекомендуется использовать `ШкалаТочек.ОриентацияПодписей`
  
* Свойства `ОтображатьЛегенду` и `ОтображатьЗаголовок` объектов `Диаграмма`, `ДиаграммаГанта`, `СводнаяДиаграмма` являются устаревшими и не рекомендуются для использования
* Метод глобального контекста `ОчиститьЖурналРегистрации()` применим только к журналу в формате `SQLite`, признан устаревшим и его использование не рекомендуется
  
## Источники
<!-- Необходимо указывать ссылки на все источники, из которых почерпнута информация для создания диагностики -->

Источник: [Описание изменений платформы 8.3.12](https://dl04.1c.ru/content/Platform/8_3_12_1714/1cv8upd_8_3_12_1714.htm)

## Сниппеты

<!-- Блоки ниже заполняются автоматически, не трогать -->
### Экранирование кода

```bsl
// BSLLS:DeprecatedAttributes8312-off
// BSLLS:DeprecatedAttributes8312-on
```

### Параметр конфигурационного файла

```json
"DeprecatedAttributes8312": false
```
