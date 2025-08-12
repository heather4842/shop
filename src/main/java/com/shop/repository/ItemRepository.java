package com.shop.repository;


import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNm(String itemNm);
//        SELECT *
//        FROM item
//        WHERE item_nm = ?

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

//        SELECT *
//        FROM item
//        WHERE item_nm = ? OR item_detail

    List<Item> findByPriceLessThan(Integer price);
//    SELECT *
//    FROM item
//    WHERE price < ?

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
//    SELECT *
//    FROM item
//    WHERE price < ?
//    ORDER BY price DESC

    @Query("SELECT i From Item i WHERE i.itemDetail LIKE %:itemDetail% ORDER BY i.price DESC")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);


    @Query(value = "SELECT * From item i WHERE i.item_Detail LIKE %:itemDetail% ORDER BY i.price DESC",
        nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);





}
