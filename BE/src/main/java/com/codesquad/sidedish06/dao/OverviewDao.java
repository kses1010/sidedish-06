package com.codesquad.sidedish06.dao;

import com.codesquad.sidedish06.domain.dto.RequestOverview;
import com.codesquad.sidedish06.domain.dto.ResponseBadge;
import com.codesquad.sidedish06.domain.dto.ResponseOverview;
import com.codesquad.sidedish06.domain.dto.ResponseOverviewData;
import com.codesquad.sidedish06.domain.entity.Badge;
import com.codesquad.sidedish06.domain.entity.Delivery;
import com.codesquad.sidedish06.domain.entity.FoodType;
import com.codesquad.sidedish06.utils.DaoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.codesquad.sidedish06.utils.DaoUtils.getFirstColumns;

@Slf4j
@Repository
public class OverviewDao {

    private final JdbcTemplate jdbcTemplate;

    public OverviewDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(RequestOverview overview, String menu) {
        if (isNotDuplicatedHash(overview)) {
            insertOverview(overview, menu);
            insertDelivery(overview);
            insertBadge(overview);
        }
    }


    private boolean isNotDuplicatedHash(RequestOverview overview) {
        String sql = "select count(*) from babchan where hash = ?";

        return this.jdbcTemplate.queryForObject(sql, new Object[]{overview.getDetail_hash()}, Integer.class) == 0;
    }

    private void insertOverview(RequestOverview overview, String menu) {

        String sql = "INSERT INTO babchan (hash, type, image, alt, title, description, n_price, s_price)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                overview.getDetail_hash(),
                menu,
                overview.getImage(),
                overview.getAlt(),
                overview.getTitle(),
                overview.getDescription(),
                overview.getN_price(),
                overview.getS_price()
        );
    }

    private void insertDelivery(RequestOverview overview) {
        String sql = "insert into delivery(hash, type) VALUES (?, ?)";

        for (Delivery delivery : overview.getDelivery_type()) {
            jdbcTemplate.update(sql, overview.getDetail_hash(), delivery.getType());
        }
    }

    private void insertBadge(RequestOverview overview) {
        String sql = "insert into badge(hash, badgeName, badgeHexa) VALUES (?, ?, ?)";

        for (Badge badge : overview.getBadge()) {
            String badgeName = badge.getBadgeName();
            jdbcTemplate.update(sql, overview.getDetail_hash(), badgeName, DaoUtils.hexaMap.get(badgeName));
        }
    }

    public ResponseOverview listOverview(String menu) {
        String sql = "select menu_index, sub_title, main_title from food_type where type = ?";

        RowMapper<ResponseOverview> rowMapper = new RowMapper<ResponseOverview>() {
            @Override
            public ResponseOverview mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResponseOverview overview = new ResponseOverview();
                overview.setMenuIndex(rs.getInt("menu_index"));
                overview.setMenuType(rs.getString("sub_title"));
                overview.setMenuTypeTitle(rs.getString("main_title"));
                overview.setData(listMenuOverviewData(menu));
                return overview;
            }
        };

        return this.jdbcTemplate.queryForObject(sql, new Object[]{menu}, rowMapper);
    }

    public List<ResponseOverviewData> listMenuOverviewData(String menu) {
        String sql = "SELECT hash, image, alt, title, description, n_price, s_price " +
                "FROM babchan WHERE type = ?";

        RowMapper<ResponseOverviewData> responseOverviewRowMapper = new RowMapper<ResponseOverviewData>() {
            @Override
            public ResponseOverviewData mapRow(ResultSet rs, int rowNum) throws SQLException {

                String hash = rs.getString("hash");

                ResponseOverviewData data = new ResponseOverviewData();
                data.setHash(hash);
                data.setImage(rs.getString("image"));
                data.setAlt(rs.getString("alt"));
                data.setDelivery_type(deliveries(hash));
                data.setTitle(rs.getString("title"));
                data.setDescription(rs.getString("description"));
                data.setOriginPrice(rs.getString("n_price"));
                data.setSalePrice(rs.getString("s_price"));
                data.setBadge(badges(hash));
                return data;
            }
        };

        return this.jdbcTemplate.query(sql, new Object[]{menu}, responseOverviewRowMapper);
    }

    public List<String> deliveries(String hash) {
        String sql = "select type from delivery where hash = ?";

        return this.jdbcTemplate.query(sql, new Object[]{hash}, getFirstColumns());
    }

    private List<ResponseBadge> badges(String hash) {
        String sql = "select badgeName, badgeHexa from badge where hash = ?";

        RowMapper<ResponseBadge> badgeRowMapper = new RowMapper<ResponseBadge>() {
            @Override
            public ResponseBadge mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResponseBadge badge = new ResponseBadge();
                badge.setBadgeName(rs.getString("badgeName"));
                badge.setBadgeHexa(rs.getString("badgeHexa"));
                return badge;
            }
        };

        return this.jdbcTemplate.query(sql, new Object[]{hash}, badgeRowMapper);
    }
}