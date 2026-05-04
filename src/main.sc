theme: /

    # ---------------------- ГОРЯЧАЯ ЛИНИЯ ----------------------
    state: HotlineRequest
        q: * номер телефона *
        q: * телефон *
        q: * горячая линия *
        q: * номер поддержки *
        q: * как связаться *
        q: * позвонить *
        
        script:
            // Проверяем, первый ли это запрос или повторный
            var firstRequest = $session.firstRequest;
            
            if (!firstRequest) {
                // Первый запрос - предлагаем помощь в чате
                $session.firstRequest = true;
                $reactions.answer("Конечно, телефон службы поддержки есть.\nНо, возможно, я смогу помочь вам быстрее, так как многие вопросы можно решить прямо в чате. Что именно вас интересует?");
                $reactions.transition("/");
            } else {
                // Повторный запрос - спрашиваем про оператора
                $reactions.answer("Я могу перевести вас на оператора для решения вопроса в чате, устроит?");
                $reactions.transition("HotlineAnswer");
                // Сбрасываем флаг после второго вопроса
                $session.firstRequest = false;
            }

    # ---------------------- ОБРАБОТКА ОТВЕТА ПРО ОПЕРАТОРА ----------------------
    state: HotlineAnswer
        script:
            var button = $message.buttonsHit;
            var query = $message.text || "";
            
            if (button == "Нет, нужен телефон" || query.indexOf("нет") != -1) {
                $reactions.answer("Номер поддержки 8 (495) 981-0-981 работает 24/7.\nЗвонок платный, стоимость зависит от тарифов вашего оператора связи.");
                $reactions.transition("CloseDialog");
            } else if (button == "Да, переведите" || query.indexOf("да") != -1) {
                $reactions.answer("Соединяю с оператором. Пожалуйста, подождите.");
                $reactions.transition("TransferToOperator");
            } else {
                $reactions.answer("Пожалуйста, ответьте 'Да' или 'Нет'.");
                $reactions.transition("HotlineAnswer");
            }
        buttons:
            "Да, переведите"
            "Нет, нужен телефон"

    # ---------------------- СПОСОБЫ ОПЛАТЫ ----------------------
    state: PaymentMethods
        q: * как оплатить *
        q: * оплатить кредит *
        q: * где внести наличные *
        q: * внести наличные *
        q: * как пополнить *
        q: * оплата *
        
        a: Выберите, что вас интересует:
        buttons:
            "Как пополнить в приложении?"
            "Где внести наличные?"
            "Сложности с оплатой"
        go: PaymentAnswer

    state: PaymentAnswer
        script:
            var button = $message.buttonsHit;
            var query = $message.text || "";
            
            if (button == "Как пополнить в приложении?" || query.indexOf("приложен") != -1) {
                $reactions.answer("Для пополнения продукта перейдите в него и выберите «Пополнить».\n💰 ознакомиться с комиссией можно при оплате.");
                $reactions.transition("CloseDialog");
            } else if (button == "Где внести наличные?" || query.indexOf("наличн") != -1) {
                $reactions.answer("Внести наличные можно:\n- в офисе нашего банка (банкомат/терминал/касса);\n- в банкоматах: «ВТБ», «Альфа-Банка», «Райффайзенбанк».\n\nКомиссии нет, а внести можно от 500 тыс. до 1.5 млн.\n\nПодобрать удобный адрес и ознакомиться с режимом работы можно в разделе «Отделения и банкоматы» (https://rencredit.ru/addresses/).\n\n🏛 Подробная информация о всех способах оплаты доступна на нашем сайте в разделе «Платежи и переводы» (https://rencredit.ru/payment/).");
                $reactions.transition("CloseDialog");
            } else if (button == "Сложности с оплатой" || query.indexOf("сложн") != -1) {
                $reactions.answer("Вас понял, уже перевожу.");
                $reactions.transition("TransferToOperator");
            } else {
                $reactions.answer("Пожалуйста, выберите вариант на кнопках.");
                $reactions.transition("PaymentMethods");
            }

    # ---------------------- ЗАВЕРШЕНИЕ ДИАЛОГА ----------------------
    state: CloseDialog
        a: Я ответил на ваш вопрос? Осталось ли что-то, чем я могу помочь?
        buttons:
            "Да, спасибо, все хорошо"
            "Нет, остался вопрос"
            "Показать список моих возможностей"
        go: CloseAnswer

    state: CloseAnswer
        script:
            var button = $message.buttonsHit;
            var query = $message.text || "";
            
            if (button == "Да, спасибо, все хорошо" || query.indexOf("спасибо") != -1) {
                $reactions.answer("Рад был помочь! Всегда обращайтесь. Хорошего дня!");
                $reactions.transition("Exit");
            } else if (button == "Показать список моих возможностей" || query.indexOf("возможн") != -1) {
                $reactions.answer("Вот что я умею:\n- Спросить номер горячей линии\n- Рассказать, как оплатить кредит или внести наличные\n- Подсказать адрес отделения\n\nПросто напишите мне ваш вопрос!");
                $reactions.transition("/");
                // Сбрасываем флаг первого запроса телефона
                $session.firstRequest = false;
            } else {
                $reactions.answer("Давайте попробуем еще раз. Задайте ваш вопрос, и я постараюсь помочь.");
                $reactions.transition("/");
                // Сбрасываем флаг первого запроса телефона
                $session.firstRequest = false;
            }

    # ---------------------- ОБРАБОТЧИК ОШИБОК ----------------------
    state: Fallback
        event!: noMatch
        a: Извините, я вас не совсем понял.
        a: Я могу:\n- сказать номер телефона банка\n- рассказать как оплатить кредит\n- подсказать где внести наличные
        a: Напишите, пожалуйста, что вас интересует.
        go: /

    # ---------------------- ПЕРЕВОД НА ОПЕРАТОРА ----------------------
    state: TransferToOperator
        event!: transferToOperator
        a: Пожалуйста, подождите, я соединяю вас с оператором чата.

    # ---------------------- ВЫХОД ----------------------
    state: Exit
        event!: exit