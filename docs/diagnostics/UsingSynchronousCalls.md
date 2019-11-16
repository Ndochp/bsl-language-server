# Использование синхронных вызовов

| Тип | Поддерживаются<br/>языки | Важность | Включена<br/>по умолчанию | Время на<br/>исправление (мин) | Тэги |
| :-: | :-: | :-: | :-: | :-: | :-: |
| `Дефект кода` | `BSL` | `Важный` | `Нет` | `15` | `standard` |

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики

При разработке конфигураций, предназначенных для работы в веб-клиенте, запрещено использовать модальные формы и диалоги и синхронные вызовы. В противном случае, конфигурация окажется неработоспособной в ряде веб-браузеров, так как модальные окна не входят в стандарт веб-разработки, а для обеспечения взаимодействия с пользователем требуются асинхронные средства.

### Ограничение диагностики

На данный момент диагностируется **только использование методов глобального контекста**.

Список методов:

|Метод|Английский вариант|
| :-- | :-- |
|ВОПРОС|DOQUERYBOX|
|ОТКРЫТЬФОРМУМОДАЛЬНО|OPENFORMMODAL|
|ОТКРЫТЬЗНАЧЕНИЕ|OPENVALUE|
|ПРЕДУПРЕЖДЕНИЕ|DOMESSAGEBOX|
|ВВЕСТИДАТУ|INPUTDATE|
|ВВЕСТИЗНАЧЕНИЕ|INPUTVALUE|
|ВВЕСТИСТРОКУ|INPUTSTRING|
|ВВЕСТИЧИСЛО|INPUTNUMBER|
|УСТАНОВИТЬВНЕШНЮЮКОМПОНЕНТУ|INSTALLADDIN|
|УСТАНОВИТЬРАСШИРЕНИЕРАБОТЫСФАЙЛАМИ|INSTALLFILESYSTEMEXTENSION|
|УСТАНОВИТЬРАСШИРЕНИЕРАБОТЫСКРИПТОГРАФИЕЙ|INSTALLCRYPTOEXTENSION|
|ПОДКЛЮЧИТЬРАСШИРЕНИЕРАБОТЫСКРИПТОГРАФИЕЙ|ATTACHCRYPTOEXTENSION|
|ПОДКЛЮЧИТЬРАСШИРЕНИЕРАБОТЫСФАЙЛАМИ|ATTACHFILESYSTEMEXTENSION|
|ПОМЕСТИТЬФАЙЛ|PUTFILE|
|КОПИРОВАТЬФАЙЛ|FILECOPY|
|ПЕРЕМЕСТИТЬФАЙЛ|MOVEFILE|
|НАЙТИФАЙЛЫ|FINDFILES|
|УДАЛИТЬФАЙЛЫ|DELETEFILES|
|СОЗДАТЬКАТАЛОГ|CREATEDIRECTORY|
|КАТАЛОГВРЕМЕННЫХФАЙЛОВ|TEMPFILESDIR|
|КАТАЛОГДОКУМЕНТОВ|DOCUMENTSDIR|
|РАБОЧИЙКАТАЛОГДАННЫХПОЛЬЗОВАТЕЛЯ|USERDATAWORKDIR|
|ПОЛУЧИТЬФАЙЛЫ|GETFILES|ПОМЕСТИТЬФАЙЛЫ|PUTFILES|
|ЗАПРОСИТЬРАЗРЕШЕНИЕПОЛЬЗОВАТЕЛЯ|REQUESTUSERPERMISSION|
|ЗАПУСТИТЬПРИЛОЖЕНИЕ|RUNAPP|

## Источники

* [Ограничение на использование модальных окон и синхронных вызовов](https://its.1c.ru/db/v8std/content/703/hdoc/)
* [Отказ от использования модальных окон](https://its.1c.ru/db/metod8dev#content:5272:hdoc)
* [Соответствие синхронных методов асинхронным аналогам](https://its.1c.ru/db/v838doc#bookmark:dev:TI000000438)
* [Асинхронные вызовы расширений и внешних компонентов](http://v8.1c.ru/o7/201412async/index.htm)