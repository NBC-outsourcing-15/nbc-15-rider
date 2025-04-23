package rider.nbc.domain.cart.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;

import java.util.List;
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
        MenuItem menuItem = new MenuItem(menu.getId(), menu.getPrice(), "밥", requestDto.getQuantity());

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

        MenuException menuException = new MenuException(

        given(menuRepository.findByIdOrElseThrow(invalidMenuId)).willThrow(new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        // when, then
        MenuException exception = assertThrows(MenuException.class, () ->
                cartService.addCartItem(authId, requestDto));
        assertEquals("메뉴를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateCart 성공 - 모두 같은 가게의 메뉴들 수정")
    void updateCart_successs(){
        // given
        Long userId = 1L;
        Long storeId = 100L;
        List<MenuItem> menuItems = List.of(
                new MenuItem(1L, 8000L,"Burger", 2),
                new MenuItem(2L, 2000L,"Fries", 1)
        );
        List<Menu> menus =List.of(
                new Menu(),
                new Menu()
        );




    }
}