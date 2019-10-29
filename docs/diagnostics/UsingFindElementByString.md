# Использование методов "НайтиПоНаименованию" и "НайтиПоКоду"

| Тип | Поддерживаются<br/>языки | Важность | Включена<br/>по умолчанию | Время на<br/>исправление (мин) | Тэги |
| :-: | :-: | :-: | :-: | :-: | :-: |
| `Дефект кода` | `BSL` | `Важный` | `Нет` | `2` | `standard`<br/>`badpractice`<br/>`performance` |

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики

Диагностика отлавливает использование методов "НайтиПоНаименованию" или "НайтиПоКоду", используюя "хардкод".

## Примеры

```bsl
Должность = Справочники.Должности.НайтиПоНаименованию("Ведущий бухгалтер");
```

или

```bsl
Должность = Справочники.Должности.НайтиПоКоду("00-0000001");
```

Допустимо использование:
```bsl
Справочники.Валюты.НайтиПоКоду(ТекущиеДанные.КодВалютыЦифровой);
```
```bsl
Справочники.КлассификаторБанков.НайтиПоКоду(СведенияОБанке.БИК);
```