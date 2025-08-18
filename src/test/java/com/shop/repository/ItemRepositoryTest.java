package com.shop.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.repository.Item.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.entity.QItem.item;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void createItemNmTest() {
        creatDummyItems();
        List<Item> items = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : items) {
            System.out.println(item);
            }
        }



        @Test
        @DisplayName("QueryDSL 조회 테스트")
        public void queryDslTest() {
            creatDummyItems();
            JPAQueryFactory queryFactory = new JPAQueryFactory(em);
            JPAQuery<Item> query = queryFactory.selectFrom(item)
            .where(item.itemSellStatus.eq(ItemSellStatus.SELL))
                    .where(item.itemDetail.like("%" + "테스트 상품 상세 설명" +  "%"))
                    .orderBy(item.price.desc());
            List<Item> items = query.fetch();
            for (Item item : items) {
                System.out.println(item);
            }
        }


        @Test
        @DisplayName("상품 QueryDSL 조회 테스트2")
        public void queryDslTest2()
        {
            creatDummyItems2();
            String itemDetail = "테스트 상품 상세 설명";
            int price = 10003;
            String itemSellStatus = "SELL";
            int pageNum = 1;

            Pageable pageable = PageRequest.of(pageNum-1, 5);
    /*
    조건1. 주어진 itemDetail 키워드를 포함
    조건2. 상품 가격이 주어진 price 보다 큰
    조건3. 조회하려는 상태가 SELL인 경우 상품의 판매 상태가 SELL인
    조건4. 한 페이지 당 5개씩 페이징된 데이터를 조회
     */
            JPAQueryFactory queryFactory = new JPAQueryFactory(em);
            JPAQuery<Item> baseQuery = queryFactory.selectFrom(item);
    /*
    SELECT * FROM item
     */

            BooleanBuilder booleanBuilder = new BooleanBuilder();
            booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
            booleanBuilder.and(item.price.gt(price));
            if(itemSellStatus.equals("SELL"))
            {
                booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));

            }
            JPAQuery<Item> conditionedQuery =  baseQuery.where(booleanBuilder);
            JPAQuery<Item> pagedQuery = conditionedQuery
                    .orderBy(item.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
    /*
    SELECT * FROM item
    WHERE itemDetail Like ?
    AND price > ?
    AND item_sell_status = "SELL" <<-- 조건부
    ORDER BY price DESC
    LIMIT 5 OFFSET ?
     */
            List<Item> contents = pagedQuery.fetch();
            Long totalCount = queryFactory
                    .select(Wildcard.count)
                    .from(item)
                    .where(booleanBuilder)
                    .fetchOne();
            Page<Item> result = new PageImpl<>(contents, pageable, totalCount);

            System.out.println("총 컨텐츠 요소의 수 : " + result.getTotalElements());
            System.out.println("조회 가능한 총 페이지 수 : " + result.getTotalPages());
            List<Item> items = result.getContent();
            for (Item item : items) {
                System.out.println(item);
            }
        }



        public void creatDummyItems(){
            for(int i=1;i<=10;i++){
                Item item = new Item();
                item.setItemNm("테스트 상품" + i);
                item.setPrice(10_000 + i);
                item.setItemDetail("테스트 상품 상세 설명" + i);
                item.setItemSellStatus(ItemSellStatus.SELL);
                item.setStockNumber(100 + i);
                item.setRegTime(LocalDateTime.now());
                item.setUpdateTime(LocalDateTime.now());
                Item savedItem = itemRepository.save(item);
        }
    }

    public void creatDummyItems2(){

        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 + i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10_000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }






    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void createItemNmOrItemDetailTest() {
        creatDummyItems();
        List<Item> items = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
                for(Item item : items){
                    System.out.println(item);
                }
    }


    @Test
    @DisplayName("가격 LessThan 테스트")
    public void createItemNmLessThanTest() {
        creatDummyItems();
        List<Item> items = itemRepository.findByPriceLessThan(10_005);
        for (Item item : items) {
            System.out.println(item);
        }
    }


    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void createItemNmGreaterThanTest() {
        creatDummyItems();
        List<Item> items = itemRepository.findByPriceLessThanOrderByPriceDesc(10_005);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest() {
        creatDummyItems();
        List<Item> items = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item);
        }
    }


    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트 by native")
    public void findByItemDetailTestByNative() {
        creatDummyItems();
        List<Item> items = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item);
        }
    }



}


