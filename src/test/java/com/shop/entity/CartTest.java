package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Persistent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations ="classpath:application-test.properties")
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@Email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원의 엔티티 매핑 조회 테스트")
    public void findCartAndMember() {

        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);



        System.out.println("저장하기 전 cart의 주소값" + cart.hashCode());
        System.out.println("저장하기 전 mamber의 주소값" + member.hashCode());

        Optional<Cart> savedCartOp = cartRepository.findById(cart.getId());
        Cart savedCart = savedCartOp.orElseThrow(EntityNotFoundException :: new);


        Member foundMember = savedCart.getMember();


        System.out.println("저장 후 cart의 주소값" +savedCart.hashCode());
        System.out.println("저장 후 mamber의 주소값" + foundMember.hashCode());

        assertEquals(foundMember.getId(), member.getId());
    }

    @Test
    @DisplayName("장바구니 회원의 엔티티 매핑 조회 테스트")
    public void findCartAndMemberEm() {

        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        Optional<Cart> savedCartOp = cartRepository.findById(cart.getId());
        Cart savedCart = savedCartOp.orElseThrow(EntityNotFoundException :: new);

        em.flush();
        em.clear();


        Member foundMember = savedCart.getMember();
        assertEquals(foundMember.getId(), member.getId());
    }
}


