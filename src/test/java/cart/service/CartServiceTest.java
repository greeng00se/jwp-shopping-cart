package cart.service;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import cart.dao.CartDao;
import cart.dao.MemberDao;
import cart.dao.ProductDao;
import cart.domain.Cart;
import cart.domain.Member;
import cart.domain.Product;
import cart.dto.CartSearchResponse;
import cart.dto.ProductDto;
import cart.exception.ProductNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private MemberDao memberDao;

    @Test
    void 장바구니에_상품을_추가한다() {
        // given
        final Long productId = productDao.saveAndGetId(new Product("pizza1", "pizza1.jpg", 8900L));
        final Long memberId = memberDao.saveAndGetId(new Member("pizza@pizza.com", "password"));
        final Cart cart = new Cart(memberId, productId);

        // when
        final Long id = cartDao.saveAndGetId(cart);

        // then
        final List<Product> result = cartDao.findAllProductByMemberId(memberId);
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(id).isPositive()
        );
    }

    @Test
    void 입력받은_사용자의_카트에_담겨있는_모든_상품을_조회한다() {
        // given
        final Long productId1 = productDao.saveAndGetId(new Product("pizza1", "pizza1.jpg", 8900L));
        final Long productId2 = productDao.saveAndGetId(new Product("pizza2", "pizza2.jpg", 18900L));
        final Long memberId = memberDao.saveAndGetId(new Member("pizza@pizza.com", "password"));
        cartDao.saveAndGetId(new Cart(memberId, productId1));
        cartDao.saveAndGetId(new Cart(memberId, productId2));

        // when
        final CartSearchResponse result = cartService.findAll(memberId);

        // then
        assertThat(result.getProducts()).usingRecursiveComparison().isEqualTo(List.of(
                new ProductDto(productId1, "pizza1", "pizza1.jpg", 8900L),
                new ProductDto(productId2, "pizza2", "pizza2.jpg", 18900L)
        ));
    }

    @Test
    void 삭제할_품목_아이디와_사용자_아이디를_받아_장바구니_항목을_제거한다() {
        // given
        final Long productId = productDao.saveAndGetId(new Product("pizza1", "pizza1.jpg", 8900L));
        final Long memberId = memberDao.saveAndGetId(new Member("pizza@pizza.com", "password"));
        cartDao.saveAndGetId(new Cart(memberId, productId));

        // when
        cartService.delete(productId, memberId);

        // then
        assertThat(cartDao.findAllProductByMemberId(memberId)).isEmpty();
    }

    @Test
    void 삭제에_실패하는_경우_ProductNotFoundException_을_던진다() {
        // expect
        assertThatThrownBy(() -> cartService.delete(MAX_VALUE, MAX_VALUE))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }
}
