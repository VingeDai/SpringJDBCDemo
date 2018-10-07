package com.test.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.test.pojo.Cost;
import com.test.pojo.CostMapper;

@Repository("jdbcCostDAO")
// @Scope("singleton")// 默认情况是单例
public class JdbcCostDAO extends JdbcDaoSupport implements ICostDao {

	@Resource
	// 将容器中的dataSource给DAOSupport注入
	public void setMyDataSource(DataSource ds) {
		super.setDataSource(ds);// DaoSupport利用ds实例化template对象，使得getJdbcTemplate()可以获取连接
	}

	@Override
	public List<Cost> findAll() throws DataAccessException {
		String sql = "select * from COST";
		CostMapper mapper = new CostMapper();
		List<Cost> list = super.getJdbcTemplate().query(sql, mapper);
		return list;
	}

	@Override
	public List<Cost> findByPage(int page, int pageSize)
			throws DataAccessException {
		String sql = "select ID,NAME,BASE_DURATION,BASE_COST,UNIT_COST,STATUS,DESCR,CREATIME,STARTIME,COST_TYPE from(select ID,NAME,BASE_DURATION,BASE_COST,UNIT_COST,STATUS,DESCR,CREATIME,STARTIME,COST_TYPE,rownum n from COST  where rownum <? order by ID)where n>?";// 分页
		// 算当前页最大行：当前页数2*每页显示5=当前最大行10
		// 小于下一页的最小行
		int nextMin = page * pageSize + 1;
		// 大于上一页的最大行
		int lastMax = (page - 1) * pageSize;
		Object[] params = { nextMin, lastMax };
		CostMapper mapper = new CostMapper();
		List<Cost> list = super.getJdbcTemplate().query(sql, params, mapper);
		return list;
	}

	@Override
	public int findTotalPage(int pageSize) throws DataAccessException {
		String sql = "select count(*) from COST";
		int size = super.getJdbcTemplate().queryForInt(sql);
		if (size % pageSize == 0) {
			return size / pageSize;
		} else {
			return size / pageSize + 1;
		}
	}

	@Override
	public void deleteById(Integer id) throws DataAccessException {
		String sql = "delete from COST where ID=?";
		Object[] params = { id };
		super.getJdbcTemplate().update(sql, params);
	}

	@Override
	public Cost findByName(String feeName) throws DataAccessException {
		String sql = "select * from COST where NAME=?";
		Object[] params = { feeName };
		CostMapper mapper = new CostMapper();
		Cost cost = (Cost) super.getJdbcTemplate().queryForObject(sql, params,
				mapper);
		return cost;
	}

	@Override
	public Cost findById(Integer id) throws DataAccessException {
		String sql = "select * from COST where ID=?";
		Object[] params = { id };
		CostMapper mapper = new CostMapper();
		Cost cost = (Cost) super.getJdbcTemplate().queryForObject(sql, params,
				mapper);
		return cost;
	}

	@Override
	public void updateCost(Cost cost) throws DataAccessException {
		String sql = "update cost set name=?,base_duration=?,"
				+ "base_cost=?,unit_cost=?,cost_type=?,descr=? " + "where id=?";
		Object[] params = { cost.getName(), cost.getBaseDuration(),
				cost.getBaseCost(), cost.getUnitCost(), cost.getCostType(),
				cost.getDescr(), cost.getId() };
		super.getJdbcTemplate().update(sql, params);
	}

}
