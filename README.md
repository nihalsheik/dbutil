
DB db = DbUtil.configure("jdbc:mysql://ip:port/db","user","pwd");
Map<String, Object> result = db.queryForMap("select * from person");

Options:

List<Object[]> list = queryForObjectList(String sql, Object... args)

Object[] queryForObject(String sql, Object... args)

List<Map<String, Object>> queryForMapList(String sql, Object... args)

Map<String, Object> queryForMap(String sql, Object... args)

public List<DataMap> queryForDataMapList(String sql, Object... args)

DataMap queryForDataMap(String sql, Object... args)

T queryForBean(String sql, Class<? extends T> type, Object... args)

List<T> queryForBeanList(String sql, Class<? extends T> type, Object... args)

