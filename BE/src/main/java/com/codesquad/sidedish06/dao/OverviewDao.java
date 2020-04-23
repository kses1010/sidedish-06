package com.codesquad.sidedish06.dao;

import com.codesquad.sidedish06.domain.dto.RequestOverview;
import com.codesquad.sidedish06.domain.dto.ResponseOverview;
import com.codesquad.sidedish06.domain.entity.Badge;
import com.codesquad.sidedish06.domain.entity.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class OverviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OverviewDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(RequestOverview overview, String menu) {

        String sql = "insert into babchan (hash, food_type, image, alt, title, description, n_price, s_price)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";

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

        sql = "insert into delivery(hash, type) VALUES (?, ?)";

        if (overview.getDelivery_type() == null) {
            jdbcTemplate.update(sql, overview.getDetail_hash(), null);
        } else {
            for (Delivery delivery : overview.getDelivery_type()) {
                jdbcTemplate.update(sql, overview.getDetail_hash(), delivery.getType());
            }
        }

        sql = "insert into badge(hash, event) VALUES (?, ?)";

        if (overview.getBadge() == null) {
            jdbcTemplate.update(sql, overview.getDetail_hash(), null);
        } else {
            for (Badge badge : overview.getBadge()) {
                jdbcTemplate.update(sql, overview.getDetail_hash(), badge.getEvent());
            }
        }
    }

    private List<String> deliveries(ResponseOverview response) {
        String sql = "select type from delivery where detail_hash = ?";

        RowMapper<Delivery> deliveryRowMapper = new RowMapper<Delivery>() {
            @Override
            public Delivery mapRow(ResultSet rs, int rowNum) throws SQLException {
                Delivery delivery = new Delivery();
                delivery.setType(rs.getString("type"));
                return delivery;
            }
        };

        List<Delivery> deliveries = this.jdbcTemplate.query(sql, new Object[]{response.getDetail_hash()}, deliveryRowMapper);

        List<String> types = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            types.add(delivery.getType());
        }

        return types;
    }

    private List<String> badges(ResponseOverview response) {
        String sql = "select event from badge where detail_hash = ?";

        RowMapper<Badge> badgeRowMapper = new RowMapper<Badge>() {
            @Override
            public Badge mapRow(ResultSet rs, int rowNum) throws SQLException {
                Badge badge = new Badge();
                badge.setEvent(rs.getString("event"));
                return badge;
            }
        };

        List<Badge> badges = this.jdbcTemplate.query(sql, new Object[]{response.getDetail_hash()}, badgeRowMapper);

        List<String> events = new ArrayList<>();

        for (Badge badge : badges) {
            events.add(badge.getEvent());
        }

        return events;
    }

    public List<ResponseOverview> listOverview() {
        String sql = "select * from overview";

        RowMapper<ResponseOverview> responseOverviewRowMapper = new RowMapper<ResponseOverview>() {
            @Override
            public ResponseOverview mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResponseOverview response = new ResponseOverview();
                response.setDetail_hash(rs.getString("detail_hash"));
                response.setImage(rs.getString("image"));
                response.setAlt(rs.getString("alt"));
                response.setDelivery_type(deliveries(response));
                response.setTitle(rs.getString("title"));
                response.setDescription(rs.getString("description"));
                response.setN_price(rs.getString("n_price"));
                response.setS_price(rs.getString("s_price"));
                response.setBadge(badges(response));
                return response;
            }
        };

        return this.jdbcTemplate.query(sql, responseOverviewRowMapper);
    }
}
