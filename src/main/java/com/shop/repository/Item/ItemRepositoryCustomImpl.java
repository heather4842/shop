package com.shop.repository.Item;

import ch.qos.logback.core.util.StringUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime now = LocalDateTime.now();
        if(StringUtils.equals(searchDateType, 1d)) {
            now = now.minusDays(1);
        }else if(StringUtils.equals(searchDateType, "1w")) {
            now = now.minusWeeks(1);
        }else if(StringUtils.equals(searchDateType, "1m")) {
            now = now.minusMonths(1);
        }else if(StringUtils.equals(searchDateType, "6m")){
            now = now.minusMonths(6);
        }else if(StringUtils.equals(searchDateType, "all") || searchDateType == null) {
            return null;
        }

        return item.regTime.after(now);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        if(itemSellStatus == null) {
            return null;
        }
        return item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals(searchBy, "itemNm")) {
            return item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals(searchBy, "createdBy")) {
            return item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }


    @Override
    public Page<Item> getAdminPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = jpaQueryFactory
                .selectFrom(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);

        return new PageImpl<>(content, pageable, total.orElse(0L));
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<MainItemDto> content = jpaQueryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.price,
                        itemImg.imgUrl
                        ))
                    .from(itemImg)
                    .innerJoin(itemImg.item)
                    .where(itemImg.repimgYn.eq("Y"))
                    .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .innerJoin(itemImg.item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .fetchOne();
        Optional<Long> total = Optional.ofNullable(totalCount);

        return new PageImpl<>(content, pageable, total.orElse(0L));

    }

}
