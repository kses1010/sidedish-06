package com.codesquad.sidedish06.dao;

import com.codesquad.sidedish06.domain.dto.RequestDetail;
import com.codesquad.sidedish06.domain.dto.ResponseDetail;
import com.codesquad.sidedish06.domain.entity.DetailSection;
import com.codesquad.sidedish06.domain.entity.ThumbImage;
import com.codesquad.sidedish06.utils.DaoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class DetailDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DetailDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(RequestDetail detail) {
        insertDetail(detail);
        insertThumbImages(detail);
        insertDetailSections(detail);
    }

    private void insertDetail(RequestDetail detail) {
        String sql = "update babchan set top_image = ?, point = ?, delivery_info = ?, delivery_fee = ? where hash = ?";

        this.jdbcTemplate.update(sql,
                detail.getTop_image(),
                detail.getPoint(),
                detail.getDelivery_info(),
                detail.getDelivery_fee(),
                detail.getHash()
        );
    }

    private void insertThumbImages(RequestDetail detail) {
        String sql = "insert into thumb_image(hash, imageUrl) VALUES (?, ?)";

        for (ThumbImage thumbImage : detail.getThumb_images()) {
            jdbcTemplate.update(sql, detail.getHash(), thumbImage.getImageUrl());
        }
    }

    private void insertDetailSections(RequestDetail detail) {
        String sql = "insert into detail_section(hash, imageUrl) VALUES (?, ?)";

        for (DetailSection detailSection : detail.getDetail_section()) {
            jdbcTemplate.update(sql, detail.getHash(), detailSection.getImageUrl());
        }
    }

    public ResponseDetail read(String hash) {
        String sql = "SELECT hash, title, top_image, description, point, delivery_info, delivery_fee, n_price, s_price " +
                "FROM babchan " +
                "WHERE hash = ?";

        RowMapper<ResponseDetail> responseDetailRowMapper = new RowMapper<ResponseDetail>() {
            @Override
            public ResponseDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResponseDetail response = new ResponseDetail();
                response.setHash(hash);
                response.setTitle(rs.getString("title"));
                response.setTop_image(rs.getString("top_image"));
                response.setThumb_images(thumbImages(hash));
                response.setDescription(rs.getString("description"));
                response.setPoint(rs.getString("point"));
                response.setDelivery_info(rs.getString("delivery_info"));
                response.setDelivery_fee(rs.getString("delivery_fee"));
                response.setOriginPrice(rs.getString("n_price"));
                response.setSalePrice(rs.getString("s_price"));
                response.setDetail_section(sections(hash));
                return response;
            }
        };

        return this.jdbcTemplate.queryForObject(sql, new Object[]{hash}, responseDetailRowMapper);
    }

    private List<String> thumbImages(String hash) {
        String sql = "select imageUrl from thumb_image where hash = ?";

        return this.jdbcTemplate.query(sql, new Object[]{hash}, DaoUtils.getFirstColumns());
    }

    private List<String> sections(String hash) {
        String sql = "select imageUrl from detail_section where hash = ?";

        return this.jdbcTemplate.query(sql, new Object[]{hash}, DaoUtils.getFirstColumns());
    }
}