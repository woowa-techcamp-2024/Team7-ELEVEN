package com.wootecam.luckyvickyauction.core.auction.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.wootecam.luckyvickyauction.context.RepositoryTest;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuctionQueryDslRepositoryTest extends RepositoryTest {

    @Nested
    class 구매자_경매목록_조회_동적_쿼리_실행시 {

        @Test
        void 조회_개수만큼_경매목록을_조회한다() {

            // given
            int offset = 0;
            int size = 3;
            var condition = new AuctionSearchCondition(offset, size);

            for (int i = 0; i < size + 3; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);

            // then
            assertThat(result.size()).isEqualTo(size);
        }

        @Test
        void offset을_적용하여_데이터를_조회한다() {
            // given
            int offset = 0;
            int size = 3;
            var condition = new AuctionSearchCondition(offset, size);

            for (int i = 0; i < size; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);
            Long baseId = result.get(2).getId();

            // then
            assertThat(result)
                    .map(AuctionEntity::getId)
                    .containsExactly(baseId + 2L, baseId + 1L, baseId);
        }
    }
}
