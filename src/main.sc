theme: /

    # ---------------------- СЦЕНАРИЙ №1: ГОРЯЧАЯ ЛИНИЯ ----------------------
    state: HotlineRequest
        intent!: /телефон горячей линии
        intent!: /как связаться с банком
        intent!: /горячая линия
        intent!: /номер поддержки
        intent!: /позвонить в банк

        state: SuggestChatHelp
            a: Конечно, телефон службы поддержки есть.
            a: Но, возможно, я смогу помочь вам быстрее, так как многие вопросы можно решить прямо в чате. Что именно вас интересует?
            go: WaitForRepeat

        state: WaitForRepeat
            intent!: /телефон горячей линии
            intent!: /как связаться с банком
            intent!: /горячая линия
            intent!: /номер поддержки
            intent!: /позвонить в банк

            a: Я могу перевести вас на оператора для решения вопроса в чате, устроит?
            buttons:
                "Да, переведите"
                "Нет, нужен телефон"
            go: HandleTransferChoice

        state: HandleTransferChoice
            script:
                var buttonText = $dialogs.buttonsHit.button;
                if (buttonText == "Да, переведите") {
                    $dialogs.answer = "Соединяю с оператором. Пожалуйста, подождите.";
                    $dialogs.transition = "/TransferToOperator";
                } else {
                    $dialogs.answer = "Номер поддержки 8 (495) 981-0-981 работает 24/7.\nЗвонок платный, стоимость зависит от тарифов вашего оператора связи.";
                    $dialogs.transition = "/CloseDialog";
                }
            a: {{$dialogs.answer}}
            go: {{$dialogs.transition}}

    # ---------------------- СЦЕНАРИЙ №2: СПОСОБЫ ОПЛАТЫ ----------------------
    state: PaymentMethods
        intent!: /как оплатить
        intent!: /способы оплаты
        intent!: /оплатить кредит
        intent!: /где внести наличные
        intent!: /как пополнить

        a: Выберите, что вас интересует:
        buttons:
            "Как пополнить в приложении?"
            "Где внести наличные?"
            "Сложности с оплатой"
        go: ProcessPaymentChoice

        state: ProcessPaymentChoice
            script:
                var buttonText = $dialogs.buttonsHit.button;
                var query = $request.query;
                
                if (buttonText == "Как пополнить в приложении?" || query.indexOf("приложен") != -1) {
                    $dialogs.answer = "Для пополнения продукта перейдите в него и выберите «Пополнить».\n💰 ознакомиться с комиссией можно при оплате.";
                    $dialogs.transition = "/CloseDialog";
                } else if (buttonText == "Где внести наличные?" || query.indexOf("наличн") != -1) {
                    $dialogs.answer = "Внести наличные можно:\n- в офисе нашего банка (банкомат/терминал/касса);\n- в банкоматах: «ВТБ», «Альфа-Банка», «Райффайзенбанк».\n\nКомиссии нет, а внести можно от 500 тыс. до 1.5 млн.\n\nПодобрать удобный адрес и ознакомиться с режимом работы можно в разделе «Отделения и банкоматы» (https://rencredit.ru/addresses/).\n\n🏛 Подробная информация о всех способах оплаты доступна на нашем сайте в разделе «Платежи и переводы» (https://rencredit.ru/payment/).";
                    $dialogs.transition = "/CloseDialog";
                } else if (buttonText == "Сложности с оплатой") {
                    $dialogs.answer = "Вас понял, уже перевожу.";
                    $dialogs.transition = "/TransferToOperator";
                } else {
                    $dialogs.answer = "Пожалуйста, выберите один из вариантов на кнопках.";
                    $dialogs.transition = "/PaymentMethods";
                }
            a: {{$dialogs.answer}}
            go: {{$dialogs.transition}}

    # ---------------------- ЗАВЕРШЕНИЕ ДИАЛОГА ----------------------
    state: CloseDialog
        a: Я ответил на ваш вопрос? Осталось ли что-то, чем я могу помочь?
        buttons:
            "Да, спасибо, все хорошо"
            "Нет, остался вопрос"
            "Показать список моих возможностей"

        state: FinalChoice
            script:
                var buttonText = $dialogs.buttonsHit.button;
                
                if (buttonText == "Да, спасибо, все хорошо") {
                    $dialogs.answer = "Рад был помочь! Всегда обращайтесь. Хорошего дня!";
                    $dialogs.transition = "/Exit";
                } else if (buttonText == "Показать список моих возможностей") {
                    $dialogs.answer = "Вот что я умею:\n- Спросить номер горячей линии\n- Рассказать, как оплатить кредит или внести наличные\n- Подсказать адрес отделения";
                    $dialogs.transition = "/";
                } else {
                    $dialogs.answer = "Давайте попробуем еще раз. Задайте ваш вопрос, и я постараюсь помочь.";
                    $dialogs.transition = "/";
                }
            a: {{$dialogs.answer}}
            go: {{$dialogs.transition}}

    # ---------------------- ОБРАБОТЧИК FALLBACK ----------------------
    state: Fallback
        event!: noMatch
        a: Извините, я вас не совсем понял. Я бот-ассистент банка и могу помочь с номерами телефонов, способами оплаты и адресами отделений.
        a: Попробуйте перефразировать ваш вопрос или воспользоваться кнопками.
        go: /CloseDialog

    # ---------------------- ПЕРЕВОД НА ОПЕРАТОРА ----------------------
    state: TransferToOperator
        event!: transferToOperator
        a: Пожалуйста, подождите, я соединяю вас с оператором чата.