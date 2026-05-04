theme: /

    state: HotlineRequest
        # Расширенный список фраз для распознавания
        q: * (телефон горячей линии|номер горячей линии|горячая линия) *
        q: * номер телефона *
        q: * телефон банка *
        q: * как связаться с банком *
        q: * номер поддержки *
        q: * позвонить в банк *
        q: * куда позвонить *
        q: * дайте номер *

        state: SuggestChatHelp
            a: Конечно, телефон службы поддержки есть.
            a: Но, возможно, я смогу помочь вам быстрее, так как многие вопросы можно решить прямо в чате. Что именно вас интересует?
            go: WaitForRepeat

        state: WaitForRepeat
            q: * номер телефона *
            q: * телефон *
            q: * позвонить *
            
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