export default (payload = null) => {
  const query = [];
  if (payload)
    Object.entries(payload).forEach(([key, value]) => {
      if (value) query.push(`${key}=${value}`);
    });
  return query;
};






