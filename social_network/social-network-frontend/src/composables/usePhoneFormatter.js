export default function formatPhoneNumber(phoneNumber) {
    if (!phoneNumber) {
        return ''; // Возвращаем пустую строку, если phoneNumber не определён
    }
   
    // Удаляем все нечисловые символы
    const digits = phoneNumber.replace(/\D/g, "");

    // Разбиваем строку на части для форматирования
    const match = digits.match(/^(\d{1})(\d{3})(\d{3})(\d{2})(\d{2})$/);

    // Если формат соответствует, форматируем номер
    if (match) {
        return `+${match[1]} (${match[2]}) ${match[3]}-${match[4]}-${match[5]}`;
    }

    // Если номер не соответствует ожидаемому формату, возвращаем исходный
    return phoneNumber;
}