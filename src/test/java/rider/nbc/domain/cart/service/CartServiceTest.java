package rider.nbc.domain.cart.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CartRedisRepository cartRedisRepository;

    @InjectMocks
    private CartService cartService;


    @Test
    @DisplayName("addCartItem 성공 - 장바구니에 메뉴 추가 성공")
    void addCartItem_success(){
        //given
        Store store = new Store();
        ReflectionTestUtils.setField(store, "id", 1L);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "store", store);
        ReflectionTestUtils.setField(menu, "price", 8000L);

        CartAddRequestDto requestDto = new CartAddRequestDto(menu.getId(), 2);
        Long authId = 1L;
        MenuItem menuItem = new MenuItem(menu.getId(), requestDto.getQuantity());

        Cart cart = new Cart(1L, store.getId(), menuItem);


        given(menuRepository.findByIdOrElseThrow(requestDto.getMenuId())).willReturn(menu);
        given(cartRedisRepository.findById(authId)).willReturn(Optional.empty());
        given(cartRedisRepository.save(any(Cart.class))).willReturn(cart);
        //when
        CartItemResponseDto result = cartService.addCartItem(authId, requestDto);

        //then
        assertNotNull(result);
        assertEquals(store.getId(), result.getStoreId());
        assertEquals(menu.getId(), result.getMenuId());
        assertEquals(2, result.getQuantity());
        assertEquals(requestDto.getQuantity() * menu.getPrice(), result.getPrice()); // 1000 * 2
    }

    @Test
    @DisplayName("addCartItem 실패 - 유효하지 않는 메뉴")
    void addCartItem_INVALID_CART_ITEM(){
        // given
        Long authId = 1L;
        Long invalidMenuId = 999L;
        CartAddRequestDto requestDto = new CartAddRequestDto(invalidMenuId, 1);

        given(menuRepository.findById(invalidMenuId)).willReturn(Optional.empty());

        // when, then
        CartException exception = assertThrows(CartException.class, () ->
                cartService.addCartItem(authId, requestDto));

        assertEquals("장바구니에 담을 수 없는 메뉴입니다.", exception.getMessage());
    }
}