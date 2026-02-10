export default function filterPhone(phone) {
  if (!phone) return '';
  return phone
    .replace('^8', '7')
    .replace(/(\d{1,3})(\d{3})(\d{3})(\d{2})(\d{2})/, '+$1 ($2) $3-$4-$5');
}