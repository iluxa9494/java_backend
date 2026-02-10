import dayjs from "dayjs";

export default function changeLocale(newLocal) {
    dayjs.locale(newLocal);
}