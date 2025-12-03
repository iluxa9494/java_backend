export default (response) => {
  return {
    total: response.data.totalPages,
    size: response.data.size,
    page: response.data.number + 1,
    totalElements: response.data.totalElements,
  };
};
