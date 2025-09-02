// ...existing code...
async fetchPractices() {
  const response = await api.getPractices();
  // 根据id去重
  const unique = Object.values(
    response.data.reduce((acc, cur) => {
      acc[cur.id] = cur;
      return acc;
    }, {})
  );
  this.practices = unique;
}
// ...existing code...

