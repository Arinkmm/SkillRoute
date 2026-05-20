const chatForm = document.querySelector("[data-chat-form]");
const messages = document.querySelector("[data-chat-messages]");
const errorBox = document.querySelector("[data-chat-error]");
const renderedMessageIds = new Set();

const formatTimestamp = (value) => {
    if (!value) {
        return "";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    const now = new Date();
    const sameDay = date.toDateString() === now.toDateString();
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);

    if (sameDay) {
        return new Intl.DateTimeFormat("ru-RU", {
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    if (date.toDateString() === yesterday.toDateString()) {
        return `вчера, ${new Intl.DateTimeFormat("ru-RU", {
            hour: "2-digit",
            minute: "2-digit"
        }).format(date)}`;
    }

    return new Intl.DateTimeFormat("ru-RU", {
        day: "2-digit",
        month: "long",
        hour: "2-digit",
        minute: "2-digit"
    }).format(date);
};

const rememberInitialMessages = () => {
    messages?.querySelectorAll("[data-message-id]").forEach((item) => {
        renderedMessageIds.add(String(item.dataset.messageId));
    });

    messages?.querySelectorAll("[data-message-time]").forEach((item) => {
        item.textContent = formatTimestamp(item.dataset.messageTime || item.textContent);
    });
};

rememberInitialMessages();

document.querySelectorAll("[data-preview-time]").forEach((item) => {
    item.textContent = formatTimestamp(item.dataset.previewTime || item.textContent);
});

if (messages) {
    messages.scrollTop = messages.scrollHeight;
}

if (chatForm) {
    const textField = chatForm.elements.text;
    const submitButton = chatForm.querySelector("button[type='submit']");
    const chatId = chatForm.dataset.chatId;

    const getCsrfHeaders = () => {
        const csrfInput = chatForm.querySelector("input[data-csrf-header]");

        if (!csrfInput) {
            return {};
        }

        return {
            [csrfInput.dataset.csrfHeader || csrfInput.name]: csrfInput.value
        };
    };

    const showError = (message) => {
        if (!errorBox) {
            return;
        }

        errorBox.textContent = message || "";
        errorBox.hidden = !message;
    };

    const isMine = (message) => Boolean(message.mine ?? message.isMine);

    const removeEmptyState = () => {
        messages?.querySelector("[data-empty-chat]")?.remove();
    };

    const appendMessage = (message) => {
        if (!messages || !message?.id) {
            return;
        }

        const id = String(message.id);
        if (renderedMessageIds.has(id)) {
            return;
        }

        removeEmptyState();
        renderedMessageIds.add(id);

        const item = document.createElement("div");
        item.className = `chat-message ${isMine(message) ? "is-mine" : ""}`;
        item.dataset.messageId = id;

        const text = document.createElement("p");
        text.textContent = message.text || "";

        const time = document.createElement("span");
        time.dataset.messageTime = message.createdAt || "";
        time.textContent = formatTimestamp(message.createdAt);

        item.append(text, time);
        messages.append(item);
        messages.scrollTop = messages.scrollHeight;
    };

    const loadMessages = async () => {
        try {
            const response = await fetch(`/chat/${chatId}/messages`);
            const data = await response.json();

            if (!response.ok) {
                showError(data.message || "Не удалось обновить чат");
                return;
            }

            showError("");
            (data.messages || []).forEach(appendMessage);
        } catch (error) {
            showError("Не удалось обновить чат. Проверьте соединение.");
        }
    };

    const autosizeTextArea = () => {
        textField.style.height = "auto";
        textField.style.height = `${Math.min(textField.scrollHeight, 140)}px`;
    };

    textField.addEventListener("input", autosizeTextArea);

    chatForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        showError("");

        const text = textField.value.trim();

        if (!text) {
            showError("Введите текст сообщения");
            return;
        }

        submitButton.disabled = true;

        try {
            const response = await fetch(`/chat/${chatId}/send`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...getCsrfHeaders()
                },
                body: JSON.stringify({ text })
            });
            const data = await response.json();

            if (!response.ok) {
                showError(data.fields?.text || data.message || "Не удалось отправить сообщение");
                return;
            }

            appendMessage(data);
            textField.value = "";
            autosizeTextArea();
        } catch (error) {
            showError("Не удалось отправить сообщение. Попробуйте еще раз.");
        } finally {
            submitButton.disabled = false;
            textField.focus();
        }
    });

    setInterval(loadMessages, 4000);
}
